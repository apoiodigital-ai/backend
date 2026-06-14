package br.com.tucunare.apoiodigital.tutorial.InformationNeeds;

import br.com.tucunare.apoiodigital.tutorial.agents.PendingValidator.PendingValidatorRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.PendingValidator.PendingValidatorResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.PendingValidator.PendingValidatorService;
import br.com.tucunare.apoiodigital.tutorial.agents.QuestionWriter.QuestionWriterRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.QuestionWriter.QuestionWriterResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.QuestionWriter.QuestionWriterService;
import org.springframework.stereotype.Service;

@Service
public class InformationNeedsService {

    private final PendingValidatorService pendingValidatorService;
    private final QuestionWriterService questionWriterService;

    public InformationNeedsService(PendingValidatorService pendingValidatorService, QuestionWriterService questionWriterService) {
        this.pendingValidatorService = pendingValidatorService;
        this.questionWriterService = questionWriterService;
    }

    public ChecksInformationNeedsResponseDTO checksInformationNeeds(
            ChecksInformationNeedsRequestDTO requestDTO){
        PendingValidatorResponseDTO agente0Response = pendingValidatorService.executeTask(
                new
                        PendingValidatorRequestDTO(requestDTO.prompt(),
                        requestDTO.contexto(), requestDTO.elementos()));


        System.out.println("INTERROMPER: " + agente0Response.interromper());
        if(agente0Response.interromper()){
            QuestionWriterResponseDTO agente1response = questionWriterService.executeTask(
                    new QuestionWriterRequestDTO(
                            agente0Response.tipo_pendencia(),
                            agente0Response.descricao_duvida(),
                            requestDTO.elementos()));
            return new ChecksInformationNeedsResponseDTO(
                    requestDTO.contexto(),
                    agente1response.pergunta(),
                    agente1response.opcoes(),
                    agente0Response.tipo_pendencia(),
                    agente0Response.descricao_duvida()
            );
        }
        return null;
    }

}
