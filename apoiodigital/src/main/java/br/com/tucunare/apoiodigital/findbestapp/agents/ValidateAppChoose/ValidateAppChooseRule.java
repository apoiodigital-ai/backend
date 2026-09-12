package br.com.tucunare.apoiodigital.findbestapp.agents.ValidateAppChoose;

import br.com.tucunare.apoiodigital.agent.AgentRule;
import br.com.tucunare.apoiodigital.agent.RuleBuilder;
import org.springframework.stereotype.Component;

@Component
public class ValidateAppChooseRule implements AgentRule {
    @Override
    public String getRule() {
        String filepath = "src/main/resources/rules/findbestapp/validate-app-choose.txt";
        RuleBuilder ruleBuilder = new RuleBuilder();
        return ruleBuilder.getRules(filepath);
    }
}
