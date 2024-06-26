package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.User;
import com.nhom15.fashion.models.Voucher;
import com.nhom15.fashion.service.UserService;
import com.nhom15.fashion.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private VoucherService voucherService;

    @GetMapping
    public String adminHome(){
        return "home/admin-home";
    }

    @GetMapping("/add-employer")
    public String addEmployerForm(Model model){
        model.addAttribute("user", new User());
        return "users/add-employer";
    }

    @PostMapping("/add-employer")
    public String addEmployer(@Valid User user, BindingResult result, Model model){
        if(result.hasErrors()){
            model.addAttribute("user", user);
            return "users/add-employer";
        }
        userService.addEmployer(user);
        return "redirect:/admin/employer-list";
    }

    @GetMapping("/employer-list")
    public String employerList(Model model){
        List<User> employerList = userService.getAllEmployer();
        model.addAttribute("employerList", employerList);
        return "users/employer-list";
    }

    @GetMapping("/livestream")
    public String showLivestreamPage() {
        return "livestream/liveStream";
    }

    @GetMapping("/vouchers")
    public String voucherList(Model model){
        List<Voucher> vouchers = voucherService.getAll();
        model.addAttribute("vouchers", vouchers);
        return "voucher/voucher-list";
    }
}