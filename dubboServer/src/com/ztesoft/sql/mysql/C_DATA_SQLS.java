package com.ztesoft.sql.mysql;

import org.springframework.stereotype.Service;

import com.ztesoft.sql.Sql;

/**
 * 数据配置模块sql
 *
 */
@Service(value = "MYSQL_C_DATA_SQLS")
public class C_DATA_SQLS extends Sql {
	
	//获取某个父目录下的第一级子目录的目录数据
	String catalog_sql = 
			  "  SELECT c.*,    "
			+ "		(SELECT count(1) from c_data_catalog tc "
			+ "			WHERE tc.state=? and tc.p_catalog_id = c.catalog_id) as children_num"
			+ "  FROM c_data_catalog c "
			+ "  WHERE c.state=? and c.p_catalog_id = ? order by c.sortby asc ";
	
	//获取所有的目录数据
	String catalog_sql_all =  "  SELECT c.* FROM c_data_catalog c WHERE c.state=? order by c.sortby asc ";
	
	//获取数据服务能力列表
	String data_ability_list = "select cda.*,csrc.extract_freq,cds.state,cds.catalog_id,cdc.catalog_name,"
							  +"csrc.extract_start_acct,csrc.is_hist_extract "
							  +"from c_data_service cds,c_data_ability cda,c_data_src csrc,c_data_catalog cdc " 
							  +"where  cds.state != ?  and cds.service_id = cda.service_id "
							  +"and csrc.service_id = cds.service_id and cdc.catalog_id = cds.catalog_id ";
	
	//获取可用的来源平台下拉值
	//String get_src_sys = "select * from meta_system ";
	String get_src_sys = "select * from src_sys ";
	
	//更新数据服务能力的状态
	String update_service_state = "update c_data_service set state=? where service_id = ?";
	
	String get_src_schema = "select a.schema_code,a.owner,a.schema_name,b.sys_code,b.omp_owner,'' as user_name "
			+ "from meta_schema a,src_lib b where a.schema_code=b.schema_code and b.SYS_CODE='HD' "
			+ " UNION "
			+ "SELECT b.schema_code,a. OWNER,b.schema_name,b.sys_code,b.omp_owner,a.user_name "
			+ "FROM omp_datasource_protocol a, src_lib b WHERE a.schema_code = b.OMP_OWNER and b.SYS_CODE<>'HD' ";
	
	/** 脱敏数据管理 start **/
	//获取算法列表
	String algothrim_list = " select a.algorithm_id,a.algorithm_code,a.type,"
							+ "a.algorithm_name,a.state,a.algorithm_func,"
							+ "date_format(a.create_time,'%Y-%m-%d %T') create_time,"
							+ "date_format(a.state_time,'%Y-%m-%d %T') state_time,a.comments "
							+ "from c_algorithms as a  "
							+ "where a.state!='00X'  ";
	
	/** 脱敏数据管理 end **/
	
	/** 内部数据目录管理 start **/
	//获取bdp平台系统列表
	String bdp_owner_list = "select distinct owner as value, owner as text from meta_schema where 1=1";
	
	//获取bdp平台库列表
	String bdp_schema_list = " select schema_id,schema_code as value,schema_name as text,schema_type,owner from meta_schema where 1=1 ";
	
	//获取bdp平台表列表
	String bdp_table_list = " select a.table_code,a.schema_code,a.table_name,a.table_type,a.status,"
			+ "a.meta_table_id,a.create_staff,b.staff_code,"
			+ "date_format(a.timetolive,'%Y-%m-%d %T') timetolive "
			+ " from meta_tables a left join dm_staff b on a.create_staff = b.staff_id "
			+ " where 1=1 ";
//			+ "where status='00A' ";
	
	//获取bdp平台字段列表
	String bdp_field_list = " select column_id,table_code,schema_code,column_code,"
			+ "column_name,column_type,length,seq "
			+ " from META_COLUMNS  where 1=1 ";
	
	//获取本平台库列表
	String schema_list = "  select css.schema_id as value,css.sys_id,css.schema_code,css.schema_name as text,css.comments, "
		+ " css.state, csys.sys_code from c_src_schema css inner join c_src_sys csys on css.sys_id = csys.sys_id  where css.state != '00X' ";
	
	//获取本平台表列表
	String table_list = " select a.table_id,a.schema_id,a.table_code,a.table_name,a.comments,"
			+ "date_format(a.create_time,'%Y-%m-%d %T') create_time ,"
			+ " a.state, b.schema_code"
			+ " from c_src_table a left join c_src_schema b on a.schema_id = b.schema_id where a.state != '00X' ";
	
	//获取本平台字段列表
	String field_list = " select a.column_id,a.table_id,a.column_code,a.column_name,a.column_type,"
			+ " a.column_length,a.comments,a.state,b.state as table_state from c_src_column a left join c_src_table b on a.table_id=b.table_id where a.state != '00X' ";
	
