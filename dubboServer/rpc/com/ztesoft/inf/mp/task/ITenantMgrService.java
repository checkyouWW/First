package com.ztesoft.inf.mp.task;

import java.util.List;
import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;
@SuppressWarnings("rawtypes")
public interface ITenantMgrService {

	RpcPageModel getTenantList(Map params);

	Map disabledTenant(Map params);

	Map enabledTenant(Map params);

	List getTenantGroupList(Map params);

	RpcPageModel getTeamList(Map params);

	Map addTenant(Map params);

	Map deleteTenant(Map params);

}
