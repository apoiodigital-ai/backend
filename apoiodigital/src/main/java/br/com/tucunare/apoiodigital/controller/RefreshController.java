package br.com.tucunare.apoiodigital.controller;

import br.com.tucunare.apoiodigital.service.impl.RefreshTokenService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/refresh")
public class RefreshController {

    private final RefreshTokenService refreshTokenService;

    public RefreshController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }
}
