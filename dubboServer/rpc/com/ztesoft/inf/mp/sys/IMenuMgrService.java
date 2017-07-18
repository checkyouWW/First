package com.ztesoft.inf.mp.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;

/**
 * 菜单信息管理
 * 
 * @author
 *
 */
public interface IMenuMgrService {
	
	/**
	 * 1. 查询菜单树列表
	 * 
	 * @param params
	 * @return
	 */
	public List<Map> getTreeGridData(Map params);

	/**
	 * 2. 新增根菜单
	 * 
	 * @param params
	 * @return
	 */
	public Map addRootMenu(Map params);

	/**
	 * 3. 新增子菜单
	 * 
	 * @param params
	 * @return
	 */
	public Map addChildMenu(Map params);

	/**
	 * 4. 菜单修改
	 * 
	 * @param params
	 * @return
	 */
	public Map editMenu(Map params);

	/**
	 * 5. 菜单删除
	 * 
	 * @param params
	 * @return
	 */
	public Map deleteMenu(Map params);
	
	/**
	 * 6. 查询按钮权限列表
	 */
	public List getBtnMenus(Map params);
	public RpcPageModel queryBtnMenus(HashMap params);

	/**
	 * 7. 按钮菜单新增
	 * 
	 * @param params
	 * @return
	 */
	public Map addBtnMenu(Map params);

	/**
	 * 8. 按钮菜单修改
	 * 
	 * @param params
	 * @return
	 */
	public Map editBtnMenu(Map params);

	/**
	 * 9. 按钮菜单删除
	 * 
	 * @param params
	 * @return
	 */
	public Map delBtnMenu(Map params);
}
