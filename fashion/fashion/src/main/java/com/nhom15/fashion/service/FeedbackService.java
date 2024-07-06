package com.nhom15.fashion.service;

import com.nhom15.fashion.models.Feedback;
import com.nhom15.fashion.repositories.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private JavaMailSender mailSender;

    public List<Feedback> getAll(){return feedbackRepository.findAll();}
    public Optional<Feedback> getById(Long id){return feedbackRepository.findById(id);}

    public void saveFeedback(Feedback feedback) {
        feedback.setDateReceived(LocalDate.now());
        feedbackRepository.save(feedback);
        sendEmail(feedback);
    }

    private void sendEmail(Feedback feedback) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(feedback.getEmail());
        message.setSubject("Cảm ơn đã gửi feedback của bạn cho chúng tôi!");
        message.setText("Chúng tôi đã nhận được nó! Chân thành cảm ơn quý khách!");
        mailSender.send(message);
    }

    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    public void deleteById(Long id){
        if(!feedbackRepository.existsById(id))
            throw new IllegalStateException("Không tìm thấy feedback này.");
        feedbackRepository.deleteById(id);
    }

}
