package com.ztesoft.dubbo.mp.sys.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.sys.bo.BdRoleBO;
import com.ztesoft.inf.mp.sys.IBdRoleService;
import com.ztesoft.inf.util.RpcPageModel;

/**
 * 大数据角色管理
 */
@Service
@SuppressWarnings("all")
public class BdRoleService implements IBdRoleService {
	
	@Autowired
	private BdRoleBO bdRoleBO;
	
	@Transactional
	@Override
	public Map getPriv(Map params) {
		return bdRoleBO.getPriv(params);
	}

	@Transactional
	@Override
	public RpcPageModel queryRole(Map params) {
		return bdRoleBO.queryRole(params);
	}

	@Transactional
	@Override
	public boolean saveRole(HashMap params) throws Exception {
		String action_type = MapUtils.getString(params, "action_type");
		if ("A".equals(action_type)) {
			return bdRoleBO.addRole(params);
		} else if ("M".equals(action_type)) {
			return bdRoleBO.updateRole(params);
		} else {
			return false;
		}
	}

	@Transactional
	@Override
	public boolean delRole(Map params) throws Exception {
		return bdRoleBO.delRole(params);
	}

	/**
	 * 查询虚拟工号角色
	 */
	@Transactional
	@Override
	public RpcPageModel queryRelStaff(Map params) {
		return bdRoleBO.queryRelStaff(params);
	}
	
	/**
	 * 添加虚拟工号角色关联
	 * @throws Exception 
	 */
	@Transactional
	@Override
	public boolean addRelStaff(Map params) throws Exception {
		return bdRoleBO.addRelStaff(params);
	}

	/**
	 * 删除虚拟工号角色关联
	 * @throws Exception 
	 */
	@Transactional
	@Override
	public boolean delRelStaff(Map params) throws Exception {
		return bdRoleBO.delRelStaff(params);
	}

	/**
	 * 查询团队工号
	 */
	@Transactional
	@Override
	public RpcPageModel queryTeamStaff(Map params) {
		return bdRoleBO.queryTeamStaff(params);
	}

	/**
	 * 查询大数据角色权限
	 */
	@Transactional
	@Override
	public RpcPageModel queryRelPriv(Map params) {
		return bdRoleBO.queryRelPriv(params);
	}

	/**
	 * 添加大数据角色权限
	 * @throws Exception 
	 */
	@Transactional
	@Override
	public boolean addRelPriv(Map params) throws Exception {
		return bdRoleBO.addRelPriv(params);
	}

	/**
	 * 删除大数据角色权限
	 * @throws Exception 
	 */
	@Transactional
	@Override
	public boolean delRelPriv(Map params) throws Exception {
		return bdRoleBO.delRelPriv(params);
	}

	/**
	 * 查询大数据权限
	 */
	@Transactional
	@Override
	public List queryPriv(Map params) throws Exception {
		return bdRoleBO.queryPriv(params);
	}

}
