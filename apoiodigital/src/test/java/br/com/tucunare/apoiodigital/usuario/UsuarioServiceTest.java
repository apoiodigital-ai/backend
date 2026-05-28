package br.com.tucunare.apoiodigital.usuario;

import br.com.tucunare.apoiodigital.auth.service.JwtService;
import br.com.tucunare.apoiodigital.auth.service.RefreshTokenService;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.repository.UsuarioRepository;
import br.com.tucunare.apoiodigital.usuario.service.PasswordEncryptionService;
import br.com.tucunare.apoiodigital.usuario.service.PasswordValidationService;
import br.com.tucunare.apoiodigital.usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}
