package com.Dolkara.question_service.Model;

import com.Dolkara.question_service.Model.enums.Difficulty;
import jakarta.persistence.*;
import lombok.Data;

//public enum Difficulty {easy, medium, hard, insane} : moved to a separate file

// in order to use java enums with the postgreSQl ones, a number of steps need to be taken :
// S1 -> same type of enum needs to be created in both java and postgres
// S2 -> use @Enumerated(EnumType.String) in the entity
// S3 -> write the following query in the given db :
// CREATE CAST (varchar AS difficulty) WITH INOUT AS IMPLICIT;

// entities are called model in the MVC arch., hence the name of the package
@Entity
@Table(name ="question")
@Data   // lombok annotation that creates getters and setters
public class Question {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "category", length = 45)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private Difficulty difficulty;

    @Column(name = "question_title", nullable = false)
    private String questionTitle;

    @Column(name = "option1", nullable = false)
    private String option1;

    @Column(name = "option2", nullable = false)
    private String option2;

    @Column(name = "option3", nullable = false)
    private String option3;

    @Column(name = "option4", nullable = false)
    private String option4;

    @Column(name = "answer", nullable = false)
    private String answer;

//    @JdbcTypeCode(Types.ARRAY)
//    @Column(name = "tags", columnDefinition = "varchar(20)[]")
//    private String[] tags;

    @Column(name = "hint")
    private String hint;


}
