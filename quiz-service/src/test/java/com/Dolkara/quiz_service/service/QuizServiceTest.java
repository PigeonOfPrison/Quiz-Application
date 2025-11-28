package com.Dolkara.quiz_service.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class QuizServiceTest {

    private final QuizService quizService;

    @Autowired
    public QuizServiceTest(QuizService quizService) {
        this.quizService = quizService;
    }

    @Test
    public void testQuizService() {
        Integer score = quizService.calculateResult(6, new ArrayList<>(), "pigeon.of.prison@gmail.com").getBody();
        assertNotNull(score);
        assertEquals(0, score);
    }

}
