package com.ztesoft.inf.util.preventattack;

import org.springframework.stereotype.Service;

import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RedisKeyUtil;
import com.ztesoft.jedis.dao.RedisClient;

/**
 * @Description: PreventIpAttack辅助类 </br>
 * @author： huang.shaobin</br>
 * @date： 2016年5月21日
 */
@Service
public class PreventIpAttackHelper {
    
	public static boolean enableIpattack = false; //是否启用ipattack
	
    public static int maxConcurrentRequests ;  //单位时间允许最大请求数 200
    
    public static int period_min ; // 10 second  
    
    public static int period_max ; // 1800 second  超过时间定时清除
    
    static{
    	// 取缓存获取basePath
    	RedisClient redisClient = new RedisClient();
    	enableIpattack = getAsInt(redisClient.get(RedisKeyUtil.getSysParamByCode(KeyValues.IPATTACK_ENABLE))) == 1 ?true:false;
    	maxConcurrentRequests = getAsInt(redisClient.get(RedisKeyUtil.getSysParamByCode(KeyValues.IPATTACK_MAXCONCURRENTREQUESTS)),200);
    	period_min = getAsInt(redisClient.get(RedisKeyUtil.getSysParamByCode(KeyValues.IPATTACK_PERIOD_MIN)),10)*1000;
    	period_max = getAsInt(redisClient.get(RedisKeyUtil.getSysParamByCode(KeyValues.IPATTACK_PERIOD_MAX)),1800)*1000;
    }
    
    /**
	 * 字符串转换成数字，转换出错或者为空则返回-1
	 * 
	 * @param str
	 * @return int
	 */
	public static int getAsInt(String str) {
		return getAsInt(str, -1);
	}

	/**
	 * 字符串转换成数字，转换出错或者为空则返回defaultv
	 * 
	 * @param str
	 * @param defaultv
	 * @return
	 */
	public static int getAsInt(String str, int defaultv) {
		if (str == null || "".equals(str)) {
			return defaultv;
		}
		try {
			return Integer.parseInt(str, 10);
		} catch (Exception e) {
			return defaultv;
		}
	}
}
