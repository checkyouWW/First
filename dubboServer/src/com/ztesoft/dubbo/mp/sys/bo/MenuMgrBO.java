package com.ztesoft.dubbo.mp.sys.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;

import appfrm.app.util.ListUtil;
import appfrm.app.util.StrUtil;
import appfrm.resource.dao.impl.DAO;
import spring.util.DBUtil;

/**
 * 菜单管理
 * 
 * @author
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MenuMgrBO {

	/**
	 * 1. 根据父菜单id/名字，查找菜单树
	 * 
	 * @param params
	 * @return
	 */
	public List<Map> getTreeGridData(Map params) {
		String id = StringUtil.getStrValue(params, "id");
		String search_sys_id = StringUtil.getStrValue(params, "search_sys_id");
		String sql = "select a.*,a.menu_id id,a.parent_menu parentId, a.menu_name name, a.menu_path path_code,a.sys_id, "
				+ "(select count(1) from dm_menu par where par.parent_menu = a.menu_id) son \n "
				+ "from dm_menu a where type = ? ";
		List<String> sqlParams = new ArrayList<String>();
		sqlParams.add(KeyValues.MENU_TYPE_MENU);
		if (StrUtil.isNotEmpty(search_sys_id)) {
			sql += " and a.sys_id = ?";
			sqlParams.add(search_sys_id);
		}
		
		if (StrUtil.isEmpty(id)) { // 为空，查询一级菜单
			sql += " and parent_menu = '-1'";
		} else if (StrUtil.isNotEmpty(id)) { // 查询一级菜单下的子菜单
			sql += " and parent_menu = ?";
			sqlParams.add(id);
		}
		sql += " order by menu_order";
		List result = DAO.queryForMap(sql, sqlParams.toArray(new String[]{}));
		for(int i=0; result != null && i<result.size(); i++){
			Map map = (Map) result.get(i);
			String son = (String) map.get("son");
			int son_count = Integer.parseInt(son);
			if(son_count == 0){
				map.put("state", "open");
			}
			else {
				map.put("state", "closed");
			}
		}
		return result;
	}

	/**
	 * 2. 添加一级菜单
	 * 
	 * @param params
	 * @return
	 */
	public Map addRootMenu(Map params) {
		// 获取主键，大写
		String menu_id = SeqUtil.getSeq("DM_MENU", "MENU_ID");
		String menu_name = StringUtil.getStrValue(params, "menu_name");
		String menu_code = StringUtil.getStrValue(params, "menu_code");
		String url = StringUtil.getStrValue(params, "url");
		String menu_order = StringUtil.getStrValue(params, "menu_order");
		String menu_desc = StringUtil.getStrValue(params, "menu_desc");
		String parent_menu = "-1";
		String type = KeyValues.MENU_TYPE_MENU;
		String menu_path = menu_id;
		String sys_id = StringUtil.getStrValue(params, "sys_id");
		// 执行插入
		Map result = new HashMap();
		try {
			String sql = "insert into dm_menu(menu_id, menu_name, menu_code, menu_order, url, menu_desc, parent_menu, type, menu_path, sys_id) "
					+ "values(?,?,?,?,?,?,?,?,?,?)";
			DAO.update(sql, new String[] { menu_id, menu_name, menu_code,
					menu_order, url, menu_desc, parent_menu, type, menu_path, sys_id});
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "新增成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "新增失败!");
		}
		return result;
	}

	/**
	 * 3. 添加子菜单
	 * 
	 * @param params
	 * @return
	 */
	public Map addChildMenu(Map params) {
		// 获取主键，大写
		String menu_id = SeqUtil.getSeq("DM_MENU", "MENU_ID");
		String menu_name = StringUtil.getStrValue(params, "menu_name");
		String menu_code = StringUtil.getStrValue(params, "menu_code");
		String url = StringUtil.getStrValue(params, "url");
		String menu_order = StringUtil.getStrValue(params, "menu_order");
		String menu_desc = StringUtil.getStrValue(params, "menu_desc");
		String parent_menu = StringUtil.getStrValue(params, "parent_menu");
		String type = KeyValues.MENU_TYPE_MENU;
		String menu_path = parent_menu + "." + menu_id;
		String sys_id = StringUtil.getStrValue(params, "sys_id");
		// 执行插入
		Map result = new HashMap();
		try {
			String sql = "insert into dm_menu(menu_id, menu_name, menu_code, menu_order, url, menu_desc, parent_menu, type, menu_path, sys_id) "
					+ "values(?,?,?,?,?,?,?,?,?,?)";
			DAO.update(sql, new String[] { menu_id, menu_name, menu_code,
					menu_order, url, menu_desc, parent_menu, type, menu_path, sys_id});
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "新增成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "新增失败!");
		}
		return result;
	}

	/**
	 * 4. 修改菜单信息
	 * 
	 * @param params
	 * @return
	 */
	public Map editMenu(Map params) {
		String menu_id = StringUtil.getStrValue(params, "menu_id");
		String menu_code = StringUtil.getStrValue(params, "menu_code");
		String menu_name = StringUtil.getStrValue(params, "menu_name");
		String menu_order = StringUtil.getStrValue(params, "menu_order");
		String url = StringUtil.getStrValue(params, "url");
		String menu_desc = StringUtil.getStrValue(params, "menu_desc");
		String sys_id = StringUtil.getStrValue(params, "sys_id");
		// 执行更新
		Map result = new HashMap();
		try {
			String sql = "update dm_menu set menu_code=?, menu_name=?, menu_order=?, menu_desc=?, url=?, sys_id = ? "
					+ "where menu_id = ?";
			DAO.update(sql, new String[] { menu_code, menu_name, menu_order,
					menu_desc, url, sys_id, menu_id });
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "修改成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "修改失败!");
		}
		return result;
	}

	/**
	 * 5. 删除菜单，拥有下级菜单无法删除
	 * 
	 * @param params
	 * @return
	 */
	public Map deleteMenu(Map params) {
		String menu_id = StringUtil.getStrValue(params, "menu_id");
		Map result = new HashMap();
		if (exitsSubMenu(menu_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "删除失败: 拥有下级菜单无法删除");
			return result;
		}
		if (exitsPrivilegeMenu(menu_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "删除失败: 菜单已被权限关联无法删除");
			return result;
		}
		// 执行删除
		try {
			String sql = "delete from dm_menu where menu_id = ? ";
			DAO.update(sql, new String[] { menu_id });
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "删除成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "删除失败!");
		}
		return result;
	}

	/**
	 * 判断是否含有下级菜单，true，表示有
	 * 
	 * @param menu_id
	 * @return
	 */
	private boolean exitsSubMenu(String menu_id) {
		boolean result = false;
		String sql = "select 1 from dm_menu t where t.parent_menu='" + menu_id
				+ "'";
		List list = DAO.queryForMap(sql, new String[] {});
		if (!ListUtil.isEmpty(list)) {
			result = true;
		}
		return result;
	}

	/**
	 * 判断菜单是否被权限使用
	 * 
	 * @param menu_id
	 * @return
	 */
	private boolean exitsPrivilegeMenu(String menu_id) {
		boolean result = false;
		String sql = "select 1 from dm_privilege where menu_id = '" + menu_id
				+ "'";
		List list = DAO.queryForMap(sql, new String[] {});
		if (!ListUtil.isEmpty(list)) {
			result = true;
		}
		return result;
	}

	/**
	 * 6. 获取按钮菜单列表
	 * 
	 * @param params
	 * @return
	 */
	public List<Map> getButtonMenuData(Map params) {
		String sql = "select a.*,a.menu_id id, a.menu_name name from dm_menu a where a.type = ?";
		return DAO.queryForMap(sql,
				new String[] { KeyValues.MENU_TYPE_BUTTON });
	}
	
	public RpcPageModel queryBtnMenus(Map params) {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String sql = "select a.*,a.menu_id id, a.menu_name name from dm_menu a where a.type = ?";
		List sqlParams = new ArrayList();
		sqlParams.add(KeyValues.MENU_TYPE_BUTTON);
		RpcPageModel result = DBUtil.getSimpleQuery().queryForRpcPageModel(sql.toString(), null, page_size, page_index,
				sqlParams.toArray(new String[] {}));
		return result;
	}

	/**
	 * 7. 增加按钮菜单
	 * 
	 * @param params
	 * @return
	 */
	public Map addBtnMenu(Map params) {
		// 获取主键，大写
		String menu_id = SeqUtil.getSeq("DM_MENU", "MENU_ID");
		String menu_name = StringUtil.getStrValue(params, "menu_name");
		String menu_code = StringUtil.getStrValue(params, "menu_code");
		String class_name = StringUtil.getStrValue(params, "class_name");
		String type = KeyValues.MENU_TYPE_BUTTON;
		Map result = new HashMap();
		try {
			String sql = "insert into dm_menu(menu_id, menu_name, menu_code, type, class_name) values(?,?,?,?,?)";
			DAO.update(sql, new String[] { menu_id, menu_name, menu_code, type,
					class_name });
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "新增成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "新增失败!");
		}
		return result;
	}

	/**
	 * 8. 修改按钮菜单
	 * 
	 * @param params
	 * @return
	 */
	public Map editBtnMenu(Map params) {
		String menu_id = StringUtil.getStrValue(params, "menu_id");
		String menu_name = StringUtil.getStrValue(params, "menu_name");
		String menu_code = StringUtil.getStrValue(params, "menu_code");
		String class_name = StringUtil.getStrValue(params, "class_name");
		Map result = new HashMap();
		try {
			String sql = "update dm_menu set menu_name=?, menu_code=?, class_name=? where menu_id = ?";
			DAO.update(sql, new String[] { menu_name, menu_code, class_name,
					menu_id });
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "修改成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "修改失败!");
		}
		return result;
	}

	/**
	 * 9. 删除按钮
	 * 
	 * @param params
	 * @return
	 */
	public Map deleteBtnMenu(Map params) {
		String menu_id = StringUtil.getStrValue(params, "menu_id");
		Map result = new HashMap();
		if (exitsPrivilegeMenu(menu_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "删除失败: 按钮已被权限关联无法删除");
			return result;
		}
		// 执行删除
		try {
			String sql = "delete from dm_menu where menu_id = ? ";
			DAO.update(sql, new String[] { menu_id });
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "删除成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "删除失败!");
		}
		return result;
	}

}
