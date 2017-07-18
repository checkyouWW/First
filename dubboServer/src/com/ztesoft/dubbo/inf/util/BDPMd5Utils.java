package com.ztesoft.dubbo.inf.util;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
public class BDPMd5Utils {
	private final static String SALT = "#@*A12^c0+";

	/**
	 * 使用MD5加密算法对字符串加密
	 * 
	 * @param oriCode
	 *            原始字符串
	 * @return 加密字符串
	 */
	public static String encrypt(String oriCode) {
		return encrypt(oriCode, SALT);
	}

	/**
	 * 对字符串使用加盐的方式进行MD5加密
	 * 
	 * @param oriCode
	 *            原始字符串
	 * @param salt
	 *            加盐字符串
	 * @return 加密字符串
	 */
	public static String encrypt(String oriCode, String salt) {
		if (StringUtils.isNotEmpty(oriCode) && StringUtils.isNotEmpty(salt)) {
			return DigestUtils.md5Hex(oriCode + "{" + salt + "}");
		}
		return oriCode;
	}
	
	public static void main(String[] args) {
		String str = encrypt("111aaa","1010101");
		System.out.println(str);
	}
}
