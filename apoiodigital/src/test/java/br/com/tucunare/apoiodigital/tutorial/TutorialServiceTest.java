package br.com.tucunare.apoiodigital.tutorial;

import br.com.tucunare.apoiodigital.findbestapp.FirstTry.FindBestAppResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector.ElementSelectorRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector.ElementSelectorResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector.ElementSelectorService;
import br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner.ScreenContextDefinerRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner.ScreenContextDefinerResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner.ScreenContextDefinerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class TutorialServiceTest {

    @Mock
    private ElementSelectorService elementSelectorService;

    @Mock
    private ScreenContextDefinerService screenContextDefinerService;

    @Autowired
    @InjectMocks
    private FindBestAnswerService findBestAnswerService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Success process with valid params")
    void findBestAnswer(){

        AndroidComponentDTO escolhido = new AndroidComponentDTO(0, "classname", "addinfo");
        List<AndroidComponentDTO> listaComponentes = new ArrayList<AndroidComponentDTO>(1);
        listaComponentes.add(escolhido);

        ElementSelectorRequestDTO request = new ElementSelectorRequestDTO("contexto", "prompt", "pe", "re",
                listaComponentes);

        ElementSelectorResponseDTO elementSelectorResponseDTO = Mockito.mock(ElementSelectorResponseDTO.class);
        ScreenContextDefinerResponseDTO screenContextDefinerResponseDTO = Mockito.mock(ScreenContextDefinerResponseDTO.class);

        when(elementSelectorService.executeTask(any(ElementSelectorRequestDTO.class))).thenReturn(elementSelectorResponseDTO);
        when(screenContextDefinerService.executeTask(any(ScreenContextDefinerRequestDTO.class))).thenReturn(screenContextDefinerResponseDTO);
//        when(request.elementos().get(anyInt())).thenReturn(escolhido);

        FindBestAnswerResponseDTO findBestAnswerResponseDTO = findBestAnswerService.findBestAnswer(request);

        assertNotNull(findBestAnswerResponseDTO);
    }

    @Test
    @DisplayName("IndexOutOfBounds in components list")
    void findBestAnswerIndexOutOfBounds(){

        AndroidComponentDTO escolhido = new AndroidComponentDTO(0, "classname", "addinfo");
        List<AndroidComponentDTO> listaComponentes = new ArrayList<AndroidComponentDTO>(1);
        listaComponentes.add(escolhido);

        ElementSelectorRequestDTO request = new ElementSelectorRequestDTO("contexto", "prompt", "pe", "re",
                listaComponentes);

        ScreenContextDefinerResponseDTO screenContextDefinerResponseDTO = Mockito.mock(ScreenContextDefinerResponseDTO.class);

        when(elementSelectorService.executeTask(any(ElementSelectorRequestDTO.class))).thenReturn(
                new ElementSelectorResponseDTO(2, "raciocinio incorreto")
        );
        when(screenContextDefinerService.executeTask(any(ScreenContextDefinerRequestDTO.class))).thenReturn(screenContextDefinerResponseDTO);

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> findBestAnswerService.findBestAnswer(request)
        );
    }

}
