package br.com.tucunare.apoiodigital.tts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calls the Gemini "generateContent" REST API directly with {@code responseModalities: ["AUDIO"]}
 * to synthesize speech. Spring AI's bundled google-genai starter (used everywhere else in this
 * codebase for chat) does not expose an audio/TTS model abstraction in the version pinned in
 * pom.xml, so this talks to the public Generative Language API over plain HTTP using the same
 * API key already configured for chat (spring.ai.google.genai.api-key / APIKEY env var) —
 * consistent with how the rest of the app already depends on that same external API.
 *
 * <p>The API returns raw PCM audio (16-bit signed, mono, typically 24kHz — the actual rate is
 * read back from the response's mimeType when present) with no container around it, so it is
 * wrapped in a minimal WAV header before being written to disk; without that header most audio
 * players/decoders on the SDK side would not recognize the bytes as playable audio.</p>
 */
@Service
public class GeminiTtsService implements TtsService {

    private static final Logger log = LoggerFactory.getLogger(GeminiTtsService.class);
    private static final Pattern SAMPLE_RATE_PATTERN = Pattern.compile("rate=(\\d+)");

    private final boolean enabled;
    private final String apiKey;
    private final String model;
    private final String voice;
    private final Path storageDir;
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiTtsService(
            @Value("${app.tts.enabled:true}") boolean enabled,
            @Value("${app.tts.api-key:}") String apiKey,
            @Value("${app.tts.model:gemini-2.5-flash-preview-tts}") String model,
            @Value("${app.tts.voice:Kore}") String voice,
            @Value("${app.audio.storage-dir:audio-storage}") String storageDir,
            RestClient.Builder restClientBuilder
    ) {
        this.enabled = enabled;
        this.apiKey = apiKey;
        this.model = model;
        this.voice = voice;
        this.storageDir = Path.of(storageDir);
        this.restClient = restClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    @Override
    public Optional<String> synthesize(String texto) {
        if (!enabled || apiKey == null || apiKey.isBlank() || texto == null || texto.isBlank()) {
            return Optional.empty();
        }

        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(Map.of("text", texto))
                    )),
                    "generationConfig", Map.of(
                            "responseModalities", List.of("AUDIO"),
                            "speechConfig", Map.of(
                                    "voiceConfig", Map.of(
                                            "prebuiltVoiceConfig", Map.of("voiceName", voice)
                                    )
                            )
                    )
            );

            String responseJson = restClient.post()
                    .uri("/v1beta/models/{model}:generateContent?key={apiKey}", model, apiKey)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return parseAndStore(responseJson);
        } catch (Exception e) {
            log.warn("Falha ao sintetizar áudio via Gemini TTS; seguindo sem mensagem_voz_url", e);
            return Optional.empty();
        }
    }

    private Optional<String> parseAndStore(String responseJson) throws IOException {
        JsonNode root = objectMapper.readTree(responseJson);
        JsonNode inlineData = root
                .path("candidates").path(0)
                .path("content").path("parts").path(0)
                .path("inlineData");

        String base64Data = inlineData.path("data").asText(null);
        if (base64Data == null || base64Data.isBlank()) {
            log.warn("Resposta do Gemini TTS sem áudio (inlineData.data ausente)");
            return Optional.empty();
        }

        String mimeType = inlineData.path("mimeType").asText("audio/L16;rate=24000");
        int sampleRate = extractSampleRate(mimeType);

        byte[] pcm = Base64.getDecoder().decode(base64Data);
        byte[] wav = wrapPcmAsWav(pcm, sampleRate, 1, 16);

        Files.createDirectories(storageDir);
        String filename = UUID.randomUUID() + ".wav";
        Files.write(storageDir.resolve(filename), wav);

        return Optional.of(filename);
    }

    private static int extractSampleRate(String mimeType) {
        Matcher matcher = SAMPLE_RATE_PATTERN.matcher(mimeType);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 24000;
    }

    private static byte[] wrapPcmAsWav(byte[] pcm, int sampleRate, int channels, int bitsPerSample) throws IOException {
        int byteRate = sampleRate * channels * bitsPerSample / 8;
        int blockAlign = channels * bitsPerSample / 8;
        int dataLength = pcm.length;

        ByteArrayOutputStream out = new ByteArrayOutputStream(44 + dataLength);
        out.write("RIFF".getBytes());
        out.write(intToLittleEndian(36 + dataLength));
        out.write("WAVE".getBytes());
        out.write("fmt ".getBytes());
        out.write(intToLittleEndian(16)); // subchunk1 size (PCM)
        out.write(shortToLittleEndian((short) 1)); // audio format = PCM
        out.write(shortToLittleEndian((short) channels));
        out.write(intToLittleEndian(sampleRate));
        out.write(intToLittleEndian(byteRate));
        out.write(shortToLittleEndian((short) blockAlign));
        out.write(shortToLittleEndian((short) bitsPerSample));
        out.write("data".getBytes());
        out.write(intToLittleEndian(dataLength));
        out.write(pcm);

        return out.toByteArray();
    }

    private static byte[] intToLittleEndian(int value) {
        return new byte[]{
                (byte) (value & 0xff),
                (byte) ((value >> 8) & 0xff),
                (byte) ((value >> 16) & 0xff),
                (byte) ((value >> 24) & 0xff)
        };
    }

    private static byte[] shortToLittleEndian(short value) {
        return new byte[]{
                (byte) (value & 0xff),
                (byte) ((value >> 8) & 0xff)
        };
    }
}
