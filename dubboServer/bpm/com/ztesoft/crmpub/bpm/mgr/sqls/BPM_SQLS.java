package com.ztesoft.crmpub.bpm.mgr.sqls;

import java.util.Map;

import com.ztesoft.crm.sqls.SqlUtil;
import com.ztesoft.crm.sqls.Sqls;
import com.ztesoft.crmpub.bpm.consts.BPMConsts;



/**
 * @author wumingchao 核心版本整理 ：用于定义本地的SQL(针对oracle数据库) 具体使用的时候和核心版本的一样，
 *         例如SF.custSql("my_name") 如果定义了和核心版本同名的SQL，那么将覆盖核心版本的SQL
 */
public class BPM_SQLS extends Sqls {
	
	// 相关的SQL放置到集合map中
	public  BPM_SQLS() {
		SqlUtil.initSqls(BPM_SQLS.class, this , sqls) ;
		try{
			Map<String , String>  mysqls = this.sqls;
			// 核心版本.wumingzhao; 分离本地SQL和核心版本SQL文件, 本地的SQL语句放在相同包下，以_LOCAL结尾
			Class u_sql_local_class = Class.forName(this.getClass().getName() + "_LOCAL");
			Sqls u_sql_local = (Sqls)u_sql_local_class.newInstance();
			SqlUtil.initSqls(u_sql_local_class, u_sql_local , mysqls) ;
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}

	final String MyOrderBo_findDisVerifyOrder = 
		"select distinct a.wo_id,\n" +
		"                a.task_title,\n" + 
		"                a.flow_id,\n" + 
		"                b.bo_type_id,\n" +
		"                f.bo_url,\n" +
		"                a.state_date,\n" +
		"                '待审核' as tate,\n" + 
		"                b.create_oper_id,\n" + 
		"                b.bo_id,\n" + 
		"                g.tache_url,\n" + 
		"                g.tache_code,\n" +
		"                b.flow_name,\n" + 
		"                b.bo_title\n" +
		"  from bpm_wo_task a, bpm_bo_flow_inst b, bpm_wo_task_exec d,bpm_bo_flow_def f, BPM_BO_FLOW_TACHE g \n" + 
		" where a.wo_id = d.wo_id\n" + 
		"   and a.flow_id = b.flow_id\n" + 
		"   and b.bo_type_id = f.bo_type_id\n" + 
		"    and  ( " +
		"		( d.task_worker = ? and d.worker_type = 'STAFF' )  or  " +
		"		( d.task_worker in ( select role_id from staff_role where party_role_id = ? ) and d.worker_type = 'ROLE' )  " +
		"	)  \n" +
		"   and d.tache_code != '" + BPMConsts.TACHE_CODE_NEW_REQ + "'\n"+
		"   and g.work_type = 'AUDIT'\n"+
		"   and g.bo_type_id = f.bo_type_id \n"+
		"   and g.tache_code = d.tache_code \n"+
		"   and g.tache_code = a.tache_code \n"+
		"   and a.wo_state = 'READY'\n "+
		"   order by a.state_date desc \n";
	
	final String MyOrderBo_queryDisHandleOrder   = 
		"select distinct a.wo_id,\n" +
		"                a.task_title,\n" + 
		"                a.flow_id,\n" + 
		"                b.bo_type_id,\n" +
		"                a.state_date,\n" +
		"                f.bo_url,\n" +
		"                '待处理' as tate,\n" + 
		"                b.create_oper_id,\n" + 
		"                b.bo_id,\n" + 
		"                g.tache_url,\n" + 
		"                g.tache_code,\n" +
		"                b.flow_name,\n" + 
		"                b.bo_title\n" +
		"  from bpm_wo_task a, bpm_bo_flow_inst b, bpm_wo_task_exec d, bpm_bo_flow_def f, BPM_BO_FLOW_TACHE g \n" + 
		" where a.wo_id = d.wo_id\n" + 
		"   and a.flow_id = b.flow_id\n" + 
		"   and b.bo_type_id = f.bo_type_id\n" + 
		"    and  ( " +
		"		( d.task_worker = ? and d.worker_type = 'STAFF' )  or  " +
		"		( d.task_worker in ( select role_id from staff_role where party_role_id = ? ) and d.worker_type = 'ROLE' )  " +
		"	)  \n" +
		"   and d.tache_code != '" + BPMConsts.TACHE_CODE_NEW_REQ + "'\n"+
		"   and g.work_type = 'DEAL'\n"+
		"   and g.bo_type_id = f.bo_type_id \n"+
		"   and g.tache_code = d.tache_code \n"+
		"   and g.tache_code = a.tache_code \n"+
		"   and a.wo_state = 'READY'\n "+
		"   order by a.state_date desc \n";
	
	
	
	final String MyOrderBo_queryHandleOrder =
		"select distinct a.wo_id,\n" +
		"                a.task_title,\n" + 
		"                a.flow_id,\n" + 
		"                b.flow_name,\n" + 
		"                b.bo_type_id,\n" +
		"                a.state_date,\n" +
		"                '已处理' as tate,\n" + 
		"                b.create_oper_id,\n" + 
		"                b.bo_id,\n" + 
		"                g.tache_url,\n" + 
		"                g.tache_code,\n" +
		"                b.bo_title\n" +
		"  from bpm_wo_task a, bpm_bo_flow_inst b, bpm_wo_task_exec d, BPM_BO_FLOW_TACHE g \n" + 
		" where a.wo_id = d.wo_id\n" + 
		"   and a.flow_id = b.flow_id\n" + 
		"   and g.tache_code = d.tache_code \n"+
		"   and g.tache_code = a.tache_code \n"+
		"   and a.bo_type_id = g.bo_type_id \n"+
		"   and b.bo_type_id = g.bo_type_id \n"+
		"   and d.bo_type_id = g.bo_type_id \n"+
		"   and a.work_type = 'DEAL' \n"+
		"    and  ( " +
		"		( d.task_worker = ? and d.worker_type = 'STAFF' )  or  " +
		"		( d.task_worker in ( select role_id from staff_role where party_role_id = ? ) and d.worker_type = 'ROLE' )  " +
		"	)  \n" +
		"   and a.resp_oper_id = ? \n"+
		"   and a.wo_state in ('FINISH','FAIL')\n"+
		"   order by a.state_date desc\n";
   
	final String MyOrderBo_queryHandleOrderHis =
		"select distinct a.wo_id,\n" +
		"                a.task_title,\n" + 
		"                b.flow_name,\n" + 
		"                a.flow_id,\n" + 
		"                b.bo_type_id,\n" +
		"                a.state_date,\n" +
		"                '已处理' as tate,\n" + 
		"                b.create_oper_id,\n" + 
		"                b.bo_id,\n" + 
		"                g.tache_url,\n" + 
		"                g.tache_code,\n" + 
		"                b.bo_title\n" +
		"  from L_BPM_WO_TASK a, L_BPM_BO_FLOW_INST b, L_BPM_WO_TASK_EXEC d, BPM_BO_FLOW_TACHE g \n" + 
		" where a.wo_id = d.wo_id\n" + 
		"   and a.flow_id = b.flow_id\n" + 
		"   and g.tache_code = d.tache_code \n"+
		"   and g.tache_code = a.tache_code \n"+
		"   and a.bo_type_id = g.bo_type_id \n"+
		"   and b.bo_type_id = g.bo_type_id \n"+
		"   and d.bo_type_id = g.bo_type_id \n"+
		"   and a.work_type = 'DEAL' \n"+
		"    and  ( " +
		"		( d.task_worker = ? and d.worker_type = 'STAFF' )  or  " +
		"		( d.task_worker in ( select role_id from staff_role where party_role_id = ? ) and d.worker_type = 'ROLE' )  " +
		"	)  \n" +
		"   and a.resp_oper_id = ? \n"+
		"   and a.wo_state in ('FINISH','FAIL')\n"+
		"   order by a.state_date desc\n";
	
	final String MyOrderBo_queryVerifyOrder =
		"select distinct a.wo_id,\n" +
		"                a.task_title,\n" + 
		"                a.flow_id,\n" + 
		"                b.flow_name,\n" + 
		"                b.bo_type_id,\n" +
		"                a.state_date,\n" +
		"                '已审核' as tate,\n" + 
		"                b.create_oper_id,\n" + 
		"                b.bo_id,\n" + 
		"                g.tache_url,\n" + 
		"                g.tache_code,\n" +
		"                b.bo_title\n" +
		"  from bpm_wo_task a, bpm_bo_flow_inst b, bpm_wo_task_exec d, BPM_BO_FLOW_TACHE g \n" + 
		" where a.wo_id = d.wo_id\n" + 
		"   and a.flow_id = b.flow_id\n" + 
		"   and g.tache_code = d.tache_code \n"+
		"   and g.tache_code = a.tache_code \n"+
		"   and a.bo_type_id = g.bo_type_id \n"+
		"   and b.bo_type_id = g.bo_type_id \n"+
		"   and d.bo_type_id = g.bo_type_id \n"+
		"   and a.work_type = 'AUDIT' \n"+
		"    and  ( " +
		"		( d.task_worker = ? and d.worker_type = 'STAFF' )  or  " +
		"		( d.task_worker in ( select role_id from staff_role where party_role_id = ? ) and d.worker_type = 'ROLE' )  " +
		"	)  \n" +
		"   and a.resp_oper_id = ? \n"+
		"   and a.wo_state in ('FINISH','FAIL')\n"+
		"   order by a.state_date desc\n";
	
	final String MyOrderBo_queryVerifyOrderHis =
		"select distinct a.wo_id,\n" +
		"                a.task_title,\n" + 
		"                b.flow_name,\n" + 
		"                a.flow_id,\n" + 
		"                b.bo_type_id,\n" +
		"                a.state_date,\n" +
		"                '已审核' as tate,\n" + 
		"                b.create_oper_id,\n" + 
		"                b.bo_id,\n" + 
		"                g.tache_url,\n" + 
		"                g.tache_code,\n" + 
		"                b.bo_title\n" +
		"  from L_BPM_WO_TASK a, L_BPM_BO_FLOW_INST b, L_BPM_WO_TASK_EXEC d, BPM_BO_FLOW_TACHE g \n" + 
		" where a.wo_id = d.wo_id\n" + 
		"   and a.flow_id = b.flow_id\n" + 
		"   and g.tache_code = d.tache_code \n"+
		"   and g.tache_code = a.tache_code \n"+
		"   and a.bo_type_id = g.bo_type_id \n"+
		"   and b.bo_type_id = g.bo_type_id \n"+
		"   and d.bo_type_id = g.bo_type_id \n"+
		"   and a.work_type = 'AUDIT' \n"+
		"    and  ( " +
		"		( d.task_worker = ? and d.worker_type = 'STAFF' )  or  " +
		"		( d.task_worker in ( select role_id from staff_role where party_role_id = ? ) and d.worker_type = 'ROLE' )  " +
		"	)  \n" +
		"   and a.resp_oper_id = ? \n"+
		"   and a.wo_state in ('FINISH','FAIL')\n"+
		"   order by a.state_date desc\n";
	
	final String MyOrderBo_findMyBoCCByCond = 
		"select distinct a.wo_id,\n" +
		"                a.task_title,\n" + 
		"                a.flow_id,\n" + 
		"                a.bo_type_id,\n" +
		"                '抄送给我' as tate,\n" + 
		"                c.create_oper_id,\n" + 
		"                c.bo_id,\n" + 
		"                c.bo_title\n" +
		"       from bpm_wo_task a, bpm_wo_cc b, bpm_bo_flow_inst c, bpm_wo_task_exec d, staff e\n" + 
		"       where a.wo_id = d.wo_id\n" + 
		"        and a.wo_id = b.wo_id\n" + 
		"        and a.flow_id = c.flow_id\n" + 
		"        and e.staff_id = b.task_cc\n" + 
		"        and b.task_cc=? ";
		
	
	final String MyOrderBo_findCaseByMeOrder = 
		"select distinct a.wo_id,\n" +
		"                a.task_title,\n" + 
		"                a.flow_id,\n" + 
		"                a.bo_type_id,\n" +
		"                a.wo_state,\n" + 
		"                case\n" + 
		"                when a.wo_state = 'READY' then\n" + 
		"                '就绪'\n" + 
		"                when a.wo_state = 'DEALING' then\n" + 
		"                '正在处理'\n" + 
		"                when a.wo_state = 'FINISH' then\n" + 
		"                '完成'\n" + 
		"                end as tate,\n" + 
		"                f.staff_name,\n" + 
		"                b.bo_id,\n" + 
		"                b.flow_name,\n" + 
		"                b.bo_title,\n" +
		"                e.tache_url\n" +
		"       from bpm_wo_task a, bpm_bo_flow_inst b, bpm_wo_task_exec c, BPM_BO_FLOW_DEF d, BPM_BO_FLOW_TACHE e, staff f\n" + 
		"       where a.flow_id = b.flow_id\n" + 
		"        and a.wo_id = c.wo_id\n" + 
		"        and a.create_oper_id = f.staff_id\n" + 
		"        and b.bo_type_id = d.bo_type_id\n" + 
		"        and e.bo_type_id = d.bo_type_id\n" + 
		"        and a.tache_code = '" + BPMConsts.TACHE_CODE_NEW_REQ + "'\n" + 
		"        and a.tache_code = e.tache_code\n" + 
		"        and a.create_oper_id = c.task_worker\n" + 
		"        and a.create_oper_id=?  order by a.wo_id desc";

	final String MyOrderBo_searchOrder =
		"	select distinct b.flow_id,	\n"+
		"	                     b.bo_type_id,	\n"+
		"	                     b.create_oper_id,	\n"+
		"	                     b.bo_title,	\n"+
		"	                     b.flow_name,	\n"+
		"	                     b.bo_id,	\n"+
		"	                     b.state_date,	\n"+
		"	                     b.create_date,	\n"+
		"	                     b.bo_state_name,	\n"+
		"	                     c.staff_name,	\n"+
		"                       (select ss.staff_name from staff  ss where ss.staff_id = bc.task_worker and rownum=1)deal_staff_name "+
		"	       from bpm_bo_flow_inst b,staff c,organization d,bpm_wo_task t,bpm_wo_task_exec bc	\n"+
		"	       where b.create_oper_id=c.staff_id(+)	\n"+
		"	       and c.org_id=d.party_id(+)	\n"+
		"          and b.flow_id = t.flow_id \n"+
		"          and t.wo_id = bc.wo_id \n "+
        "          and t.wo_state='READY' \n"+
		"          and b.bo_type_id in(select bf.bo_type_id from bpm_bo_flow_def bf where bf.type='"+BPMConsts.FLOW_DEF_TYPE_TASK+"')";		
	
	final String MyOrderBo_searchOrder_l =
		"	select distinct b.flow_id,	\n"+
		"	                     b.bo_type_id,	\n"+
		"	                     b.create_oper_id,	\n"+
		"	                     b.bo_title,	\n"+
		"	                     b.flow_name,	\n"+
		"	                     b.bo_id,	\n"+
		"	                     b.state_date,	\n"+
		"	                     b.create_date,	\n"+
		"	                     b.bo_state_name,	\n"+
		"	                     c.staff_name,	\n"+
		"                        '已处理完成' deal_staff_name \n" +
		"	       from l_bpm_bo_flow_inst b,staff c,organization d	\n"+
		"	       where b.create_oper_id=c.staff_id(+)	\n"+
		"	       and c.org_id=d.party_id(+)	\n"+
		"          and b.bo_type_id in(select bf.bo_type_id from bpm_bo_flow_def bf where bf.type='"+BPMConsts.FLOW_DEF_TYPE_TASK+"')";	

	
	final String MyOrderBo_querySelectOrder = 
			"	select a.tache_name,	b.task_exec_id,b.exec_state,\n"+
			"	       a.tache_code,	\n"+
			"	       a.wo_id,	\n"+
			"	       a.task_title,	\n"+
			"	       a.wo_state,	\n"+
			"	       c.staff_name||'【'||o.org_name||'】' staff_name,	\n"+
			"	       a.bo_id,	\n"+
			"	       a.bo_type_id,	\n"+
			"	       a.resp_result,	\n"+
			"	       a.resp_content ,	\n"+
			"	       d.tache_url,	\n"+
			"	       a.flow_id,	\n"+
	        "	       to_char(b.exec_interval,'fm9999990.0') exec_interval,\n"+
	        "	       to_char(b.dispatch_date,'yyyy-mm-dd hh24:mi') dispatch_date,	\n"+
			"	       to_char(nvl(b.exec_date,a.state_date),'yyyy-mm-dd hh24:mi') exec_date,	\n"+
			"	       decode(wo_state,'READY','就绪','FAIL','审核未通过','DEALING',	\n"+
			"	       '正在处理','FINISH','完成','ARCHIVED','完成','WITHDRAW','撤单') wo_state_name,	\n" +
			"		   decode( b.process_mode,'0','PC','1','手机','PC') process_mode,	\n" +
			"		   to_char(a.limit_date,'yyyy-mm-dd hh24:mi') limit_date,	\n" +
			"		   (select sa.attr_value from staff_attr sa where sa.staff_id = c.staff_id and sa.attr_id = '30000043') tel"+
			"	from bpm_wo_task a, bpm_wo_task_exec b, staff c,bpm_bo_flow_tache d,organization o	\n"+
			"	where a.flow_id=?	\n"+
			"	 and a.wo_id = b.wo_id	\n"+
			"	 and b.task_worker = c.staff_id and c.org_id=o.party_id	and b.worker_type='STAFF' \n"+
			"	 and a.bo_type_id = d.bo_type_id	\n"+
			"	 and a.tache_code = d.tache_code	\n"+
	        "	union all select a.tache_name,b.task_exec_id,b.exec_state,	\n"+
            "	       a.tache_code,	\n"+
            "	       a.wo_id,	\n"+
            "	       a.task_title,	\n"+
            "	       a.wo_state,	\n"+
            "	       c.group_name staff_name,	\n"+
            "	       a.bo_id,	\n"+
            "	       a.bo_type_id,	\n"+
            "	       a.resp_result,	\n"+
            "	       a.resp_content ,	\n"+
            "	       d.tache_url,	\n"+
            "	       a.flow_id,	\n"+
            "	       to_char(b.exec_interval,'fm9999990.0') exec_interval,\n"+
            "	       to_char(b.dispatch_date,'yyyy-mm-dd hh24:mi') dispatch_date,	\n"+
            "	       to_char(nvl(b.exec_date,a.state_date),'yyyy-mm-dd hh24:mi') exec_date,	\n"+
            "	       decode(wo_state,'READY','就绪','FAIL','审核未通过','DEALING',	\n"+
            "	       '正在处理','FINISH','完成','ARCHIVED','完成','WITHDRAW','撤单') wo_state_name	,	\n" +
			"		   decode( b.process_mode,'0','PC','1','手机','PC') process_mode,	\n" +
			"		   to_char(a.limit_date,'yyyy-mm-dd hh24:mi') limit_date,	\n" +
			"		   '' tel"+
            "	from bpm_wo_task a, bpm_wo_task_exec b, audit_group c,bpm_bo_flow_tache d	\n"+
            "	where a.flow_id=?	\n"+
            "	 and a.wo_id = b.wo_id	\n"+
            "	 and b.task_worker =c.group_id	and b.worker_type='TEAM' \n"+
            "	 and a.bo_type_id = d.bo_type_id	\n"+
            "	 and a.tache_code = d.tache_code	\n"+
            "	union all select a.tache_name,b.task_exec_id,b.exec_state,	\n"+
            "	       a.tache_code,	\n"+
            "	       a.wo_id,	\n"+
            "	       a.task_title,	\n"+
            "	       a.wo_state,	\n"+
            "	       c.org_name staff_name,	\n"+
            "	       a.bo_id,	\n"+
            "	       a.bo_type_id,	\n"+
            "	       a.resp_result,	\n"+
            "	       a.resp_content ,	\n"+
            "	       d.tache_url,	\n"+
            "	       a.flow_id,	\n"+
            "	       to_char(b.exec_interval,'fm9999990.0') exec_interval,\n"+
            "	       to_char(b.dispatch_date,'yyyy-mm-dd hh24:mi') dispatch_date,	\n"+
            "	       to_char(nvl(b.exec_date,a.state_date),'yyyy-mm-dd hh24:mi') exec_date,	\n"+
            "	       decode(wo_state,'READY','就绪','FAIL','审核未通过','DEALING',	\n"+
            "	       '正在处理','FINISH','完成','ARCHIVED','完成','WITHDRAW','撤单') wo_state_name	,	\n" +
			"		   decode( b.process_mode,'0','PC','1','手机','PC') process_mode,	\n" +
			"		   to_char(a.limit_date,'yyyy-mm-dd hh24:mi') limit_date,	\n" +
			"		   '' tel"+
            "	from bpm_wo_task a, bpm_wo_task_exec b, organization c,bpm_bo_flow_tache d	\n"+
            "	where a.flow_id=?	\n"+
            "	 and a.wo_id = b.wo_id	\n"+
            "	 and b.task_worker =c.party_id and b.worker_type='ORG' \n"+
            "	 and a.bo_type_id = d.bo_type_id	\n"+
            "	 and a.tache_code = d.tache_code	\n";


	final String MyOrderBo_querySelectOrder_l = 
			"	select a.tache_name,b.task_exec_id,b.exec_state,	\n"+
			"	       a.tache_code,	\n"+
			"	       a.wo_id,	\n"+
			"	       a.task_title,	\n"+
			"	       a.wo_state,	\n"+
			"	       c.staff_name||'【'||o.org_name||'】' staff_name,	\n"+
			"	       a.bo_id,	\n"+
			"	       a.bo_type_id,	\n"+
			"	       a.resp_result,	\n"+
			"	       a.resp_content ,	\n"+
			"	       d.tache_url,	\n"+
			"	       a.flow_id,	\n"+
	        "	       to_char(b.exec_interval,'fm9999990.0') exec_interval,\n"+
	        "	       to_char(b.dispatch_date,'yyyy-mm-dd hh24:mi') dispatch_date,	\n"+
	        "	       to_char(nvl(b.exec_date,a.state_date),'yyyy-mm-dd hh24:mi') exec_date,	\n"+
			"	       decode(wo_state,'READY','就绪','FAIL','审核未通过','DEALING',	\n"+
			"	       '正在处理','FINISH','完成','ARCHIVED','完成','WITHDRAW','撤单') wo_state_name	,	\n" +
			"		   decode( b.process_mode,'0','PC','1','手机','PC') process_mode,	\n" +
			"		   to_char(a.limit_date,'yyyy-mm-dd hh24:mi') limit_date,	\n" +
			"		   (select sa.attr_value from staff_attr sa where sa.staff_id = c.staff_id and sa.attr_id = '30000043') tel"+
			"	from l_bpm_wo_task a, l_bpm_wo_task_exec b, staff c,bpm_bo_flow_tache d,organization o	\n"+
			"	where a.flow_id=?	\n"+
			"	 and a.wo_id = b.wo_id	\n"+
			"	 and b.task_worker = c.staff_id and c.org_id=o.party_id and b.worker_type='STAFF'	\n"+
			"	 and a.bo_type_id = d.bo_type_id	\n"+
			"	 and a.tache_code = d.tache_code	\n"+
	        "union all select a.tache_name,b.task_exec_id,b.exec_state,	\n"+
	        "	       a.tache_code,	\n"+
	        "	       a.wo_id,	\n"+
	        "	       a.task_title,	\n"+
	        "	       a.wo_state,	\n"+
	        "	       c.group_name staff_name,	\n"+
	        "	       a.bo_id,	\n"+
	        "	       a.bo_type_id,	\n"+
	        "	       a.resp_result,	\n"+
	        "	       a.resp_content ,	\n"+
	        "	       d.tache_url,	\n"+
	        "	       a.flow_id,	\n"+
	        "	       to_char(b.exec_interval,'fm9999990.0') exec_interval,\n"+
	        "	       to_char(b.dispatch_date,'yyyy-mm-dd hh24:mi') dispatch_date,	\n"+
	        "	       to_char(nvl(b.exec_date,a.state_date),'yyyy-mm-dd hh24:mi') exec_date,	\n"+
	        "	       decode(wo_state,'READY','就绪','FAIL','审核未通过','DEALING',	\n"+
	        "	       '正在处理','FINISH','完成','ARCHIVED','完成','WITHDRAW','撤单') wo_state_name	,	\n" +
			"		   decode( b.process_mode,'0','PC','1','手机','PC') process_mode,	\n" +
			"		   to_char(a.limit_date,'yyyy-mm-dd hh24:mi') limit_date,	\n" +
			"		   '' tel"+
	        "	from l_bpm_wo_task a, l_bpm_wo_task_exec b, audit_group c,bpm_bo_flow_tache d	\n"+
	        "	where a.flow_id=?	\n"+
	        "	 and a.wo_id = b.wo_id	\n"+
	        "	 and b.task_worker =c.group_id and b.worker_type='TEAM' \n"+
	        "	 and a.bo_type_id = d.bo_type_id	\n"+
	        "	 and a.tache_code = d.tache_code	\n"+
	        "union all select a.tache_name,b.task_exec_id,b.exec_state,	\n"+
	        "	       a.tache_code,	\n"+
	        "	       a.wo_id,	\n"+
	        "	       a.task_title,	\n"+
	        "	       a.wo_state,	\n"+
	        "	       c.org_name staff_name,	\n"+
	        "	       a.bo_id,	\n"+
	        "	       a.bo_type_id,	\n"+
	        "	       a.resp_result,	\n"+
	        "	       a.resp_content ,	\n"+
	        "	       d.tache_url,	\n"+
	        "	       a.flow_id,	\n"+
	        "	       to_char(b.exec_interval,'fm9999990.0') exec_interval,\n"+
	        "	       to_char(b.dispatch_date,'yyyy-mm-dd hh24:mi') dispatch_date,	\n"+
	        "	       to_char(nvl(b.exec_date,a.state_date),'yyyy-mm-dd hh24:mi') exec_date,	\n"+
	        "	       decode(wo_state,'READY','就绪','FAIL','审核未通过','DEALING',	\n"+
	        "	       '正在处理','FINISH','完成','ARCHIVED','完成','WITHDRAW','撤单') wo_state_name	,	\n" +
			"		   decode( b.process_mode,'0','PC','1','手机','PC') process_mode,	\n" +
			"		   to_char(a.limit_date,'yyyy-mm-dd hh24:mi') limit_date,	\n" +
			"		   '' tel"+
	        "	from l_bpm_wo_task a, l_bpm_wo_task_exec b, organization c,bpm_bo_flow_tache d\n"+
	        "	where a.flow_id=?	\n"+
	        "	 and a.wo_id = b.wo_id	\n"+
	        "	 and b.task_worker =c.party_id and b.worker_type='ORG' \n"+
	        "	 and a.bo_type_id = d.bo_type_id	\n"+
	        "	 and a.tache_code = d.tache_code	\n";
	
	final String MyOrderBo_querySegment = "select * from bpm_bo_flow_tache where bo_type_id=? order by seq_no asc";
	
	final String MyOrderBo_queryAllOrder1 = 
		"select distinct b.flow_id,\n" +
		"                '[宽带]' || e.service_offer_name || '流程' flowName,\n" + 
		"                b.flow_name,\n" + 
		"                b.bo_title,\n" + 
		"                b.create_oper_id,\n" + 
		"                c.staff_name,\n" + 
		"                b.bo_id,\n" + 
		"                case\n" + 
		"                  when b.bo_state = '0' then\n" + 
		"                   '激活'\n" + 
		"                  when b.bo_state = '1' then\n" + 
		"                   '挂起'\n" + 
		"                  when b.bo_state = '2' then\n" + 
		"                   '作废'\n" + 
		"                  when b.bo_state = '9' then\n" + 
		"                   '结束'\n" + 
		"                end as bo_state\n" + 
		"  from bpm_bo_flow_inst b, staff c, order_item d, service_offer e\n" + 
		" where b.create_oper_id = c.staff_id\n" + 
		"   and b.bo_id = d.order_item_id\n" + 
		"   and d.service_offer_id = e.service_offer_id ";



		

	final String MyOrderBo_queryAllOrder2 =

		"select distinct b.flow_id,\n" +
		"                '[宽带]' || e.service_offer_name || '流程' flowName,\n" + 
		"                b.flow_name,\n" + 
		"                b.bo_title,\n" + 
		"                b.create_oper_id,\n" + 
		"                c.staff_name,\n" + 
		"                b.bo_id,\n" + 
		"                case\n" + 
		"                  when b.bo_state = '0' then\n" + 
		"                   '激活'\n" + 
		"                  when b.bo_state = '1' then\n" + 
		"                   '挂起'\n" + 
		"                  when b.bo_state = '2' then\n" + 
		"                   '作废'\n" + 
		"                  when b.bo_state = '9' then\n" + 
		"                   '结束'\n" + 
		"                end as bo_state\n" + 
		"  from l_bpm_bo_flow_inst b, staff c, l_order_item d, service_offer e\n" + 
		" where b.create_oper_id = c.staff_id\n" + 
		"   and b.bo_id = d.order_item_id\n" + 
		"   and d.service_offer_id = e.service_offer_id ";
	
	final String MyOrderBo_queryAllOrder3 =

		"select distinct b.flow_id,\n" +
		"                '[宽带]' || e.service_offer_name || '流程' flowName,\n" + 
		"                b.flow_name,\n" + 
		"                b.bo_title,\n" + 
		"                b.create_oper_id,\n" + 
		"                c.staff_name,\n" + 
		"                b.bo_id,\n" + 
		"                case\n" + 
		"                  when b.bo_state = '0' then\n" + 
		"                   '激活'\n" + 
		"                  when b.bo_state = '1' then\n" + 
		"                   '挂起'\n" + 
		"                  when b.bo_state = '2' then\n" + 
		"                   '作废'\n" + 
		"                  when b.bo_state = '9' then\n" + 
		"                   '结束'\n" + 
		"                end as bo_state\n" + 
		"  from l_bpm_bo_flow_inst b, staff c, order_item d, service_offer e\n" + 
		" where b.create_oper_id = c.staff_id\n" + 
		"   and b.bo_id = d.order_item_id\n" + 
		"   and d.service_offer_id = e.service_offer_id ";
	
	final String MyOrderBo_queryAllOrder4 =

		"select distinct b.flow_id,\n" +
		"                '[宽带]' || e.service_offer_name || '流程' flowName,\n" + 
		"                b.flow_name,\n" + 
		"                b.bo_title,\n" + 
		"                b.create_oper_id,\n" + 
		"                c.staff_name,\n" + 
		"                b.bo_id,\n" + 
		"                case\n" + 
		"                  when b.bo_state = '0' then\n" + 
		"                   '激活'\n" + 
		"                  when b.bo_state = '1' then\n" + 
		"                   '挂起'\n" + 
		"                  when b.bo_state = '2' then\n" + 
		"                   '作废'\n" + 
		"                  when b.bo_state = '9' then\n" + 
		"                   '结束'\n" + 
		"                end as bo_state\n" + 
		"  from bpm_bo_flow_inst b, staff c, l_order_item d, service_offer e\n" + 
		" where b.create_oper_id = c.staff_id\n" + 
		"   and b.bo_id = d.order_item_id\n" + 
		"   and d.service_offer_id = e.service_offer_id ";


	final String MyOrderBo_queryOnesOrder = " select *from bpm_wo_task where bo_id=? ";
	final String MyOrderBo_queryOnesOrder2 = " select *from l_bpm_wo_task where bo_id=? ";
	
	final String MyOrderBo_queryWoTask = "select distinct d.bo_id,\n"
				+ "                d.wo_id,\n" + "                b.seq_no,\n"
				+ "                b.tache_name,\n"
				+ "                d.wo_state,\n"
				+ "                d.resp_result,\n"
				+ "                e.task_worker,\n"
				+ "                f.staff_name,\n"
				+ "                e.exec_date\n"
				+ "  from BPM_BO_FLOW_DEF   a,\n"
				+ "       BPM_BO_FLOW_TACHE b,\n"
				+ "       bpm_bo_flow_inst  c,\n"
				+ "       bpm_wo_task       d,\n"
				+ "       bpm_wo_task_exec  e,\n"
				+ "       staff             f\n" + " where c.bo_id = ?\n"
				+ "   and c.bo_type_id = a.bo_type_id\n"
				+ "   and a.bo_type_id = b.bo_type_id\n"
				+ "   and d.wo_id = e.wo_id\n" + "   and c.bo_id = d.bo_id\n"
				+ "   and d.tache_code = b.tache_code\n"
				+ "   and e.task_worker = f.staff_id\n"
				+ "   and e.flow_id = c.flow_id\n" + " order by b.seq_no";
	
	final String MyOrderBo_queryWoTask2 = "select distinct\n" + "                d.bo_id,\n"
				+ "                d.wo_id,\n"
				+ "                b.seq_no,\n"
				+ "                b.tache_name,\n"
				+ "                d.wo_state,\n"
				+ "                d.resp_result,\n"
				+ "                e.task_worker,\n"
				+ "                f.staff_name,\n"
				+ "                e.exec_date\n"
				+ "  from BPM_BO_FLOW_DEF   a,\n"
				+ "       BPM_BO_FLOW_TACHE b,\n"
				+ "       L_bpm_bo_flow_inst  c,\n"
				+ "       L_bpm_wo_task       d,\n"
				+ "       L_bpm_wo_task_exec  e,\n" + "       staff             f\n" 
				+ " where c.bo_id = ?\n" 
				+ "   and c.bo_type_id = a.bo_type_id\n"
				+ "   and a.bo_type_id = b.bo_type_id\n"
				+ "   and d.wo_id = e.wo_id\n"
				+ "   and c.bo_id = d.bo_id\n"
				+ "   and d.tache_code = b.tache_code\n"
				+ "   and e.task_worker = f.staff_id\n"
				+ "   and e.flow_id = c.flow_id order by b.seq_no";
	
	final String MyOrderBo_queryflow = 
		"SELECT   *\n" +
		"    FROM bpm_bo_flow_def a, bpm_bo_flow_tache b\n" + 
		"   WHERE a.bo_type_id = b.bo_type_id\n" + 
		"     AND b.bo_type_id = (SELECT DISTINCT d.bo_type_id\n" + 
		"                                    FROM bpm_wo_task d\n" + 
		"                                   WHERE d.bo_id = ?)\n" + 
		"ORDER BY b.seq_no";

	
	final String MyOrderBo_queryflow2 = 
		"SELECT   *\n" +
		"    FROM bpm_bo_flow_def a, bpm_bo_flow_tache b\n" + 
		"   WHERE a.bo_type_id = b.bo_type_id\n" + 
		"     AND b.bo_type_id = (SELECT DISTINCT d.bo_type_id\n" + 
		"                                    FROM l_bpm_wo_task d\n" + 
		"                                   WHERE d.bo_id = ?)\n" + 
		"ORDER BY b.seq_no";

	final String MyOrderBo_queryStaffName = "select staff_name from staff where staff_id=?";
	
	
	final String MyReqOrder = " select flow_id, bo_title, flow_name, bo_id, bo_type_id,  bo_state_name,wo_state,wo_id,state_date, "+
		 " case when wo_state = 'READY' then '就绪' when wo_state = 'DEALING' then '正在处理' when wo_state = 'FINISH' then '完成' end as tate, "+
		 " ( select tache_url from BPM_BO_FLOW_TACHE  where b.BO_TYPE_ID = bo_type_id and tache_code = '" + BPMConsts.TACHE_CODE_NEW_REQ + "'  ) as tache_url "+
		 " from "+
		 " ( "+
		 "	select a.flow_id, a.bo_title, a.flow_name, a.bo_id, a.bo_type_id,  a.bo_state_name, a.state_date, "+
		 "	( select wo_state from bpm_wo_task where flow_id = a.flow_id and tache_code = '" + BPMConsts.TACHE_CODE_NEW_REQ + "'  and bo_id = a.bo_id and bo_type_id = a.bo_type_id ) as wo_state, "+
		 "	( select wo_id from bpm_wo_task where flow_id = a.flow_id and tache_code = '" + BPMConsts.TACHE_CODE_NEW_REQ + "'  and bo_id = a.bo_id and bo_type_id = a.bo_type_id ) as wo_id "+  
		 "	from BPM_BO_FLOW_INST a where a.create_oper_id = ?  "+
		 "	union "+
		 "	select a.flow_id, a.bo_title, a.flow_name, a.bo_id, a.bo_type_id,  a.bo_state_name, a.state_date, "+
		 "	( select wo_state from l_bpm_wo_task where flow_id = a.flow_id and tache_code = '" + BPMConsts.TACHE_CODE_NEW_REQ + "'  and bo_id = a.bo_id and bo_type_id = a.bo_type_id ) as wo_state, "+
		 "	( select wo_id from l_bpm_wo_task where flow_id = a.flow_id and tache_code = '" + BPMConsts.TACHE_CODE_NEW_REQ + "'  and bo_id = a.bo_id and bo_type_id = a.bo_type_id ) as wo_id "+  
		 "	from L_BPM_BO_FLOW_INST a where a.create_oper_id = ?  "+
		 " ) b order by b.flow_id desc ";
	
	
//	final String MyReqOrderNew = " select flow_id, bo_title, flow_name, bo_id, bo_type_id,  bo_state_name,state_date " +
//	 " from "+
//	 " ( "+
//	 "	select a.flow_id, a.bo_title, a.flow_name, a.bo_id, a.bo_type_id,  a.bo_state_name, a.state_date "+
//	 " from BPM_BO_FLOW_INST a where a.create_oper_id = ?  "+
//	 "	union "+
//	 "	select a.flow_id, a.bo_title, a.flow_name, a.bo_id, a.bo_type_id,  a.bo_state_name, a.state_date "+
//	 "	from L_BPM_BO_FLOW_INST a where a.create_oper_id = ?  "+
//	 " ) b where 1=1 and order by flow_id desc";
	
	
	final String MyReqOrderNew = " " +
	 " select flow_id, bo_title, flow_name, bo_id, bo_type_id,  bo_state_name,wo_state,wo_id,state_date, "+
	 "   case when wo_state = 'READY' then '就绪' when wo_state = 'FAIL' then '审核未通过' when wo_state = 'DEALING'  or wo_state = 'NEW' then '正在处理'" +
	 "       when wo_state = 'FINISH' then '审批中' when wo_state = 'ARCHIVED' then '完成' end as tate, "+
	 "  (select tache_url from BPM_BO_FLOW_TACHE  where b.BO_TYPE_ID = bo_type_id and tache_code = b.tache_code  ) as tache_url," +
	 "(select mobile_tache_url " +
	 "          from BPM_BO_FLOW_TACHE " + 
	 "         where b.BO_TYPE_ID = bo_type_id " + 
	 "           and tache_code = b.tache_code) as mobile_tache_url,"+
	 "  'old' as new_flag," +
	 "	'' as deal_obj_type,-1 as deal_obj_id,bo_state,ROUND(TO_NUMBER(b.limit_date - sysdate) * 24 * 60)||'' over_time_flag," +
	 " limit_date, "+
	 " to_char(plan_finish_time, 'yyyy-MM-dd') plan_finish_time "+
	 " from "+
	 " ( "+
	 "	select a.flow_id, a.bo_title, a.flow_name, a.bo_id, a.bo_type_id,  a.bo_state_name, a.state_date, "+
	 " b.wo_state,b.wo_id,a.bo_state,b.tache_code," +
	 " (select bwt.limit_date from bpm_wo_task bwt where bwt.wo_state='READY' and bwt.wo_id=b.wo_id)limit_date,a.plan_finish_time	" +
	 " from BPM_BO_FLOW_INST a,bpm_wo_task b where a.bo_id=b.bo_id and a.create_oper_id = ?  "+
	 "    and a.bo_type_id in(select bf.bo_type_id from bpm_bo_flow_def bf where bf.type='"+BPMConsts.FLOW_DEF_TYPE_TASK+"')"+
     "    and b.wo_id in(select max(t.wo_id) from bpm_wo_task t where t.bo_id=a.bo_id)"+
	 "	union "+
	 "	select a.flow_id, a.bo_title, a.flow_name, a.bo_id, a.bo_type_id,  a.bo_state_name, a.state_date, "+
	 "	'ARCHIVED' as wo_state, "+
	 "	-1 wo_id,a.bo_state,(select tache_code from bpm_bo_flow_tache bbft where bbft.bo_type_id=a.bo_type_id and seq_no=1)tache_code," +
	 "  null limit_date,a.plan_finish_time " +
	 " from L_BPM_BO_FLOW_INST a where a.create_oper_id = ?  "+
	 "    and a.bo_type_id in(select bf.bo_type_id from bpm_bo_flow_def bf where bf.type='"+BPMConsts.FLOW_DEF_TYPE_TASK+"')"+
	 " ) b where 1=1 ";
	
	 String MyReqOrder__queryTaskInfo =" union"+
			"	       select 	"+
			"	        a.task_inst_id flow_id,	"+
			"	       a.task_name bo_title,	"+
			"	       b.task_type_name  flow_name,	"+
			"	       a.task_inst_id ||'' bo_id,	"+
			"	       b.task_type_id bo_type_id,	"+
			"	       decode(a.state,'001','新建','002','处理中','003','处理失败','004','审批不通过','005','完成') bo_state_name,"+
			"	       '' wo_state,	"+
			"	       a.task_inst_id wo_id, 	"+
			"	       a.create_date state_date,	"+
			"	       decode(a.state,'001','新建','002','处理中','003','处理失败','004','审批不通过','005','完成') tate,	"+
			"	       b.task_url tache_url	,''mobile_tache_url," +
			"			'new' as new_flag,  "+
			"          a.deal_obj_type," +
			"          a.deal_obj_id,decode(a.state,'001','NEW','002','ACTIVE','003','INVALID','004','INVALID','005','END') bo_state,'1' over_time_flag,  "+
			"      	   '' plan_finish_time " +
			"	       from act_task_inst  a,	"+
			"	       task_type      b,	"+
			"	       staff_position c,	"+
			"	       staff          d 	"+
			"	       where a.task_type_id = b.task_type_id 	"+
			"	       and a.create_pos_id = c.staff_pos_id 	"+
			"	       and c.party_role_id = d.staff_id 	"+
			"	       and b.task_sub_type='01'"+
			"	       and c.staff_pos_id=?	";
	
	 String MyReqOrder__queryTaskInfo_l =" union"+
		"	       select 	"+
		"	        a.task_inst_id flow_id,	"+
		"	       a.task_name bo_title,	"+
		"	       b.task_type_name  flow_name,	"+
		"	       a.task_inst_id ||'' bo_id,	"+
		"	       b.task_type_id bo_type_id,	"+
		"	       decode(a.state,'001','新建','002','处理中','003','处理失败','004','审批不通过','005','完成') bo_state_name,"+
		"	       '' wo_state,	"+
		"	       a.task_inst_id wo_id, 	"+
		"	       a.create_date state_date,	"+
		"	       decode(a.state,'001','新建','002','处理中','003','处理失败','004','审批不通过','005','完成') tate,	"+
		"	       b.task_url tache_url,''mobile_tache_url,	"+
		"			'new' as new_flag, "+
		" 			'POS' deal_obj_type," +
		"		   a.create_pos_id as deal_obj_id,decode(a.state,'001','NEW','002','ACTIVE','003','INVALID','004','INVALID','005','END') bo_state,'1' over_time_flag,   "+
		"      	   '' plan_finish_time " +
		"	       from l_act_task_inst  a,	"+
		"	       task_type      b,	"+
		"	       staff_position c,	"+
		"	       staff          d 	"+
		"	       where a.task_type_id = b.task_type_id 	"+
		"	       and a.create_pos_id = c.staff_pos_id 	"+
		"	       and c.party_role_id = d.staff_id 	"+
		"	       and b.task_sub_type='01'"+
		"	       and c.staff_pos_id=?	";
	 
	final String MyOrderBo_findDisVerifyOrderNew = 
		"select distinct a.wo_id,\n" +
		"                a.task_title,\n" + 
		"                a.flow_id,\n" + 
		"                b.bo_type_id,\n" +
		"                f.bo_url,\n" +
		"                a.state_date,\n" +
		"                '待审核' as tate,\n" + 
		"                b.create_oper_id,\n" + 
		"                b.bo_id,\n" + 
		"                g.tache_url,\n" + 
		"                g.tache_code,\n" +
		"                b.flow_name,\n" + 
		"                b.bo_title,\n" +
		"                c.staff_name,\n" +
		"                c.staff_code\n" +
		"  from bpm_wo_task a, bpm_bo_flow_inst b,staff c,bpm_wo_task_exec d,bpm_bo_flow_def f, BPM_BO_FLOW_TACHE g \n" + 
		" where a.wo_id = d.wo_id and c.staff_id = b.create_oper_id\n" + 
		"   and a.flow_id = b.flow_id\n" + 
		"   and b.bo_type_id = f.bo_type_id\n" + 
		"    and  ( " +
		"		( d.task_worker = ? and d.worker_type = 'STAFF' )  or  " +
		"		( d.task_worker in ( select role_id from staff_role where party_role_id = ? ) and d.worker_type = 'ROLE' )  " +
		"	)  \n" +
		"   and d.tache_code != '" + BPMConsts.TACHE_CODE_NEW_REQ + "'\n"+
		"   and g.work_type = 'AUDIT'\n"+
		"   and g.bo_type_id = f.bo_type_id \n"+
		"   and g.tache_code = d.tache_code \n"+
		"   and g.tache_code = a.tache_code \n"+
		"   and a.wo_state = 'READY'\n ";
	
	final String MyOrderBo_queryDisHandleOrderNew   = 
			/*"select distinct a.wo_id,d.worker_type,d.task_exec_id,\n" +
			"                a.task_title,\n" + 
			"                a.flow_id,\n" + 
			"                b.bo_type_id,\n" +
			"                b.bo_state_name,\n" +
			"                to_char(a.create_date,'yyyy-mm-dd hh24:mi') create_date,\n" +
	        "                (select count(*) from bpm_wo_task_exec e where e.bo_id=b.bo_id) tache_code_nums,"+
			"                to_char(a.limit_date,'YYYY-MM-DD HH24:mi') limit_date,\n" +
			"                ROUND(TO_NUMBER(a.limit_date - sysdate)*24*60) over_time_flag, \n"+
			"                f.bo_url,\n" +
			"                decode(g.work_type,'AUDIT','待审核','待处理') as tate,\n" + //将待审核工单和待处理工单页面整合
			"                b.create_oper_id,\n" + 
			"                b.bo_id,\n" + 
			"                g.tache_url,\n" + 
			"                g.tache_code,g.tache_name,\n" +
			"                b.flow_name,\n" + 
			"                b.bo_title,\n" +
			"                c.staff_name,\n" +
			"                (select attr_value from staff_attr sa where sa.staff_id = c.staff_id and sa.attr_id = 30000043) contact_tel,\n" +
			"                g.work_type,\n" +
			"                c.staff_code,\n" +
			"                'oldTask' flow_type "+//区分新老流程
			"  from bpm_wo_task a, bpm_bo_flow_inst b,staff c, bpm_wo_task_exec d, bpm_bo_flow_def f, BPM_BO_FLOW_TACHE g \n" + 
			"  where a.wo_id = d.wo_id and c.staff_id = b.create_oper_id \n" + 
			"    and a.flow_id = b.flow_id\n" + 
			"    and b.bo_type_id = f.bo_type_id and (d.exec_state<>'FINISH' or d.exec_state is null)\n" +
			"    and  ( " +
			"		( d.task_worker = ? and d.worker_type = 'STAFF' )  or  " +
			"		( d.task_worker in ( select role_id from staff_role where party_role_id = ? ) and d.worker_type = 'ROLE' ) or " +
	        "		( d.task_worker in ( select org_id from staff_position where state='00A' and party_role_id=? ) and d.worker_type = 'ORG' ) or " +
	        "		( d.task_worker in ( select group_id from audit_staff_group where staff_id = ? ) and d.worker_type = 'TEAM' )  " +
			"	)  \n" +
			"   and ((d.tache_code != '" + BPMConsts.TACHE_CODE_NEW_REQ + "' and a.wo_state = 'READY') or (d.tache_code = '" + BPMConsts.TACHE_CODE_NEW_REQ + "' and a.wo_state in('FAIL','READY')))" +
			"   and g.bo_type_id = f.bo_type_id \n"+
			"   and g.tache_code = d.tache_code \n"+
			"   and g.tache_code = a.tache_code \n"+
			"   and f.type = '"+BPMConsts.FLOW_DEF_TYPE_TASK+"' \n " ; */

			"select distinct a.wo_id, " +
			"                d.worker_type, " + 
			"                d.task_exec_id, " + 
			"                a.task_title, " + 
			"                a.flow_id, " + 
			"                b.bo_type_id, " + 
			"                b.bo_state_name, " + 
			"                to_char(a.create_date, 'yyyy-mm-dd hh24:mi') create_date, " + 
			"                to_char(b.plan_finish_time, 'yyyy-mm-dd') plan_finish_time, " +
			"                (select count(*) " + 
			"                   from bpm_wo_task_exec e " + 
			"                  where e.bo_id = b.bo_id) tache_code_nums, " + 
			"                to_char(a.limit_date, 'YYYY-MM-DD HH24:mi') limit_date, " + 
			"                ROUND(TO_NUMBER(a.limit_date - sysdate) * 24 * 60) over_time_flag, " + 
			"                f.bo_url, " + 
			"                decode(g.work_type, 'AUDIT', '待审核', '待处理') as tate, " + 
			"                b.create_oper_id, " + 
			"                b.bo_id, " + 
			"                g.tache_url, " + 
			"                g.tache_code, " + 
			"                g.tache_name, " + 
			"                b.flow_name, " + 
			"                b.bo_title, " + 
			"                c.staff_name, " + 
			"                (select attr_value " + 
			"                   from staff_attr sa " + 
			"                  where sa.staff_id = c.staff_id " + 
			"                    and sa.attr_id = 30000043) contact_tel, " + 
			"                g.work_type, " + 
			"                c.staff_code, " + 
			"                'oldTask' flow_type,g.mobile_tache_url " + 
			"  from bpm_wo_task       a, " + 
			"       bpm_bo_flow_inst  b, " + 
			"       staff             c, " + 
			"       bpm_wo_task_exec  d, " + 
			"       bpm_bo_flow_def   f, " + 
			"       BPM_BO_FLOW_TACHE g " + 
			" where a.wo_id = d.wo_id " + 
			"   and c.staff_id = b.create_oper_id " + 
			"   and a.flow_id = b.flow_id " + 
			"   and b.bo_type_id = f.bo_type_id " + 
			"   and (d.exec_state <> 'FINISH' or d.exec_state is null) " + 
			"   and ((d.task_worker = ? and d.worker_type = 'STAFF') or " + 
			" " + 
			"       (d.task_worker in (select org_id " + 
			"                             from staff_position " + 
			"                            where state = '00A' " + 
			"                              and staff_pos_id = ?) and " + 
			"       d.worker_type = 'ORG') ) " + 
			"   and ((d.tache_code != '" + BPMConsts.TACHE_CODE_NEW_REQ + "' and a.wo_state = 'READY') or " + 
			"       (d.tache_code = '" + BPMConsts.TACHE_CODE_NEW_REQ + "' and a.wo_state in ('FAIL', 'READY'))) " + 
			"   and g.bo_type_id = f.bo_type_id " + 
			"   and g.tache_code = d.tache_code " + 
			"   and g.tache_code = a.tache_code" +
			"   and f.type = '"+BPMConsts.FLOW_DEF_TYPE_TASK+"'" ;

	
	
	//查询没有流程的任务 liu.yuming 2013-07-21
	final String MyOrderBo_queryTaskInfo = 
		"   union " +
		"   select a.task_inst_id wo_id,'' worker_type,-1 task_ecex_id, \n"+
		"   a.task_name task_title, \n"+
		"   -1 flow_id, \n"+
		"   b.task_type_id bo_type_id,\n"+
		"	decode(a.state,'001','新建','002','处理中','003','处理失败','004','审批不通过','005','完成') bo_state_name,"+
		"   to_char(a.create_date,'yyyy-mm-dd hh24:mi') create_date,\n"+
		"   0 tache_code_nums,"+
		"   '' limit_date,\n"+
		"   1 over_time_flag ,\n" +
		"   '' bo_url,\n"+
		"   decode(a.state,'001','新建','002','处理中','003','处理失败','004','审批不通过','005','完成') tate,\n"+
		"   a.create_pos_id create_oper_id,\n"+
		"   a.task_inst_id||'' bo_id,\n"+
		"   task_url tache_url,\n"+
		"   '' tache_code,'' tache_name,\n"+
		"   b.task_type_name flow_name,\n"+
		"   b.task_type_name bo_title,\n"+
		"   d.staff_name,\n"+
		"   (select attr_value from staff_attr sa where sa.staff_id = d.staff_id and sa.attr_id = 30000043) contact_tel,\n" +
		"   '' work_type,\n"+
		"   d.staff_code,\n"+
		"   'newTask' flow_type "+//区分新老流程
		"   from act_task_inst  a,\n"+
		"   task_type      b,\n"+
		"   staff_position c,\n"+
		"   staff          d \n"+
		//"   staff_position e \n"+
		"   where a.task_type_id = b.task_type_id \n"+
		"   and a.create_pos_id = c.staff_pos_id(+) \n"+
		"   and c.party_role_id = d.staff_id(+) \n"+
		"   and a.STATE!='005'"+
		//"   and a.deal_obj_id = e.staff_pos_id " +
		//"   and a.deal_obj_type='POS' \n"+
		"   and not exists \n"+//and f.pos_type_id in(20,21,22,23,24,25,26,27,28,29,30,40)
		"   (select 1 from bpm_bo_flow_def p where b.task_type_id = p.bo_type_id) \n"+
		"   and (1=2 or exists (select 1 from staff_position e where a.deal_obj_id = e.staff_pos_id and a.deal_obj_type='POS' and e.staff_pos_id=? ) "+
		"   or exists (select 1 from staff e,staff_position f where e.staff_id=f.party_role_id and f.org_id=a.deal_obj_id and a.deal_obj_type='ORG' and f.staff_pos_id=? " +
		"                and not exists(select 1 from MKT_WORK_SHEET s where s.WAVE_ID = a.TASK_INST_ID) )" +
		"	 OR EXISTS ( SELECT 1 FROM MKT_WORK_SHEET S WHERE S.WAVE_ID = a.TASK_INST_ID AND A.TASK_TYPE_ID = 10000000 and S.ORDER_DEAL_TYPE = 'POS' AND S.DEAL_STAFF_POS_ID = ? AND S.DEAL_STATE IN (?)" +
		"						 AND TO_DATE(TO_CHAR(S.FINISH_LIMIT_DATE,'yyyy-MM-dd'),'yyyy-MM-dd') >= TO_DATE(TO_CHAR(SYSDATE,'yyyy-MM-dd'),'yyyy-MM-dd'))" +
		"	 OR EXISTS (SELECT 1 FROM MKT_WORK_SHEET S,staff_position f WHERE S.WAVE_ID = a.TASK_INST_ID AND A.TASK_TYPE_ID = 10000000 and f.pos_type_id in(@#) and S.ORDER_DEAL_TYPE = 'ORG' and f.org_id= S.DEAL_STAFF_POS_ID AND f.staff_pos_id = ? AND S.DEAL_STATE IN (?)" +
		"						 AND TO_DATE(TO_CHAR(S.FINISH_LIMIT_DATE,'yyyy-MM-dd'),'yyyy-MM-dd') >= TO_DATE(TO_CHAR(SYSDATE,'yyyy-MM-dd'),'yyyy-MM-dd'))" +
		"   ) ";
	

	//我已处理的任务（未归档）
	final String MyOrderBo_queryHandleOrderNew =
			"select distinct a.wo_id,d.worker_type,d.task_exex_id,\n" +
			"                a.task_title,\n" + 
			"                a.flow_id,\n" + 
			"                b.flow_name,\n" + 
			"                b.bo_type_id,\n" +
			"                a.state_date,\n" +
			"                '已处理' as tate,\n" + 
			"                b.create_oper_id,\n" + 
			"                b.bo_id,\n" + 
			"                g.tache_url,\n" + 
			"                g.tache_code,g.tache_name,\n" +
			"                b.bo_title,\n" +
			"                c.staff_name,\n" +
			"                (select attr_value from staff_attr sa where sa.staff_id = c.staff_id and sa.attr_id = 30000043) contact_tel,\n" +
			"                c.staff_code\n" +
			"  from bpm_wo_task a, bpm_bo_flow_inst b,staff c,bpm_wo_task_exec d, BPM_BO_FLOW_TACHE g \n" + 
			" where a.wo_id = d.wo_id and c.staff_id = b.create_oper_id \n" + 
			"   and a.flow_id = b.flow_id\n" + 
			"   and g.tache_code = d.tache_code \n"+
			"   and g.tache_code = a.tache_code \n"+
			"   and a.bo_type_id = g.bo_type_id \n"+
			"   and b.bo_type_id = g.bo_type_id \n"+
			"   and d.bo_type_id = g.bo_type_id \n"+
			"   and a.work_type = 'DEAL' \n"+
			"    and  ( " +
			"		( d.task_worker = ? and d.worker_type = 'STAFF' )  or  " +
	        "		( d.task_worker in ( select role_id from staff_role where party_role_id = ? ) and d.worker_type = 'ROLE' ) or " +
	        "		( d.task_worker in ( select org_id from staff_position where state='00A' and party_role_id=? ) and d.worker_type = 'ORG' ) or " +
	        "		( d.task_worker in ( select group_id from audit_staff_group where staff_id = ? ) and d.worker_type = 'TEAM' )  " +
			"	)  \n" +
			"   and a.resp_oper_id = ? \n"+
			"   and a.wo_state in ('FINISH','FAIL')\n";
	
	final String MyOrderBo_queryVerifyOrderNew =
			"select distinct a.wo_id,\n" +
			"                a.task_title,\n" + 
			"                a.flow_id,\n" + 
			"                b.flow_name,\n" + 
			"                b.bo_type_id,\n" +
			"                a.state_date,\n" +
			"	             decode(wo_state,'READY','就绪','FAIL','审核未通过','DEALING','正在处理','FINISH','审批中','ARCHIVED','完成','WITHDRAW','撤单') tate,	\n"+
			"	             b.bo_state_name,	\n"+
			"                b.create_oper_id,\n" + 
			"                b.bo_id,\n" + 
			"                g.tache_url,g.mobile_tache_url,\n" + 
			"                g.tache_code,\n" +
			"                c.staff_name,\n" +
			"                c.staff_code,\n" +
			"                b.bo_title,\n" +
			"                'old' as new_flag,b.bo_state, \n" +
			" to_char(b.plan_finish_time, 'yyyy-MM-dd') plan_finish_time," +
			" (select bwt.limit_date\n" + 
			"                                   from bpm_wo_task bwt\n" + 
			"                                  where bwt.flow_id = a.flow_id\n" + 
			"                                    and bwt.wo_state = 'READY' and rownum=1) limit_date, "+
			"ROUND(TO_NUMBER((select nvl(bwt.limit_date,\n" +
			"                                            to_date('2099-12-31',\n" + 
			"                                                    'yyyy-mm-dd hh24:mi:ss'))\n" + 
			"                                   from bpm_wo_task bwt\n" + 
			"                                  where bwt.flow_id = a.flow_id\n" + 
			"                                    and bwt.wo_state = 'READY' and rownum=1) - sysdate) * 24 * 60)||'' over_time_flag"+
			"  from bpm_wo_task a, bpm_bo_flow_inst b,staff c, bpm_wo_task_exec d, BPM_BO_FLOW_TACHE g \n" + 
			" where a.wo_id = d.wo_id and c.staff_id = b.create_oper_id \n" + 
			"   and a.flow_id = b.flow_id\n" + 
			"   and g.tache_code = d.tache_code \n"+
			"   and g.tache_code = a.tache_code \n"+
			"   and a.bo_type_id = g.bo_type_id \n"+
			"   and b.bo_type_id = g.bo_type_id \n"+
			"   and d.bo_type_id = g.bo_type_id \n"+
			//"   and a.work_type = 'AUDIT' \n"+
			"    and  ( " +
			"		( d.task_worker = ? and d.worker_type = 'STAFF' )  or  " +
	        "		( d.task_worker in ( select role_id from staff_role where party_role_id = ? ) and d.worker_type = 'ROLE' ) or " +
	        "		( d.task_worker in ( select org_id from staff_position where state='00A' and party_role_id=? ) and d.worker_type = 'ORG' ) or " +
	        "		( d.task_worker in ( select group_id from audit_staff_group where staff_id = ? ) and d.worker_type = 'TEAM' )  " +
			"	)  \n" +
			"   and a.resp_oper_id = ? \n"+
			"   and a.wo_state in ('FINISH','FAIL')\n"+
		    "   and b.bo_type_id in(select bf.bo_type_id from bpm_bo_flow_def bf where bf.type='"+BPMConsts.FLOW_DEF_TYPE_TASK+"') " +
		    "	and d.wo_id = (select max(be.wo_id)  from bpm_wo_task_exec be where be.flow_id = d.flow_id and be.task_worker =?)";

	final String MyOrderBo_queryVerifyOrderHisNew =
			"select distinct a.wo_id,\n" +
			"                a.task_title,\n" + 
			"                a.flow_id,\n" + 
			"                b.flow_name,\n" + 
			"                b.bo_type_id,\n" +
			"                a.state_date,\n" +
			"	             decode(wo_state,'READY','就绪','FAIL','审核未通过','DEALING','正在处理','FINISH','完成','ARCHIVED','完成','WITHDRAW','撤单') tate,	\n"+
			"	             b.bo_state_name,	\n"+
			"                b.create_oper_id,\n" + 
			"                b.bo_id,\n" + 
			"                g.tache_url,g.mobile_tache_url,\n" + 
			"                g.tache_code,\n" + 
			"                (select c.staff_name  from staff c where c.STAFF_ID = b.create_oper_id and rownum=1) staff_name,\n" + 
			"                (select c.staff_code  from staff c where c.STAFF_ID = b.create_oper_id and rownum=1) staff_code,\n" + 
			"                b.bo_title,\n" +
			"                'old' as new_flag,b.bo_state," +
			" to_char(b.plan_finish_time, 'yyyy-MM-dd') plan_finish_time,null limit_date, "+
			"				 '1'over_time_flag \n" +
			"  from L_BPM_WO_TASK a, L_BPM_BO_FLOW_INST b,L_BPM_WO_TASK_EXEC d, BPM_BO_FLOW_TACHE g \n" + 
			" where a.wo_id = d.wo_id \n" + 
			"   and a.flow_id = b.flow_id\n" + 
			"   and g.tache_code = d.tache_code \n"+
			"   and g.tache_code = a.tache_code \n"+
			"   and a.bo_type_id = g.bo_type_id \n"+
			"   and b.bo_type_id = g.bo_type_id \n"+
			"   and d.bo_type_id = g.bo_type_id \n"+
			//"   and a.work_type = 'AUDIT' \n"+
			"    and  ( " +
			"		( d.task_worker = ? and d.worker_type = 'STAFF' )  or  " +
	        "		( d.task_worker in ( select role_id from staff_role where party_role_id = ? ) and d.worker_type = 'ROLE' ) or " +
	        "		( d.task_worker in ( select org_id from staff_position where state='00A' and party_role_id=? ) and d.worker_type = 'ORG' ) or " +
	        "		( d.task_worker in ( select group_id from audit_staff_group where staff_id = ? ) and d.worker_type = 'TEAM' )  " +
			"	)  \n" +
			"   and a.resp_oper_id = ? \n"+
			"   and a.wo_state in ('FINISH','FAIL')\n"+
		    "   and b.bo_type_id in(select bf.bo_type_id from bpm_bo_flow_def bf where bf.type='"+BPMConsts.FLOW_DEF_TYPE_TASK+"') " +
		    "	and d.wo_id = (select max(be.wo_id)  from L_BPM_WO_TASK_EXEC be where be.flow_id = d.flow_id and be.task_worker = ? )";

	
	final String MyOrderBo_queryVerifiedTaskInfo =
			"	select 	\n"+
			"	 a.task_inst_id wo_id,   	\n"+
			"	 a.task_name task_title, 	\n"+
			"	 a.task_inst_id flow_id, 	\n"+
			"	 b.task_type_name  flow_name, 	\n"+
			"	 b.task_type_id bo_type_id,	\n"+
			"	 a.create_date state_date, 	\n"+
			"	 decode(a.state,'001','新建','002','处理中','003','处理失败','004','审批不通过','005','完成') tate, 	\n"+
			"    decode(a.state,'001','新建','002','处理中','003','处理失败','004','审批不通过','005','完成') bo_state_name ,"+
			"	 a.create_pos_id as create_oper_id,	\n"+
			"	 a.task_inst_id ||'' bo_id, 	\n"+
			"	 b.task_url tache_url,''mobile_tache_url,  	\n"+
			"	 '' tache_code,	\n"+
			"	 d.staff_name,	\n"+
			"	 d.staff_code, 	\n"+
			"	 a.task_name bo_title,	\n"+
			"    'new' as new_flag,decode(a.state,'001','NEW','002','ACTIVE','003','INVALID','004','INVALID','005','END') bo_state," +
			"  	 '' plan_finish_time,  \n"+
			"  	 null limit_date,  \n"+
			"    '1'over_time_flag \n" +
			"	from l_act_task_inst  a,  	\n"+
			"	 task_type      b,  	\n"+
			"	 staff_position c,  	\n"+
			"	 staff          d   	\n"+
			"	where a.task_type_id = b.task_type_id   	\n"+
			"	 and a.create_pos_id = c.staff_pos_id   	\n"+
			"	 and c.party_role_id = d.staff_id   	\n"+
			"	 and not exists (select 1 from bpm_bo_flow_def p where b.task_type_id = p.bo_type_id) 	\n"+
			"	 and exists (select 1 from staff_position e where a.deal_obj_id = e.staff_pos_id and 	\n"+
			"	 a.deal_obj_type='POS' and e.staff_pos_id=? ) 	\n";

	
	final String MyOrderBo_queryHandleOrderHisNew =
			"select distinct a.wo_id,\n" +
			"                a.task_title,\n" + 
			"                b.flow_name,\n" + 
			"                a.flow_id,\n" + 
			"                b.bo_type_id,\n" +
			"                a.state_date,\n" +
			"                '已处理' as tate,\n" + 
			"                b.create_oper_id,\n" + 
			"                b.bo_id,\n" + 
			"                g.tache_url,\n" + 
			"                g.tache_code,\n" + 
			"                b.bo_title,\n" +
			"                c.staff_name,\n" +
			"                c.staff_code\n" +
			"  from L_BPM_WO_TASK a, L_BPM_BO_FLOW_INST b,staff c,L_BPM_WO_TASK_EXEC d, BPM_BO_FLOW_TACHE g \n" + 
			" where a.wo_id = d.wo_id and c.staff_id = b.create_oper_id\n" + 
			"   and a.flow_id = b.flow_id\n" + 
			"   and g.tache_code = d.tache_code \n"+
			"   and g.tache_code = a.tache_code \n"+
			"   and a.bo_type_id = g.bo_type_id \n"+
			"   and b.bo_type_id = g.bo_type_id \n"+
			"   and d.bo_type_id = g.bo_type_id \n"+
			"   and a.work_type = 'DEAL' \n"+
			"    and  ( " +
			"		( d.task_worker = ? and d.worker_type = 'STAFF' )  or  " +
	        "		( d.task_worker in ( select role_id from staff_role where party_role_id = ? ) and d.worker_type = 'ROLE' ) or " +
	        "		( d.task_worker in ( select org_id from staff_position where state='00A' and party_role_id=? ) and d.worker_type = 'ORG' ) or " +
	        "		( d.task_worker in ( select group_id from audit_staff_group where staff_id = ? ) and d.worker_type = 'TEAM' )  " +
			"	)  \n" +
			"   and a.resp_oper_id = ? \n"+
			"   and a.wo_state in ('FINISH','FAIL')\n";

    final String MyOrderBo_queryOrderListNew =
            "select  a.wo_id,\n" +
                    "                a.task_title,\n" +
                    "                a.flow_id,\n" +
                    "                b.flow_name,\n" +
                    "                b.bo_type_id,\n" +
                    "                a.state_date,\n" +
                    "	             decode(wo_state,'READY','就绪','FAIL','审核未通过','DEALING','正在处理','FINISH','审批中','ARCHIVED','完成','WITHDRAW','撤单') tate,	\n"+
                    "	             b.bo_state_name,	\n"+
                    "                b.create_oper_id,\n" +
                    "                b.bo_id,\n" +
                    "                g.tache_url,\n" +
                    "                g.tache_code,\n" +
                    "                c.staff_name,\n" +
                    "                c.staff_code,\n" +
                    "                b.bo_title,\n" +
                    "                'old' as new_flag,b.bo_state \n" +
                    "  from bpm_wo_task a, bpm_bo_flow_inst b,staff c, bpm_wo_task_exec d, BPM_BO_FLOW_TACHE g \n" +
                    " where a.wo_id = d.wo_id and c.staff_id = b.create_oper_id \n" +
                    "   and a.flow_id = b.flow_id\n" +
                    "   and g.tache_code = d.tache_code \n"+
                    "   and g.tache_code = a.tache_code \n"+
                    "   and a.bo_type_id = g.bo_type_id \n"+
                    "   and b.bo_type_id = g.bo_type_id \n"+
                    "   and d.bo_type_id = g.bo_type_id \n"+
                    "   and b.bo_type_id in(select bf.bo_type_id from bpm_bo_flow_def bf where bf.type='"+BPMConsts.FLOW_DEF_TYPE_TASK+"')" +
                    "    and a.wo_id in (select max(t.wo_id)\n" +
                    "                             from bpm_wo_task t \n" +
                    "                            where t.wo_state='READY' and  t.bo_id = b.bo_id)";

    final String MyOrderBo_queryOrderListNew_l =
            "select a.wo_id,\n" +
                    "                a.task_title,\n" +
                    "                a.flow_id,\n" +
                    "                b.flow_name,\n" +
                    "                b.bo_type_id,\n" +
                    "                a.state_date,\n" +
                    "	             decode(wo_state,'READY','就绪','FAIL','审核未通过','DEALING','正在处理','FINISH','完成','ARCHIVED','完成','WITHDRAW','撤单') tate,	\n"+
                    "	             b.bo_state_name,	\n"+
                    "                b.create_oper_id,\n" +
                    "                b.bo_id,\n" +
                    "                g.tache_url,\n" +
                    "                g.tache_code,\n" +
                    "                (select c.staff_name  from staff c where c.STAFF_ID = b.create_oper_id and rownum=1) staff_name,\n" +
                    "                (select c.staff_code  from staff c where c.STAFF_ID = b.create_oper_id and rownum=1) staff_code,\n" +
                    "                b.bo_title,\n" +
                    "                'old' as new_flag,b.bo_state \n" +
                    "  from L_BPM_WO_TASK a, L_BPM_BO_FLOW_INST b,L_BPM_WO_TASK_EXEC d, BPM_BO_FLOW_TACHE g \n" +
                    " where a.wo_id = d.wo_id \n" +
                    "   and a.flow_id = b.flow_id\n" +
                    "   and g.tache_code = d.tache_code \n"+
                    "   and g.tache_code = a.tache_code \n"+
                    "   and a.bo_type_id = g.bo_type_id \n"+
                    "   and b.bo_type_id = g.bo_type_id \n"+
                    "   and d.bo_type_id = g.bo_type_id \n"+
                    "   and b.bo_type_id in(select bf.bo_type_id from bpm_bo_flow_def bf where bf.type='"+BPMConsts.FLOW_DEF_TYPE_TASK+"')" +
                    "  and a.wo_id in (select max(t.wo_id)\n" +
                    "                             from l_bpm_wo_task t \n" +
                    "                            where t.bo_id = b.bo_id)";


    final String MyOrderBo_queryOrderList_TaskInfo =
            "	select 	\n"+
                    "	 a.task_inst_id wo_id,   	\n"+
                    "	 a.task_name task_title, 	\n"+
                    "	 a.task_inst_id flow_id, 	\n"+
                    "	 b.task_type_name  flow_name, 	\n"+
                    "	 b.task_type_id bo_type_id,	\n"+
                    "	 a.create_date state_date, 	\n"+
                    "	 decode(a.state,'001','新建','002','处理中','003','处理失败','004','审批不通过','005','完成') tate, 	\n"+
                    "    decode(a.state,'001','新建','002','处理中','003','处理失败','004','审批不通过','005','完成') bo_state_name ,"+
                    "	 a.create_pos_id as create_oper_id,	\n"+
                    "	 a.task_inst_id ||'' bo_id, 	\n"+
                    "	 b.task_url tache_url,  	\n"+
                    "	 '' tache_code,	\n"+
                    "	 d.staff_name,	\n"+
                    "	 d.staff_code, 	\n"+
                    "	 a.task_name bo_title,	\n"+
                    "    'new' as new_flag,decode(a.state,'001','NEW','002','ACTIVE','003','INVALID','004','INVALID','005','END') bo_state \n" +
                    "	from l_act_task_inst  a,  	\n"+
                    "	 task_type      b,  	\n"+
                    "	 staff_position c,  	\n"+
                    "	 staff          d   	\n"+
                    "	where a.task_type_id = b.task_type_id   	\n"+
                    "	 and a.create_pos_id = c.staff_pos_id   	\n"+
                    "	 and c.party_role_id = d.staff_id   	\n"+
                    "	 and not exists (select 1 from bpm_bo_flow_def p where b.task_type_id = p.bo_type_id) 	\n";
}
