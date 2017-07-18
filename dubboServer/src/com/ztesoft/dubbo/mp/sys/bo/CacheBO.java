package com.ztesoft.dubbo.mp.sys.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;

import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.inf.util.RedisKeyUtil;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.jedis.dao.RedisClient;

import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;
import redis.clients.jedis.Jedis;

/**
 * 缓存管理
 * 
 * @author
 *
 */
@SuppressWarnings({ "all" })
public class CacheBO {
	
	public RpcPageModel getAttrList(Map params) {
		RedisClient redisClient = new RedisClient();
		
		int pageIndex = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int pageSize = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String search_attr_code = StringUtil.getStrValue(params, "attr_code");
		String sql = 
				"select * from (select attr_code,attr_name from dc_attribute"
				+ " union all "
				+ "select dc_name as attr_code,dc_desc as attr_name from dc_sql) t where 1=1";
		List<String> sqlParams = new ArrayList<String>();
		if(StringUtil.isNotEmpty(search_attr_code)){
			sql += " and attr_code like ?";
			sqlParams.add("%"+search_attr_code+"%");
		}
		PageModel page = DAO.queryForPageModel(sql, pageSize, pageIndex, sqlParams);
		List list = page.getList();
		if(list != null){
			for(int i=0; i<list.size(); i++){
				Map map = (Map) list.get(i);
				String attr_code = (String) map.get("attr_code");
				String key = RedisKeyUtil.getAttrCode(attr_code);
				String json = redisClient.get(key);
				map.put("json", json);
			}
		}
		return PageModelConverter.pageModelToRpc(page);
	}

	public RpcPageModel getDcSystemParamList(Map params) {
		RedisClient redisClient = new RedisClient();
		int pageIndex = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int pageSize = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String search_param_code = StringUtil.getStrValue(params, "param_code");
		String sql = "select * from dc_system_param where 1=1";
		List<String> sqlParams = new ArrayList<String>();
		if(StringUtil.isNotEmpty(search_param_code)){
			sql += " and param_code like ?";
			sqlParams.add("%"+search_param_code+"%");
		}
		PageModel page = DAO.queryForPageModel(sql, pageSize, pageIndex, sqlParams);
		List list = page.getList();
		if(list != null){
			for(int i=0; i<list.size(); i++){
				Map map = (Map) list.get(i);
				String param_code = (String) map.get("param_code");
				String key = RedisKeyUtil.getSysParamByCode(param_code);
				String json = redisClient.get(key);
				map.put("json", json);
			}
		}
		return PageModelConverter.pageModelToRpc(page);
	}

	public Map refresh(Map params) {
		String type = StringUtil.getStrValue(params, "type");
		String value = StringUtil.getStrValue(params, "value");
		String action = StringUtil.getStrValue(params, "action");
		RedisClient redisClient = new RedisClient();
		
		String key = null;
		//静态数据
		if("attr".equals(type)){
			key = RedisKeyUtil.getAttrCode(value);
			if("all".equals(action)){
				this.refreshByPattern("attr_spec:attr_code:*");
			}
		}
		//系统参数
		else if("param".equals(type)){
			key = RedisKeyUtil.getSysParamByCode(value);
			if("all".equals(action)){
				this.refreshByPattern("dcsystemparam:paramcode:*");
			}
		}
		
		if(StringUtil.isNotEmpty(key)){
			redisClient.del(key);
		}
		
		Map result = new HashMap();
		result.put("success", true);
		return result;
	}

	private void refreshByPattern(String pattern) {
		RedisClient redisClient = new RedisClient();
		Collection<Jedis> jedises = redisClient.getAllShards();
		for(Jedis jedis : jedises){
			Set<String> paramKeys = jedis.keys(pattern);
			if(paramKeys != null && !paramKeys.isEmpty()){
				Iterator<String> it = paramKeys.iterator();
				while(it.hasNext()){
					String all_key_one = it.next();
					redisClient.del(all_key_one);
				}
			}
		}
		
	}

}
