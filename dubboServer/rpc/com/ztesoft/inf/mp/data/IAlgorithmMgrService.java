package com.ztesoft.inf.mp.data;

import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;

/**
 * 后台数据管理  脱敏管理
 * @author chen.xinwu
 *
 */
@SuppressWarnings("rawtypes")
public interface IAlgorithmMgrService {
	
	//获取算法列表
	public RpcPageModel getAlgorithmPage(Map m);
	
	//获取字段列表
	public RpcPageModel getFieldPage(Map m);
	
	//新增算法
	public Map addAlgorithm(Map m);
	
	//新增字段
	public Map addField(Map m);
	
//	public Map updateAlgorithm(Map m);
//	
//	public Map updateField(Map m);
	
	//上架算法
	public Map upAlgorithm(Map m);
	
	//上架字段
	public Map upField(Map m);
	
	//下架算法
	public Map downAlgorithm(Map m);
	
	//下架字段
	public Map downField(Map m);
	
	//删除算法
	public Map deleteAlgorithm(Map m);
	
	//删除字段
	public Map deleteFiled(Map m);
	
}
