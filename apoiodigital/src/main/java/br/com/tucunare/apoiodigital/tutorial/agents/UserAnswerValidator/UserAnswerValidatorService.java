package br.com.tucunare.apoiodigital.tutorial.agents.UserAnswerValidator;

import br.com.tucunare.apoiodigital.agent.PromptBuilder;
import br.com.tucunare.apoiodigital.agent.ResponseBuilder;
import br.com.tucunare.apoiodigital.agent.providers.LLMProvider;
import br.com.tucunare.apoiodigital.agent.types.TaskAgent;
import org.springframework.stereotype.Service;

@Service
public class UserAnswerValidatorService implements TaskAgent {

    private final PromptBuilder promptBuilder;
    private final ResponseBuilder responseBuilder;
    private final UserAnswerValidatorRule userAnswerValidatorRule;
    private final LLMProvider llmProvider;

    public UserAnswerValidatorService(PromptBuilder promptBuilder, ResponseBuilder responseBuilder, UserAnswerValidatorRule userAnswerValidatorRule, LLMProvider llmProvider) {
        this.promptBuilder = promptBuilder;
        this.responseBuilder = responseBuilder;
        this.userAnswerValidatorRule = userAnswerValidatorRule;
        this.llmProvider = llmProvider;
    }

    @Override
    public UserAnswerValidatorResponseDTO executeTask(Object request) {
        String rule = userAnswerValidatorRule.getRule();
        String prompt = promptBuilder.build(request);
        String responseRaw = llmProvider.generateText(rule, prompt, 0.1);
        return responseBuilder.build(responseRaw, UserAnswerValidatorResponseDTO.class);
    }
}
