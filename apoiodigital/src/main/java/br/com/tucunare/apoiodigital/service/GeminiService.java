package br.com.tucunare.apoiodigital.service;

import br.com.tucunare.apoiodigital.dto.*;
import br.com.tucunare.apoiodigital.model.AppSuportado;
import br.com.tucunare.apoiodigital.repository.AppSuportadoRepository;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeminiService {
//
//    GenerativeModel model = GenerativeModel.builder()
//            .modelName("gemini-pro")
//            .build();

    @Autowired
    private AppSuportadoRepository appSuportadoRepository;

    @Value("${APIKEY}")
    private String apiKey;

    @Value("gemini-2.5-flash-lite")
    private String modelName;

    private Client client;


    @PostConstruct
    public void init() {
        // Initialize the client with your key
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();

    }

    public String analyzeText(String input, GenerateContentConfig config) {
        try {
            GenerateContentResponse response = client.models.generateContent(
                    modelName,
                    input,
                    config
            );
            return response.text();
        } catch (Exception e) {
            // Handle rate limits (429) or connection issues
            return "Error calling Gemini API: " + e.getMessage();
        }
    }

    public String getRules(String filepath){
        try{
            Path caminho = Path.of(filepath);
            String rule = Files.readString(caminho);
            System.out.println(rule);
            return rule;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    GenerateContentConfig generateConfig(String additionalRules, Float temp){
                return GenerateContentConfig.builder()
                        .responseMimeType("application/json") // This is the "magic" line
                .systemInstruction(Content.fromParts(
                        Part.fromText(additionalRules)
                )).temperature(temp)
                .build();
    }

    // Gera um título através do prompt_limpo de 3 a 6 palavras
    // TODO: MELHORAR REGRA...
    public String definirTituloAtalho(String prompt_limpo){ // A5
        String additionalRules = getRules("src/main/resources/rules/defineTitleAtalho-rules.txt");

        ObjectMapper objectMapper = new ObjectMapper();

        GenerateContentConfig config = generateConfig(additionalRules, 0.1f);

        try {
            String response = analyzeText(prompt_limpo, config);

            JsonNode jsonNode = objectMapper.readTree(response);

            String titulo = jsonNode.get("titulo").asText();

            System.out.println("\nTITULO DO ATALHO CRIADO: " + titulo);

            return titulo;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Simplifica o prompt vindo do usuário e retorna prompt_limpo.
    // o A1 reduz o prompt do usuário em prompts mais objetivos e menos complexos para os próximos agentes
    public String simplificarPrompt(String prompt){ // A1
        ObjectMapper objectMapper = new ObjectMapper();

        String rules = getRules("src/main/resources/rules/simplifyPrompt-rule.txt");

        GenerateContentConfig config = generateConfig(rules, 0.1f);

        try {
            String input = objectMapper.writeValueAsString(prompt);

            String response = analyzeText(input, config);

            JsonNode jsonNode = objectMapper.readTree(response);

            String promptLimpo = jsonNode.get("prompt_limpo").asText();

            System.out.println("\nPrompt Limpo: " + promptLimpo);

            return promptLimpo;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // O GenerateContextAppDTO exige prompt_limpo, nome_app_instalado e nome_app_banco
    // prompt_limpo é dado pelo A1
    // nome_app_instalado vai ser pego por RequestInputToGeminiDTO.lista_apps_instalados.get(i), tal que i é dado pelo A2
    // nome_app_banco vai ser pego por RequestInputToGeminiDTO.lista_apps_banco.get(j), tal que j é dado pelo A3
    public String definirContexto(GenerateContextAppDTO dto){ // A4
        ObjectMapper objectMapper = new ObjectMapper();

        String rules = getRules("src/main/resources/rules/defineContext-rule.txt");

        GenerateContentConfig config = generateConfig(rules, 0.1f);

        try {
            String input = objectMapper.writeValueAsString(dto);

            String response = analyzeText(input, config);

            JsonNode jsonNode = objectMapper.readTree(response);

            String promptLimpo = jsonNode.get("contexto").asText();

            System.out.println("\nPrompt Limpo: " + promptLimpo);

            return promptLimpo;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: REESTRUTURAR FUNCAO
    // Tal função vai ser reestruturada para "juntar" todos os agentes. O RequestInputToGeminiDTO possui todas as informacoes iniciais necessárias.
    // A ideia é reunir todos os agentes e definir o fluxo definido (A0 -> A1 -> A2 -> A3 -> A4 -> A5) aqui
    // NOTA: CRIAR OUTRA FUNCAO PARA A2 E A3... acharMelhorApp agora se refere à ação como um toodo.
    public FindBestAppResponseDTO acharMelhorApp(RequestInputToGeminiDTO dto) {
        ObjectMapper objectMapper = new ObjectMapper();

//        List<AppSuportadoToGeminiDTO> lista_apps_banco = appSuportadoRepository.findAllApps();

        String additionalRules = getRules("src/main/resources/rules/findBestApp-rules.txt");

        try{
            String input = objectMapper.writeValueAsString(dto);

            GenerateContentConfig config = generateConfig(additionalRules, 0.1f);

            String response = analyzeText(input, config);

            System.out.println("RESPONSE DA IA: \n" + response);

            FindBestAppResponseDTO bestApp = objectMapper.readValue(
                    response,
                    FindBestAppResponseDTO.class
            );

            return bestApp;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }



        // gerar config -> additionalRules
        // gere um contexto baseado no appSuportado e no prompt; gere um titulo de no maximo 5 palavras...


//      {
//            "prompt": quero pedir comida
//            "apps_instalados": [
//                     { "id": 1, "nome": ifood},
//                      ...
//                    ]
//            "id_usuario": jir29jfo3oo1o1k-492JJWOPA0
//        }


    }


}
