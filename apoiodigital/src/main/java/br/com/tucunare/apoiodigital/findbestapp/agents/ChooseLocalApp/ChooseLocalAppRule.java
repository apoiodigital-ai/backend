package br.com.tucunare.apoiodigital.findbestapp.agents.ChooseLocalApp;

import br.com.tucunare.apoiodigital.agent.AgentRule;
import br.com.tucunare.apoiodigital.agent.RuleBuilder;
import org.springframework.stereotype.Component;

@Component
public class ChooseLocalAppRule implements AgentRule {
    @Override
    public String getRule() {
        String filepath = "src/main/resources/rules/agent2-rule.txt";
        RuleBuilder ruleBuilder = new RuleBuilder();
        return ruleBuilder.getRules(filepath);
    }
}
