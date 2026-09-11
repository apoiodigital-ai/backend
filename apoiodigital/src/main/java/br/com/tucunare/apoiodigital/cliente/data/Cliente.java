package br.com.tucunare.apoiodigital.cliente.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A partner company (bank, telemedicine provider, pharmacy, ...) that embeds the Cane SDK
 * in its own app. This is the tenant boundary of the whole system: every {@code Usuario} and
 * every downstream Pedido/Resposta/Componente is reachable only through the Cliente that owns
 * it. Requests are authenticated by presenting {@link #accessKey} in the {@code x-api-key}
 * header (see {@code br.com.tucunare.apoiodigital.security}).
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

    @OneToMany(mappedBy = "cliente")
    private List<Personalizacao> personalizacoes = new ArrayList<>();

    public Cliente(String nome, String accessKey, String areaAtuacao) {
        this.nome = nome;
        this.accessKey = accessKey;
        this.areaAtuacao = areaAtuacao;
    }
}
