package com.Dolkara.quiz_service.service;

import com.Dolkara.quiz_service.Model.*;
import com.Dolkara.quiz_service.dao.QuizDao;
import com.Dolkara.quiz_service.feign.QuizInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    private final QuizDao quizDao;
    //private final QuestionDao questionDao;
    private final QuizInterface qinterface;
    private final KafkaTemplate<String, Mail> kafkaTemplate;

    private final String SUBJECT;
    private final String BODY;

    @Autowired
    public QuizService(QuizDao quizDao, QuizInterface qinterface, KafkaTemplate<String, Mail> kafkaTemplate) {
        this.quizDao = quizDao;
        this.qinterface = qinterface;
        this.kafkaTemplate = kafkaTemplate;

        SUBJECT = "Quiz Service Score";
        BODY = "Greetings %s ! You have scored %d out of %d in the quiz %s. Hope you continue using our service!";
    }


    public ResponseEntity<Quiz> createQuiz(String category, int numQ, String title) {

        List<Integer> questions = qinterface.getQuestionsForQuiz(category, numQ).getBody();

        Quiz quiz = new Quiz(title, category, numQ);
        quiz.setQuestionIds(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>(quiz, HttpStatus.CREATED);
    }

    public ResponseEntity<Quiz> getQuizById(int id) {
        Optional<Quiz> quiz = quizDao.findById(id);
        return null;
    }

    public ResponseEntity<QuizResponse> getQuizQuestions(Integer id) {
        Optional<Quiz> quiz = quizDao.findById(id);

        if(quiz.isPresent()) {
            List<Integer> questionIds = quiz.get().getQuestionIds();
            List<QuestionWrapper> questionWrappers = qinterface.getQuestionsFromId(questionIds).getBody();


            return new ResponseEntity<>(new QuizResponse(id, questionWrappers), HttpStatus.OK);
        }
        else return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses, String email) {
        Optional<Quiz> quiz = quizDao.findById(id);

        if(quiz.isPresent()) {
            ResponseEntity<Integer> res =  qinterface.getScore(responses);

            String body = BODY.formatted(email, res.getBody(), responses.size(), quiz.get().getTitle());
            Mail mail = new Mail(email, SUBJECT, body);
            kafkaTemplate.send("mail", mail);

            return res;
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<QuizResponse> getRandomQuizQuestions() {

        long count = quizDao.count();
        int randomId = (int) (Math.random() * count);
        Quiz quiz = quizDao.findAll(PageRequest.of(randomId, 1))
                .getContent()
                .getFirst();

        if(quiz.getQuestionIds().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return getQuizQuestions(quiz.getId());
    }
}
