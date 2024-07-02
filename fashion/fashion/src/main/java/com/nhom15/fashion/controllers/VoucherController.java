package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.Product;
import com.nhom15.fashion.models.Voucher;
import com.nhom15.fashion.service.CategoryService;
import com.nhom15.fashion.service.ProductService;
import com.nhom15.fashion.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/vouchers")
public class VoucherController {
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String voucherList(Model model) {
        List<Voucher> vouchers = voucherService.getAll();
        model.addAttribute("vouchers", vouchers);
        return "voucher/voucher-list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("voucher", new Voucher());
        model.addAttribute("categories", categoryService.getAll());
        return "voucher/add-voucher";
    }

    @PostMapping("/add")
    public String addVoucher(@Valid Voucher voucher, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAll());
            return "voucher/add-voucher";
        }
        voucherService.add(voucher);
        return "redirect:/vouchers";
    }

    @GetMapping("update/{id}")
    public String updateForm(@PathVariable("id") String id, Model model) {
        Voucher voucher = voucherService.getById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm mã giảm giá này."));
        model.addAttribute("voucher", voucher);
        model.addAttribute("categories", categoryService.getAll());
        return "redirect:/vouchers";
    }

    @PostMapping("update/{id}")
    public String updateVoucher(@PathVariable String id, @Valid Voucher voucher, BindingResult result, Model model){
        if (result.hasErrors()){
            voucher.setId(id);
            model.addAttribute("voucher", voucher);
            model.addAttribute("categories", categoryService.getAll());
            return "vouchers/update-voucher";
        }
        voucherService.update(voucher);
        return "redirect:/vouchers";
    }

    @GetMapping("/delete/{id}")
    public String deleteVoucher(@PathVariable String id){
        voucherService.deleteById(id);
        return "redirect:/vouchers";
    }
}
