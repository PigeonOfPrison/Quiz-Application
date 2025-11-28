package com.Dolkara.api_content_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TriviaResponse {

    private Integer responseCode;
    private List<TriviaQuestion> results;
}
