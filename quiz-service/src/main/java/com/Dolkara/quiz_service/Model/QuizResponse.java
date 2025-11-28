package com.Dolkara.quiz_service.Model;

import lombok.Data;

import java.util.List;

@Data
public class QuizResponse {

    private Integer id;
    private List<QuestionWrapper> questions;

    public QuizResponse() {

    }

    public QuizResponse(Integer id, List<QuestionWrapper> questions) {
        this.id = id;
        this.questions = questions;
    }
}
