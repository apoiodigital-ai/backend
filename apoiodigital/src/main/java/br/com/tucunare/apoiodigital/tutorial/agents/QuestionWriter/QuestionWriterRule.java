package br.com.tucunare.apoiodigital.tutorial.agents.QuestionWriter;

import br.com.tucunare.apoiodigital.agent.AgentRule;
import br.com.tucunare.apoiodigital.agent.RuleBuilder;
import org.springframework.stereotype.Component;

@Component
public class QuestionWriterRule implements AgentRule {
    @Override
    public String getRule() {
        String filepath = "src/main/resources/rules/tutorial/agent1-rule.txt";
        RuleBuilder ruleBuilder = new RuleBuilder();
        return ruleBuilder.getRules(filepath);
    }
}
