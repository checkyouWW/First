package com.ztesoft.inf.util;

/**
 * 定义/获取redis相关的key值
 */
public class RedisKeyUtil {
	
	/**
	 * 访问者ip
	 * 
	 * @return
	 */
	public static String getVisitIpKey(String ip) {
		return "user:ip:" + ip;
	}

	/**
	 * 拦截ip
	 * 
	 * @return
	 */
	public static String getBlackIpKey(String ip) {
		return "user:balckip:" + ip;
	}

	/**
	 * 获取登录错误次数key值
	 */
	public static String loginErrorTimes(String loginID) {
		return "login:user_id:" + loginID + ":errortimes";
	}
	
	/**
	 * 根据配置参数
	 * 
	 * @return
	 */
	public static String getSysParamByCode(String paramCode) {
		return "dcsystemparam:paramcode:" + paramCode;
	}
	
	/**
	 * 根据配置参数
	 * 
	 * @return
	 */
	public static String getAttrCode(String attr_code) {
		return "attr_spec:attr_code:" + attr_code;
	}
	
	/** 1. 系统登录用户信息对应的key 
	 * @param consumer_session_id */
	public static String getSysUserInfoKey(String staff_id, String consumer_session_id) {
		return "sysuser:info:" + staff_id + ":session:"+consumer_session_id;
	}

	/** 2. 系统登录用户的菜单权限 */
	public static String getSysUserMenuPrivilege(String staff_id, String consumer_session_id) {
		return "sysuser:menupri:" + staff_id + ":session:"+consumer_session_id;
	}

	/** 3. 系统登录用户的按钮权限 */
	public static String getSysUserBtnPrivilege(String staff_id, String consumer_session_id) {
		return "sysuser:btnpri:" + staff_id + ":session:"+consumer_session_id;
	}
}
