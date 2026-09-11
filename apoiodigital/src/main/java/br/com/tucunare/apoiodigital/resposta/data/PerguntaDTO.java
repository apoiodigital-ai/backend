package br.com.tucunare.apoiodigital.resposta.data;

import java.util.List;

public record PerguntaDTO(String texto, List<String> opcoes) {
}
