package com.ztesoft.dubbo.mp.sys.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.inf.util.KeyValues;

import appfrm.app.util.ListUtil;
import appfrm.app.util.SeqUtil;
import appfrm.resource.dao.impl.DAO;

/**
 * 人员--组织； 人员--角色； 角色--权限； 人员--权限； 权限--菜单------------------
 * 人员：状态state=00A有效，00X无效------------------------------------------
 * 
 * @author zhao.jingang
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class LoginBO {
	
	/**
	 * 0 细化验证逻辑，分开验证用户名和密码
	 * 根据用户名获取用户ID
	 * @author liao.yongtao
	 */
	public Map getStaffID(String staff_code) {
		String sql = "select staff_id from dm_staff where staff_code = ? and state = '00A'";
		List list = DAO.queryForMap(sql, new String[] { staff_code });
		if (!ListUtil.isEmpty(list)) {
			return (Map) list.get(0);
		}
		return new HashMap();
	}

	/**
	 * 1 登录，根据用户名/密码获取用户
	 */
	public Map getStaffByCodeAnPwd(String staff_code, String password) {
		String sql = "select staff_id from dm_staff where staff_code = ? and password = ? and state = '00A'";
		List list = DAO.queryForMap(sql, new String[] { staff_code, password });
		if (!ListUtil.isEmpty(list)) {
			return (Map) list.get(0);
		}
		return new HashMap();
	}

	/**
	 * 2.1 获取菜单权限ID-------------------------------------------------------
	 * 根据人员ID获取对应的权限列表，用户--角色--权限， 用户--权限
	 * 此情况是一次性查询登录用户的所有角色下的所有权限+用户对应的权限，然后去重，获取一个权限列表，不存在切换权限的功能
	 */
	public List getPrivileges(String staff_id) {
		String sql = "SELECT privilege_id FROM dm_role_privilege "
				+ "WHERE page_menu_id is null and role_id IN "
				+ "(SELECT t1.ROLE_ID FROM dm_staff_role t1, dm_role t2 "
				+ "	WHERE t1.ROLE_ID = t2.ROLE_ID and t1.staff_id =(SELECT STAFF_ID FROM dm_staff WHERE STAFF_ID = ?)) "
				+ "UNION "
				+ "SELECT privilege_id FROM dm_staff_privilege "
				+ "WHERE page_menu_id is null and staff_id = (SELECT STAFF_ID FROM dm_staff WHERE STAFF_ID = ?) "
				+ "ORDER BY PRIVILEGE_ID";
		return DAO.queryForMap(sql, staff_id, staff_id);
	}

	/**
	 * 2.2 根据菜单权限列表获取对应的菜单
	 */
	public List getPrivilegeMenus(List privileges) {
		if(ListUtil.isEmpty(privileges)){
			return new ArrayList();
		}
		String sql = "SELECT * from dm_menu where MENU_ID in ("
				+ "select MENU_ID from dm_privilege WHERE PRIVILEGE_ID in (";
		String param[] = new String[privileges.size()];
		for (int i = 0; i < privileges.size(); i++) {
			Map map = (Map) privileges.get(i);
			param[i] = StringUtil.getStrValue(map, "privilege_id");
			sql += "?,";
		}
		sql = sql.substring(0, sql.length() - 1) + "))";
		return DAO.queryForMap(sql, param);
	}

	/**
	 * 3. 根据进入页面，获取页面按钮列表
	 * 
	 * @param params
	 * @return
	 */
	public List<?> getPageBtnPrivilege(List menus, String staff_id) {
		List list = new ArrayList();
		// 先获取所有的有url的页面
		for (int i = 0; i < menus.size(); i++) {
			Map menu = (Map) menus.get(i);
			String url = StringUtil.getStrValue(menu, "url");
			if (StringUtil.isEmpty(url)) {
				continue;
			}
			String page_menu_id = StringUtil.getStrValue(menu, "menu_id");
			String sql = "select DISTINCT menu_code,menu_name,class_name from dm_menu t1, dm_privilege t2 "
					+ "where t1.MENU_ID = t2.MENU_ID and t1.TYPE = ?  "
					+ "and t2.PRIVILEGE_ID in ( "
					+ "select PRIVILEGE_ID from dm_role_privilege where ROLE_ID in  "
					+ "(select ROLE_ID from dm_staff_role where STAFF_ID = ?)  "
					+ "and PAGE_MENU_ID = ?  "
					+ "UNION "
					+ "select PRIVILEGE_ID from dm_staff_privilege where STAFF_ID = ?  "
					+ "and PAGE_MENU_ID = ? )";
			List btns = DAO.queryForMap(sql, KeyValues.MENU_TYPE_BUTTON,
					staff_id, page_menu_id, staff_id, page_menu_id);
			Map map = new HashMap();
			map.put("menu_id", StringUtil.getStrValue(menu, "menu_id"));
			map.put("btns", btns);
			list.add(map);
		}
		return list;
	}

	/**
	 * 4. 根据登录人id获取登录人信息
	 * 
	 * @param staff_id
	 * @return
	 */
	public Map getUserInfoById(String staff_id) {
		String sql = "select t1.staff_id, t1.staff_code, t1.staff_name, t1.org_id, t2.org_name, t1.def_team_id, t1.is_manager "
				+ "from dm_staff t1 left join dm_organization t2 "
				+ "on t1.ORG_ID = t2.ORG_ID "
				+ "left join dm_organization team on t1.def_team_id = team.org_id  "
				+ "where t1.staff_id = ? and t1.state = '00A'";
		List list = DAO.queryForMap(sql, new String[] { staff_id });
		if (!ListUtil.isEmpty(list)) {
			Map m = (Map) list.get(0);
			return m;
		}
		return null;
	}

	/**
	 * 4.1 根据登录信息找出该成员的所属团队
	 * 按默认团队查找，如果未设置默认团队，或默认团队无效，则查找其第一个所属团队，否则置空
	 * @param loginInfo
	 */
	public void getTeamInfo(Map loginInfo) {
		String defaultTeamId = (String) loginInfo.get("def_team_id");
		String staffId = (String) loginInfo.get("staff_id");
		List<Map> teams = this.getTeamListByStaffId(staffId);
		if(!ListUtil.isEmpty(teams)) {
			if (defaultTeamId != null) {
				for (int i = 0; i < teams.size(); i++) {
					Map m = teams.get(i);
					if (defaultTeamId.equals(m.get("team_id"))) {
						loginInfo.putAll(m);
						return;
					}
				}
			}
			Map m = teams.get(0);
			loginInfo.putAll(m);
		}
	}

	/**
	 * 4.2 查询团队列表
	 * @param staffId
	 * @return
	 */
	public List<Map> getTeamListByStaffId(String staffId) {
		String sql = "select org.org_id team_id, org.org_name team_name, org.lan_id, vs.vr_staff_id, mtm.is_director, mtm.tenant_code " +
				"from m_team_member mtm, vr_staff vs, dm_organization org " +
				"where mtm.vr_staff_id = vs.vr_staff_id and mtm.org_id = org.org_id " +
				"and vs.staff_id = ? ";
		List<Map> teams = DAO.queryForMap(sql, staffId);
		return teams;
	}
	
	/**
	 * 5. 登录日志
	 * 
	 * @param
	 */
	public void addEntryLog(Map params){
		String sql = "insert into LOG_STAFF_LOGIN "
				+ "(ID, STAFF_ID, STAFF_NAME, STAFF_CODE, "
				+ "ORG_ID, LOGIN_ACTION, IS_SUCCESS, "
				+ "CREATE_TIME, DESCRIBE) "
				+ "values(?,?,?,?,?,?,?,to_date(?, 'YYYY-MM-DD HH24:MI:SS'),?)";
		String id = SeqUtil.getInst().getNext("LOG_STAFF_LOGIN", "ID");
		String staff_id = StringUtil.getStrValue(params, "staff_id");
		String staff_name = StringUtil.getStrValue(params, "staff_name");
		String staff_code = StringUtil.getStrValue(params, "staff_code");
		String org_id = StringUtil.getStrValue(params, "org_id");
		String login_action = StringUtil.getStrValue(params, "login_action");
		String is_success = StringUtil.getStrValue(params, "is_success");
		String create_time = DateUtil.getFormatedDateTime();
		String describe = StringUtil.getStrValue(params, "describe");
		
		DAO.update(sql, new String[] {id, staff_id, staff_name, staff_code, org_id, 
				login_action, is_success, create_time, describe});
		
	}
	
	/**
	 * 6.1 查询最近的几条日志
	 * @param
	 */
	public Map getCurrentEntryLog(Map params){
		String rownum = StringUtil.getStrValue(params, "rownum");
		String sql = "select STAFF_CODE, CREATE_TIME from "
				+ "(select STAFF_CODE, CREATE_TIME form LOG_STAFF_LOGIN "
				+ "order by CREATE_TIME DESC) "
				+ "where rownum<? ";
		
		List list = DAO.queryForMap(sql, new String[] { rownum });
		if (!ListUtil.isEmpty(list)) {
			return (Map) list.get(0);
		}
		return new HashMap();
	}
	
	/**
	 * 6.2 查询全部日志
	 * @param
	 */
	public Map getAllEntryLog(Map params){
		String rownum = StringUtil.getStrValue(params, "rownum");
		String sql = "select STAFF_CODE, CREATE_TIME from "
				+ "(select STAFF_CODE, CREATE_TIME form LOG_STAFF_LOGIN "
				+ "order by CREATE_TIME DESC) "
				+ "where rownum<? ";
		
		List list = DAO.queryForMap(sql, new String[] { rownum });
		if (!ListUtil.isEmpty(list)) {
			return (Map) list.get(0);
		}
		return new HashMap();
	}
}
