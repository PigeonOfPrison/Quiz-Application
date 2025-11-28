package com.Dolkara.api_gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtFilterGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtFilterGatewayFilterFactory.Config> {

    private final JwtFilter jwtFilter;

    @Autowired
    public JwtFilterGatewayFilterFactory(JwtFilter jwtFilter) {
        super(Config.class);
        this.jwtFilter = jwtFilter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return jwtFilter;
    }

    public static class Config {
        // Configuration properties can be added here if needed in the future
        // For example: private boolean enabled = true;
    }
}
