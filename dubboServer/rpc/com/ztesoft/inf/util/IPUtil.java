package com.ztesoft.inf.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @Description: 获取请求服务器的IP地址
 * @author zhao.jingang  
 * @date 2016-5-9 上午10:23:18 
 * @version V1.0
 */
public class IPUtil {
	/**
	 * get IP from request
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
