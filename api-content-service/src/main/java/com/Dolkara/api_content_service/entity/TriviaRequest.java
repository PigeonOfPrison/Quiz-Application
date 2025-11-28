package com.Dolkara.api_content_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TriviaRequest {
    private Integer amount;
    private String category;
    private String difficulty;
    private String type;
}
