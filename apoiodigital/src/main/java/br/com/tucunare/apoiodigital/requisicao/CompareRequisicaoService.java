package br.com.tucunare.apoiodigital.requisicao;

import br.com.tucunare.apoiodigital.model.Requisicao;
import br.com.tucunare.apoiodigital.model.Usuario;
import br.com.tucunare.apoiodigital.requisicao.repository.RequisicaoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompareRequisicaoService {

    private final RequisicaoRepository requisicaoRepository;

    public CompareRequisicaoService(RequisicaoRepository requisicaoRepository) {
        this.requisicaoRepository = requisicaoRepository;
    }

    public Optional<Requisicao> compararRequisicoes(
            String prompt, Usuario usuario
    ) {
        return requisicaoRepository.findFirstByPromptAndUsuarioOrderByCriacaoDesc(prompt, usuario);
    }

}
