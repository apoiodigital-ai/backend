package br.com.tucunare.apoiodigital.findbestapp.DefineContext;

import br.com.tucunare.apoiodigital.dto.findbestapp.GenerateContextAppDTO;
import org.springframework.stereotype.Service;

@Service
public class DefineContextService {

    public DefineContextResponseDTO executeTask(GenerateContextAppDTO dto) {
        String contexto = dto.prompt_limpo() + " Acabei de entrar no app " + dto.nome_app_instalado() + " esperando o próximo passo para prosseguir";
        return new DefineContextResponseDTO(contexto);
    }
}
