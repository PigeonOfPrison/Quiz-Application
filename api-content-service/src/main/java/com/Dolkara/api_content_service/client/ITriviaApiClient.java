package com.Dolkara.api_content_service.client;

import com.Dolkara.api_content_service.entity.TriviaQuestion;
import com.Dolkara.api_content_service.entity.TriviaRequest;

import java.util.List;

public interface ITriviaApiClient {
    List<TriviaQuestion> getTrivia(TriviaRequest req);
    List<TriviaQuestion> refreshTrivia(TriviaRequest req);
}
