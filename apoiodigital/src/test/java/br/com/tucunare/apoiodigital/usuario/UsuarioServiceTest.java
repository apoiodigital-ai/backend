package br.com.tucunare.apoiodigital.usuario;

import br.com.tucunare.apoiodigital.auth.data.RefreshToken;
import br.com.tucunare.apoiodigital.auth.service.JwtService;
import br.com.tucunare.apoiodigital.auth.service.RefreshTokenService;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.exception.InvalidPasswordLengthException;
import br.com.tucunare.apoiodigital.usuario.exception.TelefoneAlreayExistsException;
import br.com.tucunare.apoiodigital.usuario.exception.UsuarioDoesNotExistException;
import br.com.tucunare.apoiodigital.usuario.repository.UsuarioRepository;
import br.com.tucunare.apoiodigital.usuario.service.PasswordEncryptionService;
import br.com.tucunare.apoiodigital.usuario.service.PasswordValidationService;
import br.com.tucunare.apoiodigital.usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncryptionService passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    PasswordValidationService passWordValidationService;

    private Usuario usuario = new Usuario("Paulo", "123456789", "123456");

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Autowired
    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Success case")
    public void salvarUsuario(){

        when(usuarioRepository.findByTelefone(usuario.getTelefone())).thenReturn(Optional.empty());
        when(passwordEncoder.criptografar(usuario.getSenha())).thenReturn("senhaCriptografada");
        when(passWordValidationService.validar(usuario.getSenha())).thenReturn(true);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);

        assertNotNull(usuarioSalvo);
        assertEquals("senhaCriptografada", usuarioSalvo.getSenha());
        verify(usuarioRepository, times(1)).save(usuario);

    }

    @Test
    @DisplayName("TelefoneAlreadyExisitsException")
    public void salvarUsuarioTelefoneException(){
        Usuario usuarioFound = new Usuario("nome1", usuario.getTelefone(), "senha");

        when(usuarioRepository.findByTelefone(usuario.getTelefone())).thenReturn(Optional.of(usuarioFound));
        assertThrows(TelefoneAlreayExistsException.class,
                () -> usuarioService.salvarUsuario(usuario));

    }

    @Test
    @DisplayName("InvalidPasswordLengthException")
    public void salvarUsuarioInvalidPasswordException(){

        when(usuarioRepository.findByTelefone(usuario.getTelefone())).thenReturn(Optional.empty());
        when(passWordValidationService.validar(usuario.getSenha())).thenReturn(false);

        assertThrows(InvalidPasswordLengthException.class,
                () -> usuarioService.salvarUsuario(usuario));

    }

    @Test
    @DisplayName("Validar senha success case")
    public void validarSenha(){
        String rawPassword = "rawPassword";
        when(usuarioRepository.findByTelefone(usuario.getTelefone())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.validar(rawPassword, usuario.getSenha())).thenReturn(true);

        String accessToken = "token-valido";
        when(jwtService.gerarToken(usuario)).thenReturn(accessToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-valido");

        when(refreshTokenService.createRefreshToken(usuario)).thenReturn(refreshToken);

        Map<String, String> response = usuarioService.validarLogin(usuario.getTelefone(), rawPassword);

        assertNotNull(response);
        assertEquals(2, response.size());

        assertTrue(response.containsKey("accessToken"));
        assertTrue(response.containsKey("refreshToken"));

        assertEquals("token-valido", response.get("accessToken"));
        assertEquals("refresh-valido", response.get("refreshToken"));

    }


    @Test
    @DisplayName("Validar senha UsuarioDoesNotExistException case in Password")
    public void validarSenhaUsuarioDoesNotExistExceptionInPassword(){
        String rawPassword = "rawPassword";
        when(usuarioRepository.findByTelefone(usuario.getTelefone())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.validar(rawPassword, usuario.getSenha())).thenReturn(false);

        assertThrows(UsuarioDoesNotExistException.class,
        () -> usuarioService.validarLogin(usuario.getTelefone(), rawPassword));

    }

    @Test
    @DisplayName("Validar senha UsuarioDoesNotExistException case in Phone")
    public void validarSenhaUsuarioDoesNotExistExceptionInPhone(){
        String rawPassword = "rawPassword";
        when(usuarioRepository.findByTelefone(usuario.getTelefone())).thenReturn(Optional.empty());

        assertThrows(UsuarioDoesNotExistException.class,
                () -> usuarioService.validarLogin(usuario.getTelefone(), rawPassword));

    }
}
