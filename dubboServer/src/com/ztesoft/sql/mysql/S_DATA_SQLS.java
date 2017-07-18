package com.ztesoft.sql.mysql;

import org.springframework.stereotype.Service;

import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.sql.Sql;

/**
 * 数据服务模块sql
 */
@Service(value = "MYSQL_S_DATA_SQLS")
public class S_DATA_SQLS extends Sql {

    /* 我的工作台-我的数据服务 */
    public String SQL_MY_DATA_SERVICE = "" +
            "SELECT " +
            "  cda.data_name, " +
            "  cda.data_code, " +
            "  /* 一级分区 */ " +
            "  (SELECT dav.attr_value_desc " +
            "   FROM dc_attr_value dav, dc_attribute da " +
            "   WHERE da.attr_id = dav.attr_id AND da.attr_code = 'FIRST_DIVISION' AND " +
            "         dav.attr_value = cda.first_division)  first_division, " +
            "  cda.first_division                           first_division_code, " +
            "  /* 二级分区 */ " +
            "  (SELECT dav.attr_value_desc " +
            "   FROM dc_attr_value dav, dc_attribute da " +
            "   WHERE da.attr_id = dav.attr_id AND da.attr_code = 'SECOND_DIVISION' AND " +
            "         dav.attr_value = cda.second_division) second_division, " +
            "  cda.second_division                          second_division_code, " +
            "  ssi.inst_id, " +
            "  ssi.apply_staff_id, " +
            "  cda.service_id," +
            " (SELECT dav.attr_value_desc  " +
            "    FROM dc_attr_value dav, dc_attribute da  " +
            "    WHERE da.attr_id = dav.attr_id AND da.attr_code = 'EXTRACT_FREQ' AND  " +
            "         dav.attr_value = cds.extract_freq ) extract_freq_name,   " +
            " cds.extract_freq, " +
            " ssi.eff_date, " +
            " ssi.exp_date, " + 
            " datediff(ssi.exp_date, ssi.eff_date) + 1 diff, " +
            " datediff(ssi.exp_date, now()) + 1 now_diff, " +
            " sdi.create_order_time " +
            "FROM " +
            "  s_service_inst ssi, " +
            "  c_data_ability cda, " +
            "  s_data_inst sdi, " +
            "  c_data_src cds " +
            "WHERE " +
            "  ssi.service_type = 'DATA' " +
            "  AND ssi.service_id = cda.service_id " +
            "  AND cds.service_id = ssi.service_id " +
            "  AND sdi.apply_id=ssi.apply_id " +
            "  AND ssi.apply_staff_id = ? " +
            "  AND ssi.org_id = ? ";
    /* 我的工作台-我的任务服务 */
    public String SQL_MY_TASK_SERVICE = "" +
            "SELECT " +
            "    /* 任务名称 */ " +
            "    cts.task_name, " +
            "    /* 任务编码 */ " +
            "    cts.task_code, " +
            "    /* 引擎类型 */ " +
            "    cts.engine_type engine_type, " +
            "    cts.engine_type engine_type_code, " +
            "    /* 任务描述 */ " +
            "    cts.comments comments, " +
            "    /* 使用说明 */ " +
            "    cts.instructions instructions, " +
            "    ssi.inst_id, " +
            "    ssi.apply_staff_id, " +
            "    cts.service_id " +
            "FROM " +
            "    s_service_inst ssi, " +
            " c_task_service cts " +
            "WHERE " +
            "    ssi.service_type = 'TASK' " +
            "    AND ssi.service_id = cts.service_id " +
            "    AND ssi.apply_staff_id = ? " +
            "    AND ssi.org_id = ? ";
    
    public String MY_TASK_SERVICE_SQL = "select a.task_id,"
    		                          + "       a.task_name,"
    		                          + "       a.task_code,"
    		                          + "       b.service_id,"
    		                          + "       b.apply_id,"
    		                          + "       b.apply_code,"
    		                          + "       b.eff_date,"
    		                          + "       b.exp_date,"
    		                          + "       datediff(b.exp_date, b.eff_date) + 1 diff,"
    		                          + "       datediff(b.exp_date, now()) + 1 now_diff,"
    		                          + "       (b.exp_date - now()) now_time_diff"
    		                          + "  from s_task_info a, s_service_apply b"
    		                          + " where a.apply_id = b.apply_id"
    		                          + "   and b.apply_staff_id = ?"
    		                          + "   and b.org_id = ?";

