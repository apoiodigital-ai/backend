package br.com.tucunare.apoiodigital.service;

import br.com.tucunare.apoiodigital.dto.tutorial.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FindBestAnswerService {

    private final ChatClient chatClient;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public FindBestAnswerService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    private String getRules(String filepath) {
        try {
            return Files.readString(Path.of(filepath));
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                            .build()
                    )
                    .call()
                    .content();

            return objectMapper.readValue(response, targetClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    IAAgent0TutorialResponseDTO agent0(IAAgent0TutorialRequestDTO request) {
        return agentStructure(request, "src/main/resources/rules/tutorial/agent0-rule.txt", 0.1, IAAgent0TutorialResponseDTO.class);
    }

    IAAgent1TutorialResponseDTO agent1(IAAgent1TutorialRequestDTO request) {
        return agentStructure(request, "src/main/resources/rules/tutorial/agent1-rule.txt", 0.1, IAAgent1TutorialResponseDTO.class);
    }

    IAAgentYTutorialResponseDTO agentY(IAAgentYTutorialRequestDTO request) {
        return agentStructure(request, "src/main/resources/rules/tutorial/agenty-rule.txt", 0.1, IAAgentYTutorialResponseDTO.class);
    }

    IAAgentXTutorialResponseDTO agentX(IAAgentXTutorialRequestDTO request) {
        return agentStructure(request, "src/main/resources/rules/tutorial/agentx-rule.txt", 0.1, IAAgentXTutorialResponseDTO.class);
    }

    IAAgentZTutorialResponseDTO agentZ(IAAgentZTutorialRequestDTO request) {
        return agentStructure(request, "src/main/resources/rules/tutorial/agentz-rule.txt", 0.1, IAAgentZTutorialResponseDTO.class);
    }
    public ChecksInformationNeedsResponseDTO
    checksInformationNeeds(ChecksInformationNeedsRequestDTO
                                   requestDTO){
        IAAgent0TutorialResponseDTO agente0Response = agent0(
                new
                        IAAgent0TutorialRequestDTO(requestDTO.prompt(),
                        requestDTO.contexto(), requestDTO.elementos()));
        if(agente0Response.interromper()){IAAgent1TutorialResponseDTO agente1response = agent1(
                new IAAgent1TutorialRequestDTO(
                        agente0Response.tipo_pendencia(),
                        agente0Response.descricao_duvida(),
                        requestDTO.elementos())
        );
            return new ChecksInformationNeedsResponseDTO(
                    agente1response.pergunta(),
                    agente1response.opcoes(),
                    agente0Response.tipo_pendencia(),
                    agente0Response.descricao_duvida()
            );
        }
        return null;
    }
    public IAAgentYTutorialResponseDTO
    checksQuestionReturns(IAAgentYTutorialRequestDTO request){
        return agentY(request);
    }
    public FindBestAnswerResponseDTO
    findBestAnswer(IAAgentXTutorialRequestDTO request){
        IAAgentXTutorialResponseDTO agenteXresponse =
                agentX(request);
        IAAgentZTutorialResponseDTO agenteZresponse = agentZ(
                new
                        IAAgentZTutorialRequestDTO(request.contexto(),
                        agenteXresponse.raciocinio(),
                        request.elementos().get(agenteXresponse.viewID())) // pega elemento destacado
        );
        return new FindBestAnswerResponseDTO(
                agenteXresponse.viewID(),
                agenteZresponse.novo_contexto(),agenteZresponse.mensagem_escrita(),
                agenteZresponse.mensagem_voz()
        );
    }
}
