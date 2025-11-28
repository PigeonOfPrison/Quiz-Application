package com.Dolkara.quiz_service.Model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Mail {

    private String to;
    private String subject;
    private String body;
}
