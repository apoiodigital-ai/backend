package br.com.tucunare.apoiodigital.service;

import br.com.tucunare.apoiodigital.model.Atalho;
import br.com.tucunare.apoiodigital.model.Requisicao;
import br.com.tucunare.apoiodigital.repository.AtalhoRepository;
import br.com.tucunare.apoiodigital.repository.RequisicaoRepository;
import br.com.tucunare.apoiodigital.repository.UsuarioRepository;
import br.com.tucunare.apoiodigital.exception.AtalhoDoesNotExistException;
import br.com.tucunare.apoiodigital.exception.RequisicaoDoesNotExistException;
import br.com.tucunare.apoiodigital.exception.UsuarioDoesNotExistException;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class AtalhoService {

    private final AtalhoRepository atalhoRepository;
    private final RequisicaoRepository requisicaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FindBestAppService geminiService;


    public AtalhoService(
            AtalhoRepository atalhoRepository,
            RequisicaoRepository requisicaoRepository,
            UsuarioRepository usuarioRepository, FindBestAppService geminiService
    ) {
        this.atalhoRepository = atalhoRepository;
        this.requisicaoRepository = requisicaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.geminiService = geminiService;
    }

    public Atalho findById(UUID id) {
        return atalhoRepository.findById(id)
                .orElseThrow(AtalhoDoesNotExistException::new);
    }

    public Atalho save(Atalho atalho) {
        return atalhoRepository.save(atalho);
    }

    public void deleteById(UUID id) {
        atalhoRepository.deleteById(id);
    }

    public void salvarAtalhosIniciais(List<Requisicao> requisicoes) {
        if (requisicoes == null || requisicoes.size() < 3) {
            throw new IllegalArgumentException("Lista de requisições insuficiente");
        }

        Atalho atalho1 = new Atalho(requisicoes.get(0), "Quero pedir comida");
        Atalho atalho2 = new Atalho(requisicoes.get(1), "Quero mandar mensagem");
        Atalho atalho3 = new Atalho(requisicoes.get(2), "Quero pedir Uber");

        atalhoRepository.saveAll(
                Arrays.asList(atalho1, atalho2, atalho3)
        );
    }

    public void criarAtalho(Requisicao requisicao, UUID id_req_match) {


        if(id_req_match != null){
            Requisicao reqMatch = requisicaoRepository.findById(id_req_match).orElseThrow(RequisicaoDoesNotExistException::new);
            Atalho opAtalho = atalhoRepository.findByRequisicao(reqMatch).orElseThrow(AtalhoDoesNotExistException::new);
            Atalho atalho = new Atalho(requisicao, opAtalho.getTitulo());
            atalhoRepository.save(atalho);
            return;
        }

        String titulo = geminiService.definirTituloAtalho(requisicao.getPrompt());

        Atalho atalho = new Atalho(requisicao, titulo);
        atalhoRepository.save(atalho);
    }

    public Requisicao iniciarAtalho(UUID idAtalho) {
        Atalho atalho = findById(idAtalho);

        Requisicao requisicaoBase = atalho.getRequisicao();

        Requisicao novaRequisicao = new Requisicao(
                requisicaoBase.getUsuario(),
                requisicaoBase.getPrompt(),
                requisicaoBase.getAppSuportado()
        );

        return requisicaoRepository.save(novaRequisicao);
    }

    public List<Atalho> carregarAtalhos(UUID idUsuario) {
        usuarioRepository.findById(idUsuario)
                .orElseThrow(UsuarioDoesNotExistException::new);

        List<Atalho> atalhos =
                atalhoRepository.findByRequisicaoUsuarioId(idUsuario);

        return atalhos.subList(0, atalhos.size());
    }
}
