package br.com.tucunare.apoiodigital.security;

import br.com.tucunare.apoiodigital.handler.ExceptionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Replaces Spring Security's default 401 page with the same clean JSON error shape the rest of
 * the API uses, for requests that are missing a valid x-api-key.
 */
@Component
public class ApiKeyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ExceptionDTO body = new ExceptionDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "x-api-key ausente ou inválido"
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
