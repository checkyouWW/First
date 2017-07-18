package com.ztesoft.inf.util.preventattack;


import org.apache.log4j.Logger;

import com.ztesoft.inf.util.RedisKeyUtil;
import com.ztesoft.jedis.dao.RedisClient;

/**
 * 预防攻击
 * @author huang.shaobin
 *
 */
public class PreventIpAttack implements Runnable {  
  
    private static Logger log = Logger.getLogger(PreventIpAttack.class);  
    
    private String ip;
    
    private long currentTimeMillis;
    
    public PreventIpAttack() {
	}
    
    public PreventIpAttack(String ip,long currentTimeMillis) {
    	this.ip = ip;
    	this.currentTimeMillis = currentTimeMillis;
    }
   

    /**
     * 请求处理
     * @Description：: </br>
     * @author：huang.shaobin</br>
     * @date：2016年5月20日</br>
     */
	@Override
	public void run() {
		boolean isOverflow = false;  
		RedisClient redisClient = new RedisClient();
		String visitIpKey = RedisKeyUtil.getVisitIpKey(ip);
		
		long end = currentTimeMillis;
		long start = end-PreventIpAttackHelper.period_min;
		
		//最小时间段内访问次数
		long perCount = redisClient.zcount(visitIpKey, start,end);
		//System.out.println(perCount);
		//请求次数大于设定的最大请求数（200次）并且在10秒内，这被认为非法请求,并且加入黑名单
		if(perCount >= PreventIpAttackHelper.maxConcurrentRequests){ 
			String blackIpKey = RedisKeyUtil.getBlackIpKey(ip);
			redisClient.set(blackIpKey, ip);
			redisClient.expire(blackIpKey, PreventIpAttackHelper.period_max); //加入黑名单
			isOverflow = true;
		}else{
			//删除当前时间的计数
			redisClient.zremrangeByScore(visitIpKey, 0, start);
			//将访问ip key 添加到redis缓存计数
			redisClient.zadd(visitIpKey, end, end+"");
		}
    	
        if (isOverflow) {  
        	if (log.isInfoEnabled()) {
        		log.info(" ip "+ip+" has reached the threshold "+PreventIpAttackHelper.maxConcurrentRequests+" in "+PreventIpAttackHelper.period_min+" second, block it!");
			}
        }  
	}
    
    
}  
