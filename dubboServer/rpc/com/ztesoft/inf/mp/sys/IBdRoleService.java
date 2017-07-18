package com.ztesoft.inf.mp.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;

@SuppressWarnings("all")
public interface IBdRoleService {
	
	public Map getPriv(Map params);

	public RpcPageModel queryRole(Map params);
	
	public boolean saveRole(HashMap params) throws Exception;
	
	public boolean delRole(Map params) throws Exception;
	
	public RpcPageModel queryRelStaff(Map params);
	
	public boolean addRelStaff(Map params) throws Exception;
	
	public boolean delRelStaff(Map params) throws Exception;
	
	public RpcPageModel queryTeamStaff(Map params);
	
	public RpcPageModel queryRelPriv(Map params);
	
	public boolean addRelPriv(Map params) throws Exception;
	
	public boolean delRelPriv(Map params) throws Exception;
	
	public List queryPriv(Map params) throws Exception;
	
}
