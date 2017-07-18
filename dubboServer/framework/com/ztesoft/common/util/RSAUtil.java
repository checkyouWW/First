package com.ztesoft.common.util;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateKeySpec;

import javax.crypto.Cipher;

/**
 * @Description: 密码解密工具类(JvascriptS公钥加密)，通过RSAKeyUtil.java生成配置文件
 * @author yin.linping
 * @date 2016-5-21
 * @version V1.0
 */
public class RSAUtil {

	public static final java.security.Provider provider = new org.bouncycastle.jce.provider.BouncyCastleProvider();

	/** 模 */
	public static final String MODULUS = "6837127824580763455699834286214827717922808387737124998496630420251252881809727784551287007453185126695223991178736636563349947907413646943913911713411241";
	/** 公钥指数 */
	public static final String PUBLIC_EXPONENT = "65537";
	/** 私钥指数 */
	private static final String PRIVATE_EXPONENT = "LnEDJTZyOgfKNiFBAsaGkJ5TVt/1U/MaRMiu5opk6/btJIxIVwOVdQsAOaLDmxH0UOnHXQLgLq/EGxdz2FnHi8kCg22uUlor5lXK9+lqBH0X9xoW0uQr14jta/I+u0n15iZ3/eCEskWszgu+i6GP7zq07vGUKbFJAYV6/nfE7mUZwXrrvHAd0t0B7KSGeUMjWxk4DKeCvXMOVwqpzo36TQ==";

	/**
	 * 解密，在JS公钥加密，回来私钥解密
	 * 
	 * @param encrypted 密文
	 * @return
	 */
	public static String decrypt(String encrypted) {
		Cipher dec;
		try {
			try {
				dec = Cipher.getInstance("RSA/NONE/NoPadding");
			} catch (Exception e) {
				dec = Cipher.getInstance("RSA/NONE/NoPadding", provider);
			}
			// 对私钥进行解密
			String priExponent = AES128Util.getDecryptStr(PRIVATE_EXPONENT);
			RSAPrivateKey privateKey = getPrivateKey(MODULUS, priExponent);
			dec.init(Cipher.DECRYPT_MODE, privateKey);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException("RSA algorithm not supported", e);
		}
		String[] blocks = encrypted.split("\\s");
		StringBuffer result = new StringBuffer();
		try {
			for (int i = blocks.length - 1; i >= 0; i--) {
				byte[] data = hexStringToByteArray(blocks[i]);
				byte[] decryptedBlock = dec.doFinal(data);
				result.append(new String(decryptedBlock, "UTF-8"));
			}
		} catch (Exception e) {
			// throw new RuntimeException("Decrypt error", e);
		}
		return result.reverse().toString().substring(2);
	}

	/**
	 * 获取私钥
	 * 
	 * @param modulus
	 * @param exponent
	 *            私钥指数
	 * @return
	 */
	private static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
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

	private static byte[] hexStringToByteArray(String data) {
		int k = 0;
		byte[] results = new byte[data.length() / 2];
		for (int i = 0; i < data.length();) {
			results[k] = (byte) (Character.digit(data.charAt(i++), 16) << 4);
			results[k] += (byte) (Character.digit(data.charAt(i++), 16));
			k++;
		}
		return results;
	}

	/**JS侧使用*/
	public static String getPublicKeyExponent() {
		BigInteger bigInt = new BigInteger(PUBLIC_EXPONENT);
		return bigInt.toString(16);
	}

	/**JS侧使用*/
	public static String getPublicKeyModulus() {
		BigInteger bigInt = new BigInteger(MODULUS);
		return bigInt.toString(16);
	}

	/**JS侧使用*/
	public static int getMaxDigits(int keyLength) {
		return ((keyLength * 2) / 16) + 3;
	}

	// 测试
	public static void main(String[] args) {
		System.out.println(RSAUtil.decrypt("2dcec7f94b677a4b70e8f5dc87661426ac5e1c1dcb0e3dd3621f4a23fc0183c41e78ddd92e465f00bcaa4e908245b1abbeaa4a4d469f73b723850aa65879a2df"));
	}

}
