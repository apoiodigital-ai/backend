package br.com.tucunare.apoiodigital.usuario.data;

import br.com.tucunare.apoiodigital.usuario.exception.InvalidPasswordLengthException;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Data
@Table(name="Usuario")
public class Usuario {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "senha")
    private String senha;


    public Usuario(String nome, String telefone, String senha) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.telefone = telefone;
        this.senha = senha;
    }
}
