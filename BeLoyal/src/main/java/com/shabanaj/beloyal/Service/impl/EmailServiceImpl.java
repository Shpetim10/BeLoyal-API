package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.activation.base-url}")
    private String activationBaseUrl;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }


    @Override
    public void sendActivationEmail(User user, String token) {
        String activationLink = activationBaseUrl + "?token=" + token;

        String subject = "Activate your account";
        String content = buildActivationEmailHtml(user.getFirstName(), activationLink);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(content, true); // true = HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildActivationEmailHtml(String name, String activationLink) {
        return """
        <html>
          <body>
            <h2>Hello %s,</h2>
            <p>Thanks for registering. Please activate your account by clicking the button below:</p>
            <p>
              <a href="%s" 
                 style="display:inline-block;padding:10px 20px;background-color:#4CAF50;color:white;text-decoration:none;border-radius:4px;">
                 Activate Account
              </a>
            </p>
            <p>If the button doesn't work, copy and paste this link into your browser:</p>
            <p>%s</p>
          </body>
        </html>
        """.formatted(name, activationLink, activationLink);
    }
}
