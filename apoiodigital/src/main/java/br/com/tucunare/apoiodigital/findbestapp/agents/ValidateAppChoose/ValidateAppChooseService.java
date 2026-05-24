package br.com.tucunare.apoiodigital.findbestapp.agents.ValidateAppChoose;

import br.com.tucunare.apoiodigital.agent.PromptBuilder;
import br.com.tucunare.apoiodigital.agent.ResponseBuilder;
import br.com.tucunare.apoiodigital.agent.providers.LLMProvider;
import br.com.tucunare.apoiodigital.agent.types.TaskAgent;
import org.springframework.stereotype.Service;

@Service
public class ValidateAppChooseService implements TaskAgent {

    private final PromptBuilder promptBuilder;
    private final ResponseBuilder responseBuilder;
    private final ValidateAppChooseRule validateAppChooseRule;
    private final LLMProvider llmProvider;

    public ValidateAppChooseService(PromptBuilder promptBuilder, ResponseBuilder responseBuilder, ValidateAppChooseRule validateAppChooseRule, LLMProvider llmProvider) {
        this.promptBuilder = promptBuilder;
        this.responseBuilder = responseBuilder;
        this.validateAppChooseRule = validateAppChooseRule;
        this.llmProvider = llmProvider;
    }

    @Override
    public ValidateAppChooseDTO executeTask(Object request) {
        String rule = validateAppChooseRule.getRule();
        String prompt = promptBuilder.build(request);
        String responseRaw = llmProvider.generateText(rule, prompt, 0.1);
        return responseBuilder.build(responseRaw, ValidateAppChooseDTO.class);
    }
}
