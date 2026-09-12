package br.com.tucunare.apoiodigital.findbestapp.agents.DefineAtalhoTitle;

import br.com.tucunare.apoiodigital.agent.PromptBuilder;
import br.com.tucunare.apoiodigital.agent.ResponseBuilder;
import br.com.tucunare.apoiodigital.agent.providers.LLMProvider;
import br.com.tucunare.apoiodigital.agent.types.TaskAgent;
import org.springframework.stereotype.Service;

@Service
public class DefineAtalhoTitleService implements TaskAgent {
    private final PromptBuilder promptBuilder;
    private final ResponseBuilder responseBuilder;
    private final DefineAtalhoTitleRule defineAtalhoTitleRule;
    private final LLMProvider llmProvider;

    public DefineAtalhoTitleService(PromptBuilder promptBuilder, ResponseBuilder responseBuilder, DefineAtalhoTitleRule defineAtalhoTitleRule, LLMProvider llmProvider) {
        this.promptBuilder = promptBuilder;
        this.responseBuilder = responseBuilder;
        this.defineAtalhoTitleRule = defineAtalhoTitleRule;
        this.llmProvider = llmProvider;
    }

    @Override
    public DefineAtalhoTitleResponseDTO executeTask(Object request) {
        String rule = defineAtalhoTitleRule.getRule();
        String prompt = promptBuilder.build(request);
        String responseRaw = llmProvider.generateText(rule, prompt, 0.1);
        return responseBuilder.build(responseRaw, DefineAtalhoTitleResponseDTO.class);
    }
}
