package com.ztesoft.sql.mysql;

import org.springframework.stereotype.Service;

import com.ztesoft.common.util.DBUtils;
import com.ztesoft.sql.Sql;

/**
 * 任务服务模块sql
 *
 */
@Service(value = "MYSQL_S_TASK_SQLS")
public class S_TASK_SQLS extends Sql {
	
	String test = "test";
	
	public String INSERT_ATTACH_FILE_SQL = "insert into attach_file (file_id, file_name, file_location_type, file_location, table_name, table_pk_name, table_pk_value, create_date, status) values (?, ?, ?, ?, ?, ?, ?, " + DBUtils.to_date(2) + ", ?)";
	public String UPDATE_ATTACH_FILE_SQL = "update attach_file set table_pk_value = ? where file_id = ?";
	public String DELETE_ATTACH_FILE_SQL = "delete from attach_file where file_id = ?";
	public String SELECT_ATTACH_FILE_SQL = "select file_id, file_name as new_file_name from attach_file where table_name = ? and table_pk_name = ? and table_pk_value = ?";
	
	public String INSERT_S_SERVICE_APPLY_SQL = "insert into s_service_apply (apply_id, apply_code, apply_name, service_id, service_type, eff_date, exp_date, apply_date, apply_staff_id, apply_reason, org_id, state, state_date) values (?, ?, ?, ?, ?, " + DBUtils.to_date(2) + ", " + DBUtils.to_date(2) + ", " + DBUtils.to_date(2) + ", ?, ?, ?, ?, " + DBUtils.to_date(2) + ")";
	public String SELECT_S_SERVICE_APPLY_SQL = "select s.apply_id,"
			                                 + "       s.apply_code,"
			                                 + "       s.apply_name,"
			                                 + "       s.service_id,"
			                                 + "       s.service_type,"
			                                 + "       " + DBUtils.to_char("s.eff_date", 2) + " eff_date,"
			                                 + "       " + DBUtils.to_char("s.exp_date", 2) + " exp_date,"
			                                 + "       " + DBUtils.to_char("s.apply_date", 2) + " apply_date,"
			                                 + "       s.apply_staff_id,"
			                                 + "       (select d.staff_name from dm_staff d where d.staff_id = s.apply_staff_id) staff_name,"
			                                 + "       s.apply_reason,"
			                                 + "       s.org_id,"
			                                 + "       (select o.org_name from dm_organization o where o.org_id = s.org_id) team_name,"
			                                 + "       s.state"
			                                 + "  from s_service_apply s"
			                                 + " where s.apply_id = ?";
	
	public String INSERT_S_TASK_INFO_SQL = "insert into s_task_info (task_id, apply_id, task_name, task_code, task_type, prior, run_command, task_desc) values (?, ?, ?, ?, ?, ?, ?, ?)";
	public String SELECT_S_TASK_INFO_SQL = "select task_id, apply_id, task_name, task_code, task_type, prior, run_command, task_desc from s_task_info where 1 = 1 ";
	
	public String INSERT_S_DATA_INST_SQL = "insert into s_data_inst (data_inst_id, apply_id, data_code) values (?, ?, ?)";
	public String SELECT_S_DATA_INST_SQL = "select data_inst_id, apply_id, data_code from s_data_inst where apply_id = ?";
	
	public String INSERT_C_DATA_COLUMN_SQL = "insert into c_data_column (column_id, service_id, column_name, column_code, column_type, column_length) values (?, ?, ?, ?, ?, ?)";
	
	public String INSERT_S_DATA_COLUMN_SQL = "insert into s_data_column (column_inst_id, data_inst_id, column_id) values (?, ?, ?)";
	
	public String SELECT_COL_SQL = "select s.column_inst_id,"
			                     + "       s.data_inst_id,"
			                     + "       c.column_id,"
			                     + "       c.service_id,"
			                     + "       c.column_name,"
			                     + "       c.column_code,"
			                     + "       c.column_type,"
			                     + "       c.column_length"
			                     + "  from s_data_column s, c_data_column c"
			                     + " where s.column_id = c.column_id"
			                     + "   and s.data_inst_id = ?";
	
	public String INSERT_S_DATA_DISPATCH_SQL = "insert into s_data_dispatch (dispatch_id, data_inst_id, dispatch_type, ftp_data_type, ftp_ip, ftp_port, ftp_user, ftp_password, ftp_def_dir, ftp_split, import_type, create_table, sqoop_type, db_url, db_user, db_password, db_type,def_file_name) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
	public String SELECT_S_DATA_DISPATCH_SQL = "select dispatch_id, data_inst_id, dispatch_type, ftp_data_type, ftp_ip, ftp_port, ftp_user, ftp_password, ftp_def_dir, ftp_split, import_type, create_table, sqoop_type, db_url, db_user, db_password, db_type,def_file_name from s_data_dispatch where data_inst_id = ?";
	
	public String SELECT_C_TASK_SCHEDULE_SQL = "select schedule_url from c_task_schedule where service_id = ?";
	public String SELECT_C_TASK_PARAM_SQL = "select param_id, param_name, param_code, param_type, param_desc, required from c_task_param where service_id = ?";
	
	public String SELECT_SERVICE_ORDER_SQL = "select service_order_id, data_inst_id, service_inst_id, service_id, service_type, apply_id, ability_id, state, inf_resp_id from service_order where 1 = 1 ";
	
	public String INSERT_SERVICE_ORDER_SQL = "insert into service_order (service_order_id, data_inst_id, service_inst_id, service_id, service_type, apply_id, ability_id, create_date, state_date, state, inf_resp_id) values (?, ?, ?, ?, ?, ?, ?, " + DBUtils.to_date(2) + ", " + DBUtils.to_date(2) + ", ?, ?)";
	
	public String get_schedule_task_statis = "SELECT count(*) num, date(so.create_date) date FROM s_service_inst ssi,service_order so WHERE "
			+"ssi.inst_id = so.service_inst_id AND DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(so.create_date) AND DATE_SUB(CURDATE(), INTERVAL 1 DAY) >= date(so.create_date) AND ssi.service_type='TASK' GROUP BY date ORDER BY "
			+"so.create_date ASC";
	
}