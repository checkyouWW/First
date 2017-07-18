package com.ztesoft.dubbo.mp.sys.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.sys.bo.PrivilegeBO;
import com.ztesoft.inf.mp.sys.IPrivilegeService;
import com.ztesoft.inf.util.RpcPageModel;

@Service
@SuppressWarnings({ "rawtypes" })
public class PrivilegeService implements IPrivilegeService {

	@Autowired
	private PrivilegeBO privilegeBO;

	/**
	 * 查询权限树列表
	 */
	@Transactional
	public List<Map> getTreeGridData(Map params) {
		return privilegeBO.getTreeGridData(params);
	}

	/**
	 * 获取按钮权限列表
	 */
	@Transactional
	@Override
	public RpcPageModel getBtnPrivilegeData(Map params) {
		return privilegeBO.getBtnPrivilegeData(params);
	}

	/**
	 * 添加根权限
	 */
	@Transactional
	@Override
	public Map addRootPrivilege(Map params) {
		return privilegeBO.addRootPrivilege(params);
	}

	/**
	 * 添加子权限
	 */
	@Transactional
	@Override
	public Map addSubPrivilege(Map params) {
		return privilegeBO.addChildPrivilege(params);
	}

	/**
	 * 修改权限
	 */
	@Transactional
	@Override
	public Map editPrivilege(Map params) {
		return privilegeBO.editPrivilege(params);
	}

	/**
	 * 删除权限
	 */
	@Transactional
	@Override
	public Map delPrivilege(Map params) {
		return privilegeBO.delPrivilege(params);
	}

	/**
	 * 按钮权限新增
	 */
	@Transactional
	@Override
	public Map addBtnPrivilege(Map params) {
		return privilegeBO.addBtnPrivilege(params);
	}

	/**
	 * 按钮权限修改
	 */
	@Transactional
	@Override
	public Map editBtnPrivilege(Map params) {
		return privilegeBO.editBtnPrivilege(params);
	}

	/**
	 * 按钮权限删除
	 */
	@Transactional
	@Override
	public Map delBtnPrivilege(Map params) {
		return privilegeBO.delBtnPrivilege(params);
	}

}
