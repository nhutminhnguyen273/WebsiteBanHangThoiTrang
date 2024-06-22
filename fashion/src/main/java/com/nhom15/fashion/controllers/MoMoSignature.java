package com.nhom15.fashion.controllers;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MoMoSignature {

    public static String generateSignature(MoMoPaymentRequest request, String secretKey) {
        try {
            String data = request.getPartnerCode() + "|" +
                    request.getAccessKey() + "|" +
                    request.getRequestId() + "|" +
                    request.getOrderId() + "|" +
                    request.getAmount() + "|" +
                    request.getOrderInfo() + "|" +
                    request.getReturnUrl() + "|" +
                    request.getNotifyUrl() + "|" +
                    request.getRequestType();

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeyObj = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKeyObj);
            byte[] hmacBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }
}
