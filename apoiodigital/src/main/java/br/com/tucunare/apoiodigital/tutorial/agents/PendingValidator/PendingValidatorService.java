package br.com.tucunare.apoiodigital.tutorial.agents.PendingValidator;

import br.com.tucunare.apoiodigital.agent.PromptBuilder;
import br.com.tucunare.apoiodigital.agent.ResponseBuilder;
import br.com.tucunare.apoiodigital.agent.providers.LLMProvider;
import br.com.tucunare.apoiodigital.agent.types.TaskAgent;
import org.springframework.stereotype.Service;

@Service
public class PendingValidatorService implements TaskAgent {

    private final PromptBuilder promptBuilder;
    private final ResponseBuilder responseBuilder;
    private final PendingValidatorRule pendingValidatorRule;
    private final LLMProvider llmProvider;

    public PendingValidatorService(PromptBuilder promptBuilder, ResponseBuilder responseBuilder, PendingValidatorRule pendingValidatorRule, LLMProvider llmProvider) {
        this.promptBuilder = promptBuilder;
        this.responseBuilder = responseBuilder;
        this.pendingValidatorRule = pendingValidatorRule;
        this.llmProvider = llmProvider;
    }

    @Override
    public PendingValidatorResponseDTO executeTask(Object request) {
        String rule = pendingValidatorRule.getRule();
        String prompt = promptBuilder.build(request);
        String responseRaw = llmProvider.generateText(rule, prompt, 0.1);
        return responseBuilder.build(responseRaw, PendingValidatorResponseDTO.class);
    }
}
