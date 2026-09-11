package br.com.tucunare.apoiodigital.usuario.data;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * An end user of a partner ({@link Cliente}) app. Under the SDK model the partner app supplies
 * an already-anonymized identity via {@code CaneSDK.registerUser()} (see
 * {@code UsuarioController#registrar}) — this backend never stores a phone number or a
 * password for end users, only an opaque id scoped to the partner that registered it. (The old
 * standalone-app fields {@code telefone}/{@code senha} were removed along with the phone+password
 * login flow they supported; see the refactor report for why.)
 */
@Entity
@Data
@Table(name = "usuario")
@NoArgsConstructor
public class Usuario {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "nome")
    private String nome;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    @JsonIgnore
    private Cliente cliente;

    public Usuario(String nome, Cliente cliente) {
        this.nome = nome;
        this.cliente = cliente;
    }
}
