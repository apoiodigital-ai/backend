package br.com.tucunare.apoiodigital.findbestapp.agents.SimplifyPrompt;

import br.com.tucunare.apoiodigital.agent.AgentRule;
import br.com.tucunare.apoiodigital.agent.RuleBuilder;
import org.springframework.stereotype.Component;

@Component
public class SimplifyPromptRule implements AgentRule {

    @Override
    public String getRule() {
        String filepath = "src/main/resources/rules/simplifyPrompt-rule.txt";
        RuleBuilder ruleBuilder = new RuleBuilder();
        return ruleBuilder.getRules(filepath);
    }
}
