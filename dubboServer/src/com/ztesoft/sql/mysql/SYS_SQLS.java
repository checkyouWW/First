package com.ztesoft.sql.mysql;

import org.springframework.stereotype.Service;

import com.ztesoft.common.util.DBUtils;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.sql.Sql;

import appfrm.app.annotaion.DBField;

@Service(value = "MYSQL_SYS_SQLS")
public class SYS_SQLS extends Sql {
	
	public String SELECT_DM_ORGANIZATION_SQL = "select o.org_id, o.parent_org_id, o.org_code, o.org_name, o.org_content, o.app_token, o.lan_id, " + DBUtils.to_char("o.create_date", 2) + " create_date,"
			                                 + "       case"
			                                 + "         when exists (select 1"
			                                 + "                 from m_team_member t"
			                                 + "                where t.org_id = o.org_id"
			                                 + "                  and t.is_director = '" + KeyValues.IS_TEAM_DIRECTOR_T + "') then 1"
			                                 + "         else 0"
			                                 + "       end as exist_director"
			                                 + "  from dm_organization o"
			                                 + " where 1 = 1";
	
	public String DELETE_DM_ORGANIZATION_SQL = "delete from dm_organization where org_id = ?";
	public String DELETE_M_TEAM_MEMBER_SQL = "delete from m_team_member where org_id = ?";
	
	public String SELECT_TEAM_DIRECTOR_COUNT_SQL = "select count(*) from m_team_member where org_id = ? and is_director = ?";
	public String SELECT_TEAM_DIRECTOR_SQL = "select * from m_team_member where org_id = ? and is_director = ?";
	public String UPDATE_TEAM_DIRECTOR_SQL = "update m_team_member set is_director = ? where org_id = ? and vr_staff_id = ?";
	
	public String SELECT_M_NOTICE_SQL = "select a.notice_id,"
			                          + "       a.notice_title,"
			                          + "       a.notice_content,"
			                          + "       a.notice_range,"
			                          + "       b.notice_obj_id,"
			                          + "       case"
			                          + "         when a.notice_range = '" + KeyValues.NOTICE_RANGE_2 + "' then"
			                          + "          (select c.org_name from dm_organization c where c.org_id = b.notice_obj_id)"
			                          + "         else"
			                          + "          ''"
			                          + "       end as notice_obj_name,"
			                          + "       " + DBUtils.to_char("a.create_date", 2) + " create_date,"
			                          + "       " + DBUtils.to_char("a.state_date", 2) + " state_date,"
			                          + "       d.staff_name,"
			                          + "     a.state"
			                          + "  from m_notice a"
			                          + "  left join m_notice_obj b on a.notice_id = b.notice_id"
			                          + "  left join dm_staff d on a.staff_id = d.staff_id"
			                          + " where 1 = 1";
	
	public String INSERT_M_NOTICE_SQL = "insert into m_notice (notice_id, notice_title, notice_content, notice_range, create_date, staff_id, state_date, state) values (?, ?, ?, ?, " + DBUtils.to_date(2) + ", ?, " + DBUtils.to_date(2) + ", ?)";
	public String INSERT_M_NOTICE_OBJ_SQL = "insert into m_notice_obj (notice_id, notice_obj_id) values (?, ?)";
	public String UPDATE_M_NOTICE_SQL = "update m_notice set notice_title = ?, notice_content = ?, notice_range = ?, state_date = " + DBUtils.to_date(2) + " where notice_id = ?";
	public String UPDATE_M_NOTICE_STATE_SQL = "update m_notice set state = ?, state_date = " + DBUtils.to_date(2) + " where notice_id = ?";
	public String DELETE_M_NOTICE_SQL = "delete from m_notice where notice_id = ?";
	public String DELETE_M_NOTICE_OBJ_SQL = "delete from m_notice_obj where notice_id = ?";
	
	public String QUERY_STAFF_PRIVILEGE_SQL = "select a.privilege_id,"
			                                + "       a.privilege_name,"
			                                + "       a.type,"
			                                + "       b.staff_id,"
			                                + "       case"
			                                + "         when exists (select 1"
			                                + "                 from dm_role_privilege c, dm_staff_role d, dm_role e"
			                                + "                where d.role_id = e.role_id"
			                                + "                  and e.role_id = c.role_id"
			                                + "                  and c.privilege_id = a.privilege_id"
			                                + "                  and d.staff_id = ?) then"
			                                + "          '1'"
			                                + "         else"
			                                + "          '0'"
			                                + "       end as role_rel,"
			                                + "       case"
			                                + "         when exists (select 1"
			                                + "                 from dm_privilege f"
			                                + "                where f.parent_privilege_id = a.privilege_id) then"
			                                + "          'closed'"
			                                + "         else"
			                                + "          'open'"
			                                + "       end as state"
			                                + "  from dm_privilege a"
			                                + "  left join dm_staff_privilege b on a.privilege_id = b.privilege_id and b.staff_id = ?"
			                                + " where 1 = 1 ";
	
	//插入服务申请审批日志
	public String INSERT_M_APPLY_AUDIT_LOG = "insert into m_apply_audit_log(apply_id,apply_type,audit_staff_id,audit_content,audit_result,audit_date) values(?,?,?,?,?,?)";
	
	public String INSERT_VR_STAFF_SQL = "insert into vr_staff (vr_staff_id, vr_staff_code, staff_id, password) values (?, ?, ?, ?)";
	
