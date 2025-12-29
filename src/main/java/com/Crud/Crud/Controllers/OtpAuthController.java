package com.Crud.Crud.Controllers;

import com.Crud.Crud.Dtos.OtpRequest;
import com.Crud.Crud.Dtos.OtpVerifyRequest;
import com.Crud.Crud.Entity.Refreshtoken;
import com.Crud.Crud.Entity.User;
import com.Crud.Crud.Repository.RefreshtokenRepo;
import com.Crud.Crud.Repository.Userrepo;
import com.Crud.Crud.Security.Jwtservice;
import com.Crud.Crud.Service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/otp")
@RequiredArgsConstructor
public class OtpAuthController {

    private final OtpService otpService;
    private final Userrepo userRepository;
    private final Jwtservice jwtService;
    private final RefreshtokenRepo refreshtokenRepo;

    // 1️⃣ Send OTP
    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BadCredentialsException("Invalid email or Email not Registered"));
        otpService.generateAndSendOtp(request.getEmail());
        return ResponseEntity.ok("OTP sent to email");
    }

    // 2️⃣ Verify OTP & Login
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BadCredentialsException("Invalid email or Email not Found"));

        otpService.validateOtp(request.getEmail(), request.getOtp());

        //for genration of refresh token jti is needed which is to be stored in database
        String jti = UUID.randomUUID().toString();
        var refreshtokenob = Refreshtoken.builder()
                .jti(jti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();

//        Saving information of refresh token in database
        Refreshtoken save = refreshtokenRepo.save(refreshtokenob);


        String accesstoken = jwtService.generateAccessToken(user);
        String refreshtoken = jwtService.generateRefreshToken(user, refreshtokenob.getJti());


        return ResponseEntity.ok(
                Map.of(
                        "accessToken", accesstoken,
                        "refreshToken", refreshtoken,
                        "tokenType", "Bearer"
                )
        );
    }
}
