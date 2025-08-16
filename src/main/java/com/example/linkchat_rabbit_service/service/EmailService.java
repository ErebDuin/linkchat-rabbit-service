package com.example.linkchat_rabbit_service.service;

import com.example.linkchat_rabbit_service.dto.InviteEmailPayload;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private final JavaMailSender emailSender;

    @Autowired
    private final TemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.from-email}")
    private String fromEmail;

    @Value("${app.emailSubject}")
    private String emailSubject;

    public EmailService(JavaMailSender emailSender, TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

//    public void sendInviteEmail(InviteEmailPayload payload) {
//        for (String recipient : payload.getRecipients()) {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(recipient);
//            message.setSubject(payload.getChatSubject());
//            message.setText("You’re invited to join chat: " + payload.getChatTitle() +
//                    "\nLink: " + payload.getChatLink());
//            emailSender.send(message);
//            System.out.println("✅ Email sent to: " + recipient);
//        }
//    }
//}


    public void sendInviteEmail(InviteEmailPayload payload) throws MessagingException, IOException {
        try {
            Context context = new Context();
            baseUrl = this.baseUrl;
            fromEmail = this.fromEmail;
            emailSubject = this.emailSubject;
            context.setVariable("title", payload.getChatTitle());
            context.setVariable("chatLink", baseUrl + payload.getChatLink());


            String html = templateEngine.process("invite-email-template", context);
            String[] recipients = payload.getRecipients().toArray(new String[0]);

            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeHelper.setFrom(fromEmail);
            mimeHelper.setTo(recipients);
            mimeHelper.setSubject(emailSubject);
            mimeHelper.setText(html, true);

            MimeMultipart multipart = getMimeMultipart(html);
            mimeMessage.setContent(multipart);

            emailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("::Failed to send invite emails: " + e.getMessage());
        }
    }

    private static MimeMultipart getMimeMultipart(String html) throws MessagingException, IOException {
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(html, "text/html; charset=UTF-8");

        MimeBodyPart imagePart = new MimeBodyPart();
        imagePart.attachFile(new ClassPathResource("static/images/linkchat-logo-150x150.png").getFile());
        imagePart.setContentID("<linkchatLogo>");
        imagePart.setDisposition(MimeBodyPart.INLINE);

        MimeMultipart multipart = new MimeMultipart("related");
        multipart.addBodyPart(htmlPart);
        multipart.addBodyPart(imagePart);
        return multipart;
    }
}
