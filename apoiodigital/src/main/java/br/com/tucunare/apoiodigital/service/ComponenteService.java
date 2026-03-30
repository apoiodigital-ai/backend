package br.com.tucunare.apoiodigital.service;

import br.com.tucunare.apoiodigital.model.Componente;
import br.com.tucunare.apoiodigital.model.Resposta;
import br.com.tucunare.apoiodigital.repository.ComponenteRepository;
import br.com.tucunare.apoiodigital.repository.RespostaRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ComponenteService {

    private final ComponenteRepository componenteRepository;
    private final RespostaRepository respostaRepository;
    private final CryptService cryptService;

    public ComponenteService(
            ComponenteRepository componenteRepository,
            RespostaRepository respostaRepository,
            CryptService cryptService
    ) {
        this.componenteRepository = componenteRepository;
        this.respostaRepository = respostaRepository;
        this.cryptService = cryptService;
    }

    public List<Componente> salvar(List<Componente> componentes) {
        return componenteRepository.saveAll(componentes);
    }

    public Boolean comparar(
            String textoCriptografado,
            String chaveNova,
            UUID idResposta
    ) {
        Resposta resposta = respostaRepository.findById(idResposta)
                .orElse(null);

        if (resposta == null) {
            return false;
        }

        return true;
    }
}
