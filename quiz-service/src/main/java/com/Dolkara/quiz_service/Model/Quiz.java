package com.Dolkara.quiz_service.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "category")
    private String category;

    @Column(name = "number_of_questions")
    private int numberOfQuestions;

    // can't make
    @ElementCollection
    private List<Integer> questionIds;

    public Quiz() {

    }

    public Quiz(String title, String category, int numberOfQuestions) {
        this.title = title;
        this.category = category;
        this.numberOfQuestions = numberOfQuestions;
    }

}
