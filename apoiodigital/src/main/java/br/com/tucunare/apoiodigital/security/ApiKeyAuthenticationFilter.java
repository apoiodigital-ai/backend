package br.com.tucunare.apoiodigital.security;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.cliente.repository.ClienteRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Resolves the calling partner (tenant) for every SDK-facing request.
 *
 * <p>Every request must carry {@code x-api-key: <accessKey>}. When it maps to a known
 * {@link Cliente} we put that Cliente into the Spring Security context as the authenticated
 * principal (see {@link TenantContext}) and let the request through; otherwise we leave the
 * context empty and let Spring Security's normal authorization step reject the request with
 * 401 (handled with a clean JSON body by {@link ApiKeyAuthenticationEntryPoint}) — this filter
 * itself never has to know which paths are public, that's {@link SecurityConfig}'s job.</p>
 */
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    public static final String API_KEY_HEADER = "x-api-key";

    private final ClienteRepository clienteRepository;

    public ApiKeyAuthenticationFilter(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String accessKey = request.getHeader(API_KEY_HEADER);

        if (accessKey != null && !accessKey.isBlank()) {
            Optional<Cliente> cliente = clienteRepository.findByAccessKey(accessKey);

            if (cliente.isPresent()) {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        cliente.get(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_PARTNER"))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
