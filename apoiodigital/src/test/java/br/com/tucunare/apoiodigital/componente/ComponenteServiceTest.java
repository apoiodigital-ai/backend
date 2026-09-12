package br.com.tucunare.apoiodigital.componente;

import br.com.tucunare.apoiodigital.agent.AndroidComponentDTO;
import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.componente.data.Componente;
import br.com.tucunare.apoiodigital.componente.exception.ComponenteNaoEncontradoException;
import br.com.tucunare.apoiodigital.componente.repository.ComponenteRepository;
import br.com.tucunare.apoiodigital.componente.service.ComponenteService;
import br.com.tucunare.apoiodigital.pedido.data.Pedido;
import br.com.tucunare.apoiodigital.resposta.data.Resposta;
import br.com.tucunare.apoiodigital.resposta.exception.RespostaNaoEncontradaException;
import br.com.tucunare.apoiodigital.resposta.repository.RespostaRepository;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Covers the real assinatura-hashing logic that replaced ComponenteService#comparar's previous
 * dead implementation (it used to ignore both of its string arguments and always return true
 * once a Resposta id was found).
 */
class ComponenteServiceTest {

    @Mock
    private ComponenteRepository componenteRepository;

    @Mock
    private RespostaRepository respostaRepository;

    private ComponenteService componenteService;

    private Cliente cliente;
    private Resposta resposta;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        componenteService = new ComponenteService(componenteRepository, respostaRepository);

        cliente = new Cliente("Banco Teste", "access-key-teste", "banco");
        cliente.setId(UUID.randomUUID());

        Usuario usuario = new Usuario("usuario-teste", cliente);
        usuario.setId(UUID.randomUUID());

        Pedido pedido = new Pedido(usuario, "prompt de teste");
        pedido.setId(UUID.randomUUID());

        resposta = new Resposta(pedido, "mensagem", "raciocinio");
        resposta.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("gerarAssinatura is deterministic regardless of element order")
    void gerarAssinaturaIgnoraOrdem() {
        List<AndroidComponentDTO> ordemA = List.of(
                new AndroidComponentDTO(1, "Button", "Buscar"),
                new AndroidComponentDTO(2, "TextView", "Bem-vindo")
        );
        List<AndroidComponentDTO> ordemB = List.of(
                new AndroidComponentDTO(2, "TextView", "Bem-vindo"),
                new AndroidComponentDTO(1, "Button", "Buscar")
        );

        assertEquals(componenteService.gerarAssinatura(ordemA), componenteService.gerarAssinatura(ordemB));
    }

    @Test
    @DisplayName("gerarAssinatura changes when an element's content changes")
    void gerarAssinaturaMudaComConteudoDiferente() {
        List<AndroidComponentDTO> original = List.of(new AndroidComponentDTO(1, "Button", "Buscar"));
        List<AndroidComponentDTO> alterado = List.of(new AndroidComponentDTO(1, "Button", "Cancelar"));

        assertNotEquals(componenteService.gerarAssinatura(original), componenteService.gerarAssinatura(alterado));
    }

    @Test
    @DisplayName("comparar returns true when the screen hash matches the stored assinatura")
    void compararTelaIgual() {
        List<AndroidComponentDTO> elementos = List.of(new AndroidComponentDTO(1, "Button", "Buscar"));
        String assinatura = componenteService.gerarAssinatura(elementos);
        Componente componente = new Componente(assinatura, resposta);

        when(respostaRepository.findByIdAndPedido_Usuario_Cliente_Id(resposta.getId(), cliente.getId()))
                .thenReturn(Optional.of(resposta));
        when(componenteRepository.findFirstByRespostaIdOrderByIdDesc(resposta.getId()))
                .thenReturn(Optional.of(componente));

        assertTrue(componenteService.comparar(resposta.getId(), elementos, cliente));
    }

    @Test
    @DisplayName("comparar returns false when the screen changed since the assinatura was stored")
    void compararTelaDiferente() {
        List<AndroidComponentDTO> elementosOriginais = List.of(new AndroidComponentDTO(1, "Button", "Buscar"));
        List<AndroidComponentDTO> elementosNovos = List.of(new AndroidComponentDTO(1, "Button", "Cancelar"));
        String assinatura = componenteService.gerarAssinatura(elementosOriginais);
        Componente componente = new Componente(assinatura, resposta);

        when(respostaRepository.findByIdAndPedido_Usuario_Cliente_Id(resposta.getId(), cliente.getId()))
                .thenReturn(Optional.of(resposta));
        when(componenteRepository.findFirstByRespostaIdOrderByIdDesc(resposta.getId()))
                .thenReturn(Optional.of(componente));

        assertFalse(componenteService.comparar(resposta.getId(), elementosNovos, cliente));
    }

    @Test
    @DisplayName("comparar fails closed with 404 when the Resposta doesn't belong to the caller's tenant")
    void compararTenantErrado() {
        when(respostaRepository.findByIdAndPedido_Usuario_Cliente_Id(any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(RespostaNaoEncontradaException.class,
                () -> componenteService.comparar(UUID.randomUUID(), List.of(), cliente));
    }

    @Test
    @DisplayName("comparar fails when there is no Componente saved yet for the Resposta")
    void compararSemComponenteSalvo() {
        when(respostaRepository.findByIdAndPedido_Usuario_Cliente_Id(resposta.getId(), cliente.getId()))
                .thenReturn(Optional.of(resposta));
        when(componenteRepository.findFirstByRespostaIdOrderByIdDesc(resposta.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ComponenteNaoEncontradoException.class,
                () -> componenteService.comparar(resposta.getId(), List.of(), cliente));
    }
}
