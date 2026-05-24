package br.com.tucunare.apoiodigital.service.impl;

import br.com.tucunare.apoiodigital.dto.tutorial.*;
import br.com.tucunare.apoiodigital.model.GeminiAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindBestAnswerService {

    @Autowired
    private GeminiAgent geminiAgent;

    IAAgent0TutorialResponseDTO agent0(IAAgent0TutorialRequestDTO request) {
        return geminiAgent.agentStructure(request, "src/main/resources/rules/tutorial/agent0-rule.txt", 0.1, IAAgent0TutorialResponseDTO.class);
    }

    IAAgent1TutorialResponseDTO agent1(IAAgent1TutorialRequestDTO request) {
        return geminiAgent.agentStructure(request, "src/main/resources/rules/tutorial/agent1-rule.txt", 0.1, IAAgent1TutorialResponseDTO.class);
    }

    IAAgentYTutorialResponseDTO agentY(IAAgentYTutorialRequestDTO request) {
        return geminiAgent.agentStructure(request, "src/main/resources/rules/tutorial/agenty-rule.txt", 0.1, IAAgentYTutorialResponseDTO.class);
    }

    IAAgentXTutorialResponseDTO agentX(IAAgentXTutorialRequestDTO request) {
        return geminiAgent.agentStructure(request, "src/main/resources/rules/tutorial/agentx-rule.txt", 0.1, IAAgentXTutorialResponseDTO.class);
    }

    IAAgentZTutorialResponseDTO agentZ(IAAgentZTutorialRequestDTO request) {
        return geminiAgent.agentStructure(request, "src/main/resources/rules/tutorial/agentz-rule.txt", 0.1, IAAgentZTutorialResponseDTO.class);
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
