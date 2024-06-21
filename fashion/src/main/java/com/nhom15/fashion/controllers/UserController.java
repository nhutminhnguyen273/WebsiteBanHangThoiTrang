package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.User;
import com.nhom15.fashion.service.EmailService;
import com.nhom15.fashion.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final EmailService emailService;

    @GetMapping("/login")
    public String register(@NotNull Model model){
        model.addAttribute("user", new User());
        return "users/login";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, @NotNull BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "users/login";
        }
        userService.save(user);
        userService.setDefaultRole(user.getUsername());
        return "redirect:/login";
    }

    @GetMapping("/user")
    public String userList(Model model){
        List<User> userList = userService.getAll();
        model.addAttribute("userList", userList);
        return "users/user-list";
    }

    @GetMapping("/google")
    public String oauth2LoginSuccess(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        User user = userService.processOAuthPostLogin(oauth2User);
        model.addAttribute("user", user);
        return "redirect:/home";
    }

    @GetMapping("/forgot-password")
    public String displayForgotPasswordPage() {
        return "users/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPasswordForm(@RequestParam("email") String email, Model model) {
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isPresent()) {
            userService.createPasswordResetTokenForUser(optionalUser.get());
            model.addAttribute("message", "A password reset link has been sent to " + email);
        } else {
            model.addAttribute("error", "Email address not found.");
        }
        return "redirect:/login";
    }

    @GetMapping("/reset-password")
    public String displayResetPasswordPage(@RequestParam("token") String token, Model model) {
        Optional<User> userOptional = userService.getUserByPasswordResetToken(token);
        if (userOptional.isPresent()) {
            model.addAttribute("token", token);
        } else {
            model.addAttribute("error", "Invalid password reset token.");
        }
        return "users/reset-password";
    }

    @PostMapping("/reset-password")
    public String handlePasswordReset(@RequestParam("token") String token,
                                      @RequestParam("password") String password,
                                      Model model) {
        Optional<User> userOptional = userService.getUserByPasswordResetToken(token);
        if (userOptional.isPresent()) {
            userService.resetPassword(userOptional.get(), password);
            model.addAttribute("message", "Your password has been reset successfully.");
        } else {
            model.addAttribute("error", "Invalid password reset token.");
        }
        return "redirect:/login";
    }
}