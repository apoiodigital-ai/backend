package br.com.tucunare.apoiodigital.tutorial;

import br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector.ElementSelectorService;
import br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector.ElementSelectorRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector.ElementSelectorResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner.ScreenContextDefinerRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner.ScreenContextDefinerResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner.ScreenContextDefinerService;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

@Service
public class FindBestAnswerService {

    private final ElementSelectorService elementSelectorService;
    private final ScreenContextDefinerService screenContextDefinerService;


    public FindBestAnswerService(ElementSelectorService elementSelectorService, ScreenContextDefinerService screenContextDefinerService) {
        this.elementSelectorService = elementSelectorService;
        this.screenContextDefinerService = screenContextDefinerService;
    }

    public FindBestAnswerResponseDTO findBestAnswer(ElementSelectorRequestDTO request){
        Gson gson = new Gson();
        System.out.println(gson.toJson(request));

        ElementSelectorResponseDTO agenteXresponse = elementSelectorService.executeTask(request);
        ScreenContextDefinerResponseDTO agenteZresponse = screenContextDefinerService.executeTask(
                new ScreenContextDefinerRequestDTO(request.contexto(),
                        agenteXresponse.raciocinio(),
                        request.elementos().get(agenteXresponse.viewID())) // pega elemento destacado
        );
        return new FindBestAnswerResponseDTO(
                agenteXresponse.viewID(),
                agenteZresponse.novo_contexto(),agenteZresponse.mensagem_escrita(),
                agenteZresponse.mensagem_voz()
        );
    }
}
