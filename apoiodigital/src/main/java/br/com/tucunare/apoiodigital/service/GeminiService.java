package br.com.tucunare.apoiodigital.service;

import br.com.tucunare.apoiodigital.dto.*;
import br.com.tucunare.apoiodigital.enums.IAAgent2ModoEnum;
import br.com.tucunare.apoiodigital.model.AppSuportado;
import br.com.tucunare.apoiodigital.repository.AppSuportadoRepository;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.Objects;

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
            System.out.println("RESPONSE TEXT: " + response.text());
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
//            System.out.println(rule);
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
    //agente 0
    //o A0 valida o prompt do usuario impedindo jailbreaks e requests nocivos
    public boolean agent0(String prompt){
        ObjectMapper objectMapper = new ObjectMapper();
        String rules = getRules("src/main/resources/rules/agent0-rule.txt");
        GenerateContentConfig config = generateConfig(rules, 0.1f);
        try{
            ObjectNode json = objectMapper.createObjectNode();
            json.put("prompt", prompt);

            String input = json.toString();

            String r = analyzeText(input, config);

            JsonNode jsonNode = objectMapper.readTree(r);

            boolean n = jsonNode.get("aprovado").asBoolean();
            if(!n){return false;}
            else{return true;}
        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Simplifica o prompt vindo do usuário e retorna prompt_limpo.
    // o A1 reduz o prompt do usuário em prompts mais objetivos e menos complexos para os próximos agentes
    public String agent1(String prompt){ // A1
        ObjectMapper objectMapper = new ObjectMapper();

        String rules = getRules("src/main/resources/rules/simplifyPrompt-rule.txt");

        GenerateContentConfig config = generateConfig(rules, 0.1f);

        try {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("prompt", prompt);

            String input = json.toString();

            String response = analyzeText(input, config);

            JsonNode jsonNode = objectMapper.readTree(response);

            String promptLimpo = jsonNode.get("prompt_limpo").asText();

            System.out.println("\nPrompt Limpo: " + promptLimpo);

            return promptLimpo;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    //Agente 2
    //Agente que acha o id do app instalado que condiz com o prompt (limpo)
    public Long agent2(IAAgent2RequestDTO dto){
        ObjectMapper objectMapper = new ObjectMapper();

        String rules = getRules("src/main/resources/rules/agent2-rule.txt");

        GenerateContentConfig config = generateConfig(rules, 0.1f);

        try{
            String input = objectMapper.writeValueAsString(dto);

            String response = analyzeText(input, config);

            JsonNode jsonNode = objectMapper.readTree(response);

            Long p = jsonNode.get("id_app_instalado").asLong();

            return p;
//
//            if(p.canConvertToLong()){
//                return p.asLong();
//            }else{
//                return -1L;
//            }

        }catch (JsonProcessingException e) {

            throw new RuntimeException(e);

        }

    }


    //Agente 3
    //A3
    public Long agent3(IAAgent3RequestDTO dto){
        ObjectMapper objectMapper = new ObjectMapper();

        String rules = getRules("src/main/resources/rules/agent3-rule.txt");

        GenerateContentConfig config = generateConfig(rules, 0.1f);

        try{
            String input = objectMapper.writeValueAsString(dto);

            String response = analyzeText(input, config);

            JsonNode jsonNode = objectMapper.readTree(response);

            JsonNode id_app_banco = jsonNode.get("id_app_banco");
            //select x.package id_app_instalado = select y.package id_app_banco

            if(id_app_banco.canConvertToLong()){
                return jsonNode.get("id_app_banco").asLong();
            }else{
                return -1L;
            }

        }catch (JsonProcessingException e) {

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
        //lista app banco
        List<AppSuportado> lista_app_suportado = appSuportadoRepository.findAll();

        ObjectMapper objectMapper = new ObjectMapper();

//        if(!agent0(dto.prompt())){return null;} // valida prompt

        String prompt = agent1(dto.prompt()); // limpa prompt

        IAAgent2RequestDTO iaAgent2RequestDTO = new IAAgent2RequestDTO(
                prompt, dto.lista_apps_instalados(), IAAgent2ModoEnum.inicial);

        Long id_app_instalado = agent2(iaAgent2RequestDTO); // busca app instalado condizente

        boolean f = false;
        Long id_app_banco = -1L;

        for (AppRequestDTO app: dto.lista_apps_instalados()){
            System.out.println(app.nome());
        }

        for (AppSuportado appSuportado : lista_app_suportado) {
            // verifica se o app_instalado escolhido existe no banco de dados
            String x = dto.lista_apps_instalados().get((int) (id_app_instalado - 1)).pacote();

            String y = appSuportado.getPacote();

            if (Objects.equals(x, y)) {

                System.out.println("APP NO BANCO DE DADOS!!!!: " + x + id_app_instalado);
                id_app_banco = appSuportado.getId();
                f = true; break; // aqui diz que existe
            }
        }

        if(!f){ // se não existe, irá chamar o agente3 para escolher app semelhante

            IAAgent3RequestDTO iaAgent3RequestDTO = new IAAgent3RequestDTO(
                    dto.lista_apps_instalados().get((int) (id_app_instalado - 1)).pacote(),
                    lista_app_suportado);

            id_app_banco = agent3(iaAgent3RequestDTO);

            boolean achou = false;

            Long id_play_store = -1L;

            for (AppRequestDTO appRequestDTO : dto.lista_apps_instalados()) {

                if(Objects.equals(appRequestDTO.pacote(), "com.android.vending")){
                    // aqui vamos definir o id do app_instalado referente à playStore
                    id_play_store = appRequestDTO.id();
                }

                if(Objects.equals(lista_app_suportado.get(
                        (int) (id_app_banco-1)).getPacote(),
                        appRequestDTO.pacote())){
                    achou = true;
                    id_app_instalado = appRequestDTO.id();
                }

            }
            if(!achou){
                id_app_instalado = id_play_store;
            }
        }

        // agora vamos definir o contexto
        String contexto = definirContexto(new GenerateContextAppDTO(
                prompt,
                lista_app_suportado.get((int) (id_app_banco-1)).getNome(),
                dto.lista_apps_instalados().get((int) (id_app_instalado-1)).nome()
        ));


        return new FindBestAppResponseDTO(contexto, id_app_banco, id_app_instalado);
    }


}
