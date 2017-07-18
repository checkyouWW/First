package com.ztesoft.common.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Description: 加密解密工具类，服务器相互之间使用
 * @author yin.linping
 * @date 2016-5-6
 * @version V1.0
 */
public class AES128Util {

	/**密钥明文，必须是16位的*/
	private static final String SecretKey = "AAAabcdefghijklm";

	/**
	 * 加密
	 * 
	 * @param sSrc
	 * @return
	 */
	public static String getEncryptStr(String sSrc) {
		try {
			if (SecretKey == null) {
				System.out.print("Key为空null");
				return null;
			}
			// 判断Key是否为16位
			if (SecretKey.length() != 16) {
				System.out.print("Key长度不是16位");
				return null;
			}
			byte[] raw = SecretKey.getBytes("utf-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// "算法/模式/补码方式"
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
			// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
			return new Base64().encodeToString(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 * 
	 * @param sSrc
	 * @return
	 */
	public static String getDecryptStr(String sSrc) {
		try {
			// 判断Key是否正确
			if (SecretKey == null) {
				System.out.print("Key为空null");
				return null;
			}
			// 判断Key是否为16位
			if (SecretKey.length() != 16) {
				System.out.print("Key长度不是16位");
				return null;
			}
			byte[] raw = SecretKey.getBytes("utf-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted1 = new Base64().decode(sSrc);// 先用base64解密
			try {
				byte[] original = cipher.doFinal(encrypted1);
				String originalString = new String(original, "utf-8");
				return originalString;
			} catch (Exception e) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 测试
	public static void main(String[] args) {
		String s = "dasdasd129013eqw13";
		System.out.println("原始：" + s);
		s = AES128Util.getEncryptStr(s);
		System.out.println("加密：" + s);
		s = AES128Util.getDecryptStr(s);
		System.out.println("解密：" + s);
	}
}
