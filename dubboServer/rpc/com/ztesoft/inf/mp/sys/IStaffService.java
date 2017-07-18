package com.ztesoft.inf.mp.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appfrm.app.vo.PageModel;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.inf.util.RpcPageModel;

@SuppressWarnings({ "rawtypes" })
public interface IStaffService {

	public RpcPageModel queryStaff(HashMap params);
	
	public RpcPageModel getStaffDatas(Map params);
	
	public Map addStaff(Map params);
	
	public Map editStaff(Map params);
	
	public Map delStaff(Map params);
	
	public List<Map> getStaffRoleList(Map params);
	
	public Map addStaffRole(Map params);
	
	public Map delStaffRole(Map params);
	
	public List<Map> getStaffPrivileges(Map params);
	
	public Map addStaffPrivilege(Map params);
	
	public Map delStaffPrivilege(Map params);

	@Transactional
	Map setDefTeamId(Map params);

	@Transactional
	List getTeamList(Map params);

	@Transactional
	Map getTeamInfo(Map params);

	@Transactional
	Map setTeamId(Map params);
}
