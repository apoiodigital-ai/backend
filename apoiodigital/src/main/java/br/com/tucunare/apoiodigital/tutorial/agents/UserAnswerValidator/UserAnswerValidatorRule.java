package br.com.tucunare.apoiodigital.tutorial.agents.UserAnswerValidator;

import br.com.tucunare.apoiodigital.agent.AgentRule;
import br.com.tucunare.apoiodigital.agent.RuleBuilder;
import org.springframework.stereotype.Component;

@Component
public class UserAnswerValidatorRule implements AgentRule {
    @Override
    public String getRule() {
        String filepath = "rules/tutorial/user-answer-validator-rule.txt";
        RuleBuilder ruleBuilder = new RuleBuilder();
        return ruleBuilder.getRules(filepath);
    }
}
