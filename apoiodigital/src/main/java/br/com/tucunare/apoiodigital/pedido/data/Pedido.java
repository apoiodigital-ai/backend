package br.com.tucunare.apoiodigital.pedido.data;

import br.com.tucunare.apoiodigital.agent.TiposPendencia;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * One user request/screen-step handled by the agent pipeline — the target schema's Pedido,
 * repurposed from the old (now removed) Requisicao entity. Under the old standalone-app model a
 * Requisicao also carried an AppSuportado (which of the user's installed apps to route to);
 * that concept has no equivalent under the SDK model, where the partner's own app is always
 * the single context, so it is dropped rather than carried forward.
 *
 * <p>Beyond the three columns the schema calls for (usuario, prompt, timestamp), this entity
 * also carries the small amount of state needed to run the clarification loop described by
 * POST /resposta/validar/necessidade-informacoes and POST /resposta/validar/resposta-necessidade:
 * when the Gatekeeper (Agente 0) raises a pendency, {@link #status} moves to
 * {@code AGUARDANDO_INFORMACAO} and the pendency + pending question are stashed here so the
 * second endpoint — which only receives {@code idPedido} and the user's raw answer — can
 * reload what question is being answered instead of trusting the client to resend it.</p>
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @Column(name = "prompt", columnDefinition = "TEXT")
    private String prompt;

    @Column(name = "timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PedidoStatus status = PedidoStatus.PRONTO;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pendencia")
    private TiposPendencia tipoPendencia;

    @Column(name = "descricao_duvida", columnDefinition = "TEXT")
    private String descricaoDuvida;

    @Column(name = "pergunta_pendente", columnDefinition = "TEXT")
    private String perguntaPendente;

    public Pedido(Usuario usuario, String prompt) {
        this.usuario = usuario;
        this.prompt = prompt;
        this.timestamp = LocalDateTime.now();
        this.status = PedidoStatus.PRONTO;
    }

    public void marcarAguardandoInformacao(TiposPendencia tipoPendencia, String descricaoDuvida, String pergunta) {
        this.status = PedidoStatus.AGUARDANDO_INFORMACAO;
        this.tipoPendencia = tipoPendencia;
        this.descricaoDuvida = descricaoDuvida;
        this.perguntaPendente = pergunta;
    }

    public void marcarPronto() {
        this.status = PedidoStatus.PRONTO;
    }
}
