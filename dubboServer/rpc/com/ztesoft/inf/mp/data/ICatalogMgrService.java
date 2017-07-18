package com.ztesoft.inf.mp.data;

import java.util.List;
import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;

/**
 * 后台数据管理  目录管理
 * @author chen.xinwu
 *
 */
@SuppressWarnings("rawtypes")
public interface ICatalogMgrService {
	
	List getOwnerFromBDP(Map map);
	
	//从bdp获取库数据
	List getSchemasFromBDP(Map m);
	
	//根据schema从bdp获取表数据
	RpcPageModel getTablesFromBDP(Map m);
	
	//根据table从bdp获取字段数据
	List getFieldsFromBDP(Map m);
	
	//从本地库获取库数据
	List getSchemas(Map m);
	
	//根据schema从本地库获取表数据
	RpcPageModel getTables(Map m);
	
	//根据table从本地库获取字段数据
	RpcPageModel getFields(Map m);
	
	//同步数据
	Map synInfo(Map m);

	//删除表信息
	void deleteTables(Map m);

	//删除字段信息
	void deleteField(Map m);

	//修改本地表信息
	void modifyTableInfo(Map m);

	//修改本地字段信息
	void modifyColumnInfo(Map m);

	//获取bdp账期编码
	Map getBdpAccountCode(Map m);
}
