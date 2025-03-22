package com.sopera.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.sopera.config.JwtUtil;
import com.sopera.model.enums.AuthProvider;
import com.sopera.model.User;
import com.sopera.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{

        OAuth2User oAuth2User=(OAuth2User) authentication.getPrincipal();
        String email=oAuth2User.getAttribute("email");
        String  provider=authentication.getAuthorities().toString().toLowerCase();
        AuthProvider authProvider;

        System.out.println("provider: "+provider);
        if(provider.contains("google")){
            authProvider=AuthProvider.GOOGLE;
        }else if(provider.contains("github")){
            authProvider=AuthProvider.GITHUB;
        }else{
            authProvider=AuthProvider.LOCAL;
        }

        User user=userRepository.findByEmail(email).orElseGet(
            ()->{
                System.out.println("creating new user in db");
                User newUser=new User();
                newUser.setEmail(email);
                newUser.setAuthProvider(authProvider);
                newUser.setPassword("OAuth2User");
                return userRepository.save(newUser);
            }
        );
        String token = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        String s=request.getHeader("Authorization");
        System.out.println("header token: "+s);

        if (token == null || token.isEmpty()) {
            token = jwtUtil.generateToken(user.getEmail());
        }

        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(1 * 60 * 60); 
        jwtCookie.setPath("/");
        System.out.println("jwtCookie: "+jwtCookie.getName());

        response.addCookie(jwtCookie);

        response.setHeader("Authorization", "Bearer " + token);

        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect("http://localhost:8080/auth/check?="+token);
    
    }
}
