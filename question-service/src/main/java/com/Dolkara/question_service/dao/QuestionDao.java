package com.Dolkara.question_service.dao;


import com.Dolkara.question_service.Model.Question;
import com.Dolkara.question_service.Model.enums.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDao extends JpaRepository<Question, Integer> {

    // using the maven dependency : spring-boot-starter-data-jpa
    // will create a personalised dao in the future

    List<Question> findByCategory(String category);

    List<Question> findByDifficulty(Difficulty difficulty);

    @Query(value = "SELECT q.id FROM question q where q.category=:category ORDER BY RANDOM() LIMIT :numQ", nativeQuery = true)
    List<Integer> findRandomQuestionsByCategory(String category, int numQ);
}
