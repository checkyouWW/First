package com.ztesoft.dubbo.mp.sys.bo;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import spring.util.DBUtil;
import spring.util.SpringContextUtil;
import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.dao.DAOUtils;
import com.ztesoft.common.ssh.SSHUtil;
import com.ztesoft.common.ssh.ShellResult;
import com.ztesoft.common.util.DBUtils;
import com.ztesoft.common.util.DcSystemParamUtil;
import com.ztesoft.common.util.RSAUtil;
import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.common.util.SessionHelper;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.dubbo.inf.ITenantHandler;
import com.ztesoft.dubbo.inf.impl.TenantHandler;
import com.ztesoft.dubbo.mp.data.util.BooleanWrap;
import com.ztesoft.dubbo.mp.sys.service.FtpService;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.ftp.IFtpUtil;
import com.ztesoft.inf.util.ftp.imp.FtpUtil;
import com.ztesoft.sql.Sql;
import comx.order.inf.IKeyValues;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class OrgMgrBO {
	
	@Autowired
	private StaffBO staffBO;
	
	private static final Logger log = Logger.getLogger(StaffBO.class);
	
	public ITenantHandler getTenantHandler() {
		ITenantHandler tenantHandler = new TenantHandler();
		return tenantHandler;
	}
	
	public String getAppToken(Map params) {
		
		String app_token = UUID.randomUUID().toString().replaceAll("-", "");
		
		return app_token;
	}
	
	public Map getInitInfo(Map params) {
		Map result = new HashMap();
		
		DecimalFormat df = new DecimalFormat("000000000");
		String org_code = "TEAM" + df.format(Long.parseLong(SeqUtil.getSeq("DM_ORGANIZATION", "ORG_CODE")));
		result.put("org_code", org_code);
		
		return result;
	}

	/**
	 * 查询团队列表
	 * @param params
	 * @return
	 */
	public PageModel queryOrg(HashMap params) {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String org_name = StringUtils.trim(MapUtils.getString(params, "org_name"));
		String org_code = StringUtils.trim(MapUtils.getString(params, "org_code"));
		
		StringBuffer sql = new StringBuffer(Sql.SYS_SQLS.get("SELECT_DM_ORGANIZATION_SQL"));
		
		List sqlParams = new ArrayList();
		if (StringUtils.isNotEmpty(org_name)) {
			sql.append(" and o.org_name like ? ");
			sqlParams.add("%" + org_name + "%");
		}
		if (StringUtils.isNotEmpty(org_code)) {
			sql.append(" and o.org_code like ? ");
			sqlParams.add("%" + org_code + "%");
		}
		sql.append(" order by o.create_date desc");
		PageModel result = DBUtil.getSimpleQuery().queryForPageModel(sql.toString(), null, page_size, page_index, sqlParams.toArray(new String[]{}));
		return result;
	}
	
	public List queryOrgList(Map params) {
		String org_id = StringUtils.trim(MapUtils.getString(params, "org_id"));
		StringBuffer sql = new StringBuffer(Sql.SYS_SQLS.get("SELECT_DM_ORGANIZATION_SQL"));

		List sqlParams = new ArrayList();
		if (StringUtils.isNotEmpty(org_id)) {
			sql.append(" and o.org_id = ? ");
			sqlParams.add(org_id);
		}

		List orgs = DBUtil.getSimpleQuery().queryForMapListBySql(sql.toString(), sqlParams);
		return orgs;
	}
	
	/**
	 * 新增团队
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map addOrg(HashMap params) throws Exception {
		Map result = new HashMap();
		result.put("success", false);
		int ck = check(params);
		if (ck != 0) {
			result.put("msg", "团队编码已存在，请重新填写！");
			return result;
		}
		
		String org_id = SeqUtil.getSeq("DM_ORGANIZATION", "ORG_ID");
		String org_name = MapUtils.getString(params, "org_name");
		
		ITenantHandler handler = getTenantHandler();;
		String action_type = "add";	//操作类型：add创建 del 删除
		String group_code = "BDSP_" + org_id	;	//租户组编码
		String group_name = org_name	;	//租户组名称
		String group_desc = org_name + "的租户组"	;	//租户组描述
		
		Map ret = handler.AddDelTenantGroup(action_type, group_code, group_name, group_desc);
		boolean success = (Boolean) ret.get("success");
		if(success){
			System.out.println("新增租户组成功");
		}
		else {
			System.out.println(ret);
			result.put("msg", ret.get("retMsg"));
			return result;
		}
		
		String owner = DcSystemParamUtil.getSysParamByCache("BDP_QUEUE_OWNER");
		String schema_code = group_code.toLowerCase();//HIVE库只能小写
		String schema_desc = "";
		String schema_name = group_code;
		String storage_quota = "";
		String bdp_staff_code = SessionHelper.getStaffCode();
		String bdp_staff_passwd = SessionHelper.getPasswordRsa();
		//广西创建库读取这个配置
		String bdp_tenant_code = DcSystemParamUtil.getBdpSysParamByCache("KERBEROS_ADMIN_USER");
		if(StringUtil.isEmpty(bdp_tenant_code)){
			bdp_tenant_code = SessionHelper.getTenantCode();
		}
		
		ITenantHandler hiveSchemaHandler = new TenantHandler(bdp_staff_code, bdp_staff_passwd, bdp_tenant_code);;
		Map schemaRet = hiveSchemaHandler.createHiveSchema(owner, schema_code, schema_desc, schema_name, storage_quota);
		if(schemaRet != null){
			success = (Boolean) schemaRet.get("success");
			if(!success){
				//删除租户组
				handler.AddDelTenantGroup("del", group_code, group_name, group_desc);
				
				result.put("msg", schemaRet.get("retMsg"));
				return result;
			}
			System.out.println(schemaRet);
		}
		
		try {
			//租户信息表
			String insert_group_sql = "insert into tenant_group(tenant_group_code,tenant_group_name,state,create_time,org_id) "
					+ "values(?,?,'00A',now(),?)";
			DAO.update(insert_group_sql, group_code, group_name, org_id);
			
			String insert_schema_sql = "insert into org_schema(schema_code,schema_name,schema_desc,create_date,org_id,owner,storage_quota) "
					+ "values(?,?,?,now(),?,?,?)";
			DAO.update(insert_schema_sql, schema_code,schema_name,schema_desc,org_id,owner,storage_quota);
			
			List sqlParams = new ArrayList();
			sqlParams.add(org_id);
			sqlParams.add("-1");
			sqlParams.add(MapUtils.getString(params, "org_code"));
			sqlParams.add(org_name);
			sqlParams.add(MapUtils.getString(params, "app_token"));
			sqlParams.add(MapUtils.getString(params, "org_content"));
			sqlParams.add(MapUtils.getString(params, "lan_id"));
			//团队表
			String sql = "insert into dm_organization (org_id, parent_org_id, org_code, org_name, app_token, org_content, lan_id, create_date) values (?, ?, ?, ?, ?, ?, ?, " + DBUtils.to_date(DAOUtils.getFormatedDate()) + ")";
			DBUtil.getSimpleQuery().excuteUpdate(sql, sqlParams);
			
			//在返回成功之前，进行增加ip和地址映射的操作
			String ftpId = Const.getStrValue(params, "ftp_def_id");
			String ftpUsr = Const.getStrValue(params, "ftp_def_usr");
			String ftpPwd = Const.getStrValue(params, "ftp_def_pwd");
			String ftpPath = Const.getStrValue(params, "ftp_def_dir");
			
			Map sendMap = new HashMap();
			sendMap.put("ftp_id", ftpId);
			sendMap.put("user", ftpUsr);
			sendMap.put("password", ftpPwd);
			sendMap.put("path", ftpPath);
			sendMap.put("org_id", org_id);
			SpringContextUtil.getApplicationContext().getBean(FtpService.class).saveOrgFtpRel(sendMap);
			
			String ftpIp = DAO.querySingleValue("select ip from ftp_server  where ftp_id=?", new String[]{ftpId});
			//构建ftp
			BooleanWrap wr = this.editVSFtp(ftpIp,ftpUsr, ftpPwd, "1");
			if(!wr.getResult()){
				throw new RuntimeException(wr.getTips("创建ftp用户时出错"));
			}
			
			/*
			String insertFtpelsql = "insert into org_ftp_rel(ftp_id,org_id,user,password,path,state,create_date) values "
					+ 		"(?,?,?,?,?,?,now())";
			DAO.update(insertFtpelsql, new String[]{ftpId,org_id,ftpUsr,ftpPwd,ftpPath,IKeyValues.STATE_00A});
			*/
			
		} catch (Exception e) {
			e.printStackTrace();
			
			//删除接口的数据,保持事物一致性
			handler.AddDelTenantGroup("del", group_code, group_name, group_desc);
			
			result.put("msg", "新增租户信息和团队信息出错");
			return result;
		}

		result.put("success", true);
		result.put("msg", "成功");
		return result;
	}
	
	public Map updateOrg(HashMap params) {
		Map result = new HashMap();
		result.put("success", false);
		
		String org_id = MapUtils.getString(params, "org_id");
		if (StringUtils.isEmpty(org_id)) {
			result.put("msg", "org_id不能为空！");
			return result;
		}
		String sql = "update dm_organization set org_name = ?, org_code = ?, app_token = ?, org_content = ?, lan_id = ? where org_id = ?";
		
		List sqlParams = new ArrayList();
		sqlParams.add(MapUtils.getString(params, "org_name"));
		sqlParams.add(MapUtils.getString(params, "org_code"));
		sqlParams.add(MapUtils.getString(params, "app_token"));
		sqlParams.add(MapUtils.getString(params, "org_content"));
		sqlParams.add(MapUtils.getString(params, "lan_id"));
		sqlParams.add(MapUtils.getString(params, "org_id"));
		
		DBUtil.getSimpleQuery().excuteUpdate(sql, sqlParams);
		
		//获取ftp当前阶段的信息
		List<Map> ftpMsgList = DAO.queryForMap("SELECT ofr.org_id,ofr. USER,ofr. PASSWORD,fs.ip AS ftp_ip,ofr.rel_id "
				+ "FROM org_ftp_rel ofr,ftp_server fs  WHERE fs.ftp_id = ofr.ftp_id AND org_id=? and ofr.state = ?  ", 
				new String[]{MapUtils.getString(params, "org_id"),IKeyValues.STATE_00A});
		
		String relId = "";
		Map oldFtpMsg = new HashMap();
		if(ftpMsgList!=null && ftpMsgList.size()!=0){
			oldFtpMsg = ftpMsgList.get(0);
			relId = Const.getStrValue(oldFtpMsg, "rel_id");
		}
		
		//顺道更新ftp信息
		String ftpId = Const.getStrValue(params, "ftp_def_id");
		String ftpUsr = Const.getStrValue(params, "ftp_def_usr");
		String ftpPwd = Const.getStrValue(params, "ftp_def_pwd");
		String ftpPath = Const.getStrValue(params, "ftp_def_dir");
		
		if(StringUtil.isNotEmpty(relId)){
			String updateFtpSql = "update org_ftp_rel set ftp_id=?,user=?,password=?,path=? where rel_id=? ";
			DAO.update(updateFtpSql, new String[]{ftpId,ftpUsr,ftpPwd,ftpPath,relId});
		}else{
			Map sendMap = new HashMap();
			sendMap.put("ftp_id", ftpId);
			sendMap.put("user", ftpUsr);
			sendMap.put("password", ftpPwd);
			sendMap.put("path", ftpPath);
			sendMap.put("org_id", org_id);
			SpringContextUtil.getApplicationContext().getBean(FtpService.class).saveOrgFtpRel(sendMap);
		}
		
		//此处真正写ftp信息
		//首先获取旧的IP
		String oldFtpIp = Const.getStrValue(oldFtpMsg, "ftp_ip");
		String newFtpIp = DAO.querySingleValue("select ip from ftp_server  where ftp_id=? ", new String[]{ftpId});
		//获取新旧的用户名
		String oldUsrName = Const.getStrValue(oldFtpMsg, "user");
		String newUsrName = ftpUsr;
		//获取旧的密码
		String oldPasswd = Const.getStrValue(oldFtpMsg, "password");
		String newPasswd = ftpPwd;
		
		BooleanWrap rs = new BooleanWrap(false);
		//如果ip和用户名都完全相同的话直接执行更新操作
		if(oldFtpIp!=null && oldFtpIp.equals(newFtpIp) && oldUsrName!=null && oldUsrName.equals(newUsrName)){
			rs = this.editVSFtp(newFtpIp,ftpUsr, ftpPwd, "2");
		}
		//如果ip或用户名发生变化，先删除旧的ftp目录，然后再执行增加操作
		else{
			//无论删除成功与否，都执行增加操作
			rs = this.editVSFtp(newFtpIp, newUsrName, newPasswd, "1");
			//新增成功后才去删除旧的
			if(rs.getResult()){
				BooleanWrap rs_tmp = this.editVSFtp(oldFtpIp,oldUsrName, oldPasswd, "0");
				if(!rs_tmp.getResult())
					log.error("删除"+oldFtpIp+"上的ftp用户出错："+rs_tmp.getTips());
			}
		}
		
		if(!rs.getResult()){
			//抛异常，回滚整个事务
			throw new RuntimeException(rs.getTips("修改FTP信息时出现异常"));
		}
		
		result.put("success", true);
		result.put("msg", "成功");
		return result;
	}
	
	/**
	 * 删除团队
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map delOrg(HashMap params) throws Exception {
		Map result = new HashMap();
		result.put("success", false);
		
		String org_id = MapUtils.getString(params, "org_id");
		if (StringUtils.isEmpty(org_id)) {
			return result;
		}
		
		//获取ftp当前阶段的信息
		List<Map> ftpMsgList = DAO.queryForMap("SELECT ofr.org_id,ofr. USER,ofr. PASSWORD,fs.ip AS ftp_ip,ofr.rel_id "
				+ "FROM org_ftp_rel ofr,ftp_server fs  WHERE fs.ftp_id = ofr.ftp_id AND org_id=? and ofr.state = ?  ", 
				new String[]{org_id,IKeyValues.STATE_00A});
		Map oldFtpMsg = new HashMap();
		if(ftpMsgList!=null && ftpMsgList.size()!=0){
			oldFtpMsg = ftpMsgList.get(0);
		}
		String oldFtpIp = Const.getStrValue(oldFtpMsg, "ftp_ip");
		//获取新旧的用户名
		String oldUsrName = Const.getStrValue(oldFtpMsg, "user");
		//获取旧的密码
		String oldPasswd = Const.getStrValue(oldFtpMsg, "password");
		
		ITenantHandler handler = getTenantHandler();;
		String group_code = handler.getTenantGroupByOrgId(org_id);//租户组编码
		
		if(StringUtil.isNotEmpty(group_code)){
			String action_type = "del";	//操作类型：add创建 del 删除
			
			Map ret = handler.AddDelTenantGroup(action_type, group_code, "", "");
			boolean success = (Boolean) ret.get("success");
			if(success){
				System.out.println("删除租户组成功");
				
				DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("DELETE_DM_ORGANIZATION_SQL"), new String[] { org_id });
				DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("DELETE_M_TEAM_MEMBER_SQL"), new String[] { org_id });
			}
			else {
				System.out.println(ret);
				result.put("msg", ret.get("retMsg"));
				return result;
			}
		}
		
		//删除成功返回前，把ftp用户也删掉：
		BooleanWrap rs_tmp = this.editVSFtp(oldFtpIp,oldUsrName, oldPasswd, "0");
		if(!rs_tmp.getResult())
			log.error("删除"+oldFtpIp+"上的ftp用户出错："+rs_tmp.getTips());
		
		result.put("success", true);
		result.put("msg", "成功");
		return result;
	}

	public int check(HashMap params) {
		String org_code = MapUtils.getString(params, "org_code");
		if (StringUtils.isNotEmpty(org_code)) {
			String count = DBUtil.getSimpleQuery().querySingleValue(
					"select count(*) from dm_organization o where o.org_code = ?", new String[] { org_code });
			if (!"0".equals(count)) {
				return -1;
			}
		}
		return 0;
	}
	
	public PageModel queryOrgMem(HashMap params) {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String org_id = MapUtils.getString(params, "org_id");
		String staff_name = StringUtils.trim(MapUtils.getString(params, "staff_name"));
		String staff_code = StringUtils.trim(MapUtils.getString(params, "staff_code"));
		if (StringUtils.isEmpty(org_id)) {
			return new PageModel();
		}
		
		List sqlParams = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select s.staff_id,");
		sql.append("       s.staff_name,s.staff_code,");
		sql.append("       vs.vr_staff_id,");
		sql.append("       o.org_id,");
		sql.append("       (select a.org_name from dm_organization a where a.org_id = s.org_id) org_name,");
		sql.append("       " + DBUtils.to_char("t.add_date", 2) + " add_date,");
		sql.append("       t.is_director");
		sql.append("  from dm_organization o, m_team_member t, vr_staff vs, dm_staff s");
		sql.append(" where o.org_id = t.org_id");
		sql.append("   and t.vr_staff_id = vs.vr_staff_id");
		sql.append("   and vs.staff_id = s.staff_id");
		
		if (StringUtils.isNotEmpty(org_id)) {
			sql.append(" and o.org_id = ? ");
			sqlParams.add(org_id);
		}
		if (StringUtils.isNotEmpty(staff_name)) {
			sql.append(" and s.staff_name like ? ");
			sqlParams.add("%" + staff_name + "%");
		}
		if(StringUtils.isNotBlank(staff_code)) {
			sql.append(" and s.staff_code = ? ");
			sqlParams.add(staff_code);
		}
		sql.append(" order by o.create_date desc");
		
		
		PageModel result = DBUtil.getSimpleQuery().queryForPageModel(sql.toString(), null, page_size, page_index, sqlParams.toArray(new String[]{}));
		return result;
	}
	
	public boolean addOrgMem(HashMap params) throws Exception {
		String org_id = MapUtils.getString(params, "org_id");
		List<Map> staffs = (List<Map>) MapUtils.getObject(params, "staffs");
		
		ITenantHandler handler = getTenantHandler();;
		String tenant_group_code = handler.getTenantGroupByOrgId(org_id);//租户组编码
		
		List sqlParams = new ArrayList();
		for (Map staff : staffs) {
			String staff_id = MapUtils.getString(staff, "staff_id");
			String staff_name = MapUtils.getString(staff, "staff_name");
			if (!checkMem(org_id, staff_id)) {
				continue;
			}
			String vr_staff_id = staffBO.addVRStaff(staff_id);
			
			String action_type = "add";	//操作类型：add创建 del 删除
			String tenant_code = "BDSP_" + staff_id	+ "_" + org_id;
			String tenant_name = staff_name;	//租户名称
			
			String password = "123456";
			
			List list = new ArrayList();
			list.add(org_id);
			list.add(vr_staff_id);
			list.add(KeyValues.IS_TEAM_DIRECTOR_F);
			list.add(staff_id);
			list.add(tenant_code);
			sqlParams.add(list.toArray(new String[] {}));
			
			Map ret = handler.AddDelTenant(action_type, tenant_code, password, tenant_name, tenant_group_code);
			boolean success = (Boolean) ret.get("success");
			if(success){
				System.out.println("新增租户成功");
				//租户的状态 00A 有效   00S 失效  00X 删除
				String sql = "insert into tenant_info(tenant_code,tenant_group_code,tenant_name,state,create_time,comments,password) "
						+ "values(?,?,?,'00A',now(),'接口自动生成',?)";
				DAO.update(sql, tenant_code, tenant_group_code, tenant_name, password);
				
				String insert_sql = "insert into vr_staff_tenant(tenant_code,vr_staff_id,create_date) values(?, ?, now())";
				DAO.update(insert_sql, tenant_code, vr_staff_id);
			}
			else {
				System.out.println(ret);
				return false;
			}
		}
		if (sqlParams.size() > 0) {
			String sql = "insert into m_team_member (org_id, vr_staff_id, is_director, add_date, staff_id, tenant_code) values (?, ?, ?, "
					+ DBUtils.to_date(DAOUtils.getFormatedDate()) + ", ?, ?)";
			DBUtil.getSimpleQuery().batchUpdate(sql, sqlParams);
			
			// 增加团队成员调用BDP接口，待实现
		}
		return true;
	}
	
	public boolean delOrgMem(HashMap params) throws Exception {
		String org_id = MapUtils.getString(params, "org_id");
		String staff_id = MapUtils.getString(params, "staff_id");
		String staff_name = MapUtils.getString(params, "staff_name");
		String vr_staff_id = MapUtils.getString(params, "vr_staff_id");
		if (StringUtils.isEmpty(vr_staff_id) || StringUtils.isEmpty(vr_staff_id)) {
			return false;
		}
		
		ITenantHandler handler = getTenantHandler();;
		String tenant_group_code = handler.getTenantGroupByOrgId(org_id);//租户组编码
		
		String action_type = "del";	//操作类型：add创建 del 删除
		String tenant_code = handler.getTenantCode(staff_id, org_id);
		String tenant_name = staff_name;	//租户名称
		
		String password = "";
		
		Map ret = handler.AddDelTenant(action_type, tenant_code, password, tenant_name, tenant_group_code);
		boolean success = (Boolean) ret.get("success");
		if(success){
			System.out.println("删除租户成功");
			
			String sql = "delete from vr_staff_tenant where tenant_code = ? and vr_staff_id = ?";
			DAO.update(sql, tenant_code, vr_staff_id);
			
			String del_tenant_sql = "delete from tenant_info where tenant_code = ? ";
			DAO.update(del_tenant_sql, tenant_code);
			
			DBUtil.getSimpleQuery().excuteUpdate("delete from m_team_member where org_id = ? and vr_staff_id = ?",
					new String[] { org_id, vr_staff_id });
		}
		else {
			System.out.println(ret);
			return false;
		}
		
		return true;
	}
	
	/**
	 * 设置团队负责人
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int setDirector(HashMap params) throws Exception {
		String org_id = MapUtils.getString(params, "org_id");
		String staff_id = MapUtils.getString(params, "staff_id");
		String staff_name = MapUtils.getString(params, "staff_name");
		String vr_staff_id = MapUtils.getString(params, "vr_staff_id");
		
		// 判断是否已存在团队负责人，存在的话不能再设置
		if (checkDirectorExist(org_id)) {
			return -1;
		}
		
		/*ITenantHandler handler = getTenantHandler();;
		for(int i=0; i<5; i++){
			String role_code = vr_staff_id + "_r" + i;
			String role_name = staff_name + "_r" + i;	//角色名称
			
			// 调用BDP接口创建团队负责人信息
			String action_type = "add";	//操作类型：add创建 del 删除
			String role_type = KeyValues.BDP_DEF_ROLE_TYPE;
			Map ret = handler.AddDelTenantRole(action_type, role_code, role_name, role_type);
			boolean success = (Boolean) ret.get("success");
			if(success){
				String type = "grant";
				String tenant_code = handler.getTenantCode(staff_id, org_id);
				//建立关系
				handler.tenantRoleRel(type, tenant_code, role_code, role_type);
				
				String role_insert_sql = "insert into bd_role(role_code, role_name, create_date, state_date, state) "
						+ "values (?, ?, now(), now(), '00A')";
				
				String staff_role_insert_sql = "insert into vr_staff_role(vr_staff_id, role_code, create_date) values (?, ?, now())";
				
				DAO.update(role_insert_sql, role_code, role_name);
				
				DAO.update(staff_role_insert_sql, vr_staff_id, role_code);
			}
			else {
				System.out.println(ret);
				return -1;
			}
		}*/
		
		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("UPDATE_TEAM_DIRECTOR_SQL"),
				new String[] { KeyValues.IS_TEAM_DIRECTOR_T, org_id, vr_staff_id });
		
		return 0;
	}
	
	public boolean relieveDirector(HashMap params) {
		String org_id = MapUtils.getString(params, "org_id");
		String staff_id = MapUtils.getString(params, "staff_id");
		
		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("UPDATE_TEAM_DIRECTOR_SQL"),
				new String[] { KeyValues.IS_TEAM_DIRECTOR_F, org_id, staff_id });
		return true;
	}
	
	public boolean checkDirectorExist(String org_id) {
		String count = DBUtil.getSimpleQuery().querySingleValue(Sql.SYS_SQLS.get("SELECT_TEAM_DIRECTOR_COUNT_SQL"),
				new String[] { org_id, KeyValues.IS_TEAM_DIRECTOR_T });
		if (!"0".equals(count)) {
			return true;
		}
		return false;
	}

	public boolean checkMem(String org_id, String staff_id) {
		String count = DBUtil.getSimpleQuery().querySingleValue(
				"select count(*) from m_team_member a, vr_staff b where a.vr_staff_id = b.vr_staff_id and a.org_id = ? and a.vr_staff_id = ?",
				new String[] { org_id, staff_id });
		if (!"0".equals(count)) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param usrName   用户名
	 * @param password  密码
	 * @param actionType  0删除  1增加   2修改或增加【用户名存在时修改，不存在时添加】
	 * @return
	 */
	private BooleanWrap editVSFtp(String ftpIp,String usrName,String password,String actionType){
				
		//进入此方法前上锁：
		String shDir = DAO.querySingleValue("select param_val from dc_system_param where param_code=? for update", 
				new String[]{"ADD_VSFTP_SH_PATH"});
		
		String shFileName = DcSystemParamUtil.getSysParamByCache("ADD_VSFTP_SH_NAME");
		
		if(password.length()==128)
			password = RSAUtil.decrypt(password);
		String ftpHost = ftpIp;
		
		//根据IP获取用户名，密码
		List<Map> rsList = DAO.queryForMap("select * from ftp_server  where ip=? and state=? and ftp_type=?",
				new String[]{ftpIp,"00A","SSH"});
		if(rsList==null || rsList.size()==0)
			return new BooleanWrap(false,"在ftp_server表上没找到ssh到"+ftpIp+"的信息");
		
		Map ftpMsg = rsList.get(0);
		
		String ftpMainUsr = Const.getStrValue(ftpMsg, "user");
		String ftpMaiPwd = Const.getStrValue(ftpMsg, "password");
		String baseDir = Const.getStrValue(ftpMsg,"path");
		String port = Const.getStrValue(ftpMsg, "port");
		if(ftpMaiPwd.length()==128) ftpMaiPwd = RSAUtil.decrypt(ftpMaiPwd);
		if(shDir.charAt(shDir.length()-1)!='/') shDir+="/";
		
		//真正执行命令前，判断目标主机是否有此sh脚本
		ShellResult isExistsSH = SSHUtil.exeCmd(
				new String[]{"ls "+shDir+" | grep '^"+shFileName+"$'"}, new String[]{ ftpHost, ftpMainUsr, ftpMaiPwd,port});
		if(StringUtil.isEmpty(isExistsSH.getOutMessage().trim())){
			IFtpUtil ftpUtil = FtpUtil.getFtpUtil(ftpIp, Integer.parseInt(port), ftpMainUsr,
					ftpMaiPwd);
			ftpUtil.createPath(shDir);
			InputStream shFileIS = OrgMgrBO.class.getResourceAsStream("/com/ztesoft/dubbo/mp/sys/sh/addvsftp.sh");
			ftpUtil.uploadFile(shFileIS, shDir+shFileName);
		}
		
		
		String command = "sh "+shDir+shFileName+" " + usrName + " "+password+" "+ baseDir +"  "+actionType;
		System.out.println(command);
		ShellResult result = SSHUtil.exeCmd(new String[]{command}, new String[]{ ftpHost, ftpMainUsr, ftpMaiPwd,port});
		String responseMsg = result.getOutMessage();
		if(responseMsg==null) responseMsg="";
		String[] responseLine = responseMsg.split("\n");
		String retCode = "";
		String retMes = "";
		for(String line : responseLine ){
			if(line.indexOf("retCode:")==0){
				retCode = line.substring("retCode:".length());
			}else if(line.indexOf("retMes:")==0){
				retMes = line.substring("retMes:".length());
			}
		}
		if("0".equals(retCode)){
			return new BooleanWrap(true,retMes);
		}else{
			if(StringUtil.isEmpty(retMes) && !result.isSucess()) retMes=result.getErrMessage();
			else if(StringUtil.isEmpty(retMes)) retMes = result.getOutMessage();
			return new BooleanWrap(false, retMes);
		}		
	}
	
	public static void main(String[] args) {
		
		InputStream is = OrgMgrBO.class.getResourceAsStream("/com/ztesoft/dubbo/mp/sys/sh/addvsftp.sh");
		System.out.println(is);
		if(is!=null) return;
		
		OrgMgrBO bo = new OrgMgrBO();
		System.out.println(bo.editVSFtp("","lzj", "123", "2"));
	}
	
}
