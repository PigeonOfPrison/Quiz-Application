package com.Dolkara.api_content_service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.util.UriBuilder;

import java.util.Map;

public class QueryParamsUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static UriBuilder addParamsFromObject(UriBuilder uriBuilder, Object obj) {

        Map<String, Object> params = mapper.convertValue(obj, Map.class);

        params.forEach((key, value) -> {
            if(value != null)
                uriBuilder.queryParam(key, String.valueOf(value));
        });

        return uriBuilder;
    }
}
