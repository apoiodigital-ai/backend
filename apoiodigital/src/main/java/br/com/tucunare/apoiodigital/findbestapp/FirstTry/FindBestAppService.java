package br.com.tucunare.apoiodigital.findbestapp.FirstTry;

import br.com.tucunare.apoiodigital.findbestapp.agents.ChooseLocalApp.ChooseLocalAppResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.agents.ChooseLocalApp.ChooseLocalAppService;
import br.com.tucunare.apoiodigital.findbestapp.agents.ChooseLocalApp.ChooseLocalAppRequestDTO;
import br.com.tucunare.apoiodigital.findbestapp.agents.SimplifyPrompt.SimplifyPromptResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.agents.SimplifyPrompt.SimplifyPromptService;
import br.com.tucunare.apoiodigital.appsuportado.data.AppSuportado;
import org.springframework.stereotype.Service;

import java.util.List;

@Service public class FindBestAppService {


    private final SimplifyPromptService simplifyPromptService;
    private final ChooseLocalAppService chooseLocalAppService;

    public FindBestAppService(SimplifyPromptService simplifyPromptService, ChooseLocalAppService chooseLocalAppService) {
        this.simplifyPromptService = simplifyPromptService;
        this.chooseLocalAppService = chooseLocalAppService;
    }

    public FindBestAppResponseDTO findBestApp(String promptRaw, List<AppSuportado> listaAppSuportado){
        SimplifyPromptResponseDTO simplifyPromptResponseDTO = simplifyPromptService.executeTask(promptRaw);

        ChooseLocalAppRequestDTO chooseLocalAppRequestDTO = new ChooseLocalAppRequestDTO(
                simplifyPromptResponseDTO.prompt_limpo(), listaAppSuportado);

        ChooseLocalAppResponseDTO chooseLocalAppResponseDTO = chooseLocalAppService.executeTask(chooseLocalAppRequestDTO);

        return new FindBestAppResponseDTO(
                chooseLocalAppResponseDTO.id_app_banco(),
                simplifyPromptResponseDTO.prompt_limpo());
    }

}
