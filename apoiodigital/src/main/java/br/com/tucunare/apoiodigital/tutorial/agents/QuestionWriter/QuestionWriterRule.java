package br.com.tucunare.apoiodigital.tutorial.agents.QuestionWriter;

import br.com.tucunare.apoiodigital.agent.AgentRule;
import br.com.tucunare.apoiodigital.agent.RuleBuilder;
import org.springframework.stereotype.Component;

@Component
public class QuestionWriterRule implements AgentRule {
    @Override
    public String getRule() {
        String filepath = "rules/tutorial/question-writer-rule.txt";
        RuleBuilder ruleBuilder = new RuleBuilder();
        return ruleBuilder.getRules(filepath);
    }
}
