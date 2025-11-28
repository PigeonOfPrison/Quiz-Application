package com.Dolkara.question_service.feign;

import com.Dolkara.question_service.Model.Question;
import com.Dolkara.question_service.Model.TriviaRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("API-CONTENT-SERVICE")
public interface TriviaInterface {

    @GetMapping("get")
    List<Question> getTrivia(TriviaRequest req);

    @GetMapping("refresh")
    List<Question> refreshTrivia(TriviaRequest req);
}
