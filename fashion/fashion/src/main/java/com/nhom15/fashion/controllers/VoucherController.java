package com.nhom15.fashion.controllers;

import com.nhom15.fashion.models.Product;
import com.nhom15.fashion.models.User;
import com.nhom15.fashion.models.Voucher;
import com.nhom15.fashion.service.CartService;
import com.nhom15.fashion.service.CategoryService;
import com.nhom15.fashion.service.ProductService;
import com.nhom15.fashion.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/vouchers")
public class VoucherController {
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CartService cartService;

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
    @PostMapping("/apply-discount")
    public String applyDiscount(@RequestParam("voucherCode") String voucherCode, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Long userId = null;
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            userId = user.getId();
        } else if (authentication.getPrincipal() instanceof OAuth2User) {
        }

        if (userId == null) {
            return "redirect:/login";
        }
        Optional<Voucher> voucherOptional = voucherService.getVoucherByCode(voucherCode);

        if (voucherOptional.isPresent()) {
            Voucher voucher = voucherOptional.get();
            double discountPercentage = voucher.getDiscount();
            cartService.applyDiscountToCartItems(userId, discountPercentage);
            model.addAttribute("discountedAmount", true);
            model.addAttribute("discountPercentage", discountPercentage);
        } else {
            model.addAttribute("discountError", "Mã giảm giá không hợp lệ hoặc đã hết hạn.");
        }

        model.addAttribute("cartItems", cartService.getCartItems(userId));
        model.addAttribute("totalAmount", cartService.getTotalAmount(userId));
        return "redirect:/cart";
    }
}
