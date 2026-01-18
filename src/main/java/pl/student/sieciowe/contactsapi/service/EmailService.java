package pl.student.sieciowe.contactsapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@contacts-api.local}")
    private String fromEmail;

    /**
     * SMTP - Wysyła email przez skonfigurowany serwer SMTP
     */
    public String sendEmail(String to, String subject, String body) {
        // Jeśli dane logowania są domyślne, przejdź w tryb dry-run (symulacja)
        if (fromEmail.equals("your-email@gmail.com")) {
            return "Email DRY-RUN SUCCESS (No email configured in application.properties). To: " + to + ", Subject: "
                    + subject;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            return "Email sent successfully to: " + to;
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("Authentication failed")) {
                return "Email sending FAILED: Authentication failed. Sprawdź e-mail i HASŁO APLIKACJI w application.properties.";
            }
            return "Email sending FAILED: " + errorMsg;
        }
    }

    /**
     * Wysyła powiadomienie o nowym kontakcie
     */
    public String sendContactNotification(String to, String contactName) {
        String subject = "Nowy kontakt dodany";
        String body = "Dodano nowy kontakt: " + contactName;
        return sendEmail(to, subject, body);
    }
}
