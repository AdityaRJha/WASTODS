package com.solevictus.adityaroshanjha.service.impl;


import com.solevictus.adityaroshanjha.utils.enums.EmailTemplates;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private EmailTemplates emailTemplates;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String name) throws MessagingException {
        // Create and send the email using mailSender
        // (Implementation of email sending is omitted for brevity)
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        String subject = "Welcome to adityaroshanjha platform!";
        String body = EmailTemplates.WELCOME.getBody(name);
        helper.setSubject(subject);
        helper.setText(body);
        mailSender.send(message);
    }

    public void sendAccountVerificationOTP(String toEmail, String otp) throws MessagingException {
        //SimpleMailMessage message = new SimpleMailMessage();
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        String subject = "Request : Please verify your account";
        String body = EmailTemplates.ACCOUNT_VERIFICATION_OTP.getBody(otp);
        helper.setSubject(subject);
        helper.setText(body);
        mailSender.send(message);
    }

    public void sendResetOtpEmail(String toEmail, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        String subject = "Password Reset OTP";
        String body = EmailTemplates.RESET_PASSWORD_OTP.getBody(otp);
        helper.setSubject(subject);
        helper.setText(body);
        mailSender.send(message);
    }

    public void sendVerificationSuccessEmail(String email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(email);
        String subject = "Your account has been verified successfully!";
        String body = EmailTemplates.VERIFICATION_SUCCESS.getBody("");
        helper.setSubject(subject);
        helper.setText(body);
        mailSender.send(message);
    }
}
