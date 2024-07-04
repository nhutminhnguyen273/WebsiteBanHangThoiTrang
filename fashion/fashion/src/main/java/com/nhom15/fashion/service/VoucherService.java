package com.nhom15.fashion.service;

import com.nhom15.fashion.models.CartItem;
import com.nhom15.fashion.models.Voucher;
import com.nhom15.fashion.repositories.CartRepository;
import com.nhom15.fashion.repositories.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VoucherService {
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private CartRepository cartRepository;

    public List<Voucher> getAll(){
        return voucherRepository.findAll();
    }
    public Optional<Voucher> getById(String id){
        return voucherRepository.findById(id);
    }
    public void add(Voucher voucher){
        voucher.setCreatedDate(LocalDate.now());
        voucherRepository.save(voucher);
    }
    public void update(Voucher voucher){
        var existingVoucher = voucherRepository.findById(voucher.getId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy mã khuyễn mãi."));
        existingVoucher.setId(voucher.getId());
        existingVoucher.setName(voucher.getName());
        existingVoucher.setQuantity(voucher.getQuantity());
        existingVoucher.setStartDate(voucher.getStartDate());
        existingVoucher.setEndDate(voucher.getEndDate());
        existingVoucher.setDiscount(voucher.getDiscount());
        voucherRepository.save(existingVoucher);
    }
    public void deleteById(String id){
        if(!voucherRepository.existsById(id))
            throw new IllegalStateException("Không tìm thấy mã khuyến mãi");
        voucherRepository.deleteById(id);
    }

    public Optional<Voucher> getVoucherByCode(String code) {
        return voucherRepository.findByCode(code);
    }
    @Transactional
    public void applyVoucher(String code, List<CartItem> cartItems) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy mã voucher này."));

        if (voucher.getQuantity() <= 0) {
            throw new IllegalStateException("Mã khuyến mãi đã hết lượt sử dụng.");
        }

        for (CartItem cartItem : cartItems) {
            cartItem.setVoucher(voucher);
            long discountAmount = (cartItem.getProduct().getPrice() * voucher.getDiscount()) / 100;
            long discountedPrice = cartItem.getProduct().getPrice() - discountAmount;
            long newTotalPrice = discountedPrice * cartItem.getQuantity();
            cartItem.setTotalPrice(newTotalPrice);
            cartItem.setFormattedPrice(String.format("%,d VND", newTotalPrice));
            cartRepository.save(cartItem);
        }

        voucher.setQuantity(voucher.getQuantity() - 1);
        voucherRepository.save(voucher);
    }

}
