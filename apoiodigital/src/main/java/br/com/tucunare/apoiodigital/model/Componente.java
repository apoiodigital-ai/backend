package br.com.tucunare.apoiodigital.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @Column(name = "conteud_cript")
    private String conteudo_criptografado;

    @Column(name = "chave_publica")
    private String chave_publica;

    @ManyToOne
    @JoinColumn(name = "id_resposta", columnDefinition = "VARCHAR(36)")
    private Resposta resposta;

    public Componente(String conteudo_criptografado, String chave_publica, Resposta resposta) {
        this.conteudo_criptografado = conteudo_criptografado;
        this.chave_publica = chave_publica;
        this.resposta = resposta;
    }
}