    public String SQL_MY_TENANT_INFO = "" +
            "SELECT " +
            "  cti.tenant_code, " +
            "  cti.tenant_name " +
            "FROM tenant_info cti, VR_STAFF_TENANT vst, VR_STAFF vs, M_TEAM_MEMBER mtm " +
            "WHERE cti.tenant_code = vst.TENANT_CODE AND vs.VR_STAFF_ID = vst.VR_STAFF_ID AND mtm.vr_staff_id = vs.VR_STAFF_ID AND " +
            "      vs.STAFF_ID = ? AND mtm.org_id = ?";

    public String SQL_MY_TEAM = "" +
            "SELECT " +
            "    /* 员工名 */ " +
            "    ds.staff_name, " +
            "    /* 组织名 */ " +
            "    staff_org.org_name, " +
            "    /* 团队名 */ " +
            "    org.org_name as team_name, " +
            "    /* 加入时间 */ " +
            "    mtm.add_date, " +
            "    /* 是否管理员 */ " +
            "    IF ( " +
            "        mtm.is_director = 'T', " +
            "        '是', " +
            "        '否' " +
            "    ) AS is_director, " +
            "    mtm.is_director is_director_code, " +
            "    ds.staff_id, " +
            "    ds.staff_code, " +
            "    org.org_id, " +
            "    org.org_code, " +
            "    org.parent_org_id, " +
            "    org.org_level, " +
            "    mtm.vr_staff_id," +
            "    case when exists (select 1 from M_TEAM_MEMBER mtm2 where mtm2.org_id = mtm.org_id and mtm2.is_director = '" + KeyValues.IS_TEAM_DIRECTOR_T + "') then 1 else 0 end as exist_director" + 
            " FROM " +
            "    M_TEAM_MEMBER mtm " +
            "LEFT JOIN DM_ORGANIZATION org ON mtm.org_id = org.org_id, " +
            " DM_STAFF ds " +
            "LEFT JOIN DM_ORGANIZATION staff_org ON ds.org_id = staff_org.org_id, " +
            " VR_STAFF vs " +
            "WHERE " +
            "    mtm.org_id = ? " +
//            "AND org.is_team = 'T' " +
            "AND mtm.vr_staff_id = vs.vr_staff_id " +
            "AND vs.staff_id = ds.staff_id ";

    public String SQL_CHECK_MY_TEAM_ADMIN = "select is_director from m_team_member mtm,vr_staff vs where mtm.vr_staff_id = vs.vr_staff_id ";
    public String SQL_GET_VRSTAFF_BY_STAFF = "select vs.vr_staff_id from m_team_member mtm,vr_staff vs where mtm.vr_staff_id = vs.vr_staff_id and mtm.org_id = ? and vs.staff_id = ? ";
    public String SQL_DELETE_MY_TEAM_MENBER = "delete from m_team_member where  org_id = ? and vr_staff_id = ?";
    public String SQL_UPDATE_MY_TEAM_ADMIN = "update m_team_member mtm set mtm.is_director = ? where    mtm.org_id = ? and mtm.vr_staff_id = ? ";
    public String SQL_INSERT_MY_TEAM = "insert into m_team_member (org_id, vr_staff_id, is_director, add_date) values (?,?,?,now()) ";
    public String SQL_MY_TEAM_MENBER_SIMPLE = "select vs.staff_id from m_team_member mtm,vr_staff vs where mtm.vr_staff_id = vs.vr_staff_id and org_id = ?";
    public String SQL_TEAM_ADMIN_NUMBER = "select 1 from m_team_member where org_id = ? and is_director = 'T' ";
    public String SQL_REMOVE_ALL_TEAM_ADMIN = "update m_team_member set is_director='F' where org_id = ? and is_director = 'T' ";

