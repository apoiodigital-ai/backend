package br.com.tucunare.apoiodigital.cliente.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * Custom AI-behavior rule scoped to a single {@link Cliente} (e.g. tone of voice, vocabulary
 * restrictions). Read by the agent pipeline and merged into the system rule sent to the model
 * so each partner can tailor how Cane speaks to their own end users.
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "personalizacao")
public class Personalizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "regra_personalizada", columnDefinition = "TEXT")
    private String regraPersonalizada;

    public Personalizacao(Cliente cliente, String regraPersonalizada) {
        this.cliente = cliente;
        this.regraPersonalizada = regraPersonalizada;
    }
}
