package br.com.tucunare.apoiodigital.requisicao;

import br.com.tucunare.apoiodigital.appsuportado.data.AppSuportado;
import br.com.tucunare.apoiodigital.appsuportado.repository.AppSuportadoRepository;
import br.com.tucunare.apoiodigital.findbestapp.FirstTry.FindBestAppResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.FirstTry.FindBestAppService;
import br.com.tucunare.apoiodigital.requisicao.data.Requisicao;
import br.com.tucunare.apoiodigital.requisicao.data.RequisicaoInputDTO;
import br.com.tucunare.apoiodigital.requisicao.data.SaveRequisicaoResponseDTO;
import br.com.tucunare.apoiodigital.requisicao.repository.RequisicaoRepository;
import br.com.tucunare.apoiodigital.requisicao.service.CompareRequisicaoService;
import br.com.tucunare.apoiodigital.requisicao.service.RequisicaoService;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.exception.UsuarioDoesNotExistException;
import br.com.tucunare.apoiodigital.usuario.repository.UsuarioRepository;
import br.com.tucunare.apoiodigital.usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RequisicaoServiceTest {

    @Mock
    private RequisicaoRepository requisicaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AppSuportadoRepository appSuportadoRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private FindBestAppService findBestAppService;

    @Mock
    private CompareRequisicaoService compareRequisicaoService;

    @Autowired
    @InjectMocks
    private RequisicaoService requisicaoService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Similiar Requisicao found")
    public void salvarRequisicaoSemelhante(){

        RequisicaoInputDTO dto = new RequisicaoInputDTO("prompt", UUID.randomUUID());

//        Usuario usuario = Mockito.mock(Usuario.class);

        Usuario usuario = new Usuario("nome", "telefone", "senha");
        Requisicao reqMatch = Mockito.mock(Requisicao.class);

        Requisicao reqTarget = new Requisicao(usuario, dto.prompt(), reqMatch.getAppSuportado());

        Requisicao reqPersistida = Mockito.mock((Requisicao.class));
//        List<AppSuportado> apps_banco = new ArrayList<AppSuportado>();

        when(usuarioRepository.findById(dto.id_usuario())
        ).thenReturn(Optional.of(usuario));

//        when(appSuportadoRepository.findAll()).thenReturn(apps_banco);

        when(compareRequisicaoService.compararRequisicoes(dto.prompt(), usuario))
                .thenReturn(Optional.of(reqMatch));

        when(requisicaoRepository.save(reqTarget))
                .thenAnswer(invocation -> invocation.getArgument(0))
                .thenReturn(reqPersistida);

        SaveRequisicaoResponseDTO response = requisicaoService.salvarRequisicao(dto);


        assertNotNull(response);
        verify(requisicaoRepository, times(1)).save(argThat(requisicao ->
                requisicao.getUsuario().getTelefone().equals(reqTarget.getUsuario().getTelefone()) &&
                requisicao.getPrompt().equals(reqTarget.getPrompt())));


    }

    @Test
    @DisplayName("Similiar Requisicao not found")
    public void salvarRequisicaoSemSemelhante(){

        RequisicaoInputDTO dto = new RequisicaoInputDTO("prompt", UUID.randomUUID());

        Usuario usuario = new Usuario("nome", "telefone", "senha");


        Requisicao reqPersistida = Mockito.mock((Requisicao.class));
        List<AppSuportado> apps_banco = new ArrayList<AppSuportado>();
        apps_banco.add(new AppSuportado());

        when(usuarioRepository.findById(dto.id_usuario())
        ).thenReturn(Optional.of(usuario));

        when(appSuportadoRepository.findAll()).thenReturn(apps_banco);

        when(compareRequisicaoService.compararRequisicoes(dto.prompt(), usuario))
                .thenReturn(Optional.empty());

        FindBestAppResponseDTO findBestAppResponseDTO = Mockito.mock(FindBestAppResponseDTO.class);
        when(findBestAppService.findBestApp(dto.prompt(), apps_banco)).thenReturn(findBestAppResponseDTO);

        when(appSuportadoRepository.findById(findBestAppResponseDTO.id_app_banco())).thenReturn(Optional.of(apps_banco.getFirst()));

        Requisicao reqTarget = new Requisicao(usuario, dto.prompt(), apps_banco.getFirst());

        when(requisicaoRepository.save(reqTarget))
                .thenAnswer(invocation -> invocation.getArgument(0))
                .thenReturn(reqPersistida);

        SaveRequisicaoResponseDTO response = requisicaoService.salvarRequisicao(dto);

        assertNotNull(response);
        verify(requisicaoRepository, times(1)).save(argThat(requisicao ->
                requisicao.getUsuario().getTelefone().equals(reqTarget.getUsuario().getTelefone()) &&
                        requisicao.getPrompt().equals(reqTarget.getPrompt())));


    }

    @Test
    @DisplayName("Similiar Requisicao not found, but with runtimeException")
    public void salvarRequisicaoSemSemelhanteRunTimeException(){

        RequisicaoInputDTO dto = new RequisicaoInputDTO("prompt", UUID.randomUUID());

        Usuario usuario = new Usuario("nome", "telefone", "senha");

        List<AppSuportado> apps_banco = new ArrayList<AppSuportado>();
        apps_banco.add(new AppSuportado());

        when(usuarioRepository.findById(dto.id_usuario())
        ).thenReturn(Optional.of(usuario));

        when(appSuportadoRepository.findAll()).thenReturn(apps_banco);

        when(compareRequisicaoService.compararRequisicoes(dto.prompt(), usuario))
                .thenReturn(Optional.empty());

        FindBestAppResponseDTO findBestAppResponseDTO = Mockito.mock(FindBestAppResponseDTO.class);
        when(findBestAppService.findBestApp(dto.prompt(), apps_banco)).thenReturn(findBestAppResponseDTO);

        when(appSuportadoRepository.findById(findBestAppResponseDTO.id_app_banco())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> requisicaoService.salvarRequisicao(dto));

    }


}