    public String get_org_name = "select org_name from dm_organization where org_id = ?";
    public String get_staff_name = "select staff_name from dm_staff where staff_id = ?";
    
    public String get_data_inst_list = "select * from s_data_inst where apply_id = ? ";
    public String get_data_column_list = "select * from s_data_column where data_inst_id = ?";
    public String get_dispatch_sql = "select * from s_data_dispatch where data_inst_id=?";
    
    public String get_ability_column_list = "select * from c_data_column where column_id in (select column_id from s_data_column where data_inst_id = ?) order by column_id asc";
    
    public String get_used_ability_column_list = " select  cdc.column_id,cdc.column_code,cdc.column_length,cdc.column_name,cdc.column_type,cdc.comments,cdc.service_id, "
										+" sdc.column_inst_id,sdc.alg_type,sdc.is_acct,sdc.is_dst,sdc.seq "
									    +" from c_data_column cdc,s_data_column sdc where cdc.column_id = sdc.column_id "
									    +" and sdc.data_inst_id = ? order by column_id";
    
	public String get_valid_data_inst = 
			"select a.inst_id as service_inst_id," +
			"       a.service_id," + 
			"       a.service_type," + 
			"       a.apply_staff_id," + 
			"       a.org_id," + 
			"       b.*" + 
			"  from s_service_inst a, s_data_inst b, c_data_ability c" + 
			" where a.apply_id = b.apply_id" + 
			"   and b.service_id = c.service_id" + 
			"   and a.state = '00A'" + 
			"   and a.service_type = ? " + 
			"   and (now() between a.eff_date and a.exp_date)";
	
	/**
	 * 1、s_data_inst.extract_type   once、period
	 * 2、c_data_src.extract_freq M、D
	 * 3、c_data_ability begin_dispatch_time end_dispatch_time
	 * 4、1=='once' (s_data_inst.create_order_time==null or =='') && 在2与3时间内   写入定时任务扫描时间
	 * 5、1=='period' 在2与3时间内  s_data_inst.create_order_time 写入定时任务扫描时间
	 */
	public String get_valid_data_inst_l = "SELECT * FROM "
			+" (  "
			+" SELECT  "
			+" a.inst_id AS service_inst_id,  "
			+" a.apply_name,   "
			+" a.service_type,  "
			+" a.service_id, "
			+" b.data_inst_id,b.apply_id,b.ability_id,b.extract_type, "
			+" b.data_code,b.data_name,b.data_range,b.start_time,b.end_time, "
			+" b.is_history,b.history_acct,b.create_order_time, "
			+" c.begin_dispath_time,  "
			+" c.end_dispath_time,  "
			+" d.extract_freq,  "
			+" c.supply_freq "
			+" FROM  "
			+"  s_service_inst a,  "
			+" 	s_data_inst b,  "
			+" 	c_data_ability c,  "
			+" 	c_data_src d,  "
			+" 	c_data_service e 	 "
			+" WHERE  "
			+" a.apply_id = b.apply_id  "
			+" AND a.state = '00A'  "
			+" AND a.service_type = ? " 
			+" AND d.service_id = c.service_id  "
			+" AND e.service_id = c.service_id  "
			+" AND b.service_id = e.service_id  "
			+" AND (  "
			+" now() BETWEEN a.eff_date  "
			+" AND a.exp_date  "
			+" )  "
			+" AND  "
			+" IF (  "
			+" b.extract_type = 'once',  "
			+" b.create_order_time IS NULL  "
			+" OR b.create_order_time = '',  "
			+" 1 = 1  "
			+" )  "
			+" ) t  "
			+" WHERE  "
			+" IF (  "
			+" t.extract_freq = 'M',  "
			+" DAY (now()) BETWEEN t.begin_dispath_time  "
			+" AND t.end_dispath_time and ( t.create_order_time is null or t.create_order_time = '') ,  "
			+" IF (  "
			+" t.extract_freq = 'D',  "
			+" HOUR (now()) BETWEEN substring(t.begin_dispath_time, 1, 2)  "
			+" AND substring(t.end_dispath_time, 1, 2) and ( t.create_order_time is null or t.create_order_time = '') ,  "
			+ "IF( "
			+" t.extract_freq = 'H', "
			+" (t.create_order_time IS NULL ) or( t.create_order_time is not NULL and create_order_time > date_add(now(), interval t.supply_freq hour)), "
			+" t.service_id = - 1)  "
			+" )  "
			+" )";
	
