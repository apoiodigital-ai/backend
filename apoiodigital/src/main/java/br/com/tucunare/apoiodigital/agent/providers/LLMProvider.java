package br.com.tucunare.apoiodigital.agent.providers;

public interface LLMProvider {

    String generateText(String rule, String prompt, double temp);

}
