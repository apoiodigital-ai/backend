package br.com.tucunare.apoiodigital.componente.data;

import br.com.tucunare.apoiodigital.agent.AndroidComponentDTO;

import java.util.List;
import java.util.UUID;

public record ComponenteCompararRequestDTO(UUID idResposta, List<AndroidComponentDTO> elementos) {
}
