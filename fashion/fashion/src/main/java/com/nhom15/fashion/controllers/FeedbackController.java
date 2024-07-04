package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.Feedback;
import com.nhom15.fashion.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/feedback")
    public String showFeedbackForm(Model model) {
        model.addAttribute("feedback", new Feedback());
        return "feedback/feedback-form";
    }

    @PostMapping("/feedback")
    public String submitFeedback(@ModelAttribute Feedback feedback, Model model) {
        feedbackService.saveFeedback(feedback);
        model.addAttribute("message", "Liên hệ thành công!");
        return "feedback/feedback-success";
    }

    @GetMapping("/feedbacks")
    public String getFeedbacks(Model model) {
        List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
        model.addAttribute("feedbacks", feedbacks);
        return "layout-admin";
    }
}
