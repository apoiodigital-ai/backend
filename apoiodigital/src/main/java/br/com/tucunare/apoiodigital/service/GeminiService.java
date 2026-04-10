package br.com.tucunare.apoiodigital.service;

import br.com.tucunare.apoiodigital.dto.AppSuportadoToGeminiDTO;
import br.com.tucunare.apoiodigital.dto.FindBestAppResponseDTO;
import br.com.tucunare.apoiodigital.dto.RequestInputToGeminiDTO;
import br.com.tucunare.apoiodigital.dto.RequisicaoInputDTO;
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

    private String basicRules;

    @PostConstruct
    public void init() {
        // Initialize the client with your key
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
        
        basicRules = getRules("src/main/resources/rules/basic-rules.txt");
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
            return Files.readString(caminho);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    GenerateContentConfig generateConfig(String additionalRules, Float temp){
                return GenerateContentConfig.builder()
                        .responseMimeType("application/json") // This is the "magic" line
                .systemInstruction(Content.fromParts(
                        Part.fromText(basicRules + "\n" +additionalRules)
                )).temperature(temp)
                .build();
    }

    public String definirTituloAtalho(String prompt){
        String additionalRules = getRules("src/main/resources/rules/defineTitleAtalho-rules.txt");

        ObjectMapper objectMapper = new ObjectMapper();

        GenerateContentConfig config = generateConfig(additionalRules, 0.1f);

        try {
            String response = analyzeText(prompt, config);

            JsonNode jsonNode = objectMapper.readTree(response);

            String titulo = jsonNode.get("titulo").asText();

            System.out.println("\nTITULO DO ATALHO CRIADO: " + titulo);

            return titulo;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

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
