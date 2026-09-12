package br.com.tucunare.apoiodigital.resposta.data;

import br.com.tucunare.apoiodigital.pedido.data.Pedido;
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
 * The final instruction produced for a Pedido. {@link #raciocinio} is the internal
 * reasoning/audit trail behind that instruction (the ElementSelector agent's own explanation of
 * why it picked a given element) — it was entirely missing before this refactor even though it
 * is what backs the product's "auditoria e rastreabilidade total" (full audit trail) claim.
 * {@link #mensagem} remains the user-facing text, unchanged in meaning from before.
 */
@Entity
@Data
@Table(name = "resposta")
@NoArgsConstructor
public class Resposta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_pedido", nullable = false)
    @JsonIgnore
    private Pedido pedido;

    @Column(name = "mensagem", columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "raciocinio", columnDefinition = "TEXT")
    private String raciocinio;

    @Column(name = "timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

    public Resposta(Pedido pedido, String mensagem, String raciocinio) {
        this.pedido = pedido;
        this.mensagem = mensagem;
        this.raciocinio = raciocinio;
        this.timestamp = LocalDateTime.now();
    }
}
