package br.com.tucunare.apoiodigital.security;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.cliente.exception.ClienteNaoAutenticadoException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Reads the {@link Cliente} (tenant) resolved for the current request out of the Spring
 * Security context. {@link ApiKeyAuthenticationFilter} is the only place that ever puts a
 * Cliente there — every other piece of application code must go through this class instead of
 * trusting any client-supplied id, which is what fixes the IDOR class of bug this refactor was
 * asked to close: an id_usuario (or any other id) in a request body/query param is only ever
 * meaningful once it has been checked against the tenant returned here.
 */
@Component
public class TenantContext {

    public Cliente getClienteAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Cliente cliente)) {
            throw new ClienteNaoAutenticadoException();
        }
        return cliente;
    }
}
