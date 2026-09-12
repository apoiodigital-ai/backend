package br.com.tucunare.apoiodigital.resposta.service;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.pedido.data.Pedido;
import br.com.tucunare.apoiodigital.pedido.exception.PedidoDoesNotExistException;
import br.com.tucunare.apoiodigital.pedido.repository.PedidoRepository;
import br.com.tucunare.apoiodigital.resposta.repository.RespostaRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RespostaService {

    private final RespostaRepository respostaRepository;
    private final PedidoRepository pedidoRepository;

    public RespostaService(RespostaRepository respostaRepository, PedidoRepository pedidoRepository) {
        this.respostaRepository = respostaRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<Map<String, String>> listarRespostaPorPedido(UUID idPedido, Cliente cliente) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(PedidoDoesNotExistException::new);

        // Same 404 whether the Pedido doesn't exist or belongs to another tenant, so this
        // can't be used to enumerate ids across partners.
        if (!pedido.getUsuario().getCliente().getId().equals(cliente.getId())) {
            throw new PedidoDoesNotExistException();
        }

        List<Object[]> resultados = respostaRepository.listarRespostaPorIdPedido(idPedido);

        if (resultados == null || resultados.isEmpty()) {
            throw new PedidoDoesNotExistException();
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
