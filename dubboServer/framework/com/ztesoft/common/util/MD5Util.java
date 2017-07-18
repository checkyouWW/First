package com.ztesoft.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

public class MD5Util {
	
	private final static String SALT = "#@*A12^c0+";
	
	/**
	 * 对字符串使用加盐的方式进行MD5加密
	 * 
	 * @param data 原始字符串
	 * @param salt 加盐字符串
	 * @return 加密字符串
	 */
	public static String encrypt(String data, String salt) {
		if (StringUtils.isNotEmpty(data) && StringUtils.isNotEmpty(salt)) {
			return DigestUtils.md5Hex(data + "{" + SALT + salt + "}");
		}
		return data;
	}
 
    /** 
     *  
     * @param plain  明文 
     * @return 32位小写密文 
     */ 
    public static String encryption(String plain) { 
        String re_md5 = new String(); 
        try { 
            MessageDigest md = MessageDigest.getInstance("MD5"); 
            md.update(plain.getBytes()); 
            byte b[] = md.digest(); 
 
            int i; 
 
            StringBuffer buf = new StringBuffer(""); 
            for (int offset = 0; offset < b.length; offset++) { 
                i = b[offset]; 
                if (i < 0) 
                    i += 256; 
                if (i < 16) 
                    buf.append("0"); 
                buf.append(Integer.toHexString(i)); 
            } 
 
            re_md5 = buf.toString(); 
 
        } catch (NoSuchAlgorithmException e) { 
            e.printStackTrace(); 
        } 
        return re_md5; 
    }
    
    public static String MD5(String str){
    	return null;
    }

}
