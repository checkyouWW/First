package com.ztesoft.dubbo.inf.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.google.gson.reflect.TypeToken;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.DcSystemParamUtil;
import com.ztesoft.common.util.HttpClientUtil;
import com.ztesoft.common.util.SessionHelper;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.dubbo.inf.ITenantHandler;
import com.ztesoft.sql.Sql;

import appfrm.app.util.ListUtil;
import appfrm.resource.dao.impl.DAO;

/**
 * 租户相关接口
 * @author lwt
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TenantHandler implements ITenantHandler {

	private int socket_timeout = 2 * 60 * 1000;//秒
	private int connect_timeout = 2 * 60 * 1000;//秒
	private String bdp_tenant_servlet = "";//http://10.45.47.63:8280/BDP/TenantServlet
	private String bdp_staff_code = "";
	private String bdp_staff_passwd = "";
	//公共租户/共享租户
	private String bdp_share_tenant = "";
	private String bdp_def_protocol = "";
	private boolean tenant_inf_switch = true; //接口开关
	List<Header> headers = new ArrayList<Header>();
	
	public TenantHandler(){
		bdp_tenant_servlet = DcSystemParamUtil.getSysParamByCache("BDP_TENANT_SERVLET");
		bdp_staff_code = DcSystemParamUtil.getSysParamByCache("BDP_STAFF_CODE");
		bdp_staff_passwd = DcSystemParamUtil.getSysParamByCache("BDP_STAFF_PASSWD");
		//公共租户/共享租户
		bdp_share_tenant = DcSystemParamUtil.getSysParamByCache("BDP_SHARE_TENANT");
		bdp_def_protocol = DcSystemParamUtil.getSysParamByCache("BDP_QUEUE_OWNER");
		
		String bdp_socket_timeout = DcSystemParamUtil.getSysParamByCache("BDP_SOCKET_TIMEOUT");
		String bdp_connect_timeout = DcSystemParamUtil.getSysParamByCache("BDP_CONNECT_TIMEOUT");
		if(StringUtil.isNum(bdp_socket_timeout)){
			socket_timeout = Integer.parseInt(bdp_socket_timeout) * 1000;
		}
		if(StringUtil.isNum(bdp_connect_timeout)){
			connect_timeout = Integer.parseInt(bdp_connect_timeout) * 1000;
		}
		
		tenant_inf_switch = "close".equalsIgnoreCase(DcSystemParamUtil.getSysParamByCache("TENANT_INF_SWITCH")) ? false : true;
		
		//BDP管理工号(需要创建租户组和租户等的权限)
		headers = genBdpStaffHeadersFromSession();
	}
	
	public TenantHandler(String bdp_staff_code, String bdp_staff_passwd, String bdp_tenant_code){
		this();
		//BDP管理工号(需要创建租户组和租户等的权限)
		Header staff_code = new BasicHeader("staff_code", bdp_staff_code);
		Header staff_passwd = new BasicHeader("staff_passwd", bdp_staff_passwd);
		Header tenant_code = new BasicHeader("tenant_code", bdp_tenant_code);
		headers.clear();
		headers.add(staff_code);
		headers.add(staff_passwd);
		headers.add(tenant_code);
	}
	
	public List<Header> genBdpStaffHeaders() {
		List<Header> headers = new ArrayList<Header>();
		//BDP管理工号(需要创建租户组和租户等的权限)
		if(StringUtil.isEmpty(bdp_staff_code) || StringUtil.isEmpty(bdp_staff_passwd)){
			throw new RuntimeException("请配置bdp管理工号");
		}
		Header staff_code = new BasicHeader("staff_code", bdp_staff_code);
		Header staff_passwd = new BasicHeader("staff_passwd", bdp_staff_passwd);
		Header tenant_code = new BasicHeader("tenant_code", bdp_share_tenant);
		headers.add(staff_code);
		headers.add(staff_passwd);
		headers.add(tenant_code);
		this.headers = headers;
		return headers;
	}
	
	public List<Header> genBdpStaffHeadersFromSession() {
		List<Header> headers = new ArrayList<Header>();
		
		String _bdp_staff_code = SessionHelper.getStaffCode();
		String _bdp_staff_passwd = SessionHelper.getPasswordRsa();
		String _bdp_tenant_code = SessionHelper.getTenantCode();
		
		if(StringUtil.isNotEmpty(_bdp_staff_code)){
			Header staff_code = new BasicHeader("staff_code", _bdp_staff_code);
			headers.add(staff_code);
		}
		
		if(StringUtil.isNotEmpty(_bdp_staff_passwd)){
			Header staff_passwd = new BasicHeader("staff_passwd", _bdp_staff_passwd);
			headers.add(staff_passwd);
		}
		
		if(StringUtil.isNotEmpty(_bdp_tenant_code)){
			Header tenant_code = new BasicHeader("tenant_code", _bdp_tenant_code);
			headers.add(tenant_code);
		}
		
		return headers;
	}
	
	private <T> T post(Map<String, Object> param, TypeToken<T> token) throws Exception {
		//如果接口没启动,则返回
		if(!tenant_inf_switch){
			Map r = new HashMap();
			r.put("success", true);
			r.put("retMsg", "成功");
			return (T) r;
		}
		String method = StringUtil.getStrValue(param, "method");
		String url = bdp_tenant_servlet + "/" + method;//url格式
		HttpClientUtil httpUtil = new HttpClientUtil(socket_timeout, connect_timeout, headers);
		T result = httpUtil.postJson(param, token, url);
		return result;
	}
	
	/**
	 * 创建租户组服务
	 * @param action_type	操作类型：add创建 del 删除
	 * @param group_code	租户组编码
	 * @param group_name	租户组名称
	 * @param group_desc	租户组描述
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map AddDelTenantGroup(String action_type, String group_code, String group_name, String group_desc) throws Exception {
		Map urlParams = new HashMap();
		TypeToken<Map> token = new TypeToken<Map>(){};
		
		String method = "createtenantgroup";
			
		urlParams.put("method", method);
		urlParams.put("actiontype", action_type);
		urlParams.put("groupcode", group_code);
		urlParams.put("groupname", new String(group_name.getBytes("UTF-8"), "ISO8859-1"));
		urlParams.put("groupdesc", new String(group_desc.getBytes("UTF-8"), "ISO8859-1"));
		Map result = this.post(urlParams, token);
		
		return result;
	}
	
	/**
	 * 创建租户服务
	 * @param action_type		操作类型：add创建 del 删除
	 * @param tenant_code		租户编码
	 * @param password			租户密码
	 * @param name				租户名称
	 * @param tenant_group_code	租户组编码
	 */
	@Override
	public Map AddDelTenant(String action_type, String tenant_code, String password, String name, String tenant_group_code) throws Exception{
		Map urlParams = new HashMap();
		TypeToken<Map> token = new TypeToken<Map>(){};
		
		String method = "createtenant";
		urlParams.put("method", method);
		urlParams.put("actiontype", action_type);
		urlParams.put("tenantcode", tenant_code);
		urlParams.put("password", password);
		urlParams.put("name", new String(name.getBytes("UTF-8"), "ISO8859-1"));
		urlParams.put("tenantgroup", tenant_group_code);
		
		Map result = this.post(urlParams, token);
		
		return result;
	}
	
	/**
	 * 创建租户的角色
	 * @param action_type		操作类型：add创建 del 删除
	 * @param role_code			角色编码
	 * @param role_name			角色名称
	 * @throws Exception 
	 */
	@Override
	public Map AddDelTenantRole(String action_type, String role_code, String role_name,String role_type) throws Exception {
		Map urlParams = new HashMap();
		TypeToken<Map> token = new TypeToken<Map>(){};
		
		String method = "tenantrolemgr";
		
		String status_cd = "";
		if("add".equals(action_type)){
			//00A有效 00X无效
			status_cd = "00A";
		}
		else if("del".equals(action_type)){
			//00A有效 00X无效
			status_cd = "00X";
		}
		String status_date = DateUtil.getFormatedDateTime();
		
		urlParams.put("method", method);
		urlParams.put("actiontype", action_type);
		urlParams.put("rolecode", role_code);
		urlParams.put("rolename", new String(role_name.getBytes("UTF-8"), "ISO8859-1"));
		urlParams.put("statuscd", status_cd);
		urlParams.put("statusdate", status_date);
		urlParams.put("roletype", role_type);
		Map result = this.post(urlParams, token);
		return result;
	}

	/**
	 * 租户和租户角色绑定解除服务
	 * @param type				类型 grant:授予  revoke:解除
	 * @param tenant_code		租户编码
	 * @param role_code			角色编码
	 * @return
	 * @throws Exception 
	 */
	@Override
	public Map tenantRoleRel(String type, String tenant_code, String role_code, String role_type) throws Exception {
		Map urlParams = new HashMap();
		TypeToken<Map> token = new TypeToken<Map>(){};
		
		String method = "tenentroleref";
			
		urlParams.put("method", method);
		urlParams.put("type", type);
		urlParams.put("tenantcode", tenant_code);
		urlParams.put("rolecode", role_code);
		urlParams.put("roletype", role_type);
		
		Map result = this.post(urlParams, token);
		
		return result;
	}
	
	/**
	 * 租户角色授权服务
	 * @param type				类型 grant:授予  revoke:解除
	 * @param role_code			角色编码
	 * @param object_type		HDFS目录：24   HDFS 文件：25  HIVE表：23  HBASE表：12 队列：21
	 * @param object_ids		对象ID，可以填写多个，逗号隔开
	 * @return
	 * @throws Exception 
	 */
	@Override
	public Map tenantRolePrivilegeRel(String type, String role_code, String object_type, String object_ids, String role_type) throws Exception {
		Map urlParams = new HashMap();
		TypeToken<Map> token = new TypeToken<Map>(){};
		
		String method = "mtroleauth";
		
		urlParams.put("method", method);
		urlParams.put("type", type);
		urlParams.put("objecttype", object_type);
		urlParams.put("rolecode", role_code);
		urlParams.put("objectids", object_ids);
		urlParams.put("roletype", role_type);
		
		Map result = this.post(urlParams, token);
		System.out.println(result);
		return result;
	}

	/**
	 * 查询队列列表
	 * @param owner	所属协议
	 * @return
	 * @throws Exception 
	 */
	@Override
	public Map getQueues(String owner) throws Exception {
		Map urlParams = new HashMap();
		TypeToken<Map> token = new TypeToken<Map>(){};
		
		String method = "qyrqueue";
		
		urlParams.put("method", method);
		if(StringUtil.isEmpty(owner)){
			owner = bdp_def_protocol;
		}
		urlParams.put("owner", owner);
		
		Map result = this.post(urlParams, token);
		
		return result;
	}

	/**
	 * 创建hive库
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	@Override
	public Map createHiveSchema(String owner, String schema_code, String schema_desc, 
			String schema_name, String storage_quota) throws Exception {
		Map urlParams = new HashMap();
		TypeToken<Map> token = new TypeToken<Map>(){};
		
		String method = "createschema";
		
		urlParams.put("method", method);
		if(StringUtil.isEmpty(owner)){
			owner = bdp_def_protocol;
		}
		urlParams.put("owner", owner); //所属协议
		urlParams.put("schemacode", schema_code); //库编码
		urlParams.put("schemadesc", new String(schema_desc.getBytes("UTF-8"), "ISO8859-1")); //库描述
		urlParams.put("schemaname", new String(schema_name.getBytes("UTF-8"), "ISO8859-1")); //库名称
		urlParams.put("storagequota", storage_quota); //存储配额 
		
		Map result = this.post(urlParams, token);
		
		return result;
	}
	
	/**
	 * 根据团队/组织id获取租户组编码
	 * @param org_id
	 * @return
	 */
	public String getTenantGroupByOrgId(String org_id){
		String sql = Sql.SYS_SQLS.get("GET_TENANT_GROUP_BY_ORG_ID");
		String group_code = DAO.querySingleValue(sql, new String[]{org_id});//租户组编码
		return group_code;
	}
	
	/**
	 * 根据团队/组织id获取租户编码
	 * @param org_id
	 * @return
	 */
	public String getTenantCode(String staff_id, String org_id){
		String sql = Sql.SYS_SQLS.get("GET_TENANT_CODE");
		List list = DAO.queryForMap(sql, staff_id, org_id);
		String tenant_code = "";
		if(!ListUtil.isEmpty(list)){
			Map map = (Map) list.get(0);
			tenant_code = StringUtil.getStrValue(map, "tenant_code");
		}
		return tenant_code;
	}
}
