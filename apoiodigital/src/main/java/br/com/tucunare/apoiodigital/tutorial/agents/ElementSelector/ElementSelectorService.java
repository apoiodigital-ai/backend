package br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector;

import br.com.tucunare.apoiodigital.agent.PromptBuilder;
import br.com.tucunare.apoiodigital.agent.ResponseBuilder;
import br.com.tucunare.apoiodigital.agent.providers.LLMProvider;
import br.com.tucunare.apoiodigital.agent.types.TaskAgent;
import org.springframework.stereotype.Service;

@Service
public class ElementSelectorService implements TaskAgent {

    private final PromptBuilder promptBuilder;
    private final ResponseBuilder responseBuilder;
    private final ElementSelectorRule elementSelectorRule;
    private final LLMProvider llmProvider;

    public ElementSelectorService(PromptBuilder promptBuilder, ResponseBuilder responseBuilder, ElementSelectorRule elementSelectorRule, LLMProvider llmProvider) {
        this.promptBuilder = promptBuilder;
        this.responseBuilder = responseBuilder;
        this.elementSelectorRule = elementSelectorRule;
        this.llmProvider = llmProvider;
    }

    @Override
    public ElementSelectorResponseDTO executeTask(Object request) {
        String rule = elementSelectorRule.getRule();
        String prompt = promptBuilder.build(request);
        String responseRaw = llmProvider.generateText(rule, prompt, 0.1);
        return responseBuilder.build(responseRaw, ElementSelectorResponseDTO.class);
    }
}
