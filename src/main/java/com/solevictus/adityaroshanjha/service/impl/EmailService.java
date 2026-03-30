package com.solevictus.adityaroshanjha.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String name) {
        // Create and send the email using mailSender
        // (Implementation of email sending is omitted for brevity)
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        String subject = "Welcome to adityaroshanjha platform!";
        String body = "Hola! " + name + ",\n\n" +
                "Thank you for registering with us! We're excited to have you on board.\n\n" +
                "Best regards,\n" +
                "The Team";
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendResetOtpEmail(String toEmail, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        String subject = "Password Reset OTP";
        String body = "Hola!\n\n" +
                "Your OTP for password reset is: " + otp + "\n\n" +
                "This OTP is valid for 5 minutes.\n\n" +
                "Best regards,\n" +
                "The Team";
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
