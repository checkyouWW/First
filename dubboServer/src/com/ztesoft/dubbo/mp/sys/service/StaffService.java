package com.ztesoft.dubbo.mp.sys.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appfrm.app.util.ListUtil;
import com.ztesoft.common.util.SessionHelper;
import com.ztesoft.inf.util.ResultMap;
import com.ztesoft.dubbo.mp.sys.bo.LoginBO;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.ioc.LogicInvokerFactory;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.sys.bo.StaffBO;
import com.ztesoft.inf.mp.sys.IStaffService;

@Service
@SuppressWarnings({ "unchecked", "rawtypes" })
public class StaffService implements IStaffService {

	@Autowired
	private StaffBO staffBO;

	private LoginBO getLoginBO() {
		return LogicInvokerFactory.getInstance().getBO(LoginBO.class);
	}


	/**
	 * 员工查询
	 */
	@Transactional
	public RpcPageModel queryStaff(HashMap params) {
		return staffBO.queryStaff(params);
	}

	/**
	 * 设置默认团队
	 */
	@Override
	@Transactional
	public Map setDefTeamId(Map params) {
		String defaultTeamId = MapUtils.getString(params, "defaultTeamId");
		List<Map> teams = getTeamList(null);
		if (!ListUtil.isEmpty(teams)) {
			if (defaultTeamId != null) {
				for (int i = 0; i < teams.size(); i++) {
					Map m = teams.get(i);
					if (defaultTeamId.equals(m.get("team_id"))) {
						staffBO.setDefTeamId(defaultTeamId);
						SessionHelper.setDefTeamId(defaultTeamId);
						return (HashMap) new ResultMap().success();
					}
				}
			}
		}
		return new ResultMap().failed().msg("设置默认团队失败");
	}

	/**
	 * 获取当前团队列表
	 */
	@Override
	@Transactional
	public List getTeamList(Map params) {
		String staffId = SessionHelper.getStaffId();
		return getLoginBO().getTeamListByStaffId(staffId);
	}

	/**
	 * 获取登录者的团队信息
	 */
	@Override
	@Transactional
	public Map getTeamInfo(Map params) {
		Map map = new HashMap();
		map.put("def_team_id", SessionHelper.getDefTeamId());
		map.put("team_id", SessionHelper.getTeamId());
		map.put("team_name", SessionHelper.getTeamName());
		return map;
	}

	/**
	 * 切换当前登录者的所属团队
	 */
	@Override
	@Transactional
	public Map setTeamId(Map params) {
		String teamId = MapUtils.getString(params, "teamId");
		String teamName = MapUtils.getString(params, "teamName");
		List<Map> teams = getTeamList(null);
		if (!ListUtil.isEmpty(teams)) {
			if (teamId != null) {
				for (int i = 0; i < teams.size(); i++) {
					Map m = teams.get(i);
					if (teamId.equals(m.get("team_id"))) {
						SessionHelper.setTeamId(teamId);
						SessionHelper.setTeamName(teamName);
						SessionHelper.setLanId(MapUtils.getString(m, "lan_id"));
						SessionHelper.setVrStaffId(MapUtils.getString(m, "vr_staff_id"));
						SessionHelper.setIsDirector(MapUtils.getString(m, "is_director"));
						return (HashMap) new ResultMap().success();
					}
				}
			}
		}
		return new ResultMap().failed().msg("切换团队失败");
	}


	@Transactional
	@Override
	public RpcPageModel getStaffDatas(Map params) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 新增员工
	 */
	@Transactional
	@Override
	public Map addStaff(Map params) {
		return staffBO.addStaff(params);
	}

	/**
	 * 修改员工
	 */
	@Transactional
	@Override
	public Map editStaff(Map params) {
		return staffBO.editStaff(params);
	}

	/**
	 * 删除员工
	 */
	@Transactional
	@Override
	public Map delStaff(Map params) {
		return staffBO.delStaff(params);
	}

	/**
	 * 获取员工角色列表
	 */
	@Transactional
	@Override
	public List<Map> getStaffRoleList(Map params) {
		return staffBO.getStaffRoleList(params);
	}

	/**
	 * 新增员工角色
	 */
	@Transactional
	@Override
	public Map addStaffRole(Map params) {
		return staffBO.addStaffRole(params);
	}

	/**
	 * 删除员工角色
	 */
	@Transactional
	@Override
	public Map delStaffRole(Map params) {
		return staffBO.delStaffRole(params);
	}

	/**
	 * 获取员工菜单权限列表，所有的权限菜单(含角色关联的和员工直接关联的)
	 */
	@Transactional
	@Override
	public List<Map> getStaffPrivileges(Map params) {
		return staffBO.getStaffPrivileges(params);
	}

	/**
	 * 新增员工菜单权限，如果增加的是下级权限，先要增加上级权限
	 */
	@Transactional
	@Override
	public Map addStaffPrivilege(Map params) {
		return staffBO.addStaffPrivilege(params);
	}

	/**
	 * 删除员工权限
	 */
	@Transactional
	@Override
	public Map delStaffPrivilege(Map params) {
		return staffBO.delStaffPrivilege(params);
	}
	
}
