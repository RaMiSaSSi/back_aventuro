// java
package com.example.demo.Service.Auth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final Logger log = LoggerFactory.getLogger(EmailService.class);

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String subject, String verificationCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Vérification de votre adresse e-mail - Amatun Shop");

        String htmlContent = "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>"
                + "<div style='background: linear-gradient(135deg, #FF6F00 0%, #FF8F00 100%); padding: 30px; text-align: center;'>"
                + "<h1 style='color: #ffffff; margin: 0; font-size: 28px; font-weight: bold; text-shadow: 2px 2px 4px rgba(0,0,0,0.3);'>AMATUN SHOP</h1>"
                + "<p style='color: #ffffff; margin: 10px 0 0 0; font-size: 16px; opacity: 0.9;'>Bienvenue dans votre expérience d'achat</p>"
                + "</div>"
                + "<div style='padding: 40px 30px; background-color: #ffffff;'>"
                + "<div style='text-align: center; margin-bottom: 30px;'>"
                + "<div style='width: 80px; height: 80px; background: linear-gradient(135deg, #FF6F00, #FF8F00); border-radius: 50%; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center;'>"
                + "<span style='color: white; font-size: 30px;'>📧</span>"
                + "</div>"
                + "<h2 style='color: #333333; margin: 0; font-size: 24px;'>Vérification de votre e-mail</h2>"
                + "</div>"
                + "<p style='color: #555555; font-size: 16px; line-height: 1.6; margin-bottom: 25px;'>Merci de rejoindre <strong>Amatun Shop</strong> ! Pour terminer votre inscription et commencer vos achats, veuillez utiliser le code de vérification ci-dessous :</p>"
                + "<div style='text-align: center; margin: 30px 0; padding: 20px; background: linear-gradient(135deg, #333333, #1a1a1a); border-radius: 10px;'>"
                + "<p style='color: #FF6F00; font-size: 14px; margin: 0 0 10px 0; font-weight: bold;'>VOTRE CODE DE VÉRIFICATION</p>"
                + "<div style='background-color: #ffffff; color: #333333; padding: 15px 25px; border-radius: 8px; font-size: 32px; font-weight: bold; letter-spacing: 8px; font-family: monospace; border: 3px solid #FF6F00; margin: 10px auto; display: inline-block;'>" + verificationCode + "</div>"
                + "</div>"
                + "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 8px; border-left: 4px solid #FF6F00; margin: 25px 0;'>"
                + "<p style='color: #666666; font-size: 14px; margin: 0; line-height: 1.5;'><strong>Remarque :</strong> Ce code de vérification expirera dans 24 heures pour des raisons de sécurité. Si vous n'avez pas créé de compte avec Amatun Shop, veuillez ignorer cet e-mail.</p>"
                + "</div>"
                + "<p style='color: #555555; font-size: 16px; line-height: 1.6; text-align: center;'>Saisissez ce code dans l'application pour continuer votre inscription.</p>"
                + "</div>"
                + "<div style='background-color: #333333; padding: 25px; text-align: center;'>"
                + "<p style='color: #ffffff; margin: 0 0 10px 0; font-size: 18px; font-weight: bold;'>AMATUN SHOP</p>"
                + "<p style='color: #cccccc; margin: 0; font-size: 14px;'>Votre destination d'achat de confiance</p>"
                + "<div style='margin-top: 15px; padding-top: 15px; border-top: 1px solid #555555;'>"
                + "<p style='color: #999999; margin: 0; font-size: 12px;'>© 2025 Amatun Shop. Tous droits réservés.</p>"
                + "</div>"
                + "</div>"
                + "</div>";

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
    public void sendResetPasswordEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Réinitialisation du mot de passe - Amatun Shop");

            String resetLink = "https://your-frontend.example.com/reset-password?token="
                    + token + "&email=" + java.net.URLEncoder.encode(toEmail, java.nio.charset.StandardCharsets.UTF_8.name());

            String htmlContent = "<div style='max-width:600px;margin:0 auto;font-family:Arial,sans-serif;'>"
                    + "<div style='background:#FF6F00;padding:24px;border-radius:8px;color:#fff;text-align:center;'>"
                    + "<h2 style='margin:0;'>Réinitialisation du mot de passe</h2>"
                    + "</div>"
                    + "<div style='padding:24px;background:#fff;border-radius:0 0 8px 8px;color:#333;'>"
                    + "<p>Vous avez demandé la réinitialisation de votre mot de passe. Cliquez sur le bouton ci-dessous pour définir un nouveau mot de passe. Ce lien expire dans 15 minutes.</p>"
                    + "<p style='text-align:center;margin:24px 0;'><a href='" + resetLink + "' style='display:inline-block;padding:12px 20px;background:#FF6F00;color:#fff;text-decoration:none;border-radius:6px;'>Réinitialiser mon mot de passe</a></p>"
                    + "<p style='font-size:13px;color:#666;'>Si le bouton ne fonctionne pas, copiez-collez ce token dans l'application :</p>"
                    + "<div style='background:#f4f4f4;padding:12px;border-radius:6px;font-family:monospace;word-break:break-all;'>" + token + "</div>"
                    + "<p style='font-size:12px;color:#999;margin-top:18px;'>Si vous n'avez pas demandé cette opération, ignorez ce message.</p>"
                    + "</div>"
                    + "<div style='text-align:center;padding:12px;color:#999;font-size:12px;'>© 2025 Amatun Shop</div>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send reset password email to {}", toEmail, e);
        }
    }
    
}