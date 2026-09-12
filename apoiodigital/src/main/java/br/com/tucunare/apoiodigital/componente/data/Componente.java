package br.com.tucunare.apoiodigital.componente.data;

import br.com.tucunare.apoiodigital.resposta.data.Resposta;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * A cache-key signature for the UI screen a Resposta was computed against. {@code assinatura}
 * is a hash of the element hierarchy (class/text/viewID makeup — see
 * {@code ComponenteService#gerarAssinatura}), not the raw screen content itself: comparing a
 * freshly-hashed screen against the stored assinatura lets the SDK tell whether a cached
 * instruction is still valid without spending a fresh LLM call every time the partner app ships
 * a UI update that doesn't actually change this particular screen. (Previously this entity
 * stored a raw JSON blob in a field called {@code conteudo} and had no hash at all — dead
 * weight that never served that cache-key purpose.)
 */
@Entity
@Table(name = "componente")
@Data
@NoArgsConstructor
public class Componente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "assinatura", columnDefinition = "TEXT", nullable = false)
    private String assinatura;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_resposta", nullable = false)
    @JsonIgnore
    private Resposta resposta;

    public Componente(String assinatura, Resposta resposta) {
        this.assinatura = assinatura;
        this.resposta = resposta;
    }
}
