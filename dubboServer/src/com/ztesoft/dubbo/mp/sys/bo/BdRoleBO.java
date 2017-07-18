package com.ztesoft.dubbo.mp.sys.bo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ztesoft.common.dao.DAOUtils;
import com.ztesoft.common.util.DcSystemParamUtil;
import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.common.util.SessionHelper;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.dubbo.inf.ITenantHandler;
import com.ztesoft.dubbo.inf.impl.TenantHandler;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.sql.Sql;

import appfrm.app.util.ListUtil;
import spring.util.DBUtil;

@Component
@SuppressWarnings("all")
public class BdRoleBO {
	
	public ITenantHandler getTenantHandler() {
		ITenantHandler tenantHandler = new TenantHandler();
		return tenantHandler;
	}
	
	public Map getPriv(Map params) {
		Map result = new HashMap();
		result.put("is_manager", SessionHelper.isManager());
		result.put("is_director", SessionHelper.isDirector());
		result.put("team_id", SessionHelper.getTeamId());
		result.put("team_name", SessionHelper.getTeamName());
		return result;
	}

	public RpcPageModel queryRole(Map params) {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String role_name = MapUtils.getString(params, "role_name");
		String role_type = MapUtils.getString(params, "role_type");
		
		StringBuffer sql = new StringBuffer(Sql.SYS_SQLS.get("SELECT_BD_ROLE_SQL"));

		List sqlParams = new ArrayList();
		if (StringUtils.isNotEmpty(role_name)) {
			sql.append(" and a.role_name like ? ");
			sqlParams.add("%" + role_name + "%");
		}
		if (StringUtils.isNotEmpty(role_type)) {
			sql.append(" and a.role_type = ? ");
			sqlParams.add(role_type);
		}
		// 非管理员只能查看自己的角色
		if (!SessionHelper.isManager()) {
			sql.append("   and exists (select 1");
			sql.append("          from vr_staff_role b");
			sql.append("         where b.role_code = a.role_code");
			sql.append("           and b.vr_staff_id = ?)");
			sqlParams.add(SessionHelper.getVrStaffId());
		}
		sql.append(" order by state_date desc");
		RpcPageModel result = DBUtil.getSimpleQuery().queryForRpcPageModel(sql.toString(), null, page_size, page_index,
				sqlParams.toArray(new String[] {}));
		return result;
	}
	
	public boolean addRole(HashMap params) throws Exception {
		// BDSME_ROLE_ + 9位填充序列 生成角色编码
		DecimalFormat df = new DecimalFormat("000000000");
		String role_code = "BDSP_" + df.format(Long.parseLong(SeqUtil.getSeq("BD_ROLE", "ROLE_CODE")));
		String role_name = MapUtils.getString(params, "role_name");
		String role_type = MapUtils.getString(params, "role_type");
		if(role_type == null || "".equals(role_type)){
			role_type = KeyValues.BDP_DEF_ROLE_TYPE;
		}

		// 调用BDP接口创建角色
		ITenantHandler handler = getTenantHandler();
		handler.AddDelTenantRole("add", role_code, role_name, role_type);
		
		List sqlParams = new ArrayList();
		sqlParams.add(role_code);
		sqlParams.add(role_name);
		sqlParams.add(DAOUtils.getFormatedDate());
		sqlParams.add(DAOUtils.getFormatedDate());
		sqlParams.add(KeyValues.STATE_00A);
		sqlParams.add(role_type);
		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("INSERT_BD_ROLE_SQL"), sqlParams);

