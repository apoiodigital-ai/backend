package br.com.tucunare.apoiodigital.findbestapp;

import br.com.tucunare.apoiodigital.dto.*;
import br.com.tucunare.apoiodigital.dto.findbestapp.FindBestAppResponseDTO;
import br.com.tucunare.apoiodigital.dto.findbestapp.GenerateContextAppDTO;
import br.com.tucunare.apoiodigital.dto.findbestapp.IAAgent2RequestDTO;
import br.com.tucunare.apoiodigital.dto.findbestapp.IAAgent3RequestDTO;
import br.com.tucunare.apoiodigital.enums.IAAgent2ModoEnum;
import br.com.tucunare.apoiodigital.findbestapp.agents.ChooseLocalApp.ChooseLocalAppResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.agents.ChooseLocalApp.ChooseLocalAppService;
import br.com.tucunare.apoiodigital.findbestapp.agents.DefineContext.DefineContextResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.agents.DefineContext.DefineContextService;
import br.com.tucunare.apoiodigital.findbestapp.agents.SimplifyPrompt.SimplifyPromptResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.agents.SimplifyPrompt.SimplifyPromptService;
import br.com.tucunare.apoiodigital.findbestapp.agents.ValidateAppChoose.ValidateAppChooseDTO;
import br.com.tucunare.apoiodigital.findbestapp.agents.ValidateAppChoose.ValidateAppChooseService;
import br.com.tucunare.apoiodigital.model.AppSuportado;
import br.com.tucunare.apoiodigital.repository.AppSuportadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class FindBestAppService {

    @Autowired
    private AppSuportadoRepository appSuportadoRepository;

    private final SimplifyPromptService simplifyPromptService;
    private final ChooseLocalAppService chooseLocalAppService;
    private final ValidateAppChooseService validateAppChooseService;
    private final DefineContextService defineContextService;

    public FindBestAppService(SimplifyPromptService simplifyPromptService, ChooseLocalAppService chooseLocalAppService, ValidateAppChooseService validateAppChooseService, DefineContextService defineContextService) {
        this.simplifyPromptService = simplifyPromptService;
        this.chooseLocalAppService = chooseLocalAppService;
        this.validateAppChooseService = validateAppChooseService;
        this.defineContextService = defineContextService;
    }

    private Long findAppBancoByPacote(List<AppSuportado> listaAppSuportado, String pacote){

        for (AppSuportado appSuportado : listaAppSuportado) {
            if (Objects.equals(pacote, appSuportado.getPacote())) {
                return appSuportado.getId();
            }
        }

        return null;
    }

    private Long findAppInstaladoByPacote(List<AppRequestDTO> lista_app_instalado, String pacote){
        Long idPlayStore = -1L;
        for (AppRequestDTO appRequestDTO : lista_app_instalado) {

            if (Objects.equals(appRequestDTO.pacote(), "com.android.vending")) {
                idPlayStore = appRequestDTO.id();
            }

            if (Objects.equals(pacote, appRequestDTO.pacote())) {

                return appRequestDTO.id();
            }
        }

        return idPlayStore;
    }

    private FirstTryResponseDTO findBestAppFirstTry(RequestInputToGeminiDTO dto, List<AppSuportado> listaAppSuportado){
        SimplifyPromptResponseDTO simplifyPromptResponseDTO = simplifyPromptService.executeTask(dto.prompt());

        IAAgent2RequestDTO iaAgent2RequestDTO = new IAAgent2RequestDTO(
                simplifyPromptResponseDTO.prompt_limpo(), dto.lista_apps_instalados(), IAAgent2ModoEnum.inicial);

        ChooseLocalAppResponseDTO chooseLocalAppResponseDTO = chooseLocalAppService.executeTask(iaAgent2RequestDTO);

        String pacoteAppInstaladoEscolhido = dto.lista_apps_instalados().get((int) (chooseLocalAppResponseDTO.id_app_instalado() - 1)).pacote();

        Long idAppBanco = findAppBancoByPacote(listaAppSuportado, pacoteAppInstaladoEscolhido);

        return new FirstTryResponseDTO(idAppBanco, chooseLocalAppResponseDTO.id_app_instalado(), simplifyPromptResponseDTO.prompt_limpo());
    }

    private FallBackResponseDTO findBestAppFallBack(RequestInputToGeminiDTO dto, List<AppSuportado> listaAppSuportado, FirstTryResponseDTO firstTryResponse){

        String pacoteAppInstaladoEscolhido = dto.lista_apps_instalados().get((int) (firstTryResponse.id_app_instalado() - 1)).pacote();

        IAAgent3RequestDTO iaAgent3RequestDTO = new IAAgent3RequestDTO(
                pacoteAppInstaladoEscolhido,
                listaAppSuportado);

        ValidateAppChooseDTO validateAppChooseDTO = validateAppChooseService.executeTask(iaAgent3RequestDTO);

        String pacoteBancoSimilar = listaAppSuportado.get((int) (validateAppChooseDTO.id_app_banco() - 1)).getPacote();

        Long id_app_instalado = findAppInstaladoByPacote(dto.lista_apps_instalados(), pacoteBancoSimilar);

        return new FallBackResponseDTO(validateAppChooseDTO.id_app_banco(), id_app_instalado);
    }

    public FindBestAppResponseDTO acharMelhorApp(RequestInputToGeminiDTO dto){
        List<AppSuportado> listaAppSuportado = appSuportadoRepository.findAll();

        FirstTryResponseDTO firstTry = findBestAppFirstTry(dto, listaAppSuportado);
        Long id_app_banco = firstTry.id_app_banco();
        Long id_app_instalado = firstTry.id_app_instalado();

        if(id_app_banco == null){

            FallBackResponseDTO fallback = findBestAppFallBack(dto, listaAppSuportado, firstTry);
            id_app_banco = fallback.id_app_banco();
            id_app_instalado = fallback.id_app_instalado();

        }

        String nomeAppBanco = listaAppSuportado.get((int) (id_app_banco - 1)).getNome();
        String nomeAppInstalado = dto.lista_apps_instalados().get((int) (id_app_instalado - 1)).nome();

        DefineContextResponseDTO defineContextResponseDTO = defineContextService.executeTask(
                new GenerateContextAppDTO(
                        firstTry.prompt_limpo(),
                        nomeAppBanco,
                        nomeAppInstalado
                )
        );

        return new FindBestAppResponseDTO(defineContextResponseDTO.contexto(),
                id_app_banco,
                id_app_instalado);

    }

}