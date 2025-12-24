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

        helper.setFrom("contact@aventuroo.vip");
        helper.setTo(toEmail);
        helper.setSubject("Vérification de votre adresse e-mail - Aventuroo");

        String htmlContent = "<div style=\"font-family: Inter, system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial; background:#f4f5f7; padding:24px;\">"
                + "<div style=\"max-width:680px; margin:0 auto; border-radius:12px; overflow:hidden; box-shadow:0 8px 30px rgba(15,23,42,0.08); background:#ffffff;\">"
                + "  <header style=\"background:linear-gradient(90deg,#D4A017 0%,#C98900 100%); padding:24px; text-align:center;\">"
                + "    <h1 style=\"margin:0; color:#fff; font-size:20px; font-weight:700; letter-spacing:0.2px;\">Aventuroo</h1>"
                + "    <p style=\"margin:6px 0 0; color:rgba(255,255,255,0.95); font-size:13px;\">Votre prochaine aventure commence ici</p>"
                + "  </header>"
                + ""
                + "  <main style=\"padding:28px 32px; color:#1f2937;\">"
                + "    <div style=\"text-align:center; margin-bottom:18px;\">"
                + "      <h2 style=\"margin:0; font-size:18px; font-weight:700;\">Vérification de votre adresse e‑mail</h2>"
                + "      <p style=\"margin:8px 0 0; color:#4b5563; font-size:14px;\">Merci d'avoir rejoint Aventuroo — saisissez le code ci‑dessous pour valider votre compte.</p>"
                + "    </div>"
                + ""
                + "    <div style=\"display:flex; justify-content:center; margin:20px 0;\">"
                + "      <div style=\"background:#f9fafb; border:1px solid rgba(0,0,0,0.04); padding:18px 26px; border-radius:10px; text-align:center;\">"
                + "        <div style=\"font-size:12px; color:#b45309; font-weight:700; letter-spacing:1px;\">VOTRE CODE DE VÉRIFICATION</div>"
                + "        <div style=\"margin-top:12px; background:#0f172a; color:#fff; padding:14px 22px; border-radius:8px; font-size:28px; font-weight:800; letter-spacing:6px; font-family: monospace;\">"
                +           verificationCode
                + "        </div>"
                + "      </div>"
                + "    </div>"
                + ""
                + "    <p style=\"color:#4b5563; font-size:14px; line-height:1.6;\">Ce code expirera dans 24 heures pour des raisons de sécurité. Si vous n'avez pas demandé cette vérification, ignorez simplement ce message.</p>"
                + ""

                + "  </main>"
                + ""
                + "  <footer style=\"background:#0b1220; color:#9ca3af; padding:18px 24px; text-align:center; font-size:12px;\">"
                + "    <div style=\"margin-bottom:8px; color:#f3f4f6; font-weight:600;\">Aventuroo</div>"
                + "    <div>© " + java.time.Year.now() + " Aventuroo. Tous droits réservés.</div>"
                + "    <div style=\"margin-top:8px;\">"
                + "    </div>"
                + "  </footer>"
                + "</div>"
                + "</div>";

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
    public void sendResetPasswordEmail(String toEmail, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("contact@aventuroo.vip");
            helper.setTo(toEmail);
            helper.setSubject("Réinitialisation du mot de passe - Aventuroo");

            String htmlContent = "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>"
                    + "<div style='background: linear-gradient(135deg, #FF6F00 0%, #FF8F00 100%); padding: 30px; text-align: center;'>"
                    + "<h1 style='color: #ffffff; margin: 0; font-size: 28px; font-weight: bold; text-shadow: 2px 2px 4px rgba(0,0,0,0.3);'>Aventuroo</h1>"
                    + "<p style='color: #ffffff; margin: 10px 0 0 0; font-size: 16px; opacity: 0.9;'>Bienvenue dans votre expérience d'achat</p>"
                    + "</div>"
                    + "<div style='padding: 40px 30px; background-color: #ffffff;'>"
                    + "<div style='text-align: center; margin-bottom: 30px;'>"
                    + "<div style='width: 80px; height: 80px; background: linear-gradient(135deg, #FF6F00, #FF8F00); border-radius: 50%; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center;'>"
                    + "<span style='color: white; font-size: 30px;'>🔑</span>"
                    + "</div>"
                    + "<h2 style='color: #333333; margin: 0; font-size: 24px;'>Réinitialisation de votre mot de passe</h2>"
                    + "</div>"
                    + "<p style='color: #555555; font-size: 16px; line-height: 1.6; margin-bottom: 25px;'>Vous avez demandé la réinitialisation de votre mot de passe. Voici votre nouveau mot de passe :</p>"
                    + "<div style='text-align: center; margin: 30px 0; padding: 20px; background: linear-gradient(135deg, #333333, #1a1a1a); border-radius: 10px;'>"
                    + "<p style='color: #FF6F00; font-size: 14px; margin: 0 0 10px 0; font-weight: bold;'>VOTRE NOUVEAU MOT DE PASSE</p>"
                    + "<div style='background-color: #ffffff; color: #333333; padding: 15px 25px; border-radius: 8px; font-size: 24px; font-weight: bold; letter-spacing: 2px; font-family: monospace; border: 3px solid #FF6F00; margin: 10px auto; display: inline-block;'>" + password + "</div>"
                    + "</div>"
                    + "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 8px; border-left: 4px solid #FF6F00; margin: 25px 0;'>"
                    + "<p style='color: #666666; font-size: 14px; margin: 0; line-height: 1.5;'><strong>Remarque :</strong> Pour des raisons de sécurité, changez ce mot de passe dès que possible après vous être connecté. Si vous n'avez pas demandé cette opération, contactez notre support immédiatement.</p>"
                    + "</div>"
                    + "<p style='color: #555555; font-size: 16px; line-height: 1.6; text-align: center;'>Utilisez ce mot de passe pour vous connecter à votre compte.</p>"
                    + "</div>"
                    + "<div style='background-color: #333333; padding: 25px; text-align: center;'>"
                    + "<p style='color: #ffffff; margin: 0 0 10px 0; font-size: 18px; font-weight: bold;'>Aventuroo</p>"
                    + "<p style='color: #cccccc; margin: 0; font-size: 14px;'>Votre destination d'achat de confiance</p>"
                    + "<div style='margin-top: 15px; padding-top: 15px; border-top: 1px solid #555555;'>"
                    + "<p style='color: #999999; margin: 0; font-size: 12px;'>© 2025 Aventuroo. Tous droits réservés.</p>"
                    + "</div>"
                    + "</div>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send reset password email to {}", toEmail, e);
        }
    }
    // java
    public void sendValidationEmail(String toEmail, String clientName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("contact@aventuroo.vip");
        helper.setTo(toEmail);
        helper.setSubject("Validation de votre demande de location - Aventuroo");

        String htmlContent = "<div style=\"font-family: Inter, system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial; background:#f4f5f7; padding:24px;\">"
                + "<div style=\"max-width:680px; margin:0 auto; border-radius:12px; overflow:hidden; box-shadow:0 8px 30px rgba(15,23,42,0.08); background:#ffffff;\">"
                + "  <header style=\"background:linear-gradient(90deg,#D4A017 0%,#C98900 100%); padding:24px; text-align:center;\">"
                + "    <h1 style=\"margin:0; color:#fff; font-size:20px; font-weight:700; letter-spacing:0.2px;\">Aventuroo</h1>"
                + "    <p style=\"margin:6px 0 0; color:rgba(255,255,255,0.95); font-size:13px;\">Votre prochaine aventure commence ici</p>"
                + "  </header>"
                + "  <main style=\"padding:28px 32px; color:#1f2937;\">"
                + "    <div style=\"text-align:center; margin-bottom:18px;\">"
                + "      <h2 style=\"margin:0; font-size:18px; font-weight:700;\">Demande de location validée</h2>"
                + "      <p style=\"margin:8px 0 0; color:#4b5563; font-size:14px;\">Bonjour " + clientName + ", votre demande de location a été validée avec succès.</p>"
                + "    </div>"
                + "    <p style=\"color:#4b5563; font-size:14px; line-height:1.6;\">Vous pouvez maintenant procéder à la récupération de votre véhicule selon les détails fournis. Si vous avez des questions, contactez-nous.</p>"
                + "  </main>"
                + "  <footer style=\"background:#0b1220; color:#9ca3af; padding:18px 24px; text-align:center; font-size:12px;\">"
                + "    <div style=\"margin-bottom:8px; color:#f3f4f6; font-weight:600;\">Aventuroo</div>"
                + "    <div>© " + java.time.Year.now() + " Aventuroo. Tous droits réservés.</div>"
                + "  </footer>"
                + "</div>"
                + "</div>";

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    public void sendRefusalEmail(String toEmail, String clientName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("contact@aventuroo.vip");
        helper.setTo(toEmail);
        helper.setSubject("Refus de votre demande de location - Aventuroo");

        String htmlContent = "<div style=\"font-family: Inter, system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial; background:#f4f5f7; padding:24px;\">"
                + "<div style=\"max-width:680px; margin:0 auto; border-radius:12px; overflow:hidden; box-shadow:0 8px 30px rgba(15,23,42,0.08); background:#ffffff;\">"
                + "  <header style=\"background:linear-gradient(90deg,#D4A017 0%,#C98900 100%); padding:24px; text-align:center;\">"
                + "    <h1 style=\"margin:0; color:#fff; font-size:20px; font-weight:700; letter-spacing:0.2px;\">Aventuroo</h1>"
                + "    <p style=\"margin:6px 0 0; color:rgba(255,255,255,0.95); font-size:13px;\">Votre prochaine aventure commence ici</p>"
                + "  </header>"
                + "  <main style=\"padding:28px 32px; color:#1f2937;\">"
                + "    <div style=\"text-align:center; margin-bottom:18px;\">"
                + "      <h2 style=\"margin:0; font-size:18px; font-weight:700;\">Demande de location refusée</h2>"
                + "      <p style=\"margin:8px 0 0; color:#4b5563; font-size:14px;\">Bonjour " + clientName + ", nous regrettons de vous informer que votre demande de location a été refusée.</p>"
                + "    </div>"
                + "    <p style=\"color:#4b5563; font-size:14px; line-height:1.6;\">Pour plus d'informations, veuillez nous contacter. Nous nous excusons pour tout inconvénient.</p>"
                + "  </main>"
                + "  <footer style=\"background:#0b1220; color:#9ca3af; padding:18px 24px; text-align:center; font-size:12px;\">"
                + "    <div style=\"margin-bottom:8px; color:#f3f4f6; font-weight:600;\">Aventuroo</div>"
                + "    <div>© " + java.time.Year.now() + " Aventuroo. Tous droits réservés.</div>"
                + "  </footer>"
                + "</div>"
                + "</div>";

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }


}