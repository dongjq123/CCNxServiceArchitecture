package edu.bupt.sia.ccn;

import com.alibaba.fastjson.JSON;
import org.ccnx.ccn.protocol.ContentName;

import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;

import com.sun.crypto.provider.SunJCE;

import java.io.Serializable;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fish on 16-4-20.
 */
public class ServiceNameParser {
    public static ServiceNameObject getServiceName(ContentName name) {
        int count = name.count();
        ServiceNameObject serviceNameObject = new ServiceNameObject();
        serviceNameObject.setContentName(name.toURIString());
        LinkedList<String> argslist = new LinkedList<>();
        for (int i = 0; i < count; ++i) {
            String tmp = name.stringComponent(i);
            try {
                tmp = URLDecoder.decode(tmp, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (tmp.charAt(0) == '{' && tmp.charAt(tmp.length() - 1) == '}') {
                tmp = tmp.substring(1, tmp.length()-1);
                String[] t = tmp.split(":");
                switch (t[0]){
                    case "servicename":
                        serviceNameObject.setServiceName(t[1]);
                        break;
                    case "type":
                        serviceNameObject.setType(t[1]);
                        break;
                    case "version":
                        serviceNameObject.setVersion(t[1]);
                        break;
                    case "args":
                        //String arg = t[1].substring(1,t[1].length()-1);
                        argslist.add(t[1]);
                        break;
                }
//                serviceNameObject = JSON.parseObject(tmp, ServiceNameObject.class);
            }
        }
        Iterator<String> it = argslist.iterator();
        String[] arr = new String[argslist.size()];
        for (int i = 0; it.hasNext() ; i++) {
            arr[i] = it.next();
        }
        serviceNameObject.setArgs(arr);
        System.out.println(serviceNameObject);
        return serviceNameObject;
    }

    byte[] encryptKey;
    DESedeKeySpec spec;
    SecretKeyFactory keyFactory;
    SecretKey theKey;
    Cipher cipher;
    IvParameterSpec IvParameters;

    public ServiceNameParser() {
        try {
            // 检测是否有 TripleDES 加密的供应程序
            // 如无，明确地安装SunJCE 供应程序
            try {
                Cipher c = Cipher.getInstance("DESede");
            } catch (Exception e) {
                System.err.println("Installling SunJCE provider.");
                Provider sunjce = new com.sun.crypto.provider.SunJCE();
                Security.addProvider(sunjce);
            }
            // 创建一个密钥
            encryptKey = "This is a test DESede Key".getBytes();
            // 为上一密钥创建一个指定的 DESSede key
            spec = new DESedeKeySpec(encryptKey);
            // 得到 DESSede keys
            keyFactory = SecretKeyFactory.getInstance("DESede");
            // 生成一个 DESede 密钥对象
            theKey = keyFactory.generateSecret(spec);
            // 创建一个 DESede 密码
            cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            // 为 CBC 模式创建一个用于初始化的 vector 对象
            IvParameters =
                    new IvParameterSpec(new byte[]{12, 34, 56, 78, 90, 87, 65, 43});
        } catch (Exception exc) {
        // 记录加密或解密操作错误
        }
    }

    /**
     * 加密算法
     *
     * @param password 等待加密的密码
     * @return 加密以后的密码
     * @throws Exception
     */
    public byte[] encrypt(String password) {
        String encrypted_password = null;
        byte[] encrypted_pwd = null;
        try {
            // 以加密模式初始化密钥
            cipher.init(Cipher.ENCRYPT_MODE, theKey, IvParameters);
            // 加密前的密码（旧）
            byte[] plainttext = password.getBytes();
            // 加密密码
            encrypted_pwd = cipher.doFinal(plainttext);
            // 转成字符串，得到加密后的密码（新）
            encrypted_password = new String(encrypted_pwd);
        } catch (Exception ex) {
            // 记录加密错误
        }
        return encrypted_pwd;
    }

    /**
     * 解密算法
     *
     * @param password 加过密的密码
     * @return 解密后的密码
     */
    public String decrypt(byte[] password) {
        String decrypted_password = null;
        try {
            // 以解密模式初始化密钥
            cipher.init(Cipher.DECRYPT_MODE, theKey, IvParameters);
            // 构造解密前的密码
            byte[] decryptedPassword = password;
            // 解密密码
            byte[] decrypted_pwd = cipher.doFinal(decryptedPassword);
            // 得到结果
            decrypted_password = new String(decrypted_pwd);
        } catch (Exception ex) {
            // 记录解密错误
        }
        return decrypted_password;

    }

}