	public String get_dispatch_data = "select t.*,(select data_code from s_data_inst where data_inst_id = t.data_inst_id)as data_code "
			+ "from s_data_dispatch t where t.data_inst_id = ?  ";
	
	public String get_alert_id = "select alert_id from s_service_inst_alert where inst_id = ? and alert_type = ? ";
	
	public String update_alert_duration_time = "update s_service_inst_alert set duration = TIMESTAMPDIFF(SECOND,start_time,end_time)*1000  where  alert_id=? ";
	
	public String get_hive_view_name = "select sdi.view_name from service_order so,s_data_inst sdi where so.service_order_id=? and so.data_inst_id=sdi.data_inst_id";
	
	public String update_datainst_createtime = "update s_data_inst set create_order_time=? where data_inst_id in(select data_inst_id from service_order where service_order_id=?)";
	
	public String get_schedule_log_list = "select * from quartz_schedule_job_log order by start_time desc ";
	
	public String get_acct_column = "SELECT cda.acct_division as column_code,cds.extract_freq,cds.extract_start_acct FROM service_order so, c_data_ability cda, c_data_src cds "
			+ "WHERE cda.service_id = so.service_id and cds.service_id=cda.service_id and so.service_order_id=?";
	
	public String get_acct_column_inst = "select * from s_data_inst where data_inst_id in (select data_inst_id from service_order where service_order_id=?) ";
	
	public String get_lan_column_code = "select cda.lan_division from service_order so,c_data_ability cda "
			+ "where so.service_id=cda.service_id and so.service_order_id=?";
	
	public String get_lan_data_range_inst = "select sdi.data_range from s_data_inst sdi,service_order so where so.service_order_id=? and so.data_inst_id=sdi.data_inst_id";
	
	public String get_inst_column_list = "select cdc.* from s_data_column sdc,service_order so,c_data_column cdc "
				+ "where so.data_inst_id = sdc.data_inst_id and so.service_order_id = ? "
				+ "and cdc.column_id = sdc.column_id";
	
	public String get_extract_freq = "select sdi.extract_type,cds.extract_freq,cds.extract_start_acct from s_data_inst sdi,c_data_src cds "
				+ "where sdi.service_id = cds.service_id and sdi.data_inst_id =? ";
	
	public String get_service_order_list = "SELECT distinct so.service_order_id,so.state_date AS order_state_date, "
				+ " so.state AS order_state,so.inf_resp_id,ssi.inst_id,ssi.apply_id, "
				+ " ssi.apply_code,ssi.apply_name,ssi.service_id,ssi.service_type, "
				+ " ssi.eff_date,ssi.exp_date,ssi.apply_date,ssi.apply_staff_id,ssi.org_id, "
				+ " ssi.state,ssi.state_date,ssi.last_data_acct,ssi.last_data_date,so.lan_num,so.acct_time, sdi.data_code,"
				+ " (select staff_name from dm_staff where staff_id = ssi.apply_staff_id) as apply_staff_name,"
				+ " if( sdi.data_range = '' or sdi.data_range is null, '-1' ,sdi.data_range) as data_range "
				+ " FROM "
				+ " service_order so, "
				+ " s_service_inst ssi,"
				+ " s_data_inst sdi "
				+ " WHERE "
				+ " so.service_inst_id = ssi.inst_id "
				+ " AND ssi.state = '00A'"
				+ " AND sdi.data_inst_id = so.data_inst_id ";
	
	public String get_sdata_inst_byserviceid = "select * from s_data_inst where data_inst_id in (select data_inst_id from service_order where service_order_id=?)";
	
