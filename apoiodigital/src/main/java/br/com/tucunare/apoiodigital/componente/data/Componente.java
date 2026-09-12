package br.com.tucunare.apoiodigital.componente.data;

import br.com.tucunare.apoiodigital.resposta.data.Resposta;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name="Componente")
@Data
@NoArgsConstructor
public class Componente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "conteudo")
    private String conteudo;

    @ManyToOne
    @JoinColumn(name = "id_resposta", columnDefinition = "VARCHAR(36)")
    private Resposta resposta;

    public Componente(String conteudo, Resposta resposta) {
        this.conteudo = conteudo;
        this.resposta = resposta;
    }
}
