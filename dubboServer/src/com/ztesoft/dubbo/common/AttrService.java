package com.ztesoft.dubbo.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.common.util.StringUtil;
import com.ztesoft.inf.common.IAttrService;
import com.ztesoft.inf.util.JsonUtil;
import com.ztesoft.inf.util.RedisKeyUtil;
import com.ztesoft.jedis.dao.RedisClient;

import appfrm.app.util.ListUtil;
import appfrm.resource.dao.impl.DAO;
import spring.util.DBUtil;
import spring.util.SpringContextUtil;

/**
 * Description :<br>
 * Copyright 2010 ztesoft<br>
 * Author : zhou.jundi<br>
 * Date : 2012-7-17<br>
 * 
 * Last Modified :<br>
 * Modified by :<br>
 * Version : 1.0<br>
 */

@Controller
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AttrService implements IAttrService {

	public static AttrService getInstance() {
		return (AttrService) SpringContextUtil.getBean("attrService");
	}

	/**
	 * 1. 根据attr_code获取对应attr_value值集合
	 */
	@Transactional
	public List getStaticAttr(String attr_code) {
		List values = this.getAttrByRedis(attr_code); // 取缓存
		if (values == null || values.size() == 0) {
			String sql = "select t1.attr_code, t1.attr_name, t2.attr_value_id, t2.attr_value, "
					+ "t2.attr_value_desc attr_value_name, t2.SORTBY, t2.parent_value_id "
					+ "from dc_attribute t1, dc_attr_value t2 "
					+ "where t1.attr_id = t2.attr_id and t1.state = '00A' and t1.attr_code  = ? ";
			values = DAO.queryForMap(sql, attr_code);
			if(values == null || values.size() == 0){
				values = AttrService.getByDcSql(attr_code);
			}
			if(values != null){
				this.setAttrByRedis(attr_code, values);
			}
		}
		return values;
	}
	/**
	 * 1. 根据attr_code获取对应attr_value值集合 参数为Map
	 */
	@Transactional
	public List getStaticAttrValue(Map params){
		String attr_code = (String)params.get("attr_code");
		return getStaticAttr(attr_code);
	}
	/**
	 * 2. 根据attr_code和attr_value值，获取attr_value_name值，翻译
	 * 
	 * @param map
	 * @return
	 */
	@Transactional
	public String getStaticAttr(Map params) {
		String attr_code = StringUtil.getStrValue(params, "attr_code");
		String attr_value = StringUtil.getStrValue(params, "attr_value");
		List list = getStaticAttr(attr_code);
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			if (attr_value.equals(StringUtil.getStrValue(map, "attr_value"))) {
				return StringUtil.getStrValue(map, "attr_value_name");
			}
		}
		return "";
	}

	/**
	 * 3. 初始化所有静态数据
	 */
	@Transactional
	public void initStaticValue() {
		String sql = "select DISTINCT attr_code from dc_attribute where state='00A'";
		List list = DAO.queryForMap(sql, new String[] {});
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			String attr_code = StringUtil.getStrValue(map, "attr_code");
			getStaticAttr(attr_code);
		}
	}

	/**
	 * 4 根据上级值获取下级选择值
	 * 
	 * @param params
	 * @return
	 */
	@Transactional
	public List<Map> getSubStaticAttr(Map params) {
		String attrCode = (String) params.get("attr_code");
		String parentValue = (String) params.get("parent_value_id");
		return this.getSubStaticAttr(attrCode, parentValue);
	}

	/**
	 * 根据上级值获取下级选择值
	 */
	private List<Map> getSubStaticAttr(String attrCode, String parentValue) {
		List<Map> subList = getStaticAttr(attrCode);
		List<Map> filtList = new ArrayList<Map>();
		if (!StringUtil.isEmpty(parentValue)) {
			for (int i = 0; i < subList.size(); i++) {
				Map attrValue = subList.get(i);
				if (parentValue.equals(StringUtil.getStrValue(attrValue,
						"parent_value_id"))) {
					filtList.add(attrValue);
				}
			}
		}
		return filtList;
	}
	
	/**
	 * 根据dc_sql获取下拉值
	 */
	public static List getByDcSql(String dc_name) {
		try {
			String dc_sql = DAO.querySingleValue(
					"select dc_sql from dc_sql where dc_name = ? ", new String[]{dc_name});
			if(dc_sql == null || "".equals(dc_sql)){
				return new ArrayList();
			}
			List<Map> vos = DBUtil.getJdbcTemplate().query(dc_sql, new ParameterizedRowMapper<Map>(){
				public Map mapRow(ResultSet rs,int rowNum) throws SQLException{  
					int columCount =  rs.getMetaData().getColumnCount();
					Map map = new HashMap();
					map.put("attr_value", rs.getString(1));
					map.put("attr_value_name", rs.getString(2));
					if(columCount > 2) {
						map.put("parent_value_id", rs.getString(3));
					}
					
					return map;
				}
			}); 
			return vos;
		} catch (Exception e) {
			System.out.println(dc_name);
			e.printStackTrace();
			return new ArrayList();
		}
	}

	/**
	 * 清除所以缓存
	 */
	public void clearAll() {
		
	}

	/**
	 * 清除某个缓存
	 * 
	 * @param name
	 */
	public void clearByName(String attr_code) {
		RedisClient redisClient = new RedisClient();
		String key = RedisKeyUtil.getAttrCode(attr_code);
		
		redisClient.del(key);
	}
	
	private List getAttrByRedis(String attr_code){
		RedisClient redisClient = new RedisClient();
		String key = RedisKeyUtil.getAttrCode(attr_code);
		String json = redisClient.get(key);
		List result = new ArrayList();
		if(StringUtil.isNotEmpty(json)){
			result = JsonUtil.fromJson(json, List.class);
		}
		return result;
	}
	
	private void setAttrByRedis(String attr_code, List attrList){
		if(StringUtil.isEmpty(attr_code) || ListUtil.isEmpty(attrList)){
			return;
		}
		RedisClient redisClient = new RedisClient();
		String key = RedisKeyUtil.getAttrCode(attr_code);
		String json = JsonUtil.toJson(attrList);
		redisClient.set(key, json);
	}
}
