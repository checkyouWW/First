package com.ztesoft.inf.common;

import java.util.List;
import java.util.Map;

public interface IAttrService {

	/**
	 * 1. 根据attr_code获取对应attr_value值集合
	 */
	public List getStaticAttr(String attr_code);
	
	/**
	 * 2. 根据attr_code和attr_value值，获取attr_value_name值，翻译
	 * 
	 * @param map
	 * @return
	 */
	public String getStaticAttr(Map params);
	
	/**
	 * 3. 初始化所有静态数据
	 */
	public void initStaticValue();

	/**
	 * 4 根据上级值获取下级选择值
	 * 
	 * @param params
	 * @return
	 */
	public List<Map> getSubStaticAttr(Map params);

	/**
	 * 清除所以缓存
	 */
	public void clearAll();

	/**
	 * 清除某个缓存
	 * 
	 * @param name
	 */
	public void clearByName(String name);
}
