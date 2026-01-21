package com.Crud.Crud.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PromoMailService {
    private final EmailService emailService;

    //
//    @Scheduled(cron = "0 */5 * * * *") Use Your Cron Expression according to you
    public void sendPromotionalEmail() {

        String to = "receiver@gmail.com"; // test email you can get it by userrepo also
        String subject = "🔥 Special Offer from Our Store!";
        String body = "Hello!\n\nDon't miss our latest offers.\n\n- Team E-Commerce";

        emailService.sendEmail(to, subject, body);

        System.out.println("Promotional email sent at " + LocalDateTime.now());
    }
}