	String get_column_list = "select * from c_data_column where service_id = ? ";
	
	//查询平台信息
	String select_sys_nfo = "select sys_code,sys_id,sys_name,comments,state from c_src_sys where state='00A' ";
	
	//插入平台信息
	String insert_sys_info = "insert into c_src_sys(sys_code,sys_name,comments,state) values(?,?,?,'00A')";
	
	//查询库信息
	String select_schema = "select schema_id,sys_id,schema_code,schema_name,comments,state from c_src_schema where state='00A'";
	
	//插入库信息
	String insert_schema_info = "insert into c_src_schema(sys_id,schema_code,schema_name,comments,state) values(?,?,?,?,'00A')";
	
	//查询表信息
	String select_table = "select a.table_id,a.schema_id,a.table_code,a.table_name,a.comments,a.state,b.schema_code,"
			+ " date_format(a.create_time,'%Y-%m-%d %T') create_time "
			+ " from c_src_table a left join c_src_schema b on a.schema_id = b.schema_id where a.state!='00X'";
	
	//00B新增
	//插入表信息
	String insert_table = "insert into c_src_table(schema_id,table_code,table_name,comments,state,create_time) values(?,?,?,?,'00B',?)";
	
	//插入字段信息
	String insert_field = "insert into c_src_column(table_id,column_code,column_name,column_type,column_length,comments,state) values(?,?,?,?,?,?,'00A')";
	
	//查询字段信息
	String select_field = "select a.column_id,a.table_id,a.column_code,a.column_name,a.column_type,a.column_length,a.comments,a.state,b.state as table_state from c_src_column a left join c_src_table b on a.table_id = b.table_id where a.state!='00X'";
	
	//删除表信息
	String delete_table = " update c_src_table set state='00X' where 1=1 ";
	
	//删除字段信息
	String delete_field = " update c_src_column set state='00X' where 1=1 ";
	
	/** 内部数据目录管理 end **/
	
	//查询来源表
	String get_src_table = "select * from meta_tables mt where 1=1   ";
	
	//查询来源表【关系型数据库】
	String get_omp_src_table = "select * from omp_metadata_table mt where 1=1   ";
	
	String get_src_column = "select csc .*,"
			+ "(select p.partition_code from meta_partition p "
			+ "		where p.partition_code=csc.column_code and p.table_code=csc.table_code "
			+ "			and p.schema_code=csc.schema_code limit 0, 1) as partition_code "
			+ " from meta_columns csc where 1=1 ";
	String get_omp_src_column = "select csc .*,"
			+ "(select csc.column_code from omp_metadata_table_partition p "
			+ "		where p.field_id=csc.column_id limit 0,1) as partition_code "
			+ " from omp_metadata_table_columns csc where 1=1 ";
	
	//校验来源表的合法性
	String validate_src_table = "select * from meta_tables where lower(schema_code) = ? and lower(table_code) REGEXP ? ";
	String validate_omp_src_table = "select * from omp_metadata_table where lower(schema_code) = lower(?) and lower(table_code) REGEXP ? ";
	
	//获取可用的算法字段
	String get_algorithms = "select * from c_algorithms where state = ?";
	
	//置空数据服务字段信息表的原始来源字段
	String set_src_column_null = "update C_DATA_COLUMN set src_column_id = null where service_id = ?";
	
	//将脱敏算法字段置空
	String set_column_dst_null = "update c_data_column set dst_algorithm = null where column_id = ?";
	
	//将小于0的字段长度的值置为空
	String update_column_length_tonull = "update c_data_column set column_length = null where column_length<0 and  service_id=?";
	
	/**数据保障 start **/
	
	String select_data_alerts = "select sia.alert_id,sia.inst_id,sia.alert_type,sia.start_time, "
						+" sia.end_time,sia.duration,sia.details_msg,sia.service_type,sia.service_id, "
						+" cda.ability_id,cda.data_code,cda.data_name,cds.src_id,cds.src_sys_code,cds.src_schema_code,cds.extract_freq "
						+" from s_service_inst_alert sia, c_data_ability cda,c_data_src cds "
						+" where sia.service_id = cds.service_id and cds.service_id = cda.service_id ";
	
	String select_data_alerts_simple = " select * from s_service_inst_alert where 1=1 ";
	
	String insert_data_alerts = "insert into s_service_inst_alert(inst_id,alert_type,start_time,end_time,duration,details_msg,service_type,service_id) values(?,?,?,?,?,?,?,?)";
	
	/**数据保障 end **/
	
