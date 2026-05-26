package br.com.tucunare.apoiodigital.auth.controller;

import br.com.tucunare.apoiodigital.auth.service.RefreshTokenService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/refresh")
public class RefreshController {

    private final RefreshTokenService refreshTokenService;

    public RefreshController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }
}
