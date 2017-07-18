package com.ztesoft.dubbo.inf;

import java.util.Map;

import com.ztesoft.sql.Sql;

import appfrm.resource.dao.impl.DAO;

/**
 * 租户相关接口
 * @author lwt
 *
 */
@SuppressWarnings("rawtypes")
public interface ITenantHandler {

	/**
	 * 创建租户组服务
	 * @param params
	 * @return
	 */
	public Map AddDelTenantGroup(String action_type, String group_code, String group_name, String group_desc) throws Exception;
	
	/**
	 * 创建租户服务
	 * @param params
	 * @return
	 */
	public Map AddDelTenant(String action_type, String tenant_code, String password, String name, String tenant_group_code) throws Exception;
	
	/**
	 * 创建租户的角色
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public Map AddDelTenantRole(String action_type, String role_code, String role_name, String role_type) throws Exception;
	
	/**
	 * 租户和租户角色绑定解除服务
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public Map tenantRoleRel(String type, String tenant_code, String role_code, String role_type) throws Exception;
	
	/**
	 * 租户角色授权服务
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public Map tenantRolePrivilegeRel(String type, String role_code, String object_type, String object_ids, String role_type) throws Exception;
	
	/**
	 * 查询队列列表
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public Map getQueues(String owner) throws Exception;
	
	/**
	 * 创建hive库
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public Map createHiveSchema(String owner, String schema_code, String schema_desc, 
			String schema_name, String storage_quota) throws Exception;
	
	/**
	 * 根据团队/组织id获取租户组编码
	 * @param org_id
	 * @return
	 */
	public String getTenantGroupByOrgId(String org_id);
	
	/**
	 * 根据团队/组织id获取租户编码
	 * @param org_id
	 * @return
	 */
	public String getTenantCode(String staff_id, String org_id);

}
