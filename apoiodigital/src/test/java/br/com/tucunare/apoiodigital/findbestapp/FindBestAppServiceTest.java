package br.com.tucunare.apoiodigital.findbestapp;

import br.com.tucunare.apoiodigital.appsuportado.data.AppSuportado;
import br.com.tucunare.apoiodigital.findbestapp.FirstTry.FindBestAppResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.FirstTry.FindBestAppService;
import br.com.tucunare.apoiodigital.findbestapp.agents.ChooseLocalApp.ChooseLocalAppRequestDTO;
import br.com.tucunare.apoiodigital.findbestapp.agents.ChooseLocalApp.ChooseLocalAppResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.agents.ChooseLocalApp.ChooseLocalAppService;
import br.com.tucunare.apoiodigital.findbestapp.agents.SimplifyPrompt.SimplifyPromptResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.agents.SimplifyPrompt.SimplifyPromptService;
import com.google.protobuf.Internal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class FindBestAppServiceTest {

    @Mock
    private SimplifyPromptService simplifyPromptService;

    @Mock
    private ChooseLocalAppService chooseLocalAppService;

    @Autowired
    @InjectMocks
    private FindBestAppService findBestAppService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findBestApp(){

        SimplifyPromptResponseDTO simplifyPromptResponseDTO = Mockito.mock(SimplifyPromptResponseDTO.class);
        ChooseLocalAppResponseDTO chooseLocalAppResponseDTO = Mockito.mock(ChooseLocalAppResponseDTO.class);
        when(simplifyPromptService.executeTask(any(String.class))).thenReturn(simplifyPromptResponseDTO);
        when(chooseLocalAppService.executeTask(any(ChooseLocalAppRequestDTO.class))).thenReturn(chooseLocalAppResponseDTO);

        FindBestAppResponseDTO response = findBestAppService.findBestApp("prompt qualquer", new ArrayList<AppSuportado>());
        assertNotNull(response);

    }

}
