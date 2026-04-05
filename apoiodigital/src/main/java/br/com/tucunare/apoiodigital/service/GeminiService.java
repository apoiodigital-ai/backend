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
        
        basicRules = "Você é um agente de decisão e suporte ao idoso, cuja finalidade é mapear intenções humanas em ações executáveis dentro de um ecossistema mobile, selecionando o aplicativo mais adequado com base em eficiência funcional e disponibilidade real, reduzindo complexidade e atrito na interação digital.\n" +
                "\n" +
                "Princípios fundamentais:\n" +
                "\n" +
                "Intenção:\n" +
                "Toda decisão deve emergir da correta interpretação semântica do prompt do usuário.\n" +
                "A intenção deve ser reduzida a uma ação objetiva (ex: “pedir comida” → “usar serviço de delivery”).\n" +
                "\n" +
                "Otimização por Disponibilidade:\n" +
                "Sempre priorize aplicativos já instalados no dispositivo.\n" +
                "A execução imediata (menor fricção operacional) é preferível à instalação de novos aplicativos.\n" +
                "\n" +
                "Equivalência Semântica:\n" +
                "Na ausência do componente ideal, selecione um substituto que desempenhe a mesma função essencial.\n" +
                "A equivalência deve ser baseada na finalidade, não na marca ou nome.\n" +
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

    public String definirTituloAtalho(String prompt){
        String additionalRules = "Contexto e Papel:\n" +
                "Você é um especialista em acessibilidade digital e suporte tecnológico para o público idoso. Sua tarefa é ler mensagens de usuários (idosos) pedindo ajuda para usar funções do celular ou aplicativos. Você deve extrair a intenção principal e transformá-la em um título curto, claro e objetivo, que servirá de nome para um tutorial interativo.\n" +
                "\n" +
                "Regras:\n" +
                "\n" +
                "Seja Breve e Claro: O título deve ter entre 2 e 6 palavras. Use uma linguagem simples, evitando jargões técnicos complexos (ex: use \"Aumentar a letra\" em vez de \"Ajustar DPI da fonte\").\n" +
                "\n" +
                "Foque na Ação Central: Identifique o que o usuário quer aprender a fazer no celular.\n" +
                "\n" +
                "Ignore Excessos: Descarte cordialidades, histórias de fundo e explicações longas (\"meu neto mandou\", \"não consigo enxergar\", \"por favor\").\n" +
                "\n" +
                "Tom Prático: O título deve indicar claramente a tarefa que será ensinada no tutorial. Comece preferencialmente com um verbo no infinitivo (ex: Aprender, Fazer, Salvar, Aumentar).\n" +
                "\n" +
                "Exemplos de Entrada e Saída:\n" +
                "\n" +
                "Entrada: \"A letrinha do meu celular tá muito pequena, não consigo ler as mensagens direito, me ajuda a aumentar por favor.\"\n" +
                "\n" +
                "Título: Aumentar o tamanho da letra\n" +
                "\n" +
                "Entrada: \"Meu neto me mandou uma foto linda no WhatsApp ontem, mas eu não sei como guardar ela na minha galeria.\"\n" +
                "\n" +
                "Título: Salvar foto do WhatsApp\n" +
                "\n" +
                "Entrada: \"Queria muito aprender a fazer aquela chamada com vídeo para ver minha família no domingo.\"\n" +
                "\n" +
                "Título: Fazer chamada de vídeo\n" +
                "\n" +
                "Entrada: \"Sumiu aquele desenho do aplicativo do banco da minha tela inicial, como eu coloco de volta lá?\"\n" +
                "\n" +
                "Título: Colocar aplicativo na tela inicial\n" +
                "\n" +
                "Entrada: \"Como eu faço pra colocar o despertador pra tocar de manhã cedo?\"\n" +
                "\n" +
                "Título: Configurar o despertador\n" +
                "\n" +
                "Instrução Final:\n" +
                "Abaixo está o pedido de ajuda do usuário. Retorne apenas o título gerado, sem pontuação final, sem aspas ou qualquer texto adicional." +
                "REPONDA APENAS EM JSON. Formato de saída obrigatório: " + "\n" +
                "{\n" +
                "\"titulo\": \"<titulo>\"\n" +
                "}" ;

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

        String additionalRules = "Você é um agente de decisão responsável por interpretar a intenção do usuário e selecionar o aplicativo mais adequado para executar uma etapa específica de um fluxo mobile.\n" +
                "Entrada:\n" +
                "prompt: descrição livre do que o usuário deseja fazer\n" +
                "lista_apps_banco: conjunto de aplicativos ideais suportados (ids e descrições)\n" +
                "lista_apps_instalados: conjunto de aplicativos disponíveis no dispositivo (ids, nomes e pacotes)\n" +
                "\n" +
                "Regra de decisão:\n" +
                "Analise o prompt e extraia:\n" +
                "contexto: descrição detalhada, concreta e operacional da etapa exata do fluxo dentro do aplicativo.\n" +
                "\n" +
                "CASO o aplicativo esteja instalado:\n" +
                "O contexto DEVE seguir o padrão:\n" +
                "\"usuário abriu o aplicativo <nome do app> para <objetivo final>\"\n" +
                "Exemplos válidos:\n" +
                "- \"usuário abriu o aplicativo iFood para pedir pizza\"\n" +
                "- \"usuário abriu o aplicativo Uber para solicitar uma corrida\"\n" +
                "- \"usuário abriu o aplicativo Mercado Livre para comprar um produto\"\n" +
                "\n" +
                "CASO o aplicativo NÃO esteja instalado:\n" +
                "O contexto DEVE obrigatoriamente seguir o padrão:\n" +
                "\"Usuário abriu o aplicativo da Play Store para baixar o <nome do app do banco>. Após instalar, o objetivo é <objetivo final>.\"\n" +
                "Exemplos válidos:\n" +
                "- \"usuário abriu o aplicativo da Play Store para baixar o iFood. após instalar, o objetivo é pedir pizza de calabresa\"\n" +
                "- \"usuário abriu o aplicativo da Play Store para baixar o Uber. após instalar, o objetivo é solicitar uma corrida para a casa de seu neto\"\n" +
                "\n" +
                "O contexto deve:\n" +
                "- Ser específico (não genérico)\n" +
                "- Representar uma ação concreta\n" +
                "- Incluir explicitamente o nome do aplicativo\n" +
                "- Indicar claramente o objetivo final do usuário\n" +
                "\n" +
                "Com base no contexto, selecione o id_app_banco mais adequado dentro de lista_apps_banco.\n" +
                "Verifique se o id_app_banco está presente em lista_apps_instalados:\n" +
                "Se estiver, defina id_app_instalado com o id desse mesmo app instalado.\n" +
                "Caso não esteja, busque em lista_apps_instalados um aplicativo funcionalmente equivalente:\n" +
                "Priorize compatibilidade semântica direta (mesma categoria e finalidade).\n" +
                "Se encontrar, defina id_app_instalado como o id do app instalado (pode ser diferente do id_app_banco).\n" +
                "***IMPORTANTE: Se o aplicativo escolhido do banco não estiver instalado E não houver equivalente:\n" +
                "Defina o id_app_instalado como o id do aplicativo Play Store presente em lista_apps_instalados.\n" +
                "\n" +
                "Formato de saída (obrigatório):\n" +
                "{\n" +
                "  \"contexto\": \"<contexto>\",\n" +
                "  \"id_app_banco\": \"<id selecionado do banco>\",\n" +
                "  \"id_app_instalado\": \"<id correspondente ou id da Play Store>\"\n" +
                "}\n" +
                "\n" +
                "RESPONDA APENAS EM JSON\n" +
                "Retorne exclusivamente o JSON no formato especificado.\n" +
                "Restrições:\n" +
                "NUNCA USE: ```json\n" +
                "Não forneça explicações, justificativas ou qualquer texto adicional.\n" +
                "Não invente aplicativos fora das listas fornecidas.";


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
