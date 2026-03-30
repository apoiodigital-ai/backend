package br.com.tucunare.apoiodigital.model;

import br.com.tucunare.apoiodigital.exception.InvalidPasswordLengthException;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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

    public void validarSenha(){
        if(this.senha.length() < 8) throw new InvalidPasswordLengthException();
    }

}
