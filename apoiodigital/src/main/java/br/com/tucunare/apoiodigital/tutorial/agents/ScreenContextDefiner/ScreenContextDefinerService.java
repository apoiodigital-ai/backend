package br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner;

import br.com.tucunare.apoiodigital.agent.PromptBuilder;
import br.com.tucunare.apoiodigital.agent.ResponseBuilder;
import br.com.tucunare.apoiodigital.agent.providers.LLMProvider;
import br.com.tucunare.apoiodigital.agent.types.TaskAgent;
import org.springframework.stereotype.Service;

@Service
public class ScreenContextDefinerService implements TaskAgent {

    private final PromptBuilder promptBuilder;
    private final ResponseBuilder responseBuilder;
    private final ScreenContextDefinerRule screenContextDefinerRule;
    private final LLMProvider llmProvider;

    public ScreenContextDefinerService(PromptBuilder promptBuilder, ResponseBuilder responseBuilder, ScreenContextDefinerRule screenContextDefinerRule, LLMProvider llmProvider) {
        this.promptBuilder = promptBuilder;
        this.responseBuilder = responseBuilder;
        this.screenContextDefinerRule = screenContextDefinerRule;
        this.llmProvider = llmProvider;
    }

    @Override
    public ScreenContextDefinerResponseDTO executeTask(Object request) {
        String rule = screenContextDefinerRule.getRule();
        String prompt = promptBuilder.build(request);
        String responseRaw = llmProvider.generateText(rule, prompt, 0.1);
        return responseBuilder.build(responseRaw, ScreenContextDefinerResponseDTO.class);
    }
}
