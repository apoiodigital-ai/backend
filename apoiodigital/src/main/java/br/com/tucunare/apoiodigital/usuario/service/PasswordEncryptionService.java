package br.com.tucunare.apoiodigital.usuario.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncryptionService {
    private final PasswordEncoder passwordEncoder;

    public PasswordEncryptionService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String criptografar(String senha) {
        return passwordEncoder.encode(senha);
    }

    public boolean validar(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