	//获取c_data_column 数据服务字段信息
	String select_data_column = "select g.column_id,g.column_code,g.column_name,g.is_dst,g.dst_algorithm,g.ability_id,"
			+ " ca.algorithm_code, "
			+ " ca.type as algorithm_type, "
			+ "g.service_id "
			+ "from ( select cdc.column_id,cdc.column_code,cdc.column_name,cdc.column_type,cdc.is_dst,cdc.dst_algorithm,tt.*  "
		 	+ "from c_data_column cdc  "
			+ " left join (  "
			+ " select cda.* from c_data_ability cda inner join c_data_service cds on cda.service_id = cds.service_id and cds.state='00A'  "
			+ " )   "
			+ " tt on cdc.service_id = tt.service_id ) g LEFT JOIN  "
			+ " c_algorithms ca on ca.algorithm_id = g.dst_algorithm "
			+ "where 1=1  ";


	/* 数据服务详情-查询数据来源信息 */
	String select_data_src = "" +
			"SELECT " +
			"  cds.src_id, " +
			"  /* 数据来源平台 */ " +
			"  cds.src_sys_code, " +
			"  sys.sys_name, " +
			"  /* 数据来源库 */ " +
			"  cds.src_schema_code, " +
			"  sch.schema_name, " +
			"  /* 数据来源表 */ " +
			"  cds.src_table_code, " +
			"  tab.table_name, " +
			"  /* 抽取频率*/ " +
			"  cds.extract_freq     extract_freq_code, " +
			"  attr.attr_value_desc extract_freq, " +
			"  /* 是否历史数据抽取 */ " +
			"  cds.is_hist_extract, " +
			"  /* 抽取的开始历史账期 */ " +
			"  cds.extract_start_acct " +
			"FROM c_data_src cds " +
			"  LEFT JOIN c_src_sys sys ON sys.sys_code = cds.src_sys_code " +
			"  LEFT JOIN c_src_schema sch ON sch.schema_code = cds.src_schema_code " +
			"  LEFT JOIN c_src_table tab ON tab.table_code = cds.src_table_code " +
			"  LEFT JOIN (SELECT " +
			"               dav.attr_value, " +
			"               dav.attr_value_desc " +
			"             FROM dc_attribute da, dc_attr_value dav " +
			"             WHERE da.attr_id = dav.attr_id AND da.attr_code = 'EXTRACT_FREQ') attr " +
			"    ON attr.attr_value = cds.extract_freq  " +
			"  where 1=1 ";
	
	//获取抽取频率
	public String get_extract_freq = "select extract_freq from c_data_src where service_id = ?";
	
	//获取数据来源信息
	public String get_data_src_details = "select * from c_data_src where service_id = ?  ";
	
	//获取更新c_src_table表的数据
	public String update_src_table_status = "update c_src_table set state=? where table_id = ?  ";
	
	//获取指定服务关联的所有来源表
	public String get_c_data_src_table = "select * from c_data_src_table where service_id = ? ";
	
	//根据tablecode获取tableid的信息
	public String get_src_table_id = "select cst.table_id from c_src_table cst,c_src_schema css,c_src_sys csys "+
			"where csys.sys_code=? and css.schema_code=?  and cst.table_code = ? " +
			"and cst.schema_id = css.schema_id and csys.sys_id = css.sys_id";
	
	//获取使用某一个table的服务数
	public String get_use_target_table_count = "select count(*) from c_data_service cds where cds.state!='00X' and "+ 
			"service_id in (select service_id from c_data_src where src_sys_code=? and src_schema_code=? and src_id "+
			"in(select src_id from c_data_src_table where src_schema_code=? and src_table_code=?)) ";
	
	//获取datacode的结果数
	public String validate_data_code = "select count(1) from c_data_service cds , c_data_ability cda where cds.state!='00X' and cds.service_id=cda.service_id and cda.data_code=?";
	
	public String get_detail_column_list = "SELECT cdc.*, ca.algorithm_name FROM(SELECT * FROM c_data_column WHERE service_id = ?) cdc "
			+ " LEFT JOIN c_algorithms ca on cdc.dst_algorithm = ca.algorithm_id ";
	//获取c_src_column
	public String get_c_src_column = " select csc.column_id,csc.table_id,csc.column_code,csc.is_acct,csc.seq "
			+ " from "
			+ " c_src_column csc "
			+ " where csc.state='00A' ";
	
	public String get_account_column = "select * from c_src_column where table_id = ? and state='00A' and is_acct = '1'";
	
	public String get_in_using_service_count = "select count(*) from s_service_apply s where service_id = ? and state not in ('00C','00X')";
	
	public String GET_SYN_TABLES = "select distinct a.service_id,b.src_sys_code, b.src_schema_code, b.src_table_code, b.extract_freq, c.data_code "
			+ "from c_data_service a, c_data_src b, c_data_ability c "
			+ "where a.service_id = b.service_id and a.service_id = c.service_id and a.state = '00A' ";
	
	public String bdp_opt_col_type_trans = "select * from bdp_attribute_value where attr_id = '32033'";
	
	public String get_all_rr_lan_sql = "select * from rr_lan_all order by lan_id asc";
	
}