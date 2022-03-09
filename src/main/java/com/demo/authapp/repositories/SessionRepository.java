package com.demo.authapp.repositories;

import com.demo.authapp.models.Session;
import com.demo.authapp.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SessionRepository {
    final private ConcurrentHashMap<String, Session> sessionCache;

    @Value("${CONFIGURED_TIME:2}")
    private Integer configuredTime;

    @Value("${ENCRYPT_KEY:jkl0POIU1234++==}")
    private String key;

    public SessionRepository() {
        this.sessionCache = new ConcurrentHashMap<>();
    }


    public String create(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredTime = now.plusHours(configuredTime);
        sessionCache.remove(user.getName());
        Session session = new Session();
        session.setUserName(user.getName());
        String s = now + "|" + user.getName();
        try {
            session.setValue(encrypt(s, key));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        session.setValue(String.valueOf(s.hashCode()));
        session.setExpiredTime(expiredTime);
        sessionCache.putIfAbsent(user.getName(), session);
        return session.getValue();
    }

    private String encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
        return new BASE64Encoder().encode(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

    public String delete(String sessionValue) {
        try {
            String value = decrypt(sessionValue, key);
            if (null != value) {
                String[] split = value.split("\\|");
                String name = split[split.length - 1];
                sessionCache.remove(name);
                return "Success";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "failed";
    }

    private String decrypt(String sSrc, String sKey) {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                return new String(original, StandardCharsets.UTF_8);
            } catch (Exception e) {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    public String checkAuth(String sessionValue) {
        try {
            String value = decrypt(sessionValue, key);
            if (null != value) {
                String[] split = value.split("\\|");
                String name = split[split.length - 1];
                Session session = sessionCache.get(name);
                if (null == session || !session.getValue().equals(sessionValue) || LocalDateTime.now().isAfter(session.getExpiredTime())) {
                    return null;
                }
                return session.getUserName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
