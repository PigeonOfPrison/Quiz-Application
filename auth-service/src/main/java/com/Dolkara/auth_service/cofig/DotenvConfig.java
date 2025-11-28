package com.Dolkara.auth_service.cofig;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {
    
    @PostConstruct
    public void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.configure().load();
            dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        }
        catch (Exception e) {
            System.out.println("error loading Dotenv : " + e.getMessage());
        }

    }
}
