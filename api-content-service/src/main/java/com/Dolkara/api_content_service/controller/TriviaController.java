package com.Dolkara.api_content_service.controller;

import com.Dolkara.api_content_service.entity.Question;
import com.Dolkara.api_content_service.entity.TriviaRequest;
import com.Dolkara.api_content_service.service.ITriviaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("trivia")
public class TriviaController {

    private final ITriviaService triviaService;

    public TriviaController(ITriviaService triviaService) {
        this.triviaService = triviaService;
    }

    @GetMapping("get")
    public List<Question> getTrivia(TriviaRequest req) {
        return triviaService.getTrivia(req);
    }

    @GetMapping("refresh")
    public List<Question> refreshTrivia(TriviaRequest req) {
        return triviaService.refreshTrivia(req);
    }
}
