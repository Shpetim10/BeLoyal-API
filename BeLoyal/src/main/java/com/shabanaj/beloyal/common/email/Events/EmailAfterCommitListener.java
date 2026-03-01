package com.shabanaj.beloyal.common.email.Events;

import com.shabanaj.beloyal.common.email.dto.SendEmailEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class EmailAfterCommitListener {

    private final JavaMailSender mailSender;

    public EmailAfterCommitListener(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSendEmail(SendEmailEvent event) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(event.to());
            helper.setSubject(event.subject());
            helper.setText(event.bodyHtml(), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
