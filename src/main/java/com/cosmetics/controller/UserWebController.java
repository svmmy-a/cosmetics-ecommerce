package com.cosmetics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserWebController {

    /**
     * renders the login page for user authentication.
     * @param model model to pass data to the view
     * @return template name for login page
     */
    @GetMapping("/login")
    public String showLogin(Model model) {
        return "login";  // Thymeleaf template: src/main/resources/templates/login.html
    }
    
    /**
     * renders the signup page for user registration.
     * @param model model to pass data to the view
     * @return template name for signup page
     */
    @GetMapping("/signup")
    public String showSignup(Model model) {
        return "signup";  // Thymeleaf template: src/main/resources/templates/signup.html
    }
}