	public String get_total_schedule_count = "select count(*) from service_order s , s_service_inst_schedule_log sisl "
			+ "WHERE s.service_order_id = sisl.service_order_id and s.state='00A'";
	
	public String get_data_change_notify = " select * from data_change_notify where table_name = ? and owner_inst_id = ? and inst_id = ? and flow_id = ?  ";
	
	public String get_data_change_notify_1 = " select * from data_change_notify where table_name = ? and owner_inst_id = ? and flow_id = ?  ";

	public String is_data_apply_update = "select flow_id from data_change_notify where table_name = ? and owner_inst_id = ? "
			+ "and inst_id = ? and field_name = ? and field_value = ? and flow_id = (select flow_id from bpm_bo_flow_inst "
			+ "where bo_id = ? and bo_state = ? and bo_type_id = ? and table_code = ?)";
	
	public String get_ability_column_list_tmp = "select cdc.column_id,cdc.column_code,cdc.column_length,cdc.column_name,cdc.column_type,cdc.comments,cdc.service_id, "
										+" sdc.alg_type,sdc.is_acct,sdc.is_dst,sdc.seq "
									    +" from c_data_column cdc,data_change_notify_column sdc where cdc.column_id = sdc.column_id "
									    +"  and sdc.data_inst_id = ? and flow_id = ? order by column_id ";
	    
	
	public String get_data_inst_list_with_del = "SELECT * FROM s_data_inst WHERE data_inst_id NOT IN (	SELECT	field_value "
			+" 	FROM data_change_notify "
			+" WHERE action_type = 'D' AND flow_id = ? AND table_name = ?	AND owner_inst_id = ? AND field_name = ? ) "
			+" and apply_id = ? ";
	
	//获取分发过的ftp数据
	public String get_dispatch_ftp_list = "select ftp_ip as text,ftp_ip as valueText,ftp_port,ftp_user,ftp_password,ftp_def_dir,ftp_split from s_data_dispatch where data_inst_id in ( "
			+" 	select sdi.data_inst_id from s_data_inst sdi,s_service_apply ssa  "
			+" 	where sdi.apply_id = ssa.apply_id and ssa.org_id = ? "
			+" 	) "
			+" 	and ( ftp_ip is not null or ftp_ip != '') "
			+" 	group by ftp_ip;";
			
	
	//获取分发过的db数据
	public String get_dispatch_db_list = "select db_url as text,db_url as valueText,db_user,db_password,db_type,sqoop_type from s_data_dispatch where data_inst_id in ( "
			+" 	select sdi.data_inst_id from s_data_inst sdi,s_service_apply ssa  "
			+" 	where sdi.apply_id = ssa.apply_id and ssa.org_id = ? "
			+" 	) "
			+" 	and ( db_url is not null or db_url != '' )  "
			+" 	GROUP BY db_url";
			
	
	public String validate_def_file_name = "select sdd.* from s_data_dispatch sdd,s_data_inst sdi,s_service_apply ssa where " 
			+ "sdd.data_inst_id=sdi.data_inst_id and sdi.apply_id=ssa.apply_id and sdd.def_file_name=? and ssa.state!='00X' ";
	
	public String get_def_filename_by_serviceorder = "select so.apply_id,sdi.data_code,sdi.data_range,so.acct_time,sdd.def_file_code "
			+ "from service_order so,s_data_inst sdi,s_data_dispatch sdd "
			+ "where service_order_id=? and sdi.data_inst_id=so.data_inst_id and sdi.data_inst_id=sdd.data_inst_id";
	
	public String validate_def_file_name_2 = "select * from data_change_notify where table_name='s_data_dispatch' and field_name='def_file_name' and field_value=? ";
	
	public String clean_ftp_file_list = "select * from s_service_inst_dispatch_log where dispatch_type='ftp' and dispatch_state='1' ";
	
	public String update_dispatch_log_state = "update s_service_inst_dispatch_log set dispatch_state=?,state_date=now() where log_id=? ";
	
	public String get_dataability_by_orderId = "select * from c_data_ability where service_id in(select service_id from DATA_SYN_ORDER where order_id = ? )";
	
}