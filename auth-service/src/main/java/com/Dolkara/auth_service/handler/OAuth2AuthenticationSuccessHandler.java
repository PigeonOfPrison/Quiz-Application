//package com.Dolkara.auth_service.handler;
//
//import com.Dolkara.auth_service.entity.Credentials;
//import com.Dolkara.auth_service.entity.Users;
//import com.Dolkara.auth_service.service.JwtService;
//import com.Dolkara.auth_service.service.RegistrationService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    private final JwtService jwtService;
//    private final RegistrationService registrationService;
//
//    @Autowired
//    public OAuth2AuthenticationSuccessHandler(JwtService jwtService, RegistrationService registrationService) {
//        this.jwtService = jwtService;
//        this.registrationService = registrationService;
//    }
//
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        try {
//            OAuth2User user = (OAuth2User) authentication.getPrincipal();
//
//            String email = user.getAttribute("email");
//            String name = user.getAttribute("name");
//            String provider = getProvider(request); // eg : google, github, etc
//
//
//            Users newUser = createOrUpdateOAuth2User(email, name, provider);
//            String token = jwtService.generateToken(newUser.getUsername());
//
//            Map<String, Object> responseBody = new HashMap<>();
//            responseBody.put("success", true);
//            responseBody.put("token", token);
//            responseBody.put("username", newUser.getUsername());
//            responseBody.put("provider", provider);
//
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
//            new ObjectMapper().writeValue(response.getWriter(), responseBody);
//
//        }
//        catch (Exception e){
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            response.setContentType("application/json");
//            response.getWriter().write("{\"success\": false, \"error\": \"Authentication failed\"}");
//            System.out.println("There was an error : " + e.getMessage());
//        }
//    }
//
//    private String getProvider(HttpServletRequest req) {
//        String reqUri = req.getRequestURI();
//
//        if(reqUri.contains("google")) return "google";
//        else if (reqUri.contains("github")) return "github";
//        else return "unknown";
//    }
//
//    private Users createOrUpdateOAuth2User(String email, String name, String provider) {
//        Users existingUser = registrationService.getUserByEmail(email);
//
//        if(existingUser != null)
//            return existingUser;
//
//        Credentials cred = new Credentials(name, "OAUTH2_USER_NO_PASSWORD", email);
//
//        return registrationService.register(cred);
//    }
//}
