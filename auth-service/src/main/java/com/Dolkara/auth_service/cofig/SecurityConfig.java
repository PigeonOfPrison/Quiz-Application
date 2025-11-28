package com.Dolkara.auth_service.cofig;

import com.Dolkara.auth_service.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(JwtFilter jwtFilter) {

        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // disabling csrf
        http.csrf(customizer -> customizer.disable());

        // since the by creating this bean, we are letting go of  the default implementation of the security filter chain,
        // we need to re-implement some of the properties :

        // this makes it so that all requests are authenticated before they are serviced
        http.authorizeHttpRequests(request ->
                request
                        .requestMatchers("/auth/register", "/auth/login", "/auth/home", "/auth/test/**").permitAll()
                        .requestMatchers("/auth/oauth2/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated());

        // Reimplementing the default login form of spring security
        // http.formLogin(Customizer.withDefaults());

        // without this we cannot send the authentication data in the request body, only in form.
        // like, we cannot send the username, password in postman.
        http.httpBasic(Customizer.withDefaults());

        // this makes it so that no data like session id is created/stored
        http.sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
