package br.com.tucunare.apoiodigital.tutorial.agents.QuestionWriter;

import br.com.tucunare.apoiodigital.agent.PromptBuilder;
import br.com.tucunare.apoiodigital.agent.ResponseBuilder;
import br.com.tucunare.apoiodigital.agent.providers.LLMProvider;
import br.com.tucunare.apoiodigital.agent.types.TaskAgent;
import org.springframework.stereotype.Service;

@Service
public class QuestionWriterService implements TaskAgent {

    private final PromptBuilder promptBuilder;
    private final ResponseBuilder responseBuilder;
    private final QuestionWriterRule questionWriterRule;
    private final LLMProvider llmProvider;

    public QuestionWriterService(PromptBuilder promptBuilder, ResponseBuilder responseBuilder, QuestionWriterRule questionWriterRule, LLMProvider llmProvider) {
        this.promptBuilder = promptBuilder;
        this.responseBuilder = responseBuilder;
        this.questionWriterRule = questionWriterRule;
        this.llmProvider = llmProvider;
    }

    @Override
    public QuestionWriterResponseDTO executeTask(Object request) {
        String rule = questionWriterRule.getRule();
        String prompt = promptBuilder.build(request);
        String responseRaw = llmProvider.generateText(rule, prompt, 0.1);
        return responseBuilder.build(responseRaw, QuestionWriterResponseDTO.class);
    }
}
