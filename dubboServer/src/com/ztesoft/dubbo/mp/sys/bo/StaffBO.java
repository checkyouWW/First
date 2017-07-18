package com.ztesoft.dubbo.mp.sys.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import appfrm.resource.dao.impl.DAO;

import com.ztesoft.common.dao.DAOUtils;
import com.ztesoft.common.util.DBUtils;
import com.ztesoft.common.util.MD5Util;
import com.ztesoft.common.util.RSAUtil;
import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.common.util.SessionHelper;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.sql.Sql;

import appfrm.app.util.ListUtil;
import appfrm.app.vo.PageModel;
import spring.util.DBUtil;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class StaffBO {
	
	public RpcPageModel queryStaff(HashMap params) {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String staff_name = StringUtils.trim(MapUtils.getString(params, "staff_name"));
		String state = MapUtils.getString(params, "state", KeyValues.STATE_00A);
		String team_id = MapUtils.getString(params, "team_id");
		String staff_code = StringUtils.trim(MapUtils.getString(params, "staff_code"));

		StringBuffer sql = new StringBuffer("");
		sql.append("select s.staff_id, s.staff_name, s.staff_code, s.password, s.gender, s.state, s.is_manager, s.staff_desc, o.org_id, o.org_name");
		sql.append("  from bdp_dm_organization o, dm_staff s");
		sql.append(" where o.org_id = s.org_id");

		List sqlParams = new ArrayList();
		if (StringUtils.isNotEmpty(staff_name)) {
			sql.append(" and s.staff_name like ? ");
			sqlParams.add("%" + staff_name + "%");
		}
		if(StringUtils.isNotEmpty(staff_code)) {
			sql.append(" and s.staff_code = ? ");
			sqlParams.add(staff_code);
		}
		if (StringUtils.isNotEmpty(state)) {
			sql.append(" and s.state = ? ");
			sqlParams.add(state);
		}
		if (StringUtils.isNotEmpty(team_id)) {
			sql.append(" and s.staff_id not in (");
			sql.append("select vs.staff_id");
			sql.append("  from dm_organization oo, m_team_member t, vr_staff vs");
			sql.append(" where oo.org_id = t.org_id");
			sql.append("   and t.vr_staff_id = vs.vr_staff_id");
			sql.append("   and oo.org_id = ? ");
			sql.append(")");
			sqlParams.add(team_id);
		}
		sql.append(" order by s.create_date desc");
		RpcPageModel result = DBUtil.getSimpleQuery().queryForRpcPageModel(sql.toString(), null, page_size, page_index,
				sqlParams.toArray(new String[] {}));
		return result;
	}

	public int setDefTeamId(String defTeamId) {
		String sql = "update dm_staff set def_team_id = ? where staff_id = ? ";
		String staffId = SessionHelper.getStaffId();
		DAO.update(sql, defTeamId, staffId);
		return 1;
	}
	
	public int addStaff(HashMap params) {
		return 0;
	}
	
	/**
	 * 创建虚拟工号
	 * @param staff_id
	 * @return 虚拟工号ID
	 */
	public String addVRStaff(String staff_id) {
		if (StringUtils.isEmpty(staff_id)) {
			return "-1";
		}
		String vr_staff_id = SeqUtil.getSeq("DM_STAFF", "STAFF_ID");
		String staff_code = DBUtil.getSimpleQuery().querySingleValue("select staff_code from dm_staff where staff_id = ?", new String[] { staff_id });

		List sqlParams = new ArrayList();
		sqlParams.add(vr_staff_id);
		sqlParams.add(staff_code + "_vr_" + vr_staff_id);
		sqlParams.add(staff_id);
		sqlParams.add(String.valueOf(Math.abs(new Random(System.nanoTime()).nextLong())).substring(0, 8));
		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("INSERT_VR_STAFF_SQL"), sqlParams);

		return vr_staff_id;
	}

	public Map addStaff(Map params) {
		// 获取主键，大写
		String staff_id = SeqUtil.getSeq("DM_STAFF", "STAFF_ID");
		String staff_code = MapUtils.getString(params, "staff_code");
		// TODO, 密码需要加密，方式待定
		//String password = MD5Util.encryption("123456");
		System.out.println(RSAUtil.decrypt(MapUtils.getString(params, "password")));
		String password = MD5Util.encrypt(RSAUtil.decrypt(MapUtils.getString(params, "password")), staff_id);
		String staff_name = MapUtils.getString(params, "staff_name");
		String gender = MapUtils.getString(params, "gender");
		String is_manager = MapUtils.getString(params, "is_manager");
		String staff_desc = MapUtils.getString(params, "staff_desc");
		String create_date = DAOUtils.getFormatedDate();
		String eff_date = create_date;
		String state = MapUtils.getString(params, "state");
		String org_id = MapUtils.getString(params, "org_id");
		Map result = new HashMap();
		if (exitsStaffCode(staff_code, "")) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "新增失败:人员工号已经存在");
			return result;
		}
		try {
			// 执行插入
			String sql = "insert into dm_staff(staff_id, org_id, staff_code, password, staff_name, gender, staff_desc, create_date, eff_date, state, is_manager) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?)";
			DAO.update(sql, new String[] { staff_id, org_id, staff_code,
					password, staff_name, gender, staff_desc, create_date,
					eff_date, state, is_manager });
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
	 * 判断人员工号是否在已经存在，true存在
	 */
	private boolean exitsStaffCode(String staff_code, String staff_id) {
		String sql = "select 1 from dm_staff where staff_code = ? ";
		if (StringUtils.isNotEmpty(staff_id)) {
			sql += "and staff_id <> '" + staff_id + "'";
		}
		List list = DAO.queryForMap(sql, new String[] { staff_code });
		if (!ListUtil.isEmpty(list)) {
			return true;
		}
		return false;
	}
	
	public Map editStaff(Map params) {
		String staff_id = MapUtils.getString(params, "staff_id");
		String staff_code = MapUtils.getString(params, "staff_code");
		String staff_name = MapUtils.getString(params, "staff_name");
		String gender = MapUtils.getString(params, "gender");
		String is_manager = MapUtils.getString(params, "is_manager");
		String staff_desc = MapUtils.getString(params, "staff_desc");
		String state = MapUtils.getString(params, "state");
		String org_id = MapUtils.getString(params, "org_id");
		Map result = new HashMap();
		if (exitsStaffCode(staff_code, staff_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "修改失败:人员工号已被使用");
			return result;
		}
		try {
			if (StringUtils.isEmpty(StringUtils.trim(MapUtils.getString(params, "new_password")))) {
				String sql = "update dm_staff set staff_code=?, org_id=?, staff_name=?, gender=?, staff_desc=?, is_manager=?, state=? where staff_id = ?";
				DAO.update(sql, new String[] { staff_code, org_id, staff_name, gender, staff_desc, is_manager, state, staff_id });
			} else {
				String sql = "update dm_staff set staff_code=?, org_id=?, staff_name=?, gender=?, staff_desc=?, is_manager=?, state=?, password=? where staff_id = ?";
				String password = MD5Util.encrypt(RSAUtil.decrypt(MapUtils.getString(params, "new_password")), staff_id);
				DAO.update(sql, new String[] { staff_code, org_id, staff_name, gender, staff_desc, is_manager, state, password, staff_id });
			}
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "修改成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "修改失败!");
		}
		return result;
	}
	
	public Map delStaff(Map params) {
		String staff_id = MapUtils.getString(params, "staff_id");
		Map result = new HashMap();
		if (exitsStaffRole(staff_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "删除失败:人员存在关联角色无法删除");
			return result;
		}
		if (exitsStaffPrivilege(staff_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "删除失败:人员存在关联权限无法删除");
			return result;
		}
		try {
			String sql = "update dm_staff set state = '00X' where staff_id = ?";
			DAO.update(sql, new String[] { staff_id });
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
	 * 是否存在人员角色关联关系,true，存在
	 */
	private boolean exitsStaffRole(String staff_id) {
		String sql = "select 1 from dm_staff_role where staff_id = ?";
		List list = DAO.queryForMap(sql, new String[] { staff_id });
		if (!ListUtil.isEmpty(list)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否存在人员权限关联关系，true，存在
	 */
	private boolean exitsStaffPrivilege(String staff_id) {
		String sql = "select 1 from dm_staff_privilege where staff_id = ?";
		List list = DAO.queryForMap(sql, new String[] { staff_id });
		if (!ListUtil.isEmpty(list)) {
			return true;
		}
		return false;
	}

	public List<Map> getStaffRoleList(Map params) {
		String staff_id = MapUtils.getString(params, "staff_id");
		String sql = "select t1.role_id, t1.role_name, t2.role_id id"
				+ " from dm_role t1 LEFT JOIN dm_staff_role t2 on t1.ROLE_ID = t2.ROLE_ID " + "and t2.STAFF_ID = ?";

		List sqlParams = new ArrayList();
		sqlParams.add(staff_id);

		List<Map> result = DBUtil.getSimpleQuery().queryForMapListBySql(sql, sqlParams);
		return result;
	}

	public Map addStaffRole(Map params) {
		String staff_id = MapUtils.getString(params, "staff_id");
		String role_id = MapUtils.getString(params, "role_id");
		Map result = new HashMap();
		if (exitsStaffRole(staff_id, role_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "关联失败：已经存在人员角色关联关系");
			return result;
		}
		try {
			String sql = "insert into dm_staff_role(staff_id, role_id) values(?,?)";
			DAO.update(sql, new String[] { staff_id, role_id });
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
	 * 判断是否存在人员角色关系
	 */
	private boolean exitsStaffRole(String staff_id, String role_id) {
		String sql = "select 1 from dm_staff_role where role_id = ? and staff_id = ?";
		List list = DAO.queryForMap(sql, new String[] { role_id, staff_id });
		if (!ListUtil.isEmpty(list)) {
			return true;
		}
		return false;
	}
	
	public Map delStaffRole(Map params) {
		String staff_id = MapUtils.getString(params, "staff_id");
		String role_id = MapUtils.getString(params, "role_id");
		Map result = new HashMap();
		try {
			String sql = "delete from dm_staff_role where staff_id=? and role_id=?";
			DAO.update(sql, new String[] { staff_id, role_id });
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "删除成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "删除失败!");
		}
		return result;
	}
	
	public List<Map> getStaffPrivileges(Map params) {
		String staff_id = MapUtils.getString(params, "staff_id");
		String parent_privilege_id = MapUtils.getString(params, "id", "-1");
		
		StringBuffer sql = new StringBuffer(Sql.SYS_SQLS.get("QUERY_STAFF_PRIVILEGE_SQL"));
		sql.append(" and a.type = ?");
		sql.append(" and a.parent_privilege_id = ?");

		List list = DAO.queryForMap(sql.toString(), new String[] { staff_id, staff_id, KeyValues.PRIVILEGE_TYPE_MENU, parent_privilege_id });

		return list;
	}
	
	public Map addStaffPrivilege(Map params) {
		String staff_id = MapUtils.getString(params, "staff_id");
		String privilege_id = MapUtils.getString(params, "privilege_id");
		Map result = new HashMap();
		// 判断上级权限是否增加过
		if (!exitsStaffParPrivilege(staff_id, privilege_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "新增失败：请先新增此权限的上级权限");
			return result;
		}
		// 判断此权限是否增加过
		if (exitsStaffPrivilege(staff_id, privilege_id)) {
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "新增失败：该角色已经添加过此权限");
			return result;
		}
		try {
			String sql = "insert into dm_staff_privilege(staff_id, privilege_id) values(?,?)";
			DAO.update(sql, new String[] { staff_id, privilege_id });
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
	 * 新增前判断上级权限是否已经存在人员权限关系，不存在需要先增加上级权限
	 */
	private boolean exitsStaffParPrivilege(String staff_id, String privilege_id) {
		// 获取上级权限ID
		String sql = "select parent_privilege_id from dm_privilege where privilege_id=?";
		List list = DAO.queryForMap(sql, new String[] { privilege_id });
		String parent_privilege_id = MapUtils.getString((Map) list.get(0), "parent_privilege_id");
		if (parent_privilege_id.equals("-1")) { // 最上级
			return true;
		}
		return exitsStaffPrivilege(staff_id, parent_privilege_id);
	}

	/**
	 * 判断是否存在人员关联权限，true存在
	 */
	private boolean exitsStaffPrivilege(String staff_id, String privilege_id) {
		String sql = "select 1 from dm_staff_privilege where staff_id = ? and privilege_id = ?";
		List list = DAO.queryForMap(sql, new String[] { staff_id, privilege_id });
		if (!ListUtil.isEmpty(list)) {
			return true;
		}
		return false;
	}

	public Map delStaffPrivilege(Map params) {
		String staff_id = MapUtils.getString(params, "staff_id");
		String privilege_id = MapUtils.getString(params, "privilege_id");
		Map result = new HashMap();
		try {
			String sql = "delete from dm_staff_privilege where staff_id=? and privilege_id=?";
			DAO.update(sql, new String[] { staff_id, privilege_id });
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
