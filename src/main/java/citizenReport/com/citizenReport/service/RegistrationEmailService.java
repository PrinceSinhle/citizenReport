package citizenReport.com.citizenReport.service;

import citizenReport.com.citizenReport.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class RegistrationEmailService {
    private static final Logger log = LoggerFactory.getLogger(RegistrationEmailService.class);

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    public RegistrationEmailService(JavaMailSender mailSender, MailProperties mailProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    public void sendWelcomeEmail(User user) {
        String sender = mailProperties.getUsername();
        if (mailProperties.getHost() == null || mailProperties.getHost().isBlank()
                || sender == null || sender.isBlank()) {
            log.warn("Registration email skipped because MAIL_HOST or MAIL_USERNAME is not configured.");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(user.getEmail());
        message.setSubject("Welcome to Civic Report");
        message.setText("Hello " + user.getFullName() + ",\n\n"
                + "Your Civic Report account has been created. You can now sign in to report local issues "
                + "and follow their progress.\n\n"
                + "Thank you,\nCivic Report");

        try {
            mailSender.send(message);
        } catch (MailException exception) {
            // Do not fail account creation if the email provider is temporarily unavailable.
            log.warn("Registration email delivery failed; the account was still created.");
        }
    }
}
