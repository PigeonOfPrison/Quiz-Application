package com.Dolkara.api_content_service.service;

import com.Dolkara.api_content_service.client.ITriviaApiClient;
import com.Dolkara.api_content_service.entity.Question;
import com.Dolkara.api_content_service.entity.TriviaQuestion;
import com.Dolkara.api_content_service.entity.TriviaRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TriviaService implements ITriviaService{

    private final ITriviaApiClient triviaApiClient;

    public TriviaService(ITriviaApiClient triviaApiClient) {
        this.triviaApiClient = triviaApiClient;
    }


    @Override
    @Cacheable(value = "triviaCache", keyGenerator = "keyGenerator")
    public List<Question> getTrivia(TriviaRequest req) {
        // add the conversion from TriviaQuestion -> Question

        req.setType("multiple");
        List<TriviaQuestion> triviaQuestions = triviaApiClient.getTrivia(req);

        return convertTriviaQuestions(triviaQuestions);
    }

    @Override
    @CachePut(value = "TriviaCache", keyGenerator = "keyGenerator")
    public List<Question> refreshTrivia(TriviaRequest req) {
        //add the conversion logic for TriviaQuestion -> Question
        req.setType("multiple");
        List<TriviaQuestion> triviaQuestions = triviaApiClient.refreshTrivia(req);

        return convertTriviaQuestions(triviaQuestions);
    }

    @Override
    @CacheEvict(value = "TriviaCache", allEntries = true)
    public void deleteAllTrivia() {

    }

    private List<Question> convertTriviaQuestions(List<TriviaQuestion> triviaQuestions) {
        List<Question> questions = new ArrayList<>();

        for(TriviaQuestion tq : triviaQuestions) {

            ArrayList<String> options  = new ArrayList<>(tq.getIncorrectAnswers());
            options.add(tq.getCorrectAnswer());
            Collections.shuffle(options);

            Question q = new Question(
                    tq.getCategory(),
                    tq.getDifficulty(),
                    tq.getQuestion(),
                    options.get(0),
                    options.get(1),
                    options.get(2),
                    options.get(3),
                    tq.getCorrectAnswer()
            );

            questions.add(q);
        }
        return questions;
    }
}
