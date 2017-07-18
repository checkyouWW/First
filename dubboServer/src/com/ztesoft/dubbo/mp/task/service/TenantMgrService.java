package com.ztesoft.dubbo.mp.task.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.dubbo.mp.task.dao.TenantMgrDao;
import com.ztesoft.inf.mp.task.ITenantMgrService;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;

@Service
@SuppressWarnings({ "rawtypes","unchecked" })
@Deprecated
public class TenantMgrService implements ITenantMgrService{

	@Resource
	private TenantMgrDao dao;
	
	@Transactional
	@Override
	@Deprecated
	public RpcPageModel getTenantList(Map params){
		return PageModelConverter.pageModelToRpc(this.dao.getTenantList(params));
	}
	
	@Transactional
	@Override
	@Deprecated
	public Map disabledTenant(Map params){
		Map returnMap = new HashMap();
		returnMap.put("state", KeyValues.RESPONSE_FAILED);
		String tenantId = Const.getStrValue(params, "tenant_id");
		if(StringUtil.isEmpty(tenantId)) return returnMap;
		dao.updateTenantState(tenantId, KeyValues.STATE_00S);
		returnMap.put("state", KeyValues.RESPONSE_SUCCESS);
		return returnMap;
	}
	
	@Transactional
	@Override
	@Deprecated
	public Map enabledTenant(Map params){
		Map returnMap = new HashMap();
		returnMap.put("state", KeyValues.RESPONSE_FAILED);
		String tenantId = Const.getStrValue(params, "tenant_id");
		if(StringUtil.isEmpty(tenantId)) return returnMap;
		dao.updateTenantState(tenantId, KeyValues.STATE_00A);
		returnMap.put("state", KeyValues.RESPONSE_SUCCESS);
		return returnMap;
	}
	
	@Transactional
	@Override
	@Deprecated
	public Map deleteTenant(Map params){
		Map returnMap = new HashMap();
		returnMap.put("state", KeyValues.RESPONSE_FAILED);
		String tenantId = Const.getStrValue(params, "tenant_id");
		if(StringUtil.isEmpty(tenantId)) return returnMap;
		dao.updateTenantState(tenantId, KeyValues.STATE_00X);
		returnMap.put("state", KeyValues.RESPONSE_SUCCESS);
		return returnMap;
	}

	@Transactional
	@Override
	@Deprecated
	public List getTenantGroupList(Map params){
		return dao.getTenantGroupList(params);
	}
	
	@Transactional
	@Override
	@Deprecated
	public RpcPageModel getTeamList(Map params){
		return PageModelConverter.pageModelToRpc(this.dao.getTeamList(params));
	}
	
	@Transactional
	@Override
	@Deprecated
	public Map addTenant(Map params){
		return dao.addTenant(params);
	}
	
}
