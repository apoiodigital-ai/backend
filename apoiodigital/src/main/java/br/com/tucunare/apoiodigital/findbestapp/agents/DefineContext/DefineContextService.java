package br.com.tucunare.apoiodigital.findbestapp.agents.DefineContext;

import br.com.tucunare.apoiodigital.agent.PromptBuilder;
import br.com.tucunare.apoiodigital.agent.ResponseBuilder;
import br.com.tucunare.apoiodigital.agent.providers.LLMProvider;
import br.com.tucunare.apoiodigital.agent.types.TaskAgent;
import org.springframework.stereotype.Service;

@Service
public class DefineContextService implements TaskAgent {
    private final PromptBuilder promptBuilder;
    private final ResponseBuilder responseBuilder;
    private final DefineContextRule defineContextRule;
    private final LLMProvider llmProvider;

    public DefineContextService(PromptBuilder promptBuilder, ResponseBuilder responseBuilder, DefineContextRule defineContextRule, LLMProvider llmProvider) {
        this.promptBuilder = promptBuilder;
        this.responseBuilder = responseBuilder;
        this.defineContextRule = defineContextRule;
        this.llmProvider = llmProvider;
    }

    @Override
    public DefineContextResponseDTO executeTask(Object request) {
        String rule = defineContextRule.getRule();
        String prompt = promptBuilder.build(request);
        String responseRaw = llmProvider.generateText(rule, prompt, 0.1);
        return responseBuilder.build(responseRaw, DefineContextResponseDTO.class);
    }
}
