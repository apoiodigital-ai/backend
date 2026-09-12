package br.com.tucunare.apoiodigital.agent;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Loads an agent's system rule text from the classpath (src/main/resources/&lt;path&gt;).
 * Classpath lookup — instead of the plain filesystem path this used to read from — is what
 * makes rule loading work once the app runs from a packaged jar rather than straight out of
 * the source tree.
 */
@Component
public class RuleBuilder {

    public String getRules(String classpathRelativePath) {
        try (InputStream inputStream = new ClassPathResource(classpathRelativePath).getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar regra do agente: " + classpathRelativePath, e);
        }
    }

}
