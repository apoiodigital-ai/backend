package br.com.tucunare.apoiodigital.requisicao.data;

import br.com.tucunare.apoiodigital.appsuportado.data.AppSuportado;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
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
@Data
@NoArgsConstructor
@Table(name = "Requisicao")
public class Requisicao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    @JsonIgnore
    private Usuario usuario;

    @Column(name="prompt")
    private String prompt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime criacao;

    @JoinColumn(name = "id_app_suportado")
    @ManyToOne
    private AppSuportado appSuportado;

    public Requisicao(Usuario usuario, String prompt, AppSuportado appSuportado) {
        this.usuario = usuario;
        this.prompt = prompt;
        this.appSuportado = appSuportado;
        this.criacao = LocalDateTime.now();
    }
}
