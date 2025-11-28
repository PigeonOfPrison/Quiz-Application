package com.Dolkara.api_content_service.service;

import com.Dolkara.api_content_service.entity.Question;
import com.Dolkara.api_content_service.entity.TriviaRequest;

import java.util.List;

public interface ITriviaService {
    List<Question> getTrivia(TriviaRequest req);
    List<Question> refreshTrivia(TriviaRequest req);
    void deleteAllTrivia();
}
