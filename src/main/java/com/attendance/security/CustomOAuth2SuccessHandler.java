package com.attendance.security;

import com.attendance.model.Role;
import com.attendance.model.User;
import com.attendance.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Extragem datele utilizatorului de la Google
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Salvăm utilizatorul în baza de date dacă nu există deja
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setNume(name);
            user.setRole(Role.STUDENT);
            userRepository.save(user);
        }

        // Generăm token-ul JWT
        // Generăm token-ul JWT
        String jwtToken = jwtUtils.generateToken(email);

        // Preluăm utilizatorul din baza de date pentru a-i afla rolul (ex: ADMIN sau STUDENT/ANGAJAT)
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost găsit"));

        String targetUrl;
        if (user.getRole() == Role.TEACHER) {
            targetUrl = "http://localhost:3000/admin?token=" + jwtToken;
        } else {
            targetUrl = "http://localhost:3000/angajat?token=" + jwtToken; // Pagina de angajat
        }

        response.sendRedirect(targetUrl);
    }
}