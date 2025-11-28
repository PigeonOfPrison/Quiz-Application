package com.Dolkara.question_service.controller;


import com.Dolkara.question_service.Model.Question;
import com.Dolkara.question_service.Model.QuestionWrapper;
import com.Dolkara.question_service.Model.Response;
import com.Dolkara.question_service.Model.TriviaRequest;
import com.Dolkara.question_service.Model.enums.Difficulty;
import com.Dolkara.question_service.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
//@RequiredArgsConstructor : lombok annotation ; automatically creates constructors (for private final)
public class QuestionController {

    private final QuestionService questionService;
    private final Environment env;

    @Autowired
    public QuestionController(QuestionService questionService, Environment env) {
        this.questionService = questionService;
        this.env = env;
    }

    @GetMapping("allQuestions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("category/{category}")
    public ResponseEntity<List<Question>> getQuestionsByCategory(@PathVariable String category) {
        return questionService.getQuestionsByCategory(category);
    }

    @GetMapping("difficulty/{difficulty}")
    public ResponseEntity<List<Question>> getQuestionsByDifficulty(@PathVariable Difficulty difficulty) {
        return questionService.getQuestionsByDifficulty(difficulty);

    }

    @PostMapping("create")
    public ResponseEntity<String> createQuestion(@RequestBody Question question) {
        return questionService.addQuestion(question);
    }

    @PostMapping("/createAll")
    public ResponseEntity<String> bulkCreateQuestion(@RequestBody List<Question> questions) {
        return questionService.addQuestionInBulk(questions);
    }

    @GetMapping("get/random")
    public ResponseEntity<List<Question>> createRandomQuestions(TriviaRequest req) {
        return questionService.getRandomQuestions(req);
    }

    @GetMapping("get/random/refresh")
    public ResponseEntity<List<Question>> refreshRandomQuestions(TriviaRequest req) {
        return questionService.refreshRandomQuestions(req);
    }

    @GetMapping("generate")
    public ResponseEntity<List<Integer>> getQuestionsForQuiz(@RequestParam String category, @RequestParam Integer numQ) {
        return questionService.getQuestionsForQuiz(category, numQ);
    }

    @PostMapping("getQuestions")
    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(@RequestBody List<Integer> ids) {
        System.out.println(env.getProperty("local.server.port"));
        return questionService.getQuestionFromId(ids);
    }

    @PostMapping("/getScore")
    public ResponseEntity<Integer> getScore(@RequestBody List<Response> responses) {
        return questionService.calculateScore(responses);
    }



}
