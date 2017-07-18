package com.ztesoft.dubbo.common;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.common.util.StringUtil;
import com.ztesoft.inf.common.ISysConfigService;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RedisKeyUtil;
import com.ztesoft.jedis.dao.RedisClient;

import appfrm.resource.dao.impl.DAO;
import comx.order.inf.IContext;
import net.buffalo.service.invoker.ContextMeta;
import spring.util.DBUtil;

/**
 * 
 * @Description: 系统配置相关
 * @author zhao.jingang
 * @date 2016-5-15 上午09:10:09
 * @version V1.0
 */
@Service
public class SysConfigService implements ISysConfigService {

	/**
	 * 获取系统参数，写入redis
	 */
	@Transactional
	@ContextMeta(cls = IContext.DwrWebContext)
	public void loadDcSystemParam() {
		String querySql = "select param_code,param_val from dc_system_param";
		List<Map<String, Object>> list = DBUtil.getJdbcTemplate().queryForList(querySql);
		RedisClient redisClient = new RedisClient();
		for (Map map : list) {
			String param_code = StringUtil.getStrValue(map, "param_code");
			String param_val = StringUtil.getStrValue(map, "param_val");
			String redisKey = RedisKeyUtil.getSysParamByCode(param_code);
			redisClient.set(redisKey, param_val);
		}
	}

	/**
	 * 获取一个系统参数
	 * 
	 * @param param_code
	 * @return
	 */
	@Transactional
	@ContextMeta(cls = IContext.DwrWebContext)
	public String getSystemParamByCode(String param_code) {
		// 1. 查询数据库
		String sql = " select param_val from dc_system_param where param_code=? ";
		String param_val = DAO.querySingleValue(sql, new String[] { param_code });
		// 2. 放入缓存
		RedisClient redisClient = new RedisClient();
		String redisKey = RedisKeyUtil.getSysParamByCode(param_code);
		redisClient.set(redisKey, param_val);
		return param_val;
	}
	
	/**
	 * 获取BDP系统参数
	 * 
	 * @param param_code
	 * @return
	 */
	@Transactional
	@ContextMeta(cls = IContext.DwrWebContext)
	public String getBdpSystemParamByCode(String param_code) {
		// 1. 查询数据库
		String sql = " select param_val from dc_system_param where param_code=? ";
		Connection conn = IContext.getContext().getConnection(KeyValues.DATASOURCE_BDP);
		String param_val = DAO.querySingleValue(conn, sql, new String[] { param_code });
		// 2. 放入缓存
		RedisClient redisClient = new RedisClient();
		String redisKey = RedisKeyUtil.getSysParamByCode(param_code);
		redisClient.set(redisKey, param_val);
		return param_val;
	}

}
