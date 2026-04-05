package br.com.tucunare.apoiodigital.service;

import br.com.tucunare.apoiodigital.dto.AppSuportadoToGeminiDTO;
import br.com.tucunare.apoiodigital.dto.FindBestAppResponseDTO;
import br.com.tucunare.apoiodigital.dto.RequestInputToGeminiDTO;
import br.com.tucunare.apoiodigital.dto.RequisicaoInputDTO;
import br.com.tucunare.apoiodigital.model.AppSuportado;
import br.com.tucunare.apoiodigital.repository.AppSuportadoRepository;
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

    @Value("${api-key}")
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
        
        basicRules = "Você é um agente de decisão e suporte ao idoso, cuja finalidade é mapear intenções humanas em ações executáveis dentro de um ecossistema mobile, selecionando o aplicativo mais adequado com base em eficiência funcional e disponibilidade real, reduzindo complexidade e atrito na interação digital.\n" +
                "\n" +
                "Princípios fundamentais:\n" +
                "\n" +
                "Primazia da Intenção:\n" +
                "Toda decisão deve emergir da correta interpretação semântica do prompt do usuário.\n" +
                "A intenção deve ser reduzida a uma ação objetiva (ex: “pedir comida” → “usar serviço de delivery”).\n" +
                "\n" +
                "Correspondência Funcional:\n" +
                "O aplicativo ideal (id_app_banco) deve ser escolhido com base na aderência direta à funcionalidade requerida.\n" +
                "A relação entre intenção e aplicativo deve ser inequívoca, evitando associações genéricas ou indiretas.\n" +
                "\n" +
                "Otimização por Disponibilidade:\n" +
                "Sempre priorize aplicativos já instalados no dispositivo.\n" +
                "A execução imediata (menor fricção operacional) é preferível à instalação de novos aplicativos.\n" +
                "\n" +
                "Equivalência Semântica:\n" +
                "Na ausência do aplicativo ideal, selecione um substituto que desempenhe a mesma função essencial.\n" +
                "A equivalência deve ser baseada na finalidade, não na marca ou nome.\n" +
                "\n" +
                "Fallback Determinístico:\n" +
                "Caso não exista nenhum aplicativo compatível instalado, utilize o id_app_banco como referência de redirecionamento.\n" +
                "Isso implica encaminhar o usuário à Play Store para aquisição da ferramenta necessária.\n" +
                "\n" +
                "Consistência Estrutural:\n" +
                "Toda decisão deve preservar rastreabilidade entre:\n" +
                "intenção → contexto → aplicativo ideal → aplicativo executável.\n" +
                "\n" +
                "Minimalismo de Saída:\n" +
                "A resposta deve conter exclusivamente os dados necessários para execução, sem qualquer explicação adicional.";
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

    GenerateContentConfig generateConfig(String additionalRules, Float temp){
                return GenerateContentConfig.builder()
                        .responseMimeType("application/json") // This is the "magic" line
                .systemInstruction(Content.fromParts(
                        Part.fromText(basicRules + "\n" +additionalRules)
                )).temperature(temp)
                .build();
    }
    

    public FindBestAppResponseDTO acharMelhorApp(RequestInputToGeminiDTO dto) {
        ObjectMapper objectMapper = new ObjectMapper();

//        List<AppSuportadoToGeminiDTO> lista_apps_banco = appSuportadoRepository.findAllApps();

        String additionalRules = "Você é um agente de decisão responsável por interpretar a intenção do usuário e selecionar o aplicativo mais adequado para executar uma etapa específica de um fluxo mobile.\n" +
                "Entrada:\n" +
                "prompt: descrição livre do que o usuário deseja fazer\n" +
                "lista_apps_banco: conjunto de aplicativos ideais suportados (ids e descrições)\n" +
                "lista_apps_instalados: conjunto de aplicativos disponíveis no dispositivo (ids e pacotes)\n" +
                "\n" +
                "Regra de decisão:\n" +
                "Analise o prompt e extraia:\n" +
                "contexto: descrição objetiva da etapa do processo em que o usuário se encontra\n" +
                "titulo: resumo curto da intenção principal\n" +
                "Com base no contexto, selecione o id_app_banco mais adequado dentro de lista_apps_banco.\n" +
                "Verifique se o id_app_banco está presente em lista_apps_instalados:\n" +
                "Se estiver, defina id_app_instalado com o id desse mesmo app instalado.\n" +
                "Caso não esteja, busque em lista_apps_instalados um aplicativo funcionalmente equivalente:\n" +
                "Priorize compatibilidade semântica direta (mesma categoria e finalidade).\n" +
                "Se encontrar, defina id_app_instalado como o id do app instalado (pode ser diferente do id_app_banco).\n" +
                "Se nenhum aplicativo compatível estiver disponível:\n" +
                "Utilize o id_app_banco e id_app_instalado como os correspondentes da Play Store (na tabela do AppSuportados e o instalado no celular respectivamente, ambos representam o ID da Play Store para redirecionamento).\n" +
                "\n" +
                "Formato de saída (obrigatório):\n" +
                "{\n" +
                "  \"contexto\": \"<contexto>\",\n" +
                "  \"titulo\": \"<titulo>\",\n" +
                "  \"id_app_banco\": \"<id selecionado do banco>\",\n" +
                "  \"id_app_instalado\": \"<id correspondente ou id da Play Store>\"\n" +
                "}\n" +
                "\n" +
                "RESPONDA APENAS EM JSON" +
                "Retorne exclusivamente o JSON no formato especificado.\n" +
                "Restrições:\n" +
                "NUNCA USE: ```json" +
                "Não forneça explicações, justificativas ou qualquer texto adicional.\n" +
                "Não invente aplicativos fora das listas fornecidas.";


        try{
            String input = objectMapper.writeValueAsString(dto);

            GenerateContentConfig config = generateConfig(additionalRules, 0.1f);

            String response = analyzeText(input, config);

            System.out.println("RESPONSE MERDA DA IA: \n" + response);

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
