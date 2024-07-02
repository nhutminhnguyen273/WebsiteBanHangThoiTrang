package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.Category;
import com.nhom15.fashion.models.Invoice;
import com.nhom15.fashion.models.User;
import com.nhom15.fashion.repositories.InvoiceRepository;
import com.nhom15.fashion.service.CategoryService;
import com.nhom15.fashion.service.EmailService;
import com.nhom15.fashion.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final EmailService emailService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/login")
    public String login() {
        return "users/login";
    }
    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User());
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, @NotNull BindingResult bindingResult, Model model) {
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Xác nhận mật khẩu không khớp với mật khẩu.");
        }
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "users/register";
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
    public String oauth2LoginSuccessGoogle(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        User user = userService.processOAuthPostLogin(oauth2User);
        model.addAttribute("user", user);
        return "redirect:/home";
    }

    @GetMapping("/facebook")
    public String oauth2LoginSuccessFacebook(Model model) {
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
    @GetMapping("/profile")
    public String profile(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            currentUserName = ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        Optional<User> optionalUser = userService.findByUsername(currentUserName);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            model.addAttribute("user", user);
        } else {
            model.addAttribute("error", "Không tìm thấy người dùng.");
        }
        return "users/profile";
    }

    @GetMapping("/history")
    public String viewHistory(Model model) {
        Long userId = userService.getCurrentUserId();
        List<Category> categoriesList = categoryService.getAll();
        model.addAttribute("categories", categoriesList);
        List<Invoice> invoices = invoiceRepository.findByUserId(userId);
        model.addAttribute("invoices", invoices);
        return "invoice/invoice";
    }

    @GetMapping("/blog")
    public String blog(Model model){
        List<Category> categoriesList = categoryService.getAll();
        model.addAttribute("categories", categoriesList);
        return "blog/blog";
    }
}