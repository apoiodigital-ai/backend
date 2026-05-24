package br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner;

import br.com.tucunare.apoiodigital.agent.AgentRule;
import br.com.tucunare.apoiodigital.agent.RuleBuilder;
import org.springframework.stereotype.Component;

@Component
public class ScreenContextDefinerRule implements AgentRule {
    @Override
    public String getRule() {
        String filepath = "src/main/resources/rules/tutorial/screen-context-definer-rule.txt";
        RuleBuilder ruleBuilder = new RuleBuilder();
        return ruleBuilder.getRules(filepath);
    }
}
