package com.ztesoft.common.util;

import com.ztesoft.inf.common.ISysConfigService;
import com.ztesoft.inf.util.RedisKeyUtil;
import com.ztesoft.jedis.dao.RedisClient;

import spring.util.SpringContextUtil;

@SuppressWarnings({ "all" })
public class DcSystemParamUtil {
	
	// 该方法用于在BO理直接调用 不经过框架 不经过缓存
	public static String getSystemParam(String paramCode) {
		ISysConfigService service = (ISysConfigService) SpringContextUtil.getBean("sysConfigService");
		String value = service.getSystemParamByCode(paramCode);
		return value;
	}
	
	public static String getBdpSystemParam(String paramCode) {
		ISysConfigService service = (ISysConfigService) SpringContextUtil.getBean("sysConfigService");
		String value = service.getBdpSystemParamByCode(paramCode);
		return value;
	}
	
	public static void loadDcSystemParam() {
		ISysConfigService service = (ISysConfigService) SpringContextUtil.getBean("sysConfigService");
		service.loadDcSystemParam();
	}

	/**
	 * 根据系统参数编码获取系统参数 该方法只能在BO里面调用 不经过框架
	 * 
	 * @return
	 */
	public static String getSysParamByCache(String paramCode) {
		RedisClient redisClient = new RedisClient();
		String redisKey = RedisKeyUtil.getSysParamByCode(paramCode);
		String value = redisClient.get(redisKey);
		if (value != null) {
			return value;
		} else {// 如果没有，则到数据库里面查询
			value = DcSystemParamUtil.getSystemParam(paramCode);
			return value;
		}
	}

	public static String getBdpSysParamByCache(String paramCode) {
		RedisClient redisClient = new RedisClient();
		String redisKey = RedisKeyUtil.getSysParamByCode(paramCode);
		String value = redisClient.get(redisKey);
		if (value != null) {
			return value;
		} else {// 如果没有，则到数据库里面查询
			value = DcSystemParamUtil.getBdpSystemParam(paramCode);
			return value;
		}
	}
}
