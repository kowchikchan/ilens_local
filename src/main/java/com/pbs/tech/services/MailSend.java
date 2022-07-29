package com.pbs.tech.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.Properties;

public class MailSend {
    private static final Logger log = LoggerFactory.getLogger(MailSend.class);

    public void mailSend(String host, String port, String userName, String password, String toMail, String subject,
                         String msgContent, String filePath) throws AddressException {

        String[] recipientList = toMail.split(",");
        InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
        for (int i=0; i<recipientList.length;i++){
            recipientAddress[i] = new InternetAddress(recipientList[i]);
        }

        // set properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Authentication.
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });

        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(userName));
            msg.setRecipients(Message.RecipientType.TO, recipientAddress);
            msg.setSubject(subject);

            Multipart emailContent = new MimeMultipart();

            //Text body part
            MimeBodyPart textBodyPart = new MimeBodyPart();

            textBodyPart.setContent(msgContent, "text/html; charset=utf-8");


            //Attachment body part.
            MimeBodyPart pdfAttachment = new MimeBodyPart();
            pdfAttachment.attachFile(filePath);
            emailContent.addBodyPart(textBodyPart);
            emailContent.addBodyPart(pdfAttachment);

            //Attach multipart to message
            msg.setContent(emailContent);

            Transport.send(msg);
            log.info("e-mail report sent successfully.");

        }catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

    }
}