	public String SELECT_BD_ROLE_SQL = "select a.role_code,"
						             + "       a.role_name,"
						             + "       a.role_type,"
						             + "       " + DBUtils.to_char("a.create_date", 2) + " create_date,"
						             + "       " + DBUtils.to_char("a.state_date", 2) + " state_date,"
						             + "       a.state"
						             + "  from bd_role a"
						             + " where 1 = 1";
	public String INSERT_BD_ROLE_SQL = "insert into bd_role (role_code, role_name, create_date, state_date, state, role_type) values (?, ?, " + DBUtils.to_date(2) + ", " + DBUtils.to_date(2) + ", ?, ?)";
	public String UPDATE_BD_ROLE_SQL = "update bd_role set role_name = ?, state_date = " + DBUtils.to_date(2) + " where role_code = ?";
	public String DELETE_BD_ROLE_SQL = "delete from bd_role where role_code = ?";
	
	public String QUERY_BD_ROLE_REL_STAFF_SQL = "select d.staff_name,"
						                      + "       (select org_name from bdp_dm_organization o where o.org_id = d.org_id) org_name,"
						                      + "       (select org_name from dm_organization oo where oo.org_id = a.org_id) team_name,"
						                      + "       b.vr_staff_id,"
						                      + "       b.staff_id,"
						                      + "       a.org_id,"
						                      + "       a.is_director,"
						                      + "       c.role_code "
						                      + "  from m_team_member a, vr_staff b, vr_staff_role c, dm_staff d"
						                      + " where a.vr_staff_id = b.vr_staff_id"
						                      + "   and b.vr_staff_id = c.vr_staff_id"
						                      + "   and b.staff_id = d.staff_id";
	
	public String INSERT_VR_STAFF_ROLE_SQL = "insert into vr_staff_role (vr_staff_id, role_code, create_date) values (?, ?, " + DBUtils.to_date(2) + ")";
	public String DELETE_BD_ROLE_REL_STAFF_SQL = "delete from vr_staff_role where role_code = ? and vr_staff_id = ?";
	
	public String QUERY_TEAM_STAFF_SQL = "select a.vr_staff_id, c.staff_name, b.org_id team_id, a.staff_id, o.org_name team_name"
							           + "  from vr_staff a, m_team_member b, dm_staff c, dm_organization o"
							           + " where a.vr_staff_id = b.vr_staff_id"
							           + "   and a.staff_id = c.staff_id"
							           + "   and b.org_id = o.org_id";
	
	public String SELECT_BD_ROLE_PRIV_SQL = "select role_code, priv_code, priv_type, " + DBUtils.to_char("create_date", 2) + " create_date from bd_role_priv where 1 = 1 ";
	public String INSERT_BD_ROLE_PRIV_SQL = "insert into bd_role_priv (role_code, priv_code, priv_type, create_date) values (?, ?, ?, " + DBUtils.to_date(2) + ")";
	public String DELETE_BD_ROLE_PRIV_SQL = "delete from bd_role_priv where role_code = ? and priv_type = ? and priv_code = ?";
	
	public String QUERY_BD_PRIV_HIVE = "select distinct a.org_id team_id,"
			                         + "                o.org_name team_name,"
			                         + "                '" + KeyValues.BD_PRIV_29 + "' priv_type,"
			                         + "                b.view_name priv_code,"
			                         + "                s.schema_code"
			                         + "  from s_service_apply a, s_data_inst b, dm_organization o"
			                         + " left join org_schema s on o.org_id = s.org_id"
			                         + " where a.apply_id = b.apply_id"
			                         + "   and a.org_id = o.org_id"
			                         + "   and b.view_name is not null";
	
	public String GET_TENANT_GROUP_BY_ORG_ID = "select tenant_group_code from tenant_group where org_id = ?";
	
	public String GET_TENANT_CODE = "select b.tenant_code,b.password from m_team_member a,tenant_info b "
			+ "where a.tenant_code = b.tenant_code and b.state = '00A' "
			+ "and a.staff_id = ? and a.org_id = ?";
	
	public String GET_ORG_SCHEMA = "select schema_code from org_schema where org_id = ?";
	
	public String SELECT_TABLE_MAPPING_SQL = "select mapping_id, std_table_code, src_sys_code, schema_code, src_table_code from table_mapping where 1 = 1 ";
	public String INSERT_TABLE_MAPPING_SQL = "insert into table_mapping (mapping_id, std_table_code, src_sys_code, schema_code, src_table_code) values (?, ?, ?, ?, ?)";
	public String UPDATE_TABLE_MAPPING_SQL = "update table_mapping set std_table_code = ?, src_sys_code = ?, schema_code = ?, src_table_code = ? where mapping_id = ?";
	public String DELETE_TABLE_MAPPING_SQL = "delete from table_mapping where mapping_id = ?";
	
	public String SELECT_DATA_SYN_ORDER_SQL = "select order_id, sys_code, schema_code, table_code, acct_time, " + DBUtils.to_char("create_date", 2) + " create_date, " + DBUtils.to_char("exec_date", 2) + " exec_date, " + DBUtils.to_char("finish_date", 2) + " finish_date, state from data_syn_order where 1 = 1 ";
	public String INSERT_DATA_SYN_ORDER_SQL = "insert into data_syn_order (order_id, sys_code, schema_code, table_code, acct_time, create_date, exec_date, finish_date, state, owner,service_id,src_sys_code,src_owner,src_schema_code,src_table_code) "
			+ "values (?, ?, ?, ?, ?, " + DBUtils.to_date(2) + ", " + DBUtils.to_date(2) + ", " + DBUtils.to_date(2) + ", ?,?,?,?,?,?,?)";
	
	public String SELECT_DATA_SYN_ORDER_LOG_SQL = "select log_id, order_item_id, log_desc, " + DBUtils.to_char("create_date", 2) + " create_date, " + DBUtils.to_char("exec_date", 2) + " exec_date, state from data_syn_order_item_log where 1 = 1 ";
	
}