package com.nhom15.fashion.config;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class MoMoSecurity {

    public MoMoSecurity() {
        // Constructor
    }

    public String getHash(String partnerCode, String merchantRefId, String amount, String paymentCode, String storeId, String storeName, String publicKeyXML) throws Exception {
        String json = "{\"partnerCode\":\"" + partnerCode + "\",\"partnerRefId\":\"" + merchantRefId + "\",\"amount\":" + amount + ",\"paymentCode\":\"" + paymentCode + "\",\"storeId\":\"" + storeId + "\",\"storeName\":\"" + storeName + "\"}";

        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        PublicKey publicKey = getPublicKeyFromXML(publicKeyXML);

        byte[] encryptedData = encryptData(data, publicKey);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public String buildQueryHash(String partnerCode, String merchantRefId, String requestId, String publicKeyXML) throws Exception {
        String json = "{\"partnerCode\":\"" + partnerCode + "\",\"partnerRefId\":\"" + merchantRefId + "\",\"requestId\":\"" + requestId + "\"}";

        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        PublicKey publicKey = getPublicKeyFromXML(publicKeyXML);

        byte[] encryptedData = encryptData(data, publicKey);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public String buildRefundHash(String partnerCode, String merchantRefId, String momoTransId, long amount, String description, String publicKeyXML) throws Exception {
        String json = "{\"partnerCode\":\"" + partnerCode + "\",\"partnerRefId\":\"" + merchantRefId + "\",\"momoTransId\":\"" + momoTransId + "\",\"amount\":" + amount + ",\"description\":\"" + description + "\"}";

        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        PublicKey publicKey = getPublicKeyFromXML(publicKeyXML);

        byte[] encryptedData = encryptData(data, publicKey);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String signSHA256(String message, String key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private PublicKey getPublicKeyFromXML(String publicKeyXML) throws Exception {
        String publicKeyPEM = publicKeyXML.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replaceAll("\\s+", "");
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);
    }

    private byte[] encryptData(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }
}
