package com.Dolkara.api_content_service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class Question {

    private String category;
    private String difficulty;
    private String questionTitle;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String answer;


    public Question(String category, String difficulty, String questionTitle, String option1, String option2, String option3, String option4, String answer) {
        this.category = category;
        this.difficulty = difficulty;
        this.questionTitle = questionTitle;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.answer = answer;
    }
}
