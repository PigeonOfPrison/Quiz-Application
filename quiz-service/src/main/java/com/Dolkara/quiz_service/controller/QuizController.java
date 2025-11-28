package com.Dolkara.quiz_service.controller;

import com.Dolkara.quiz_service.Model.Quiz;
import com.Dolkara.quiz_service.Model.QuizDto;
import com.Dolkara.quiz_service.Model.QuizResponse;
import com.Dolkara.quiz_service.Model.Response;
import com.Dolkara.quiz_service.service.QuizService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("quiz")
public class QuizController {

    private final QuizService quizService;
    private HttpServletRequest request;

    @Autowired
    public QuizController(QuizService quizService, HttpServletRequest request) {
        this.quizService = quizService;
        this.request = request;
    }

    @PostMapping("create")
    public ResponseEntity<Quiz> createQuiz(@RequestBody QuizDto qd) {
        return quizService.createQuiz(qd.getCategory(), qd.getNumQ(), qd.getTitle());
    }

    @GetMapping("get/{id}")
    public ResponseEntity<QuizResponse> getQuizQuestions(@PathVariable Integer id) {
        return quizService.getQuizQuestions(id);
    }

    @GetMapping("get/random")
    public ResponseEntity<QuizResponse> getRandomQuizQuestions() {
        return quizService.getRandomQuizQuestions();
    }

    @PostMapping("submit/{id}")
    public ResponseEntity<Integer> submitQuiz(@PathVariable Integer id, @RequestBody List<Response> responses) {
        return quizService.calculateResult(id, responses, request.getHeader("X-User-Email"));
    }

}
