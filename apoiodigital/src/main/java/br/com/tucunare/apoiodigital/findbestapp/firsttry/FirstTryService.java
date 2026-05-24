package br.com.tucunare.apoiodigital.findbestapp.firsttry;

import br.com.tucunare.apoiodigital.dto.RequestInputToGeminiDTO;
import br.com.tucunare.apoiodigital.dto.findbestapp.IAAgent2RequestDTO;
import br.com.tucunare.apoiodigital.enums.IAAgent2ModoEnum;
import br.com.tucunare.apoiodigital.findbestapp.agents.ChooseLocalApp.ChooseLocalAppResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.agents.ChooseLocalApp.ChooseLocalAppService;
import br.com.tucunare.apoiodigital.findbestapp.agents.SimplifyPrompt.SimplifyPromptResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.agents.SimplifyPrompt.SimplifyPromptService;
import br.com.tucunare.apoiodigital.model.AppSuportado;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service public class FirstTryService {


    private final SimplifyPromptService simplifyPromptService;
    private final ChooseLocalAppService chooseLocalAppService;

    public FirstTryService(SimplifyPromptService simplifyPromptService, ChooseLocalAppService chooseLocalAppService) {
        this.simplifyPromptService = simplifyPromptService;
        this.chooseLocalAppService = chooseLocalAppService;
    }


    private Long findAppBancoByPacote(List<AppSuportado> listaAppSuportado, String pacote){

        for (AppSuportado appSuportado : listaAppSuportado) {
            if (Objects.equals(pacote, appSuportado.getPacote())) {
                return appSuportado.getId();
            }
        }

        return null;
    }

    public FirstTryResponseDTO findBestAppFirstTry(RequestInputToGeminiDTO dto, List<AppSuportado> listaAppSuportado){
        SimplifyPromptResponseDTO simplifyPromptResponseDTO = simplifyPromptService.executeTask(dto.prompt());

        IAAgent2RequestDTO iaAgent2RequestDTO = new IAAgent2RequestDTO(
                simplifyPromptResponseDTO.prompt_limpo(), dto.lista_apps_instalados(), IAAgent2ModoEnum.inicial);

        ChooseLocalAppResponseDTO chooseLocalAppResponseDTO = chooseLocalAppService.executeTask(iaAgent2RequestDTO);

        String pacoteAppInstaladoEscolhido = dto.lista_apps_instalados().get((int) (chooseLocalAppResponseDTO.id_app_instalado() - 1)).pacote();

        Long idAppBanco = findAppBancoByPacote(listaAppSuportado, pacoteAppInstaladoEscolhido);

        return new FirstTryResponseDTO(idAppBanco, chooseLocalAppResponseDTO.id_app_instalado(), simplifyPromptResponseDTO.prompt_limpo());
    }

}
