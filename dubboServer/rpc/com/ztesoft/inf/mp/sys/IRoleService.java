package com.ztesoft.inf.mp.sys;

import java.util.List;
import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;

@SuppressWarnings({ "rawtypes" })
public interface IRoleService {
	
	public RpcPageModel getRoleDatas(Map params);
	
	public Map addRole(Map params);
	
	public Map editRole(Map params);
	
	public Map delRole(Map params);
	
	public List<Map> getRolePrivilege(Map params);
	
	public Map addRolePrivilege(Map params);
	
	public Map delRolePrivilege(Map params);
	
	public RpcPageModel getBtnPrivilegeData(Map params);
	
	public Map editBtnPrivilege(Map params);

}