		return true;
	}
	
	public boolean updateRole(HashMap params) {
		String role_code = MapUtils.getString(params, "role_code");
		if (StringUtils.isEmpty(role_code)) {
			return false;
		}
		List sqlParams = new ArrayList();
		sqlParams.add(MapUtils.getString(params, "role_name"));
		sqlParams.add(DAOUtils.getFormatedDate());
		sqlParams.add(role_code);
		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("UPDATE_BD_ROLE_SQL"), sqlParams);
		return true;
	}
	
	public boolean delRole(Map params) throws Exception {
		String role_code = MapUtils.getString(params, "role_code");
		String role_type = KeyValues.BDP_DEF_ROLE_TYPE;
		if (StringUtils.isEmpty(role_code)) {
			return false;
		}
		// 调用BDP接口删除角色
		ITenantHandler handler = getTenantHandler();
		handler.AddDelTenantRole("del", role_code, "", role_type);
				
		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("DELETE_BD_ROLE_SQL"), new String[] { role_code });
		return true;
	}
	
	public RpcPageModel queryRelStaff(Map params) {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String role_code = MapUtils.getString(params, "role_code");
		String staff_name = MapUtils.getString(params, "staff_name");

		StringBuffer sql = new StringBuffer(Sql.SYS_SQLS.get("QUERY_BD_ROLE_REL_STAFF_SQL"));

		List sqlParams = new ArrayList();
		if (StringUtils.isNotEmpty(role_code)) {
			sql.append("   and c.role_code = ?");
			sqlParams.add(role_code);
		}
		if (StringUtils.isNotEmpty(staff_name)) {
			sql.append("   and d.staff_name like ?");
			sqlParams.add("%" + staff_name + "%");
		}
		RpcPageModel result = DBUtil.getSimpleQuery().queryForRpcPageModel(sql.toString(), null, page_size, page_index,
				sqlParams.toArray(new String[] {}));
		return result;
	}
	
	public boolean addRelStaff(Map params) throws Exception {
		String role_code = MapUtils.getString(params, "role_code");
		List<Map> staffs = (List<Map>) MapUtils.getObject(params, "staffs");

		ITenantHandler handler = getTenantHandler();
		List sqlParams = new ArrayList();
		for (Map staff : staffs) {
			String vr_staff_id = MapUtils.getString(staff, "vr_staff_id");
			if (!checkRelStaff(vr_staff_id, role_code)) {
				continue;
			}
			
			List list = new ArrayList();
			list.add(vr_staff_id);
			list.add(role_code);
			list.add(DAOUtils.getFormatedDate());
			sqlParams.add(list.toArray(new String[] {}));
			
		}
		if (sqlParams.size() > 0) {
			DBUtil.getSimpleQuery().batchUpdate(Sql.SYS_SQLS.get("INSERT_VR_STAFF_ROLE_SQL"), sqlParams);
			
			// 调用接口绑定租户角色，报错直接抛出不要拦截
			for (Map staff : staffs) {
				String type = "grant";
				String staff_id = MapUtils.getString(staff, "staff_id");
				String org_id = MapUtils.getString(staff, "team_id");
				String tenant_code = handler.getTenantCode(staff_id, org_id);
				String role_type = KeyValues.BDP_DEF_ROLE_TYPE;
				handler.tenantRoleRel(type, tenant_code, role_code, role_type);
			}
		}
		
		return true;
	}
	
	public boolean checkRelStaff(String vr_staff_id, String role_code) {
		String count = DBUtil.getSimpleQuery().querySingleValue(
				"select count(*) from vr_staff_role where vr_staff_id = ? and role_code = ?",
				new String[] { vr_staff_id, role_code });
		if (!"0".equals(count)) {
			return false;
		}
		return true;
	}
	
	public boolean delRelStaff(Map params) throws Exception {
		String role_code = MapUtils.getString(params, "role_code");
		String vr_staff_id = MapUtils.getString(params, "vr_staff_id");
		if (StringUtils.isEmpty(role_code) || StringUtils.isEmpty(vr_staff_id)) {
			return false;
		}
		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("DELETE_BD_ROLE_REL_STAFF_SQL"),
				new String[] { role_code, vr_staff_id });
		
		// 调用接口删除租户角色，报错直接抛出不要拦截
		ITenantHandler handler = getTenantHandler();
		String type = "revoke";
		String staff_id = MapUtils.getString(params, "staff_id");
		String org_id = MapUtils.getString(params, "org_id");
		String tenant_code = handler.getTenantCode(staff_id, org_id);
		String role_type = KeyValues.BDP_DEF_ROLE_TYPE;
		Map infRet = handler.tenantRoleRel(type, tenant_code, role_code, role_type);
		
		return true;
	}
	
	public RpcPageModel queryTeamStaff(Map params) {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String team_id = SessionHelper.isManager() ? MapUtils.getString(params, "team_id") : SessionHelper.getTeamId();
		String role_code = MapUtils.getString(params, "role_code");
		String staff_name = MapUtils.getString(params, "staff_name");

		StringBuffer sql = new StringBuffer(Sql.SYS_SQLS.get("QUERY_TEAM_STAFF_SQL"));

		List sqlParams = new ArrayList();
		if (StringUtils.isNotEmpty(role_code)) {
			sql.append("   and not exists (select 1");
			sql.append("          from vr_staff_role d");
			sql.append("         where d.vr_staff_id = a.vr_staff_id");
			sql.append("           and d.role_code = ?)");
			sqlParams.add(role_code);
		}
		if (StringUtils.isNotEmpty(staff_name)) {
			sql.append("   and c.staff_name like ?");
			sqlParams.add("%" + staff_name + "%");
		}
		if (StringUtils.isNotEmpty(team_id)) {
			sql.append("   and b.org_id = ?");
			sqlParams.add(team_id);
		}
		RpcPageModel result = DBUtil.getSimpleQuery().queryForRpcPageModel(sql.toString(), null, page_size, page_index,
				sqlParams.toArray(new String[] {}));
		return result;
	}
	
	public RpcPageModel queryRelPriv(Map params) {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String role_code = MapUtils.getString(params, "role_code");
		String priv_type = MapUtils.getString(params, "priv_type");
		String priv_code = MapUtils.getString(params, "priv_code");

		StringBuffer sql = new StringBuffer(Sql.SYS_SQLS.get("SELECT_BD_ROLE_PRIV_SQL"));

		List sqlParams = new ArrayList();
		if (StringUtils.isNotEmpty(role_code)) {
			sql.append("   and role_code = ?");
			sqlParams.add(role_code);
		}
		if (StringUtils.isNotEmpty(priv_type)) {
			sql.append("   and priv_type = ?");
			sqlParams.add(priv_type);
		}
		if (StringUtils.isNotEmpty(priv_code)) {
			sql.append("   and priv_code = ?");
			sqlParams.add(priv_code);
		}
		RpcPageModel result = DBUtil.getSimpleQuery().queryForRpcPageModel(sql.toString(), null, page_size, page_index,
				sqlParams.toArray(new String[] {}));
		return result;
	}
	
	public boolean addRelPriv(Map params) throws Exception {
		String role_code = MapUtils.getString(params, "role_code");
		List<Map> privs = (List<Map>) MapUtils.getObject(params, "privs");
		ITenantHandler handler = getTenantHandler();
		String object_type = "";	//HDFS目录：24   HDFS 文件：25  HIVE表：23  HBASE表：12 队列：21
		String object_ids = "";		//对象ID，可以填写多个，逗号隔开
		
		String role_type = KeyValues.BDP_DEF_ROLE_TYPE;
		
		List<String> objectIds = new ArrayList<String>();
		List sqlParams = new ArrayList();
		for (Map priv : privs) {
			priv.put("role_code", role_code);
			if (!checkRelPriv(priv)) {
				continue;
			}
			
			String priv_id = MapUtils.getString(priv, "priv_id");
			String priv_code = MapUtils.getString(priv, "priv_code");
			String priv_type = MapUtils.getString(priv, "priv_type");
			object_type = priv_type;
			
			if(StringUtil.isEmpty(priv_id)){
				priv_id = priv_code;
			}
			if(!objectIds.contains(priv_id)){
				objectIds.add(priv_id);
			}
			
			List list = new ArrayList();
			list.add(role_code);
			list.add(priv_code);
			list.add(priv_type);
			list.add(DAOUtils.getFormatedDate());
			sqlParams.add(list.toArray(new String[] {}));
		}
		if (sqlParams.size() > 0) {
			// 调用接口绑定租户角色权限，报错直接抛出不要拦截
			String type = "grant"; // 类型 grant:授予 revoke:解除
			if (objectIds.size() > 0) {
				object_ids = StringUtil.join(",", objectIds.iterator());
			}
			Map result = handler.tenantRolePrivilegeRel(type, role_code, object_type, object_ids, role_type);
			
			if(result != null){
				boolean success = (Boolean) result.get("success");
				if(success){
					DBUtil.getSimpleQuery().batchUpdate(Sql.SYS_SQLS.get("INSERT_BD_ROLE_PRIV_SQL"), sqlParams);
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean checkRelPriv(Map params) {
		RpcPageModel result = queryRelPriv(params);
		if (result.getTotal() > 0) {
			return false;
		}
		return true;
	}
	
	public boolean delRelPriv(Map params) throws Exception {
		String role_code = MapUtils.getString(params, "role_code");
		String priv_type = MapUtils.getString(params, "priv_type");
		String priv_code = MapUtils.getString(params, "priv_code");
		String role_type = KeyValues.BDP_DEF_ROLE_TYPE;
		if (StringUtils.isEmpty(role_code) || StringUtils.isEmpty(priv_type) || StringUtils.isEmpty(priv_code)) {
			return false;
		}
		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("DELETE_BD_ROLE_PRIV_SQL"),
				new String[] { role_code, priv_type, priv_code });
		
		// 调用接口删除租户角色权限，报错直接抛出不要拦截
		ITenantHandler handler = getTenantHandler();
		String staff_id = MapUtils.getString(params, "staff_id");
		String org_id = MapUtils.getString(params, "org_id");
		String type	= "revoke";			//类型 grant:授予  revoke:解除
		String object_type = "";	//HDFS目录：24   HDFS 文件：25  HIVE表：23  HBASE表：12 队列：21  HIVE视图：29
		String object_ids = priv_code;		//对象ID，可以填写多个，逗号隔开
		handler.tenantRolePrivilegeRel(type, role_code, object_type, object_ids, role_type);
		
		return true;
	}
	
	public List queryPriv(Map params) throws Exception {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String priv_type = MapUtils.getString(params, "priv_type");
		String priv_code = MapUtils.getString(params, "priv_code");

		StringBuffer sql = new StringBuffer();

		List sqlParams = new ArrayList();

		List result = new ArrayList();
		if (StringUtils.isNotEmpty(priv_type)) {
			if (KeyValues.BD_PRIV_21.equals(priv_type)) {
				result.addAll(queryPrivQueue(params));
			} else if (KeyValues.BD_PRIV_29.equals(priv_type)) {
				result.addAll(queryPrivHive(params));
			} else {
				
			}
		}
		
		return result;
	}
	
	public List queryPrivQueue(Map params) throws Exception {
		List result = new ArrayList();

		String owner = DcSystemParamUtil.getSysParamByCache("BDP_QUEUE_OWNER");
		if (StringUtils.isNotEmpty(owner)) {
			ITenantHandler tenantHandler = getTenantHandler();
			Map resp = tenantHandler.getQueues(owner);
			List rows = (ArrayList) resp.get("rows");
			for (int i = 0; rows != null && i < rows.size(); i++) {
				Map que = (Map) rows.get(i);
				
				Map m = new HashMap();
				m.put("role_code", MapUtils.getString(params, "role_code"));
				m.put("priv_type", KeyValues.BD_PRIV_21);
				m.put("priv_code", que.get("objCode"));
				m.put("priv_id", que.get("objId"));
				if (!checkRelPriv(m)) {
					continue;
				}
				result.add(m);
			}
		}
		return result;
	}
	
	public List queryPrivHive(Map params) {
		String role_code = MapUtils.getString(params, "role_code");
		String data_code = MapUtils.getString(params, "priv_code");

		StringBuffer sql = new StringBuffer(Sql.SYS_SQLS.get("QUERY_BD_PRIV_HIVE"));

		List sqlParams = new ArrayList();
		if (StringUtils.isNotEmpty(role_code)) {
			sql.append("   and not exists (select 1");
			sql.append("          from bd_role_priv c");
			sql.append("         where c.priv_code = b.view_name");
			sql.append("           and c.role_code = ?)");
			sqlParams.add(role_code);
		}
		if (StringUtils.isNotEmpty(data_code)) {
			sql.append("   and b.view_name like ?");
			sqlParams.add("%" + data_code + "%");
		}
		// 非管理员只能查看自己的数据
		if (!SessionHelper.isManager()) {
			sql.append("   and a.org_id = ?");
			sqlParams.add(SessionHelper.getTeamId());
		}
		List result = DBUtil.getSimpleQuery().queryForMapListBySql(sql.toString(), sqlParams);
		if(!ListUtil.isEmpty(result)){
			for(int i=0; i<result.size(); i++){
				Map map = (Map) result.get(i);
				String priv_code = StringUtil.getStrValue(map, "priv_code");
				String schema_code = StringUtil.getStrValue(map, "schema_code");
				if(StringUtil.isNotEmpty(schema_code)){
					map.put("priv_code", schema_code + "." + priv_code);
				}
			}
		}
		return result;
	}
}
