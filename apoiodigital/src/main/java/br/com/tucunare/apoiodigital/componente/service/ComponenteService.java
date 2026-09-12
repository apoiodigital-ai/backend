package br.com.tucunare.apoiodigital.componente.service;

import br.com.tucunare.apoiodigital.agent.AndroidComponentDTO;
import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.componente.data.Componente;
import br.com.tucunare.apoiodigital.componente.exception.ComponenteNaoEncontradoException;
import br.com.tucunare.apoiodigital.componente.repository.ComponenteRepository;
import br.com.tucunare.apoiodigital.resposta.data.Resposta;
import br.com.tucunare.apoiodigital.resposta.exception.RespostaNaoEncontradaException;
import br.com.tucunare.apoiodigital.resposta.repository.RespostaRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Computes and checks the assinatura ("signature") that lets the system reuse a cached
 * instruction instead of calling the model again every time the same screen is captured. The
 * previous {@code comparar} implementation never actually looked at either of its string
 * arguments and always returned true once a Resposta id was found — this replaces that dead
 * logic with a real hash comparison against {@link Componente#getAssinatura()}.
 */
@Service
public class ComponenteService {

    private final ComponenteRepository componenteRepository;
    private final RespostaRepository respostaRepository;

    public ComponenteService(
            ComponenteRepository componenteRepository,
            RespostaRepository respostaRepository
    ) {
        this.componenteRepository = componenteRepository;
        this.respostaRepository = respostaRepository;
    }

    /**
     * Hashes the element hierarchy (viewID + className + additionalInfo of every captured
     * element, sorted for determinism) into a single hex digest. Two calls with the same set
     * of elements — regardless of the order the SDK happened to enumerate them in — always
     * produce the same assinatura.
     */
    public String gerarAssinatura(List<AndroidComponentDTO> elementos) {
        String canonical = elementos.stream()
                .sorted(Comparator.comparing(
                        AndroidComponentDTO::viewID,
                        Comparator.nullsLast(Integer::compareTo)
                ))
                .map(e -> e.viewID() + "|" + nullToEmpty(e.className()) + "|" + nullToEmpty(e.additionalInfo()))
                .collect(Collectors.joining(";"));

        return sha256Hex(canonical);
    }

    public Componente salvar(Resposta resposta, List<AndroidComponentDTO> elementos) {
        Componente componente = new Componente(gerarAssinatura(elementos), resposta);
        return componenteRepository.save(componente);
    }

    /**
     * @return true when {@code elementosAtuais} hashes to the same assinatura stored for the
     * last Componente saved against {@code idResposta} — i.e. the screen hasn't meaningfully
     * changed and the cached instruction on that Resposta can still be trusted.
     */
    public boolean comparar(UUID idResposta, List<AndroidComponentDTO> elementosAtuais, Cliente cliente) {
        Resposta resposta = respostaRepository.findByIdAndPedido_Usuario_Cliente_Id(idResposta, cliente.getId())
                .orElseThrow(RespostaNaoEncontradaException::new);

        Componente componente = componenteRepository.findFirstByRespostaIdOrderByIdDesc(resposta.getId())
                .orElseThrow(ComponenteNaoEncontradoException::new);

        String assinaturaAtual = gerarAssinatura(elementosAtuais);
        return assinaturaAtual.equals(componente.getAssinatura());
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed to be available on every standard JVM; this is unreachable.
            throw new IllegalStateException(e);
        }
    }
}
