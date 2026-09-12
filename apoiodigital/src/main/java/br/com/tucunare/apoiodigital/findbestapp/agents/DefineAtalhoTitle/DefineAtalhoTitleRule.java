package br.com.tucunare.apoiodigital.findbestapp.agents.DefineAtalhoTitle;

import br.com.tucunare.apoiodigital.agent.AgentRule;
import br.com.tucunare.apoiodigital.agent.RuleBuilder;
import org.springframework.stereotype.Component;

@Component
public class DefineAtalhoTitleRule implements AgentRule {
    @Override
    public String getRule() {
        String filepath = "src/main/resources/rules/findbestapp/define-atalho-title-rule.txt";
        RuleBuilder ruleBuilder = new RuleBuilder();
        return ruleBuilder.getRules(filepath);
    }
}
