package com.ztesoft.inf.mp.data;

import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;


/**
 * 后台数据管理  数据保障
 * @author chen.xinwu
 *
 */
@SuppressWarnings("rawtypes")
public interface IDataGuardService {

	//获取数据保障列表
	RpcPageModel getDataGuards(Map m);
	
	//获取数据保障信息（alert）
	Map getDataGuardInfo(Map m);

	//保存数据保障信息
	void saveServiceInstAlert(Map m);
	
}
