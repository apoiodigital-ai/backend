package br.com.tucunare.apoiodigital.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "Resposta")
@NoArgsConstructor
public class Resposta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_requisicao")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @JsonIgnore
    private Requisicao requisicao;

    @Column(name = "mensagem")
    private String mensagem;

    @Column(name="criacao")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime criacao;

    public Resposta(Requisicao requisicao, String mensagem) {
        this.requisicao = requisicao;
        this.mensagem = mensagem;
        criacao = LocalDateTime.now();
    }
}
