package com.ztesoft.inf.mp.data;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;

/**
 * 后台数据管理
 * @author liang.zijian
 *
 */
@SuppressWarnings("rawtypes")
public interface IDataMgrService {

	/**
	 * 获取数据树
	 * @param params
	 * @return
	 */
	public List getCatalog(Map params);
	
	/**
	 * 获取数据能力列表
	 * @param params
	 * @return
	 */
	public RpcPageModel getAbilityList(Map params);
	
	/**
	 * 下架服务
	 * @param params
	 * @return
	 */
	public Map disabledService(Map params);
	
	/**
	 * 上架服务
	 * @param params
	 * @return
	 */
	public Map enabledService(Map params);
	
	/**
	 * 根据service_id获取数据服务
	 * @param params
	 * @return
	 */
	public Map getDataServiceById(Map params);
	
	
	/**
	 * 获取可用的来源平台下拉值
	 * @param params
	 * @return
	 */
	public List getSrcSysList(Map params);
	
	/**
	 * 获取可用的来源表值
	 * @param params
	 * @return
	 */
	public List getSrcSchemaList(Map params);
	
	/**
	 * 获取可用的来源表
	 * @param params
	 * @return
	 */
	public List getSrcTableList(Map params);
	
	/**
	 * 获取某个表可用的字段信息
	 * @param params
	 * @return
	 */
	public List getSrcColumnList(Map params);
	
	/**
	 * 校验输入的表的合法性
	 * @param params
	 * @return
	 */
	public Map validateSrcTable(Map params);

	/**
	 * 分页查找列数据
	 * @param params
	 * @return
	 */
	public RpcPageModel getSrcColumn(Map params);

	/**
	 * 获取算法字段列表
	 * @param params
	 * @return
	 */
	public List getAlgorithmsList(Map params);

	/**
	 * 新增服务
	 * @param params
	 * @return
	 */
	public Map addService(Map params);

	/**
	 * 删除数据服务
	 * @param params
	 * @return
	 */
	public Map deleteService(Map params);
	
	/**
	 * 获取数据服务字段列表
	 */
	public List getDataColumn(Map m);

	

	Map importData(Map params);

	Map validateDataCode(Map m);

	Map getAccountColumn(Map m);

	RpcPageModel getImportResult(Map params);

	/**
	 * 获取分区信息meta_partition
	 * @param m
	 * @return
	 */
	List<Map> getPartitionInfo(Map m);

	public Map insertSrcLib(Map params);
	
	public Map deleteSrcLib(Map params);
	
	public List queryMetaSystem(Map params);
	
	public List queryMetaSchema(Map params);

	List getAllRRLanList(Map params);
}
