package br.com.tucunare.apoiodigital.findbestapp.fallback;

import br.com.tucunare.apoiodigital.dto.AppRequestDTO;
import br.com.tucunare.apoiodigital.dto.RequestInputToGeminiDTO;
import br.com.tucunare.apoiodigital.dto.findbestapp.IAAgent3RequestDTO;
import br.com.tucunare.apoiodigital.findbestapp.firsttry.FirstTryResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.agents.ValidateAppChoose.ValidateAppChooseDTO;
import br.com.tucunare.apoiodigital.findbestapp.agents.ValidateAppChoose.ValidateAppChooseService;
import br.com.tucunare.apoiodigital.model.AppSuportado;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class FallBackService {

    private final ValidateAppChooseService validateAppChooseService;

    public FallBackService(ValidateAppChooseService validateAppChooseService) {
        this.validateAppChooseService = validateAppChooseService;
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

    public FallBackResponseDTO findBestAppFallBack(RequestInputToGeminiDTO dto, List<AppSuportado> listaAppSuportado, FirstTryResponseDTO firstTryResponse){

        String pacoteAppInstaladoEscolhido = dto.lista_apps_instalados().get((int) (firstTryResponse.id_app_instalado() - 1)).pacote();

        IAAgent3RequestDTO iaAgent3RequestDTO = new IAAgent3RequestDTO(
                pacoteAppInstaladoEscolhido,
                listaAppSuportado);

        ValidateAppChooseDTO validateAppChooseDTO = validateAppChooseService.executeTask(iaAgent3RequestDTO);

        String pacoteBancoSimilar = listaAppSuportado.get((int) (validateAppChooseDTO.id_app_banco() - 1)).getPacote();

        Long id_app_instalado = findAppInstaladoByPacote(dto.lista_apps_instalados(), pacoteBancoSimilar);

        return new FallBackResponseDTO(validateAppChooseDTO.id_app_banco(), id_app_instalado);
    }

}
