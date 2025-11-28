package com.Dolkara.quiz_service.Model;

import lombok.Data;

@Data
public class QuizDto {
    private String title;
    private String category;
    private int numQ;
}
