package com.Dolkara.notification_service.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.Dolkara.notification_service.model.Mail;
import com.Dolkara.notification_service.service.EmailService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationListener {

    private final EmailService emailService;

    @Autowired
    public NotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(
            topics = "notification",
            groupId = "notification-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    void listenString(String data) {
        log.info("Listener received the following data : {}", data);
    }

    @KafkaListener(
            topics = "mail",
            groupId = "mail-group",
            containerFactory = "mailKafkaListenerContainerFactory"
    )
    void listenMail(Mail mail) {
        log.info("Listener received the following data : {}", mail);

//        todo : Add this when you correct the password
//        try {
//            emailService.sendEmail(mail.getTo(), mail.getSubject(), mail.getBody());
//            log.info("Email send successfully to : {}", mail.getTo());
//        }
//        catch (Exception e ) {
//            log.error("Failed to send email to : {}", mail.getTo());
//        }
    }
}
