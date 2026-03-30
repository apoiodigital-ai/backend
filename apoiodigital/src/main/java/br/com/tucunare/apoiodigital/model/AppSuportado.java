package br.com.tucunare.apoiodigital.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Table(name = "AppSuportado")
public class AppSuportado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "referencia")
    private String referencia;

    @Column(name = "situacao")
    private String situacao;

}
