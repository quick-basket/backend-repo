package com.grocery.quickbasket.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String verificationLink) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom("noreply@quickbasket.com");
            helper.setTo(to);
            helper.setSubject("Email Verification Quick-Basket");

            String emailContent = buildVerificationEmailContent(verificationLink);
            helper.setText(emailContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    private String buildVerificationEmailContent(String verificationLink) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "  .container {" +
                "    max-width: 600px;" +
                "    margin: 0 auto;" +
                "    padding: 20px;" +
                "    font-family: Arial, sans-serif;" +
                "    background-color: #f9f9f9;" +
                "    border: 1px solid #ddd;" +
                "    border-radius: 10px;" +
                "  }" +
                "  .header {" +
                "    text-align: center;" +
                "    padding: 10px 0;" +
                "    background-color: #007bff;" +
                "    color: #fff;" +
                "    border-radius: 10px 10px 0 0;" +
                "  }" +
                "  .content {" +
                "    padding: 20px;" +
                "    font-size: 16px;" +
                "    color: #333;" +
                "  }" +
                "  .content a {" +
                "    color: #007bff;" +
                "    text-decoration: none;" +
                "  }" +
                "  .footer {" +
                "    text-align: center;" +
                "    font-size: 12px;" +
                "    color: #777;" +
                "    padding: 10px 0;" +
                "    border-top: 1px solid #ddd;" +
                "  }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "  <div class=\"header\">" +
                "    <h1>Quick-Basket Email Verification</h1>" +
                "  </div>" +
                "  <div class=\"content\">" +
                "    <p>Hello,</p>" +
                "    <p>Thank you for registering with Quick-Basket! Please click the link below to verify your email address:</p>" +
                "    <p><a href=\"" + verificationLink + "\">Verify Your Email</a></p>" +
                "    <p>If you did not register for this account, please ignore this email.</p>" +
                "    <p>Best regards,<br>Quick-Basket Team</p>" +
                "  </div>" +
                "  <div class=\"footer\">" +
                "    <p>&copy; 2024 Quick-Basket. All rights reserved.</p>" +
                "  </div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
