package br.com.tucunare.apoiodigital.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Atalho")
@Data
@NoArgsConstructor
public class Atalho {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_requisicao", nullable = false)
    @JsonIgnore
    private Requisicao requisicao;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    //fora do banco
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime criacao;

    public Atalho(Requisicao requisicao, String titulo) {
        this.requisicao = requisicao;
        this.titulo = titulo;
        criacao = LocalDateTime.now();
    }



}
