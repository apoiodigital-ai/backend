package br.com.tucunare.apoiodigital.findbestapp.agents.SimplifyPrompt;

import br.com.tucunare.apoiodigital.agent.PromptBuilder;
import br.com.tucunare.apoiodigital.agent.ResponseBuilder;
import br.com.tucunare.apoiodigital.agent.providers.LLMProvider;
import br.com.tucunare.apoiodigital.agent.types.TaskAgent;
import org.springframework.stereotype.Service;

@Service
public class SimplifyPromptService implements TaskAgent {

    private final PromptBuilder promptBuilder;
    private final ResponseBuilder responseBuilder;
    private final SimplifyPromptRule simplifyPromptRule;
    private final LLMProvider llmProvider;

    public SimplifyPromptService(PromptBuilder promptBuilder, ResponseBuilder responseBuilder, SimplifyPromptRule simplifyPromptRule, LLMProvider llmProvider) {
        this.promptBuilder = promptBuilder;
        this.responseBuilder = responseBuilder;
        this.simplifyPromptRule = simplifyPromptRule;
        this.llmProvider = llmProvider;
    }

    @Override
    public SimplifyPromptResponseDTO executeTask(Object request) {
        String rule = simplifyPromptRule.getRule();
        String prompt = promptBuilder.build(request);
        String responseRaw = llmProvider.generateText(rule, prompt, 0.1);
        return responseBuilder.build(responseRaw, SimplifyPromptResponseDTO.class);
    }
}
