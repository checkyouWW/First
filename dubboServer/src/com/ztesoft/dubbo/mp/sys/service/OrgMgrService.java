package com.ztesoft.dubbo.mp.sys.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.sys.bo.OrgMgrBO;
import com.ztesoft.inf.mp.sys.IOrgMgrService;

import appfrm.app.vo.PageModel;


/**
 * 组织信息管理
 * 
 * @author
 *
 */
@Service("orgMgrService")
@SuppressWarnings({ "rawtypes" })
public class OrgMgrService implements IOrgMgrService {
	
	@Autowired
	private OrgMgrBO orgMgrBO;
	
	/**
	 * 获取app_token
	 */
	@Transactional
	@Override
	public String getAppToken(Map params) {
		return orgMgrBO.getAppToken(params);
	}
	
	/**
	 * 获取初始信息
	 */
	@Transactional
	@Override
	public Map getInitInfo(Map params) {
		return orgMgrBO.getInitInfo(params);
	}

	/**
	 * 查询组织
	 */
	@Transactional
	public PageModel queryOrg(HashMap params) {
		return orgMgrBO.queryOrg(params);
	}
	
	@Transactional
	@Override
	public List queryOrgList(Map params) {
		return orgMgrBO.queryOrgList(params);
	}
	
	/**
	 * 保存组织
	 * @throws Exception 
	 */
	@Transactional
	public Map saveOrg(HashMap params) throws Exception {
		String action_type = MapUtils.getString(params, "action_type");
		if ("A".equals(action_type)) {
			return orgMgrBO.addOrg(params);
		} else if ("M".equals(action_type)) {
			return orgMgrBO.updateOrg(params);
		} else {
			return new HashMap();
		}
	}
	
	/**
	 * 删除组织
	 * @throws Exception 
	 */
	@Transactional
	public Map delOrg(HashMap params) throws Exception {
		return orgMgrBO.delOrg(params);
	}
	
	/**
	 * 查询团队成员
	 */
	@Transactional
	public PageModel queryOrgMem(HashMap params) {
		return orgMgrBO.queryOrgMem(params);
	}
	
	/**
	 * 增加团队成员
	 * @throws Exception 
	 */
	@Transactional
	public boolean addOrgMem(HashMap params) throws Exception {
		return orgMgrBO.addOrgMem(params);
	}
	
	/**
	 * 删除团队成员
	 * @throws Exception 
	 */
	@Transactional
	public boolean delOrgMem(HashMap params) throws Exception {
		return orgMgrBO.delOrgMem(params);
	}

	/**
	 * 设置团队负责人
	 * @throws Exception 
	 */
	@Transactional
	public int setDirector(HashMap params) throws Exception {
		return orgMgrBO.setDirector(params);
	}
	
	/**
	 * 解除团队负责人
	 */
	@Transactional
	public boolean relieveDirector(HashMap params) {
		return orgMgrBO.relieveDirector(params);
	}

	
	
}
