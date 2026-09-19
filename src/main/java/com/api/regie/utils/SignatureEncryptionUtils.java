package com.api.regie.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SignatureEncryptionUtils {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String AES_ALGORITHM = "AES";

    public static String generateSignatureBody(String body, String apiKey) throws Exception {
        Map<String, Object> map = mapper.readValue(body, new TypeReference<>() {});
        Map<String, Object> sorted = new TreeMap<>(map);
        Map<String, String> flatMap = new TreeMap<>();
        flattenJson("", sorted, flatMap);

        StringBuilder rawBody = new StringBuilder();
        for (Map.Entry<String, String> entry : flatMap.entrySet()) {
            rawBody.append(entry.getKey()).append(entry.getValue());
        }
        return generateHmacSignature(rawBody.toString(), apiKey);
    }

    public static String generateHmacSignature(String data, String key) throws Exception {
        Mac sha256Hmac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
        sha256Hmac.init(secretKeySpec);
        byte[] hmacBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    public static String generateSignatureAuthentification(String clientId, String secretKey) {
        String credentials = clientId + ":" + secretKey;
        return Base64.getEncoder().encodeToString(
                credentials.getBytes(StandardCharsets.UTF_8));
    }

    private static void flattenJson(String prefix, Map<String, Object> map,
                                    Map<String, String> flatMap) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nested = (Map<String, Object>) value;
                flattenJson(key, nested, flatMap);
            } else {
                flatMap.put(key, value == null ? "null" : value.toString());
            }
        }
    }

    public static String encryptData(String data, String secretKey) throws Exception {
        byte[] keyBytes = Arrays.copyOf(secretKey.getBytes(StandardCharsets.UTF_8), 16);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decryptData(String encryptedToken, String secretKey) throws Exception {
        byte[] key = Arrays.copyOf(secretKey.getBytes(StandardCharsets.UTF_8), 16);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedToken));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
