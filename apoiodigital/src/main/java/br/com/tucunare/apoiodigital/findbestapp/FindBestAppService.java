package br.com.tucunare.apoiodigital.findbestapp;

import br.com.tucunare.apoiodigital.dto.*;
import br.com.tucunare.apoiodigital.dto.findbestapp.FindBestAppResponseDTO;
import br.com.tucunare.apoiodigital.dto.findbestapp.GenerateContextAppDTO;
import br.com.tucunare.apoiodigital.findbestapp.DefineContext.DefineContextResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.DefineContext.DefineContextService;
import br.com.tucunare.apoiodigital.findbestapp.FallBack.FallBackResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.FallBack.FallBackService;
import br.com.tucunare.apoiodigital.findbestapp.FirstTry.FirstTryResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.FirstTry.FirstTryService;
import br.com.tucunare.apoiodigital.model.AppSuportado;
import br.com.tucunare.apoiodigital.repository.AppSuportadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindBestAppService {

    @Autowired
    private AppSuportadoRepository appSuportadoRepository;

    private final DefineContextService defineContextService;
    private final FirstTryService firstTryService;
    private final FallBackService fallBackService;

    public FindBestAppService(DefineContextService defineContextService, FirstTryService firstTryService, FallBackService fallBackService) {

        this.defineContextService = defineContextService;
        this.firstTryService = firstTryService;
        this.fallBackService = fallBackService;
    }

    public FindBestAppResponseDTO acharMelhorApp(RequestInputToGeminiDTO dto){
        List<AppSuportado> listaAppSuportado = appSuportadoRepository.findAll();

        FirstTryResponseDTO firstTry = firstTryService.findBestAppFirstTry(dto, listaAppSuportado);

        Long id_app_banco = firstTry.id_app_banco();
        Long id_app_instalado = firstTry.id_app_instalado();

        if(id_app_banco == null){

            FallBackResponseDTO fallback = fallBackService.findBestAppFallBack(dto, listaAppSuportado, firstTry);

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