package br.com.tucunare.apoiodigital.controller;

import br.com.tucunare.apoiodigital.model.RefreshToken;
import br.com.tucunare.apoiodigital.service.RefreshTokenService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/refresh")
public class RefreshController {

    private final RefreshTokenService refreshTokenService;

    public RefreshController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }
}
