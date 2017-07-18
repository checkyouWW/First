package com.ztesoft.dubbo.mp.sys.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;

import appfrm.app.util.ListUtil;
import appfrm.app.util.StrUtil;
import appfrm.resource.dao.impl.DAO;
import spring.util.DBUtil;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class PrivilegeBO {

	public List<Map> getTreeGridData(Map params) {
		String parent_privilege_id = MapUtils.getString(params, "id");
		String sql = "select a.*,a.privilege_id id, a.privilege_name name, b.menu_name, b.menu_order, "
				+ "(select count(1) from dm_privilege par where par.parent_privilege_id = a.privilege_id) son \n "
				+ "from dm_privilege a, dm_menu b where a.menu_id=b.menu_id and a.type=? ";
		if (StrUtil.isEmpty(parent_privilege_id)) { // 为空，查询一级权限
			sql += " and parent_privilege_id=  '-1'";
		} else if (StrUtil.isNotEmpty(parent_privilege_id)) { // 查询一级菜单下的子权限
			sql += " and parent_privilege_id=  '" + parent_privilege_id + "'";
		}
		sql += " order by b.menu_order";
		List<Map> result = DAO.queryForMap(sql, new String[] { KeyValues.PRIVILEGE_TYPE_MENU });
		for (int i = 0; result != null && i < result.size(); i++) {
			Map map = (Map) result.get(i);
			String son = (String) map.get("son");
			int son_count = Integer.parseInt(son);
			if (son_count == 0) {
				map.put("state", "open");
			} else {
				map.put("state", "closed");
			}
		}
		return result;
	}

	public RpcPageModel getBtnPrivilegeData(Map params) {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String sql = "select a.*, a.privilege_id id, b.menu_name button_name, b.class_name  "
				+ "from dm_privilege a, dm_menu b where a.menu_id=b.menu_id and a.type=? ";
		
		List sqlParams = new ArrayList();
		sqlParams.add(KeyValues.PRIVILEGE_TYPE_BUTTON);
		RpcPageModel result = DBUtil.getSimpleQuery().queryForRpcPageModel(sql.toString(), null, page_size, page_index,
				sqlParams.toArray(new String[] {}));
		return result;
	}
	
	public Map addRootPrivilege(Map params) {
		// 获取主键，大写
		String privilege_id = SeqUtil.getSeq("DM_PRIVILEGE", "PRIVILEGE_ID");
		String privilege_name = MapUtils.getString(params, "privilege_name");
		String menu_id = MapUtils.getString(params, "menu_id");
		String type = KeyValues.PRIVILEGE_TYPE_MENU;
		String parent_privilege_id = "-1";
		String path_code = privilege_id;
		Map result = new HashMap();
		// 判断是否有添加过
		if (checkMenu(menu_id, type)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "新增失败：已经添加过对应权限!");
			return result;
		}
		try {
			// 执行插入
			String sql = "insert into dm_privilege(privilege_id, privilege_name, menu_id, parent_privilege_id, type, path_code) "
					+ "values(?,?,?,?,?,?)";
			DAO.update(sql, new String[] { privilege_id, privilege_name, menu_id, parent_privilege_id, type, path_code });
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
	 * 判断菜单对应的权限是否添加过,true,表示有
	 */
	private boolean checkMenu(String menu_id, String type) {
		boolean result = false;
		String sql = "select 1 from dm_privilege t where t.menu_id=? and type=?";
		List list = DAO.queryForMap(sql, new String[] { menu_id, type });
		if (!ListUtil.isEmpty(list)) {
			result = true;
		}
		return result;
	}
	
	public Map addChildPrivilege(Map params) {
		// 获取主键，大写
		String privilege_id = SeqUtil.getSeq("DM_PRIVILEGE", "PRIVILEGE_ID");
		String privilege_name = MapUtils.getString(params, "privilege_name");
		String menu_id = MapUtils.getString(params, "menu_id");
		String type = KeyValues.PRIVILEGE_TYPE_MENU;
		String parent_privilege_id = MapUtils.getString(params, "parent_privilege_id");
		String path_code = parent_privilege_id + "." + privilege_id;
		Map result = new HashMap();
		// 判断是否有添加过
		if (checkMenu(menu_id, type)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "新增失败：已经添加过对应权限!");
			return result;
		}
		try {
			// 执行插入
			String sql = "insert into dm_privilege(privilege_id, privilege_name, menu_id, parent_privilege_id, type, path_code) "
					+ "values(?,?,?,?,?,?)";
			DAO.update(sql, new String[] { privilege_id, privilege_name, menu_id, parent_privilege_id, type, path_code });
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "新增成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "新增失败!");
		}
		return result;
	}
	
	public Map editPrivilege(Map params) {
		String privilege_id = MapUtils.getString(params, "privilege_id");
		String privilege_name = MapUtils.getString(params, "privilege_name");
		String menu_id = MapUtils.getString(params, "menu_id");
		String type = KeyValues.PRIVILEGE_TYPE_MENU;
		Map result = new HashMap();
		// 判断是否有添加过
		if (exitsMenuByPrivilegeID(menu_id, privilege_id, type)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "修改失败：已经添加过对应权限!");
			return result;
		}
		try {
			// 执行更新
			String sql = "update dm_privilege set privilege_name=?, menu_id=?, type=? where privilege_id = ?";
			DAO.update(sql, new String[] { privilege_name, menu_id, type, privilege_id });
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
	 * 判断菜单对应的权限是否添加过,修改使用，true,表示有
	 */
	private boolean exitsMenuByPrivilegeID(String menu_id, String privilege_id, String type) {
		boolean result = false;
		String sql = "select 1 from dm_privilege t where menu_id=? and type=? and privilege_id <> ?";
		List list = DAO.queryForMap(sql, new String[] { menu_id, type, privilege_id });
		if (!ListUtil.isEmpty(list)) {
			result = true;
		}
		return result;
	}
	
	public Map delPrivilege(Map params) {
		String privilege_id = MapUtils.getString(params, "privilege_id");
		Map result = new HashMap();
		if (exitsSubPrivilege(privilege_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "删除失败: 拥有下级权限无法删除");
			return result;
		}
		if (exitsPrivilegeRole(privilege_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "删除失败: 权限被角色使用中无法删除");
			return result;
		}
		if (exitsPrivilegeStaff(privilege_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "删除失败: 权限被人员使用中无法删除");
			return result;
		}
		try {
			String sql = "delete from dm_privilege where privilege_id = ? ";
			DAO.update(sql, new String[] { privilege_id });
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
	private boolean exitsSubPrivilege(String privilege_id) {
		boolean result = false;
		String sql = "select 1 from dm_privilege t where t.parent_privilege_id = '" + privilege_id + "'";
		List list = DAO.queryForMap(sql, new String[] {});
		if (!ListUtil.isEmpty(list)) {
			result = true;
		}
		return result;
	}

	/**
	 * 判断权限是否被角色关联，有关联无法删除
	 */
	private boolean exitsPrivilegeRole(String privilege_id) {
		boolean result = false;
		String sql = "select 1 from dm_role_privilege where privilege_id = '" + privilege_id + "'";
		List list = DAO.queryForMap(sql, new String[] {});
		if (!ListUtil.isEmpty(list)) {
			result = true;
		}
		return result;
	}

	/**
	 * 判断权限是否被人员使用
	 */
	private boolean exitsPrivilegeStaff(String privilege_id) {
		boolean result = false;
		String sql = "select 1 from dm_staff_privilege where privilege_id = '" + privilege_id + "'";
		List list = DAO.queryForMap(sql, new String[] {});
		if (!ListUtil.isEmpty(list)) {
			result = true;
		}
		return result;
	}

	public Map addBtnPrivilege(Map params) {
		// 获取主键，大写
		String privilege_id = SeqUtil.getSeq("DM_PRIVILEGE", "PRIVILEGE_ID");
		String privilege_name = MapUtils.getString(params, "privilege_name");
		String menu_id = MapUtils.getString(params, "menu_id");
		String type = KeyValues.PRIVILEGE_TYPE_BUTTON;
		Map result = new HashMap();
		// 判断是否有添加过
		if (checkMenu(menu_id, type)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "新增失败：已经添加过对应按钮权限!");
			return result;
		}
		try {
			// 执行插入
			String sql = "insert into dm_privilege(privilege_id, privilege_name, menu_id, type) values(?,?,?,?)";
			DAO.update(sql, new String[] { privilege_id, privilege_name, menu_id, type });
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "新增成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "新增失败!");
		}
		return result;
	}

	public Map editBtnPrivilege(Map params) {
		// 获取主键，大写
		String privilege_id = MapUtils.getString(params, "privilege_id");
		String privilege_name = MapUtils.getString(params, "privilege_name");
		String menu_id = MapUtils.getString(params, "menu_id");
		String type = KeyValues.PRIVILEGE_TYPE_BUTTON;
		Map result = new HashMap();
		// 判断是否有添加过
		if (exitsMenuByPrivilegeID(menu_id, privilege_id, type)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "修改失败：已经添加过对应按钮权限!");
			return result;
		}
		try {
			// 执行插入
			String sql = "update dm_privilege set privilege_name=?, menu_id=?, type=? where privilege_id=?";
			DAO.update(sql, new String[] { privilege_name, menu_id, type,
					privilege_id });
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
	 * 9. 按钮权限删除
	 * 
	 * @param params
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map delBtnPrivilege(Map params) {
		String privilege_id = MapUtils.getString(params, "privilege_id");
		Map result = new HashMap();
		if (exitsPrivilegeRole(privilege_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "删除失败: 权限被角色使用中无法删除");
			return result;
		}
		if (exitsPrivilegeStaff(privilege_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "删除失败: 权限被人员使用中无法删除");
			return result;
		}
		try {
			String sql = "delete from dm_privilege where privilege_id = ? ";
			DAO.update(sql, new String[] { privilege_id });
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
