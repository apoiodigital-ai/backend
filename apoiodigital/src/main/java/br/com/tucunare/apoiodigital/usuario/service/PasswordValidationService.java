package br.com.tucunare.apoiodigital.usuario.service;

import org.springframework.stereotype.Service;

@Service
public class PasswordValidationService {

    public boolean validar(String senha){
        return senha.length() > 8;
    }

}
