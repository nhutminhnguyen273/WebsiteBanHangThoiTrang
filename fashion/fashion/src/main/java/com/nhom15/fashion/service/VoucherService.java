package com.nhom15.fashion.service;

import com.nhom15.fashion.models.Voucher;
import com.nhom15.fashion.repositories.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VoucherService {
    @Autowired
    private VoucherRepository voucherRepository;

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
    public Optional<Voucher> findByCode(String code) {
        return voucherRepository.findByCode(code);
    }

    public void updateQuantity(Voucher voucher, int quantity) {
        voucher.setQuantity(quantity);
        voucherRepository.save(voucher);
    }
}
