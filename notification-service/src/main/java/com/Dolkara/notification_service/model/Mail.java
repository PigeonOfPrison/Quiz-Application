package com.Dolkara.notification_service.model;

import lombok.Data;

@Data
public class Mail {

    private String to;
    private String subject;
    private String body;
}
