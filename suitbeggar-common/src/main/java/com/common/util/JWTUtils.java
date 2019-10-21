package com.common.util;

import com.common.base.ResponseCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

/**
 * Created by fangyitao on 2019/8/7.
 * API������֤�����࣬����RSA����
 */
public class JWTUtils {
    private static RSAPrivateKey priKey;
    private static RSAPublicKey pubKey;
    private static class SingletonHolder {
        private static final JWTUtils INSTANCE = new JWTUtils();
    }

    public synchronized static JWTUtils getInstance(String modulus, String privateExponent, String publicExponent) {
        if (priKey == null && pubKey == null) {
            priKey = RSAUtils.getPrivateKey(modulus, privateExponent);
            pubKey = RSAUtils.getPublicKey(modulus, publicExponent);
        }
        return SingletonHolder.INSTANCE;
    }

    public synchronized static void reload(String modulus, String privateExponent, String publicExponent) {
        priKey = RSAUtils.getPrivateKey(modulus, privateExponent);
        pubKey = RSAUtils.getPublicKey(modulus, publicExponent);
    }

    public synchronized static JWTUtils getInstance() {
        if (priKey == null && pubKey == null) {
            priKey = RSAUtils.getPrivateKey(RSAUtils.modulus, RSAUtils.private_exponent);
            pubKey = RSAUtils.getPublicKey(RSAUtils.modulus, RSAUtils.public_exponent);
        }
        return SingletonHolder.INSTANCE;
    }

    /**
     * ��ȡToken
     * @param uid �û�ID
     * @param exp ʧЧʱ�䣬��λ����
     * @return
     */
    public static String getToken(String uid, int exp) {
        long endTime = System.currentTimeMillis() + 1000 * 60 * exp;
        return Jwts.builder().setSubject(uid).setExpiration(new Date(endTime))
                .signWith(SignatureAlgorithm.RS512, priKey).compact();
    }

    /**
     * ��ȡToken
     * @param uid �û�ID
     * @return
     */
    public String getToken(String uid) {
        long endTime = System.currentTimeMillis() + 1000 * 60 * 1440;
        return Jwts.builder().setSubject(uid).setExpiration(new Date(endTime))
                .signWith(SignatureAlgorithm.RS512, priKey).compact();
    }

    /**
     * ���Token�Ƿ�Ϸ�
     * @param token
     * @return JWTResult
     */
    public JWTResult checkToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(pubKey).parseClaimsJws(token).getBody();
            String sub = claims.get("sub", String.class);
            return new JWTResult(true, sub, "�Ϸ�����", ResponseCode.SUCCESS_CODE.getCode());
        } catch (ExpiredJwtException e) {
            // �ڽ���JWT�ַ���ʱ�����������ʱ���ֶΡ��Ѿ����ڵ�ǰʱ�䣬�����׳�ExpiredJwtException�쳣��˵�����������Ѿ�ʧЧ
            return new JWTResult(false, null, "token�ѹ���", ResponseCode.TOKEN_TIMEOUT_CODE.getCode());
        } catch (Exception e) {
            return new JWTResult(false, null, "�Ƿ�����", ResponseCode.NO_AUTH_CODE.getCode());
        }
    }

    public static class JWTResult {
        private boolean status;
        private String uid;
        private String msg;
        private int code;

        public JWTResult() {
            super();
        }

        public JWTResult(boolean status, String uid, String msg, int code) {
            super();
            this.status = status;
            this.uid = uid;
            this.msg = msg;
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
    }
}
