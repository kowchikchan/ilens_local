package com.pbs.tech.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.Properties;

public abstract class MailSend {
    private static final Logger log = LoggerFactory.getLogger(MailSend.class);

    public static void mailSend(boolean tls, boolean ssl, String host, String port, String userName, String password,
                                String toMail, String subject, String msgContent, String filePath) throws Exception {

        // multiple to mail address.
        String[] recipientList = toMail.split(",");
        InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
        for (int i=0; i<recipientList.length;i++){
            recipientAddress[i] = new InternetAddress(recipientList[i]);
        }

        // set properties
        Properties properties = null;
        properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.debug", "true");
        if(tls) {
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        } else if (ssl) {
            properties.put("mail.smtp.socketFactory.port", port);
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        if(filePath == null){
            properties.put("mail.smtp.connectiontimeout", "30000");
            properties.put("mail.smtp.timeout", "30000");
        }

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
            MimeBodyPart textBodyPart = new MimeBodyPart();
            MimeBodyPart pdfAttachment = new MimeBodyPart();

            // Attachment body part. If file path null, no attachment.
            if(filePath != null) {
                pdfAttachment.attachFile(filePath);
                emailContent.addBodyPart(pdfAttachment);
            }
            textBodyPart.setContent(msgContent, "text/html; charset=utf-8");
            emailContent.addBodyPart(textBodyPart);

            //Attach multipart to message
            msg.setContent(emailContent);

            Transport.send(msg);
            log.info("e-mail report sent successfully.");

        }catch (MessagingException | IOException e) {
            throw new Exception("Exception" + e.getMessage());
        }

    }
}
