package com.common.util;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;

/**
 * Created by fangyitao on 2019/8/7.
 */
public class RSAUtils {


    public static String modulus = "120749774428185480467622030722535804071445078993623309709775427878906293937338059423076695854937532244466465395164541641368876529295825453848760144835049363522545908104302024165873971414491110512342791594610742544380402908598585190494003507524195754273822268813447403290817343077571516216147839402414940310061";

    public static String public_exponent = "65537";

    public static String private_exponent = "73923469942286919561803730971048133587965873619209827001168953680477872428610977313161774128992838682156392947263251899461404460204267953359475632559803617319478756560848229397545070273747796303141458040475889804016062973476403760709402857872540300632704514872361476749953797952016730690123983122643596231873";

    /**
     * ��Կ����
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data) throws Exception {
        RSAPublicKey publicKey = RSAUtils.getPublicKey(modulus, public_exponent);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // ģ��
        int key_len = publicKey.getModulus().bitLength() / 8;
        // �������ݳ��� <= ģ��-11
        String[] datas = splitString(data, key_len - 11);
        String mi = "";
        // ������ĳ��ȴ���ģ��-11��Ҫ�������
        for (String s : datas) {
            mi += bcd2Str(cipher.doFinal(s.getBytes()));
        }
        return mi;
    }

    /**
     * ˽Կ����
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data) throws Exception {
        RSAPrivateKey privateKey = RSAUtils.getPrivateKey(modulus, private_exponent);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        // ģ��
        int key_len = privateKey.getModulus().bitLength() / 8;
        byte[] bytes = data.getBytes();
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
        // ������ĳ��ȴ���ģ����Ҫ�������
        String ming = "";
        byte[][] arrays = splitArray(bcd, key_len);
        for (byte[] arr : arrays) {
            ming += new String(cipher.doFinal(arr));
        }
        return ming;
    }



    /**
     * ���ɹ�Կ��˽Կ
     *
     * @throws Exception
     *
     */
    public static HashMap<String, Object> getKeys() throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        map.put("public", publicKey);
        map.put("private", privateKey);
        return map;
    }

    /**
     * ʹ��ģ��ָ������RSA��Կ
     * ע�⣺���˴�������Ĭ�ϲ�λ��ʽ��ΪRSA/None/PKCS1Padding����ͬJDKĬ�ϵĲ�λ��ʽ���ܲ�ͬ����AndroidĬ����RSA
     * /None/NoPadding��
     *
     * @param modulus
     *            ģ
     * @param exponent
     *            ָ��
     * @return
     */
    public static RSAPublicKey getPublicKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ʹ��ģ��ָ������RSA˽Կ
     * ע�⣺���˴�������Ĭ�ϲ�λ��ʽ��ΪRSA/None/PKCS1Padding����ͬJDKĬ�ϵĲ�λ��ʽ���ܲ�ͬ����AndroidĬ����RSA
     * /None/NoPadding��
     *
     * @param modulus
     *            ģ
     * @param exponent
     *            ָ��
     * @return
     */
    public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ��Կ����
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // ģ��
        int key_len = publicKey.getModulus().bitLength() / 8;
        // �������ݳ��� <= ģ��-11
        String[] datas = splitString(data, key_len - 11);
        String mi = "";
        // ������ĳ��ȴ���ģ��-11��Ҫ�������
        for (String s : datas) {
            mi += bcd2Str(cipher.doFinal(s.getBytes()));
        }
        return mi;
    }

    /**
     * ˽Կ����
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        // ģ��
        int key_len = privateKey.getModulus().bitLength() / 8;
        byte[] bytes = data.getBytes();
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
        // ������ĳ��ȴ���ģ����Ҫ�������
        String ming = "";
        byte[][] arrays = splitArray(bcd, key_len);
        for (byte[] arr : arrays) {
            ming += new String(cipher.doFinal(arr));
        }
        return ming;
    }

    /**
     * ASCII��תBCD��
     *
     */
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }

    public static byte asc_to_bcd(byte asc) {
        byte bcd;

        if ((asc >= '0') && (asc <= '9'))
            bcd = (byte) (asc - '0');
        else if ((asc >= 'A') && (asc <= 'F'))
            bcd = (byte) (asc - 'A' + 10);
        else if ((asc >= 'a') && (asc <= 'f'))
            bcd = (byte) (asc - 'a' + 10);
        else
            bcd = (byte) (asc - 48);
        return bcd;
    }

    /**
     * BCDת�ַ���
     */
    public static String bcd2Str(byte[] bytes) {
        char temp[] = new char[bytes.length * 2], val;

        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }

    /**
     * ����ַ���
     */
    public static String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str = "";
        for (int i = 0; i < x + z; i++) {
            if (i == x + z - 1 && y != 0) {
                str = string.substring(i * len, i * len + y);
            } else {
                str = string.substring(i * len, i * len + len);
            }
            strings[i] = str;
        }
        return strings;
    }

    /**
     * �������
     */
    public static byte[][] splitArray(byte[] data, int len) {
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];
        byte[] arr;
        for (int i = 0; i < x + z; i++) {
            arr = new byte[len];
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(data, i * len, arr, 0, y);
            } else {
                System.arraycopy(data, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }

    public static void main(String[] args) throws Exception {
		/*HashMap<String, Object> map = RSAUtils.getKeys();
        //���ɹ�Կ��˽Կ
        RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
        RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");

        //ģ
        String modulus = publicKey.getModulus().toString();
        System.err.println("modulus:" + modulus);
        //��Կָ��
        String public_exponent = publicKey.getPublicExponent().toString();
        System.err.println("public_exponent:" + public_exponent);
        //˽Կָ��
        String private_exponent = privateKey.getPrivateExponent().toString();
        System.err.println("private_exponent:" + private_exponent);
        //����
        String ming = "123456789";
        //ʹ��ģ��ָ�����ɹ�Կ��˽Կ
        RSAPublicKey pubKey = RSAUtils.getPublicKey(modulus, public_exponent);
        RSAPrivateKey priKey = RSAUtils.getPrivateKey(modulus, private_exponent);
        //���ܺ������
        String mi = RSAUtils.encryptByPublicKey(ming, pubKey);
        System.err.println("���ģ�"+mi);
        //���ܺ������
        ming = RSAUtils.decryptByPrivateKey(mi, priKey);
        System.err.println(ming);*/
        String jiami = encryptByPublicKey("yinjihuan");
        System.out.println(jiami);
        System.out.println(decryptByPrivateKey("2A531E49E6EE900173A04131F4C8E1AE25F5A8DF2E55E699A321DE6D4ACDAC6AB79B19E9EBF2A2EC505C34B3F6F96DCB242F7FDD0EEAD085A113B37AB74606E16A53CFD2374703D57EECEFC632C5C3FE9B25EA42907020B94DBEFCD83A30A07B1F869B035E6DFFDF18D965FC5B49AB074D1135F5D092EF5385DE384D7F695CE0"));
    }

}
