package com.ztesoft.inf.mp.sys;

import java.util.List;
import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;

@SuppressWarnings({ "rawtypes" })
public interface IPrivilegeService {

	public List<Map> getTreeGridData(Map params);
	
	public RpcPageModel getBtnPrivilegeData(Map params);
	
	public Map addRootPrivilege(Map params);
	
	public Map addSubPrivilege(Map params);
	
	public Map editPrivilege(Map params);
	
	public Map delPrivilege(Map params);
	
	public Map addBtnPrivilege(Map params);
	
	public Map editBtnPrivilege(Map params);
	
	public Map delBtnPrivilege(Map params);
	
}
