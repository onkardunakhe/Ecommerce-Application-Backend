package com.Crud.Crud.Controllers;

import com.Crud.Crud.Service.MagicLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MagicLoginController {
    private final MagicLoginService magicLoginService;

    @PostMapping("/send-magic-link")
    public String SendMagicLinktoEmail(@RequestParam String email) {
        magicLoginService.sendMagicToken(email);
        return "Login Link Send Successfully";

    }

    @GetMapping("/magic-login")
    public ResponseEntity<?> login(@RequestParam String token) {

        String jwt = magicLoginService.LoginWithMagic(token);

        return ResponseEntity.ok(
                java.util.Map.of("accessToken", jwt)
        );
    }
}
