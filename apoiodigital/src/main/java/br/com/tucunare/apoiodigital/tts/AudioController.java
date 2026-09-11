package br.com.tucunare.apoiodigital.tts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Serves the audio files GeminiTtsService writes to disk. Left out of authentication in
 * SecurityConfig (a plain {@code <audio src>} can't attach an x-api-key header); the filename
 * itself is an unguessable random UUID, and only a strict filename shape is accepted here to
 * rule out path traversal.
 */
@RestController
@RequestMapping("/audio")
public class AudioController {

    private static final Pattern SAFE_FILENAME = Pattern.compile("^[a-fA-F0-9-]+\\.wav$");

    private final Path storageDir;

    public AudioController(@Value("${app.audio.storage-dir:audio-storage}") String storageDir) {
        this.storageDir = Path.of(storageDir);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> obterAudio(@PathVariable String filename) {
        if (!SAFE_FILENAME.matcher(filename).matches()) {
            return ResponseEntity.badRequest().build();
        }

        Path file = storageDir.resolve(filename).normalize();
        if (!file.startsWith(storageDir) || !Files.isRegularFile(file)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/wav"))
                .body(new FileSystemResource(file));
    }
}
