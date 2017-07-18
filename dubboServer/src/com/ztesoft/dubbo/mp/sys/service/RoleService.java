package com.ztesoft.dubbo.mp.sys.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.sys.bo.RoleBO;
import com.ztesoft.inf.mp.sys.IRoleService;
import com.ztesoft.inf.util.RpcPageModel;

@Service
@SuppressWarnings({ "rawtypes" })
public class RoleService implements IRoleService {
	
	@Autowired
	private RoleBO roleBO;

	/**
	 * 获取角色
	 */
	@Transactional
	@Override
	public RpcPageModel getRoleDatas(Map params) {
		return roleBO.getRoleDatas(params);
	}

	/**
	 * 增加角色
	 */
	@Transactional
	@Override
	public Map addRole(Map params) {
		return roleBO.addRole(params);
	}

	/**
	 * 修改角色
	 */
	@Transactional
	@Override
	public Map editRole(Map params) {
		return roleBO.editRole(params);
	}

	/**
	 * 删除角色
	 */
	@Transactional
	@Override
	public Map delRole(Map params) {
		return roleBO.delRole(params);
	}

	/**
	 * 查询角色权限
	 */
	@Transactional
	@Override
	public List<Map> getRolePrivilege(Map params) {
		return roleBO.getRolePrivilege(params);
	}

	/**
	 * 新增角色权限
	 */
	@Transactional
	@Override
	public Map addRolePrivilege(Map params) {
		return roleBO.addRolePrivilege(params);
	}

	/**
	 * 删除角色权限
	 */
	@Transactional
	@Override
	public Map delRolePrivilege(Map params) {
		return roleBO.delRolePrivilege(params);
	}

	/**
	 * 获取角色管理的按钮权限，按钮是挂在某个具体的页面上的
	 */
	@Transactional
	@Override
	public RpcPageModel getBtnPrivilegeData(Map params) {
		return roleBO.getBtnPrivilegeData(params);
	}

	/**
	 * 编辑按钮权限
	 */
	@Transactional
	@Override
	public Map editBtnPrivilege(Map params) {
		return roleBO.editBtnPrivilege(params);
	}

}
