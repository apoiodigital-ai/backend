package br.com.tucunare.apoiodigital.findbestapp.agents.ChooseLocalApp;

import br.com.tucunare.apoiodigital.agent.PromptBuilder;
import br.com.tucunare.apoiodigital.agent.ResponseBuilder;
import br.com.tucunare.apoiodigital.agent.providers.LLMProvider;
import br.com.tucunare.apoiodigital.agent.types.TaskAgent;
import org.springframework.stereotype.Service;

@Service
public class ChooseLocalAppService implements TaskAgent {

    private final PromptBuilder promptBuilder;
    private final ResponseBuilder responseBuilder;
    private final ChooseLocalAppRule chooseLocalAppRule;
    private final LLMProvider llmProvider;

    public ChooseLocalAppService(PromptBuilder promptBuilder, ResponseBuilder responseBuilder, ChooseLocalAppRule chooseLocalAppRule, LLMProvider llmProvider) {
        this.promptBuilder = promptBuilder;
        this.responseBuilder = responseBuilder;
        this.chooseLocalAppRule = chooseLocalAppRule;
        this.llmProvider = llmProvider;
    }

    @Override
    public ChooseLocalAppResponseDTO executeTask(Object request) {
        String rule = chooseLocalAppRule.getRule();
        String prompt = promptBuilder.build(request);
        String responseRaw = llmProvider.generateText(rule, prompt, 0.1);
        return responseBuilder.build(responseRaw, ChooseLocalAppResponseDTO.class);
    }
}
