package com.ztesoft.dubbo.mp.sys.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.sys.bo.MenuMgrBO;
import com.ztesoft.inf.mp.sys.IMenuMgrService;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.ioc.LogicInvokerFactory;

/**
 * 菜单信息管理
 * 
 * @author
 *
 */
@Service("menuMgrService")
@SuppressWarnings("rawtypes")
public class MenuMgrService implements IMenuMgrService {

	private MenuMgrBO getMenuBO() {
		return LogicInvokerFactory.getInstance().getBO(MenuMgrBO.class);
	}

	/**
	 * 1. 查询菜单树列表
	 * 
	 * @param params
	 * @return
	 */
	@Transactional
	public List<Map> getTreeGridData(Map params) {
		return getMenuBO().getTreeGridData(params);
	}

	/**
	 * 2. 新增根菜单
	 * 
	 * @param params
	 * @return
	 */
	@Transactional
	public Map addRootMenu(Map params) {
		return getMenuBO().addRootMenu(params);
	}

	/**
	 * 3. 新增子菜单
	 * 
	 * @param params
	 * @return
	 */
	@Transactional
	public Map addChildMenu(Map params) {
		return getMenuBO().addChildMenu(params);
	}

	/**
	 * 4. 菜单修改
	 * 
	 * @param params
	 * @return
	 */
	
	@Transactional
	public Map editMenu(Map params) {
		return getMenuBO().editMenu(params);
	}

	/**
	 * 5. 菜单删除
	 * 
	 * @param params
	 * @return
	 */
	@Transactional
	public Map deleteMenu(Map params) {
		return getMenuBO().deleteMenu(params);
	}

	/**
	 * 6. 查询按钮权限列表
	 */
	@Transactional
	public List getBtnMenus(Map params) {
		return getMenuBO().getButtonMenuData(params);
	}
	
	/**
	 * 6. 查询按钮权限列表
	 */
	@Transactional
	public RpcPageModel queryBtnMenus(HashMap params) {
		return getMenuBO().queryBtnMenus(params);
	}

	/**
	 * 7. 按钮菜单新增
	 * 
	 * @param params
	 * @return
	 */
	@Transactional
	public Map addBtnMenu(Map params) {
		return getMenuBO().addBtnMenu(params);
	}

	/**
	 * 8. 按钮菜单修改
	 * 
	 * @param params
	 * @return
	 */
	@Transactional
	public Map editBtnMenu(Map params) {
		return getMenuBO().editBtnMenu(params);
	}

	/**
	 * 9. 按钮菜单删除
	 * 
	 * @param params
	 * @return
	 */
	@Transactional
	public Map delBtnMenu(Map params) {
		return getMenuBO().deleteBtnMenu(params);
	}

}
