package br.com.tucunare.apoiodigital.tutorial.InformationNeeds;

import br.com.tucunare.apoiodigital.tutorial.agents.UserAnswerValidator.AgentResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.UserAnswerValidator.UserAnswerValidatorRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.UserAnswerValidator.UserAnswerValidatorResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.UserAnswerValidator.UserAnswerValidatorService;
import org.springframework.stereotype.Service;

@Service
public class InformationResponseService {
    private final UserAnswerValidatorService userAnswerValidatorService;

    public InformationResponseService(UserAnswerValidatorService userAnswerValidatorService) {
        this.userAnswerValidatorService = userAnswerValidatorService;
    }

    public UserAnswerValidatorResponseDTO checksQuestionReturns(UserAnswerValidatorRequestDTO request){
        AgentResponseDTO response = userAnswerValidatorService.executeTask(request);

        return new UserAnswerValidatorResponseDTO(
                response.pergunta(),
                response.satisfaz(),
                request.pergunta(),
                request.resposta_escrita()
        );
    }
}
