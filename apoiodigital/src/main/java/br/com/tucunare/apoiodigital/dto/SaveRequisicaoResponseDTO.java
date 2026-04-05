package br.com.tucunare.apoiodigital.dto;

import br.com.tucunare.apoiodigital.model.Requisicao;

import java.util.UUID;

public record SaveRequisicaoResponseDTO(Requisicao requisicao, String contexto, Long id_app_instalado, UUID id_req_match) {
}
