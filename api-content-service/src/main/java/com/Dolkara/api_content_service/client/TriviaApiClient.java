package com.Dolkara.api_content_service.client;

import com.Dolkara.api_content_service.entity.TriviaQuestion;
import com.Dolkara.api_content_service.entity.TriviaRequest;
import com.Dolkara.api_content_service.entity.TriviaResponse;
import com.Dolkara.api_content_service.utils.QueryParamsUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Component
public class TriviaApiClient implements ITriviaApiClient{

    private final WebClient webClient;

    public TriviaApiClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://opentdb.com")
                .build();
    }

    // keyGenerator bean is self implemented
    @Override
    //@Cacheable(value = "triviaCache", keyGenerator = "keyGenerator")
    public List<TriviaQuestion> getTrivia(TriviaRequest req) {
        return fetchTriviaFromApi(req);
    }

    @Override
    //@CachePut(value = "triviaCache", keyGenerator = "keyGenerator")
    public List<TriviaQuestion> refreshTrivia(TriviaRequest req) {
        return fetchTriviaFromApi(req);
    }

    private List<TriviaQuestion> fetchTriviaFromApi(TriviaRequest req) {

        TriviaResponse res = webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/api.php");
                    QueryParamsUtils.addParamsFromObject(uriBuilder, req);
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(TriviaResponse.class)
                .block();

        return res == null ? Collections.emptyList() : res.getResults();
    }
}
