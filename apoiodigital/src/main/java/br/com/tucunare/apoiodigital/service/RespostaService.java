package br.com.tucunare.apoiodigital.service;

import br.com.tucunare.apoiodigital.repository.RespostaRepository;
import br.com.tucunare.apoiodigital.exception.RequisicaoDoesNotExistException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RespostaService {

    private final RespostaRepository respostaRepository;

    public RespostaService(RespostaRepository respostaRepository) {
        this.respostaRepository = respostaRepository;
    }

    public List<Map<String, String>> listarRespostaPorRequisicao(UUID requisicaoId) {

        List<Object[]> resultados =
                respostaRepository.listarRespostaPorIdRequisicao(requisicaoId);

        if (resultados == null || resultados.isEmpty()) {
            throw new RequisicaoDoesNotExistException();
        }

        List<Map<String, String>> respostas = new ArrayList<>();

        for (Object[] row : resultados) {

            if (row.length < 2) {
                continue;
            }

            Map<String, String> respostaMap = new HashMap<>();
            respostaMap.put("iaMessage", Objects.toString(row[0], ""));
            respostaMap.put("key", Objects.toString(row[1], ""));

            respostas.add(respostaMap);
        }

        return respostas;
    }
}
