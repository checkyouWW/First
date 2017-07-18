package com.ztesoft.dubbo.mp.sys.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ztesoft.common.dao.DAOUtils;
import com.ztesoft.common.util.DBUtils;
import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;

import appfrm.app.util.ListUtil;
import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;
import spring.util.DBUtil;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class RoleBO {

	public RpcPageModel getRoleDatas(Map params) {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String role_name = MapUtils.getString(params, "role_name");

		List sqlParams = new ArrayList();
		String sql = "select a.role_id, a.role_name, a.role_desc, " + DBUtils.to_char("a.create_date", 2) + " create_date, a.role_id id from dm_role a where 1=1 ";
		if (StringUtils.isNotEmpty(role_name)) {
			sql += "and a.role_name like ?";
			sqlParams.add("%" + role_name + "%");
		}
		sql += " order by a.create_date desc";
		RpcPageModel result = DBUtil.getSimpleQuery().queryForRpcPageModel(sql.toString(), null, page_size, page_index,
				sqlParams.toArray(new String[] {}));
		return result;
	}

	public Map addRole(Map params) {
		// 获取主键，大写
		String role_id = SeqUtil.getSeq("DM_ROLE", "ROLE_ID");
		String role_name = MapUtils.getString(params, "role_name");
		String role_desc = MapUtils.getString(params, "role_desc");
		String create_date = DAOUtils.getFormatedDate();
		Map result = new HashMap();
		try {
			// 执行插入
			String sql = "insert into dm_role(role_id, role_name, role_desc, create_date) values(?,?,?," + DBUtils.to_date(2) + ")";
			DAO.update(sql, new String[] { role_id, role_name, role_desc, create_date });
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "新增成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "新增失败!");
		}
		return result;
	}
	
	public Map editRole(Map params) {
		String role_id = MapUtils.getString(params, "role_id");
		String role_name = MapUtils.getString(params, "role_name");
		String role_desc = MapUtils.getString(params, "role_desc");
		Map result = new HashMap();
		try {
			// 执行插入
			String sql = "update dm_role set role_name = ?, role_desc = ? where role_id = ?";
			DAO.update(sql, new String[] { role_name, role_desc, role_id });
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "修改成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "修改失败!");
		}
		return result;
	}
	
	public Map delRole(Map params) {
		String role_id = MapUtils.getString(params, "role_id");
		Map result = new HashMap();
		if (exitsRolePrivilege(role_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "删除失败：角色存在关联权限无法删除");
			return result;
		}
		if (exitsRoleStaff(role_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "删除失败：角色存在关联人员无法删除");
			return result;
		}
		try {
			// 执行插入
			String sql = "delete from dm_role where role_id = ?";
			DAO.update(sql, new String[] { role_id });
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
	 * 判断角色是否存在关联的权限
	 */
	private boolean exitsRolePrivilege(String role_id) {
		String sql = "select 1 from dm_role_privilege where role_id = ?";
		List list = DAO.queryForMap(sql, new String[] { role_id });
		if (!ListUtil.isEmpty(list)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断角色是否被人员关联
	 */
	private boolean exitsRoleStaff(String role_id) {
		String sql = "select 1 from dm_staff_role where role_id = ?";
		List list = DAO.queryForMap(sql, new String[] { role_id });
		if (!ListUtil.isEmpty(list)) {
			return true;
		}
		return false;
	}

	public List<Map> getRolePrivilege(Map params) {
		String role_id = MapUtils.getString(params, "role_id");
		String type = MapUtils.getString(params, "type");
		String parent_privilege_id = MapUtils.getString(params, "id");
		String sql = "select t1.privilege_id id, t1.privilege_name name, t1.type, t1.path_code, t2.role_id, "
				+ "(select count(1) from dm_privilege par where par.parent_privilege_id = t1.privilege_id) son \n"
				+ "from dm_privilege t1 LEFT JOIN dm_role_privilege t2 " + "ON t1.PRIVILEGE_ID = t2.PRIVILEGE_ID "
				+ "and t2.ROLE_ID = ? where t1.type = ? ";
		if (StringUtils.isEmpty(parent_privilege_id)) {
			sql += " and t1.parent_privilege_id=  '-1'";
		} else {
			sql += " and t1.parent_privilege_id=  '" + parent_privilege_id + "'";
		}
		List list = DAO.queryForMap(sql, new String[] { role_id, type });
		for (int i = 0; list != null && i < list.size(); i++) {
			Map map = (Map) list.get(i);
			String son = (String) map.get("son");
			int son_count = Integer.parseInt(son);
			if (son_count == 0) {
				map.put("state", "open");
			} else {
				map.put("state", "closed");
			}
		}
		return list;
	}
	
	public Map addRolePrivilege(Map params) {
		String role_id = MapUtils.getString(params, "role_id");
		String privilege_id = MapUtils.getString(params, "privilege_id");
		Map result = new HashMap();
		// 判断上级权限是否增加过
		if (!exitsRoleParPrivilege(role_id, privilege_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "新增失败：请先新增此权限的上级权限");
			return result;
		}
		// 判断此权限是否增加过
		if (exitsRolePrivilege(role_id, privilege_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "新增失败：该角色已经添加过此权限");
			return result;
		}
		try {
			String sql = "insert into dm_role_privilege(role_id, privilege_id) values(?,?)";
			DAO.update(sql, new String[] { role_id, privilege_id });
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "关联成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "关联失败!");
		}
		return result;
	}
	
	/**
	 * 新增前判断上级权限是否已经存在角色权限关系，不存在需要先增加上级权限
	 */
	private boolean exitsRoleParPrivilege(String role_id, String privilege_id) {
		// 获取上级权限ID
		String sql = "select parent_privilege_id from dm_privilege where privilege_id=?";
		List list = DAO.queryForMap(sql, new String[] { privilege_id });
		String parent_privilege_id = MapUtils.getString((Map) list.get(0),
				"parent_privilege_id");
		if (parent_privilege_id.equals("-1")) { // 最上级
			return true;
		}
		return exitsRolePrivilege(role_id, parent_privilege_id);
	}

	/**
	 * 新增前判断是否已经存在角色权限关系,true表示存在
	 */
	private boolean exitsRolePrivilege(String role_id, String privilege_id) {
		String sql = "select 1 from dm_role_privilege where role_id = ? and privilege_id = ?";
		List list = DAO.queryForMap(sql, new String[] { role_id, privilege_id });
		if (!ListUtil.isEmpty(list)) {
			return true;
		}
		return false;
	}
	
	public Map delRolePrivilege(Map params) {
		String role_id = MapUtils.getString(params, "role_id");
		String privilege_id = MapUtils.getString(params, "privilege_id");
		Map result = new HashMap();
		try {
			String sql = "delete from dm_role_privilege where role_id = ? and privilege_id in("
					+ "select PRIVILEGE_ID from dm_privilege where PRIVILEGE_ID = ? or parent_privilege_id = ?)";
			DAO.update(sql, new String[] { role_id, privilege_id, privilege_id });
			
			// 将按钮权限一并删除
			DAO.update("delete from dm_role_privilege where role_id = ? and page_menu_id in (select b.menu_id from dm_privilege b where b.privilege_id = ?)",
					new String[] { role_id, privilege_id });
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "删除成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "删除失败!");
		}
		return result;
	}

	public RpcPageModel getBtnPrivilegeData(Map params) {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String role_id = MapUtils.getString(params, "role_id");

		// 先获取角色关联的权限菜单有url的所有菜单
		String sql = "select DISTINCT t3.menu_id, t3.menu_id id, t3.menu_name, t3.url from dm_role_privilege t1, dm_privilege t2, dm_menu t3 "
				+ "where t1.PRIVILEGE_ID = t2.PRIVILEGE_ID and t2.MENU_ID = t3.MENU_ID  "
				+ "and t3.TYPE = ? and t3.URL is not null and t3.URL <> ''  "
				+ "and t1.role_id = ? order by t1.PRIVILEGE_ID";

		List sqlParams = new ArrayList();
		sqlParams.add(KeyValues.MENU_TYPE_MENU);
		sqlParams.add(role_id);
		RpcPageModel pm = DBUtil.getSimpleQuery().queryForRpcPageModel(sql.toString(), null, page_size, page_index,
				sqlParams.toArray(new String[] {}));

		// 获取对应的按钮权限
		String btnSql = "select t1.PRIVILEGE_ID, t1.PRIVILEGE_NAME, t2.ROLE_ID, t2.page_menu_id from dm_privilege t1 LEFT JOIN dm_role_privilege t2 "
				+ "ON t1.PRIVILEGE_ID = t2.PRIVILEGE_ID and page_menu_id = ? and t2.role_id = ? where t1.type = ? order by t1.PRIVILEGE_ID";
		List list = pm.getList();
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			String menu_id = MapUtils.getString(map, "menu_id");
			List btns = DAO.queryForMap(btnSql, new String[] { menu_id, role_id, KeyValues.PRIVILEGE_TYPE_BUTTON });
			((Map) list.get(i)).put("btnPrivileges", btns);
		}
		return pm;
	}
	
	public Map editBtnPrivilege(Map params) {
		String role_id = MapUtils.getString(params, "role_id");
		String page_menu_id = MapUtils.getString(params, "page_menu_id");
		String chk_value = MapUtils.getString(params, "chk_value"); // [1,2,3]
		chk_value = chk_value.substring(1, chk_value.length() - 1);
		String[] arr = StringUtils.isEmpty(chk_value) ? new String[] {} : chk_value.split(",");
		Map result = new HashMap();
		try {
			String delSql = "delete from dm_role_privilege where role_id=? and page_menu_id=?";
			DAO.update(delSql, new String[] { role_id, page_menu_id });
			String insertSql = "insert into dm_role_privilege(role_id, privilege_id, page_menu_id) values(?,?,?)";
			for (int i = 0; i < arr.length; i++) {
				DAO.update(insertSql, new String[] { role_id, arr[i], page_menu_id });
			}
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "关联成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "关联失败!");
		}
		return result;
	}

}
