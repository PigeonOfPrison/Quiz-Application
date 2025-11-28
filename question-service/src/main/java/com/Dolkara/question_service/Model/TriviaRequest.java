package com.Dolkara.question_service.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TriviaRequest {
    private Integer amount;
    private String category;
    private String difficulty;
    private String type;
}
