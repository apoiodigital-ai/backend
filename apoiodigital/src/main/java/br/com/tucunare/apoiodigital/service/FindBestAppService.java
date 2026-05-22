package br.com.tucunare.apoiodigital.service;

import br.com.tucunare.apoiodigital.dto.*;
import br.com.tucunare.apoiodigital.enums.IAAgent2ModoEnum;
import br.com.tucunare.apoiodigital.model.AppSuportado;
import br.com.tucunare.apoiodigital.repository.AppSuportadoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class FindBestAppService {

    @Autowired
    private AppSuportadoRepository appSuportadoRepository;

    private final ChatClient chatClient;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public FindBestAppService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    private String getRules(String resourcePath) {
        try {
            return new ClassPathResource(resourcePath).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler o arquivo de regras: " + resourcePath, e); // This is the magic line!
        }
    }

    private <T> T agentStructure(Object request, String rulePath, double temp, Class<T> targetClass) {
        String rule = getRules(rulePath);

        try {
            String input = objectMapper.writeValueAsString(request);

            String response = chatClient.prompt()
                    .system(rule)
                    .user(input)
                    .options(GoogleGenAiChatOptions.builder()
                            .temperature(temp)
                            .build())
                    .call()
                    .content();

            return objectMapper.readValue(response, targetClass);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar JSON da IA", e);
        }
    }

    public String definirTituloAtalho(String promptLimpo) {
        JsonNode jsonNode = agentStructure(
                Map.of("prompt", promptLimpo),
                "rules/defineTitleAtalho-rules.txt",
                0.1,
                JsonNode.class
        );

        String titulo = jsonNode.get("titulo").asText();
        System.out.println("\nTITULO DO ATALHO CRIADO: " + titulo);
        return titulo;
    }

    public boolean agent0(String prompt) {
        try {
            JsonNode jsonNode = agentStructure(
                    Map.of("prompt", prompt),
                    "rules/agent0-rule.txt",
                    0.0,
                    JsonNode.class
            );

            boolean aprovado = jsonNode.get("aprovado").asBoolean();
            System.out.println("Prompt aprovado pelo A0: " + aprovado);
            return aprovado;

        } catch (Exception e) {
            System.out.println("A0 bloqueou o prompt ou gerou erro: " + e.getMessage());
            return false;
        }
    }

    public String agent1(String prompt) {
        JsonNode jsonNode = agentStructure(
                Map.of("prompt", prompt),
                "rules/simplifyPrompt-rule.txt",
                0.1,
                JsonNode.class
        );

        String promptLimpo = jsonNode.get("prompt_limpo").asText();
        System.out.println("\nPrompt Limpo (A1): " + promptLimpo);
        return promptLimpo;
    }

    public Long agent2(IAAgent2RequestDTO dto) {
        JsonNode jsonNode = agentStructure(
                dto,
                "rules/agent2-rule.txt",
                0.1,
                JsonNode.class
        );

        return jsonNode.get("id_app_instalado").asLong();
    }

    public Long agent3(IAAgent3RequestDTO dto) {
        JsonNode jsonNode = agentStructure(
                dto,
                "rules/agent3-rule.txt",
                0.1,
                JsonNode.class
        );

        JsonNode idAppBanco = jsonNode.get("id_app_banco");
        if (idAppBanco != null && idAppBanco.canConvertToLong()) {
            return idAppBanco.asLong();
        } else {
            return -1L;
        }
    }

    public String definirContexto(GenerateContextAppDTO dto) {
        JsonNode jsonNode = agentStructure(
                dto,
                "rules/defineContext-rule.txt",
                0.1,
                JsonNode.class
        );

        String contexto = jsonNode.get("contexto").asText();
        System.out.println("\nContexto Gerado (A4): " + contexto);
        return contexto;
    }

    // Fluxo principal (A0 -> A1 -> A2 -> A3 -> A4 -> A5)
    public FindBestAppResponseDTO acharMelhorApp(RequestInputToGeminiDTO dto) {
        List<AppSuportado> listaAppSuportado = appSuportadoRepository.findAll();

        String promptLimpo = agent1(dto.prompt());

        IAAgent2RequestDTO iaAgent2RequestDTO = new IAAgent2RequestDTO(
                promptLimpo, dto.lista_apps_instalados(), IAAgent2ModoEnum.inicial);
        Long idAppInstalado = agent2(iaAgent2RequestDTO);

        boolean encontradoNoBanco = false;
        Long idAppBanco = -1L;


        String pacoteAppInstaladoEscolhido = dto.lista_apps_instalados().get((int) (idAppInstalado - 1)).pacote();

        for (AppSuportado appSuportado : listaAppSuportado) {
            if (Objects.equals(pacoteAppInstaladoEscolhido, appSuportado.getPacote())) {
                System.out.println("APP NO BANCO DE DADOS!!!!: " + pacoteAppInstaladoEscolhido + " ID: " + idAppInstalado);
                idAppBanco = appSuportado.getId();
                encontradoNoBanco = true;
                break;
            }
        }

        if (!encontradoNoBanco) {
            IAAgent3RequestDTO iaAgent3RequestDTO = new IAAgent3RequestDTO(
                    pacoteAppInstaladoEscolhido,
                    listaAppSuportado);

            idAppBanco = agent3(iaAgent3RequestDTO);
            boolean achouSimilar = false;
            Long idPlayStore = -1L;

            for (AppRequestDTO appRequestDTO : dto.lista_apps_instalados()) {
                if (Objects.equals(appRequestDTO.pacote(), "com.android.vending")) {
                    idPlayStore = appRequestDTO.id();
                }

                String pacoteBancoSimilar = listaAppSuportado.get((int) (idAppBanco - 1)).getPacote();
                if (Objects.equals(pacoteBancoSimilar, appRequestDTO.pacote())) {
                    achouSimilar = true;
                    idAppInstalado = appRequestDTO.id();
                }
            }

            if (!achouSimilar) {
                idAppInstalado = idPlayStore;
            }
        }

        String nomeAppBanco = listaAppSuportado.get((int) (idAppBanco - 1)).getNome();
        String nomeAppInstalado = dto.lista_apps_instalados().get((int) (idAppInstalado - 1)).nome();

        String contexto = definirContexto(new GenerateContextAppDTO(
                promptLimpo,
                nomeAppBanco,
                nomeAppInstalado
        ));

        return new FindBestAppResponseDTO(contexto, idAppBanco, idAppInstalado);
    }
}