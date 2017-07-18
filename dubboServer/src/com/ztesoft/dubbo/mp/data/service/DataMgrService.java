package com.ztesoft.dubbo.mp.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.dubbo.mp.data.dao.DataMgrDao;
import com.ztesoft.inf.mp.data.IDataMgrService;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;

@Service
@SuppressWarnings({"rawtypes","unchecked"})
public class DataMgrService implements IDataMgrService {

	@Resource
	private DataMgrDao dao;

	@Override
	@Transactional
	public List getCatalog(Map params) {
		String pCatalogId = Const.getStrValue(params, "p_catalog_id");
		return dao.getCatalog(pCatalogId);
	}

	@Override
	@Transactional
	public RpcPageModel getAbilityList(Map params) {
		return dao.getAbilityList(params);
	}

	
	@Override
	@Transactional
	public Map disabledService(Map params) {
		String serviceId = Const.getStrValue(params, "service_id");
		String newState = KeyValues.STATE_00S;
		
		//如果入参为空，返回失败
		if(StringUtil.isEmpty(serviceId)){
			Map returnMap = new HashMap();
			returnMap.put("result", KeyValues.RESPONSE_FAILED);
			return returnMap;
		}
		
		this.dao.changeServiceState(serviceId, newState);
		
		Map returnMap = new HashMap();
		returnMap.put("result", KeyValues.RESPONSE_SUCCESS);
		return returnMap;
	
	}

	@Override
	@Transactional
	public Map enabledService(Map params) {
		String serviceId = Const.getStrValue(params, "service_id");
		String newState = KeyValues.STATE_00A;
		
		//如果入参为空，返回失败
		if(StringUtil.isEmpty(serviceId)){
			Map returnMap = new HashMap();
			returnMap.put("result", KeyValues.RESPONSE_FAILED);
			return returnMap;
		}
		
		this.dao.changeServiceState(serviceId, newState);
		
		Map returnMap = new HashMap();
		returnMap.put("result", KeyValues.RESPONSE_SUCCESS);
		return returnMap;
		
	}
	
	/**
	 * 删除数据服务
	 */
	@Override
	@Transactional
	public Map deleteService(Map params) {
		String serviceId = Const.getStrValue(params, "service_id");
		String newState = KeyValues.STATE_00X;
		
		//如果入参为空，返回失败
		if(StringUtil.isEmpty(serviceId)){
			Map returnMap = new HashMap();
			returnMap.put("result", KeyValues.RESPONSE_FAILED);
			return returnMap;
		}
		
		//判断能否删除
		int applyCount = dao.getInUsingApplyCount(serviceId);
		if(applyCount!=0){
			Map returnMap = new HashMap();
			returnMap.put("result", KeyValues.RESPONSE_FAILED);
			returnMap.put("tips", "当前数据服务有在用的申请，无法删除");
			return returnMap;
		}
		
		this.dao.changeServiceState(serviceId, newState);
		
		this.dao.disabledSrcTableState(serviceId);
		
		Map returnMap = new HashMap();
		returnMap.put("result", KeyValues.RESPONSE_SUCCESS);
		return returnMap;
	
	}
	
	/**
	 * 根据service_id获取数据服务
	 * @param params
	 * @return
	 */
	@Transactional
	@Override
	public Map getDataServiceById(Map params) {
		Map result = this.dao.getDataServiceById(params);
		return result;
	}

	@Override
	@Transactional
	public List getSrcSysList(Map params) {
		return dao.getSrcSysList();
	}

	@Override
	@Transactional
	public List getSrcSchemaList(Map params) {
		return dao.getSrcSchemaList(params);
	}
	
	@Transactional
	@Override
	public List getSrcTableList(Map params){
		return dao.getSrcTableList(params);
	}
	
	@Transactional
	@Override
	public List getSrcColumnList(Map params){
		//System.out.println(params);
		return dao.getSrcColumnList(params);
	}
	
	@Transactional
	@Override
	public RpcPageModel getSrcColumn(Map params){
		return dao.getSrcColumn(params);
	}
	
	@Transactional
	@Override
	public Map validateSrcTable(Map params){
		//System.out.println(params);
		return dao.validateSrcTable(params);
	}
	
	@Transactional
	@Override
	public List getAlgorithmsList(Map params){
		return dao.getAlgorithmsList(params);
	}
	
	@Transactional
	@Override
	public Map addService(Map params){
		//System.out.println(params);
		return dao.addService(params);
	}

	@Override
	@Transactional
	public List getDataColumn(Map m) {
		return dao.getDataColumn(m);
	}
	
	@Override
	@Transactional
	public Map validateDataCode(Map m) {
		return dao.validateDataCode(m);
	}
	
	@Override
	@Transactional
	public Map getAccountColumn(Map m){
		return dao.getAccountColumn(m);
	}
	
	@Override
	@Transactional
	public Map importData(Map params){
		return dao.importData(params);
	}
	
	@Override
	@Transactional
	public RpcPageModel getImportResult(Map params){
		return PageModelConverter.pageModelToRpc(dao.getImportResult(params));
	}
	
	@Override
	@Transactional
	public List<Map> getPartitionInfo(Map m) {
		return dao.getPartitionInfo(m);
	}

	@Override
	@Transactional
	public Map insertSrcLib(Map params) {
		return dao.insertSrcLib(params);
	}

	@Override
	@Transactional
	public Map deleteSrcLib(Map params) {
		return dao.deleteSrcLib(params);
	}
	
	@Override
	@Transactional
	public List queryMetaSystem(Map params) {
		return dao.queryMetaSystem(params);
	}

	@Override
	@Transactional
	public List queryMetaSchema(Map params) {
		return dao.queryMetaSchema(params);
	}
	
	@Override
	@Transactional
	public List getAllRRLanList(Map params) {
		return dao.getAllRRLanList(params);
	}
	
}
