package br.com.tucunare.apoiodigital.cliente.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * A partner company (bank, telemedicine provider, pharmacy, ...) that embeds the Cane SDK
 * in its own app. This is the tenant boundary of the whole system: every {@code Usuario} and
 * every downstream Pedido/Resposta/Componente is reachable only through the Cliente that owns
 * it. Requests are authenticated by presenting {@link #accessKey} in the {@code x-api-key}
 * header (see {@code br.com.tucunare.apoiodigital.security}).
 *
 * <p>Its one-to-many Usuario and Personalizacao rows are looked up via their own repositories
 * (e.g. {@code PersonalizacaoRepository#findByClienteId}) rather than mapped as a Java
 * collection here: this entity is also the Spring Security principal placed in the security
 * context by ApiKeyAuthenticationFilter, and a bidirectional Lombok {@code @Data} collection
 * back-reference (Cliente -> Personalizacao -> Cliente -> ...) would make any accidental
 * equals/hashCode/toString call — including ones outside this codebase's control, such as
 * framework debug logging of the authenticated principal — recurse infinitely.</p>
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "access_key", nullable = false, unique = true)
    private String accessKey;

    @Column(name = "area_atuacao")
    private String areaAtuacao;

    public Cliente(String nome, String accessKey, String areaAtuacao) {
        this.nome = nome;
        this.accessKey = accessKey;
        this.areaAtuacao = areaAtuacao;
    }
}
