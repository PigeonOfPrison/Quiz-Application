package com.Dolkara.auth_service.service;

import com.Dolkara.auth_service.entity.Credentials;
import com.Dolkara.auth_service.entity.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class OAuth2Service {

    private static final String SUCCESS_KEY = "success";
    private static final String ERROR_KEY = "error";
    private static final String TOKEN_KEY = "token";
    private static final String USERNAME_KEY = "username";
    private static final String PROVIDER_KEY = "provider";
    private static final String AUTH_URL_KEY = "authUrl";
    private static final String EMAIL_ATTRIBUTE = "email";
    private static final String NAME_ATTRIBUTE = "name";
    
    private final RegistrationService registrationService;
    private final JwtService jwtService;
    private final MyUserDetailsService userDetailsService;
    private final ClientRegistrationRepository clientRegRepo;
    private final DefaultOAuth2UserService oauth2UserService;

    public OAuth2Service(RegistrationService registrationService, JwtService jwtService, 
                        MyUserDetailsService userDetailsService, ClientRegistrationRepository clientRegRepo) {
        this.registrationService = registrationService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.clientRegRepo = clientRegRepo;
        this.oauth2UserService = new DefaultOAuth2UserService();
    }


    public ResponseEntity<Map<String, String>> handleCallback(String code, String provider) {

        try {

            ClientRegistration clientRegistration = clientRegRepo.findByRegistrationId(provider);
            if(clientRegistration == null) {
                log.error("Client registration not found for provider {}", provider);
                return onAuthenticationFailure("Invalid OAuth2 provider");
            }

            // Use Spring's built-in token exchange
            OAuth2AccessTokenResponse tokenResponse = exchangeCodeForToken(code, clientRegistration);
            if (tokenResponse == null) {
                return onAuthenticationFailure("Failed to exchange code for token");
            }

            // Get user info using Spring's built-in user service
            OAuth2UserRequest userRequest = new OAuth2UserRequest(
                clientRegistration, 
                tokenResponse.getAccessToken()
            );
            
            OAuth2User oauth2User = oauth2UserService.loadUser(userRequest);

            return onAuthenticationSuccess(oauth2User, provider);
        }
        catch (Exception e) {
            log.error("Error handling OAuth2 callback for provider: {}", provider, e);
            return onAuthenticationFailure("Authentication failed: " + e.getMessage());

        }

    }

    
    private OAuth2AccessTokenResponse exchangeCodeForToken(String code, ClientRegistration clientReg) {
        try {
            // Create RestTemplate for token exchange
            RestTemplate restTemplate = new RestTemplate();
            
            // Prepare the request parameters
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("code", code);
            params.add("redirect_uri", clientReg.getRedirectUri());
            params.add("client_id", clientReg.getClientId());
            params.add("client_secret", clientReg.getClientSecret());
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            // Make the token request
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(
                clientReg.getProviderDetails().getTokenUri(), 
                request, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> tokenResponse = response.getBody();
                
                // Build OAuth2AccessTokenResponse
                return OAuth2AccessTokenResponse.withToken((String) tokenResponse.get("access_token"))
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    .expiresIn(((Number) tokenResponse.getOrDefault("expires_in", 3600)).longValue())
                    .refreshToken((String) tokenResponse.get("refresh_token"))
                    .scopes(Set.of(clientReg.getScopes().toArray(new String[0])))
                    .build();
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("Error exchanging code for token", e);
            return null;
        }
    }

    private ResponseEntity<Map<String, String>> onAuthenticationFailure(String errorMsg) {
        Map<String, String> res = new HashMap<>();

        res.put(SUCCESS_KEY, "false");
        res.put(ERROR_KEY, errorMsg);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }

    public ResponseEntity<Map<String, String>> onAuthenticationSuccess(OAuth2User user, String provider) {
        try {
            String email = getEmailFromOAuth2User(user);
            String name = getNameFromOAuth2User(user);

            if (email == null || email.isEmpty()) {
                return onAuthenticationFailure("Email not provided by OAuth2 provider");
            }

            Users newUser = createOrUpdateOAuth2User(email, name);
            String token = jwtService.generateToken(newUser.getUsername());

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put(SUCCESS_KEY, "true");
            responseBody.put(TOKEN_KEY, token);
            responseBody.put(USERNAME_KEY, newUser.getUsername());
            responseBody.put(PROVIDER_KEY, provider);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);

        }
        catch (Exception e){
            log.error("Error during OAuth2 authentication success handling", e);
            HashMap<String, String> responseBody = new HashMap<>();
            responseBody.put(SUCCESS_KEY, "false");
            responseBody.put(ERROR_KEY, e.getMessage());

            return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, String>> getAuthorizationUrl(String provider) {

        try {
            ClientRegistration clientReg = clientRegRepo.findByRegistrationId(provider);
            
            if(clientReg == null) {
                return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, "failed to generate auth url for " + provider));
            }

            // Format scopes correctly based on provider
            String scopeParam = formatScopesForProvider(clientReg.getScopes(), provider);
            
            String authUrl = clientReg.getProviderDetails().getAuthorizationUri() +
                "?client_id=" + clientReg.getClientId() +
                "&redirect_uri=" + java.net.URLEncoder.encode(clientReg.getRedirectUri(), "UTF-8") +
                "&response_type=code" +
                scopeParam +
                "&state=" + provider;

            return ResponseEntity.ok(Map.of(AUTH_URL_KEY, authUrl));
        }
        catch (Exception e) {
            log.error("Error while creating AuthorizationUrl {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, "failed to generate auth url for " + provider));
        }
    }

    private String formatScopesForProvider(Set<String> scopes, String provider) {
        if (scopes == null || scopes.isEmpty()) {
            // Default scopes for each provider
            switch (provider.toLowerCase()) {
                case "google":
                    return "&scope=openid%20email%20profile";
                case "github":
                    return "&scope=user%3Aemail%20read%3Auser";
                default:
                    return "&scope=openid%20email%20profile";
            }
        }
        
        // For Google and most providers, scopes are space-separated
        String scopeString = String.join(" ", scopes);
        
        // URL encode the scope parameter
        try {
            scopeString = java.net.URLEncoder.encode(scopeString, "UTF-8");
            return "&scope=" + scopeString;
        } catch (Exception e) {
            log.warn("Error encoding scopes, using unencoded: {}", e.getMessage());
            return "&scope=" + String.join(" ", scopes);
        }
    }

    private String getEmailFromOAuth2User(OAuth2User user) {
        return (String) user.getAttribute(EMAIL_ATTRIBUTE);
    }

    private String getNameFromOAuth2User(OAuth2User user) {
        return (String) user.getAttribute(NAME_ATTRIBUTE);
    }

    private Users createOrUpdateOAuth2User(String email, String name) {
        Users existingUser = registrationService.getUserByEmail(email);
        Users newUser;

        if(existingUser != null) {
            newUser = existingUser;
        } else {
            Credentials cred = new Credentials(name, "OAUTH2_USER_NO_PASSWORD", email);
            newUser = registrationService.register(cred);
        }
        
        // setting authentication context
        UserDetails userDetails = userDetailsService.loadUserByUsername(newUser.getUsername());
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return newUser;
    }
}
