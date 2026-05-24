package br.com.tucunare.apoiodigital.tutorial.agents.PendingValidator;

import br.com.tucunare.apoiodigital.agent.AgentRule;
import br.com.tucunare.apoiodigital.agent.RuleBuilder;
import org.springframework.stereotype.Component;

@Component
public class PendingValidatorRule implements AgentRule {
    @Override
    public String getRule() {
        String filepath = "src/main/resources/rules/tutorial/pending-validator-rule.txt";
        RuleBuilder ruleBuilder = new RuleBuilder();
        return ruleBuilder.getRules(filepath);
    }
}
