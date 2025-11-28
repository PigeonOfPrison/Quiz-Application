package com.Dolkara.notification_service.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTests {

    private final EmailService emailService;

    @Autowired
    public EmailServiceTests(EmailService emailService) {
        this.emailService = emailService;
    }

    @Test
    void testSendEmail() {

        String body = """
                Dear Hammad,
                
                I am Hammad too, from the past, when I was creating this mail service.
                I hope you are doing well.
                and that wherever you are, and whatever you are doing, you are happy and at ease.
                
                The main reason that i am writing all of this is to test this email service, 
                but i do hope that one day when you revisit this service, you read this and it bring joyful
                memories to you.
                
                Around this time, the HLS Asia came for recruitment and took I student at 7k per month internship
                Also, sessionals are going to start from next week and I am not as well prepared as i though i could be
                """;
        emailService.sendEmail("noormhammad04@gmail.com", "Hammad's email service test", body);
    }
}
