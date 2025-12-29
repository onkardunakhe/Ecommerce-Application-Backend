package com.Crud.Crud.Service;

import com.Crud.Crud.Entity.OtpToken;
import com.Crud.Crud.Repository.OtpTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpTokenRepository otpRepo;
    private final EmailService emailService;

    private static final int OTP_EXPIRY_MINUTES = 5;

    public void generateAndSendOtp(String email) {

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setOtp(otp);
        token.setVerified(false);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));

        otpRepo.save(token);
        emailService.sendOtp(email, otp);
    }

    public void validateOtp(String email, String otp) {

        OtpToken token = otpRepo.findByEmailAndOtpAndVerifiedFalse(email, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (token.getExpiryTime().isBefore(LocalDateTime.now()) || token.isVerified()) {
            throw new RuntimeException("OTP expired or already used");
        }

        token.setVerified(true);
        otpRepo.save(token);
    }
}
