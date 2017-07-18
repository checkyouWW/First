package com.ztesoft.inf.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.RandomStringUtils;

public class Base64Util {

	/**
	 * 字符串用64位转换
	 * 
	 * @param data
	 * @return
	 */
	public static String encodeBase64(String data) {
		return encodeBase64(data.getBytes());
	}

	/**
	 * 字符数组用64位转换
	 * 
	 * @param data
	 * @return
	 */
	public static String encodeBase64(byte data[]) {
		int len = data.length;
		StringBuffer ret = new StringBuffer((len / 3 + 1) * 4);
		for (int i = 0; i < len; i++) {
			int c = data[i] >> 2 & 0x3f;
			ret.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(c));
			c = data[i] << 4 & 0x3f;
			if (++i < len) {
				c |= data[i] >> 4 & 0xf;
			}
			ret.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(c));
			if (i < len) {
				c = data[i] << 2 & 0x3f;
				if (++i < len) {
					c |= data[i] >> 6 & 0x3;
				}
				ret.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(c));
			} else {
				i++;
				ret.append('=');
			}
			if (i < len) {
				c = data[i] & 0x3f;
				ret.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(c));
			} else {
				ret.append('=');
			}
		}
		return ret.toString();
	}

	/**
	 * 还原64位的字符串
	 * 
	 * @param data
	 * @return
	 */
	public static String decodeBase64(String data) {
		return decodeBase64(data.getBytes());
	}

	/**
	 * 还原64位的字符数组
	 * 
	 * @param data
	 * @return
	 */
	public static String decodeBase64(byte data[]) {
		int len = data.length;
		StringBuffer ret = new StringBuffer((len * 3) / 4);
		for (int i = 0; i < len; i++) {
			int c = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(data[i]);
			i++;
			int c1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(data[i]);
			c = c << 2 | c1 >> 4 & 0x3;
			ret.append((char) c);
			if (++i < len) {
				c = data[i];
				if (61 == c) {
					break;
				}
				c = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf((char) c);
				c1 = c1 << 4 & 0xf0 | c >> 2 & 0xf;
				ret.append((char) c1);
			}
			if (++i >= len) {
				continue;
			}
			c1 = data[i];
			if (61 == c1) {
				break;
			}
			c1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf((char) c1);
			c = c << 6 & 0xc0 | c1;
			ret.append((char) c);
		}
		return ret.toString();
	}

	public static void main(String[] args) {
		String s1 = RandomStringUtils.randomAlphanumeric(16);
		String USK = Base64Util.encodeBase64(s1);
		System.out.println(s1);
		System.out.println(USK);
		System.out.println(Base64Util.decodeBase64(USK));
	}

}