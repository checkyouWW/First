package com.ztesoft.common.util;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ztesoft.inf.util.RedisKeyUtil;
import com.ztesoft.jedis.dao.RedisClient;

/**
 * @Description: 用户信息加密工具类
 * @author yin.linping  
 * @date 2016-5-6
 * @version V1.0
 */
public class AES128 {
	private static final String aes128CipherKey = "aes128CipherKey";
	private static final String SecretKey = "SecretKey";

	public static String getEncryptStr(String sSrc) {
		String sKey = getSystemParamByCode(SecretKey);
		try {
			String offset = getOffset();
			return Encrypt(sSrc, sKey, offset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getDecryptStr(String sSrc) {
		String sKey = getSystemParamByCode(SecretKey);
		try {
			String offset = getOffset();
			return Decrypt(sSrc, sKey, offset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取偏移量
	 * @return
	 * @throws Exception 
	 */
	public static String getOffset() throws Exception {
		String cipherKey = getSystemParamByCode(aes128CipherKey);
		String sKey = getSystemParamByCode(SecretKey);
		String offset = AES128.Decrypt(cipherKey, sKey, sKey);
		return offset;
	}

	/**
	 *  加密
	 * sSrc: 明文 
	 * sKey：秘钥
	 * offset: 偏移量
	 */
	public static String Encrypt(String sSrc, String sKey, String offset) throws Exception {
		if (sKey == null) {
			System.out.print("Key为空null");
			return null;
		}
		// 判断Key是否为16位
		if (sKey.length() != 16) {
			System.out.print("Key长度不是16位");
			return null;
		}
		byte[] raw = sKey.getBytes("utf-8");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		IvParameterSpec iv = new IvParameterSpec(offset.getBytes());
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

		return new Base64().encodeToString(encrypted);// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
	}

	/**
	 *  解密
	 * sSrc: 密文 
	 * sKey：秘钥
	 * offset: 偏移量
	 */
	public static String Decrypt(String sSrc, String sKey, String offset) throws Exception {
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
			byte[] raw = sKey.getBytes("utf-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(offset.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = new Base64().decode(sSrc);// 先用base64解密
			try {
				byte[] original = cipher.doFinal(encrypted1);
				String originalString = new String(original, "utf-8");
				return originalString;
			} catch (Exception e) {
				System.out.println(e.toString());
				return null;
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return null;
		}
	}

	public static String getSystemParamByCode(String param_code) {
		String redisKey = RedisKeyUtil.getSysParamByCode(param_code);
		RedisClient redisClient = new RedisClient();
		String value = redisClient.get(redisKey);
		if (StringUtils.isEmpty(value)) {
			String path = "spring" + File.separator + "applicationContext.xml";
			ApplicationContext ac = new ClassPathXmlApplicationContext(path);

			//ISysConfigService service = (ISysConfigService) ac.getBean("sysConfigService");
			//value = service.getSystemParamByCode(param_code);
			redisClient.setnx(redisKey, value);
		}
		return value;
	}

	/**
	 * 加密用户某些信息
	 * @param map
	 */
	public static void encryptUserInfo(Map map) {
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			if (map.get(key) == null) {
				continue;
			}
			String value = map.get(key).toString();
			if ("phone".equals(key) || "parmit_num".equals(key) || "email".equals(key) || "user_name".equals(key) || "idcard".equals(key)) {
				value = AES128.getEncryptStr(value);
				map.put(key, value);
			}
		}
	}

	/**
	 * 解密用户某些信息
	 * @param map
	 */
	public static void decryptUserInfo(Map map) {
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			if (map.get(key) == null) {
				continue;
			}
			String value = map.get(key).toString();
			if ("phone".equals(key) || "parmit_num".equals(key) || "email".equals(key) || "user_name".equals(key) || "idcard".equals(key)) {
				value = AES128.getDecryptStr(value);
				map.put(key, value);
			}
		}
	}

}
