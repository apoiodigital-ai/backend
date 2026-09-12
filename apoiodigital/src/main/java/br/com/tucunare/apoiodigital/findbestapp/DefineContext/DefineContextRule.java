package br.com.tucunare.apoiodigital.findbestapp.DefineContext;

import br.com.tucunare.apoiodigital.agent.AgentRule;
import br.com.tucunare.apoiodigital.agent.RuleBuilder;
import org.springframework.stereotype.Component;

@Component
public class DefineContextRule implements AgentRule {
    @Override
    public String getRule() {
        String filepath = "src/main/resources/rules/findbestapp/define-context-rule.txt";
        RuleBuilder ruleBuilder = new RuleBuilder();
        return ruleBuilder.getRules(filepath);
    }
}
