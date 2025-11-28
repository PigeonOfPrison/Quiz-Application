package com.Dolkara.question_service.service;


import com.Dolkara.question_service.Model.Question;
import com.Dolkara.question_service.Model.QuestionWrapper;
import com.Dolkara.question_service.Model.Response;
import com.Dolkara.question_service.Model.TriviaRequest;
import com.Dolkara.question_service.Model.enums.Difficulty;
import com.Dolkara.question_service.dao.QuestionDao;
import com.Dolkara.question_service.feign.TriviaInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionDao questionDao;
    private final TriviaInterface triviaInterface;

    @Autowired
    public QuestionService(QuestionDao questionDao, TriviaInterface triviaInterface) {
        this.questionDao = questionDao;
        this.triviaInterface = triviaInterface;
    }

    public ResponseEntity<List<Question>> getAllQuestions() {

        try {
            return new ResponseEntity<>(questionDao.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
        try {
            return new ResponseEntity<>(questionDao.findByCategory(category), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Question>> getQuestionsByDifficulty(Difficulty difficulty) {
        try {
            return new ResponseEntity<>(questionDao.findByDifficulty(difficulty), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> addQuestion(Question question) {
        try {
            questionDao.save(question);
            return new ResponseEntity<>("success", HttpStatus.CREATED);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("failure", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String category, Integer numQ) {
        List<Integer> questions = null;
        try {
            questions = questionDao.findRandomQuestionsByCategory(category, numQ);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionFromId(List<Integer> ids) {

        List<QuestionWrapper> wrappers = new ArrayList<>();
        List<Question> questions = new ArrayList<>();

        for(Integer id : ids) {
            Optional<Question> question = questionDao.findById(id);

            if(question.isPresent()) {
                Question q = question.get();
                wrappers.add(new QuestionWrapper(q.getId(), q.getQuestionTitle(), q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4()));
            }
        }

        return new ResponseEntity<>(wrappers, HttpStatus.OK);
    }

    public ResponseEntity<Integer> calculateScore(List<Response> responses) {
        int score = 0;
        if(responses.isEmpty()) return new ResponseEntity<>(score, HttpStatus.NO_CONTENT);

        try {
            for (Response r : responses) {
                Optional<Question> question = questionDao.findById(r.getId());

                if(question.isPresent()) {
                    Question q = question.get();

                    if (r.getResponse().equals(q.getAnswer()))
                        score++;
                }
                else return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(score, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> addQuestionInBulk(List<Question> questions) {
        if(!questions.isEmpty()) {
                questionDao.saveAll(questions);
                return new ResponseEntity<>("success", HttpStatus.CREATED);
        }
        else return new ResponseEntity<>("failure", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Question>> getRandomQuestions(TriviaRequest req) {
        try {
            List<Question> questions = triviaInterface.getTrivia(req);
            return new ResponseEntity<>(questions, HttpStatus.CREATED);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Question>> refreshRandomQuestions(TriviaRequest req) {
        try {
            List<Question> questions = triviaInterface.refreshTrivia(req);
            return new ResponseEntity<>(questions, HttpStatus.CREATED);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
