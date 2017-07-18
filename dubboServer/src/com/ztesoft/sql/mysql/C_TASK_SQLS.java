package com.ztesoft.sql.mysql;

import org.springframework.stereotype.Service;

import com.ztesoft.sql.Sql;

/**
 * 任务配置模块sql
 */
@Service(value = "MYSQL_C_TASK_SQLS")
public class C_TASK_SQLS extends Sql {

    public String SEARCH_TASK_LIST = "SELECT " +
            "   cts.service_id, " +
            "   cts.task_name, " +
            "   cts.task_code, " +
            "   cts.engine_type, " +
            "   cts.comments, " +
            "   cts.instructions, " +
            "   cts.state, " +
            "   cts.apply_count, " +
            "   cts.schedule_count " +
            "FROM " +
            "   c_task_service cts " +
            "WHERE " +
            "   1 = 1";

    public String update_task_state = "update c_task_service set state = ?,state_time=now() where service_id = ? ";

    public String get_task_list = "select * from c_task_service where state != ? order by service_id desc ";

	public static String get_tenant_list = "select cti.*,ctg.tenant_group_name,dmo.ORG_NAME "
				+ 	"from tenant_info cti,tenant_group ctg,DM_ORGANIZATION dmo where "
				+	"cti.tenant_group_code = ctg.tenant_group_code  and dmo.ORG_ID = cti.team_id and "
				+	"cti.state!='00X' order by cti.tenant_group_code desc ";
	
	public static String update_renant_state = "update tenant_info set state=?,state_time=now() where tenant_code = ? ";
	
	public static String get_tenantgroup_list = "select * from tenant_group where state = '00A' ";
	
	public static String get_team_list = "select * from DM_ORGANIZATION order by org_id desc ";
	
}