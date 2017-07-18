package com.ztesoft.sql.mysql;

import org.springframework.stereotype.Service;

import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.sql.Sql;

/**
 * BPM流程模块sql
 *
 */
@Service(value = "MYSQL_BPM_SQLS_LOCAL")
public class BPM_SQLS_LOCAL extends Sql {
	
	// 与我相关的(我处理的,我审批中,我审批不通过,我审批通过但是流程还没结束的)服务申请单(在途)
	public String getMyServiceApply =
			"select distinct c.apply_id," +
			"                c.apply_code," + 
			"                c.apply_name," + 
			"                c.service_id," + 
			"                c.service_type," + 
			"                c.eff_date," + 
			"                c.exp_date," + 
			"                c.apply_date," + 
			"                c.apply_staff_id," + 
			"                c.apply_reason," + 
			"                (select d.staff_name" + 
			"                   from dm_staff d" + 
			"                  where d.staff_id = c.apply_staff_id) staff_name," + 
			"                c.org_id," + 
			"                (select o.org_name from dm_organization o where o.org_id = c.org_id) org_name," + 
			"                c.state," + 
			"                c.state_date," + 
			"				 a.wo_id, " +
			"                a.flow_id, " + 
			"                b.bo_type_id, " + 
			"                b.bo_state_name, " + 
			"                date_format(a.create_date, '%y-%m-%d %t') create_date, " + 
			"                g.tache_code, " + 
			"                g.tache_name, " + 
			"                b.flow_name, " + 
			"                b.bo_state, " + 
			"                b.bo_title " + 
			"  from bpm_wo_task       a, " + 
			"       bpm_bo_flow_inst  b, " + 
			"       s_service_apply   c, " + 
			"       bpm_wo_task_exec  d, " + 
			"       bpm_bo_flow_def   f, " + 
			"       bpm_bo_flow_tache g  " + 
			" where a.wo_id = d.wo_id " + 
			"   and c.apply_id = b.bo_id " + 
			"   and a.flow_id = b.flow_id " + 
			"   and b.bo_type_id = f.bo_type_id " + 
			"   and (d.exec_state not in('FINISH','FAIL') or d.exec_state is null) " + 
			"   and ((d.task_worker = ? and d.worker_type = 'STAFF') or " + //员工
	        "		(d.task_worker in (select role_id " +
	        "							  from dm_staff_role where staff_id = ? ) and d.worker_type = 'ROLE' ) or " + //角色
			"       (d.task_worker in (select org_id " + 
			"                             from dm_staff " + 
			"                            where state = '00A' " + 
			"                              and staff_id = ?) and " + 
			"       d.worker_type = 'ORG') ) " +                       //部门
			"   and ((d.tache_code != '" + BPMConsts.TACHE_CODE_NEW_REQ + "' and a.wo_state = 'READY') or " + 
			"       (d.tache_code = '" + BPMConsts.TACHE_CODE_NEW_REQ + "' and a.wo_state in ('FAIL', 'READY'))) " + 
			"   and g.bo_type_id = f.bo_type_id " + 
			"   and g.tache_code = d.tache_code " + 
			"   and g.tache_code = a.tache_code" +
			"   and f.type = '"+BPMConsts.FLOW_DEF_TYPE_TASK+"'" ;
	
		// 与我相关的(我审批不通过,我审批通过)服务申请单(在途)
		public String getMyServiceApplySuccFail =
				"select distinct c.apply_id," +
				"                c.apply_code," + 
				"                c.apply_name," + 
				"                c.service_id," + 
				"                c.service_type," + 
				"                c.eff_date," + 
				"                c.exp_date," + 
				"                c.apply_date," + 
				"                c.apply_staff_id," + 
				"                c.apply_reason," + 
				"                (select d.staff_name" + 
				"                   from dm_staff d" + 
				"                  where d.staff_id = c.apply_staff_id) staff_name," + 
				"                c.org_id," + 
				"                (select o.org_name from dm_organization o where o.org_id = c.org_id) org_name," + 
				"                c.state," + 
				"                c.state_date," + 
				"				 a.wo_id, " +
				"                a.flow_id, " + 
				"                b.bo_type_id, " + 
				"                b.bo_state_name, " + 
				"                date_format(a.create_date, '%y-%m-%d %t') create_date, " + 
				"                g.tache_code, " + 
				"                g.tache_name, " + 
				"                b.flow_name, " + 
				"                b.bo_state, " + 
				"                b.bo_title " + 
				"  from bpm_wo_task       a, " + 
				"       bpm_bo_flow_inst  b, " + 
				"       s_service_apply   c, " + 
				"       bpm_wo_task_exec  d, " + 
				"       bpm_bo_flow_def   f, " + 
				"       bpm_bo_flow_tache g  " + 
				" where a.wo_id = d.wo_id " + 
				"   and c.apply_id = b.bo_id " + 
				"   and a.flow_id = b.flow_id " + 
				"   and b.bo_type_id = f.bo_type_id " + 
				"   and a.wo_state in ('FINISH','FAIL') " +
				"   and a.resp_oper_id = ? "+
				"   and g.bo_type_id = f.bo_type_id " + 
				"   and g.tache_code = d.tache_code " + 
				"   and g.tache_code = a.tache_code" +
				"   and f.type = '"+BPMConsts.FLOW_DEF_TYPE_TASK+"'" +
				"	and d.wo_id = (select max(be.wo_id) from bpm_wo_task be where be.flow_id = b.flow_id and be.resp_oper_id = ? )";//我处理过的,最近一次记录
		
	
	// 与我相关的(我审批不通过,我审批通过)服务申请单(历史)
	public String getMyServiceApplyHis =
			"select distinct c.apply_id," +
			"                c.apply_code," + 
			"                c.apply_name," + 
			"                c.service_id," + 
			"                c.service_type," + 
			"                c.eff_date," + 
			"                c.exp_date," + 
			"                c.apply_date," + 
			"                c.apply_staff_id," + 
			"                c.apply_reason," + 
			"                (select d.staff_name" + 
			"                   from dm_staff d" + 
			"                  where d.staff_id = c.apply_staff_id) staff_name," + 
			"                c.org_id," + 
			"                (select o.org_name from dm_organization o where o.org_id = c.org_id) org_name," + 
			"                c.state," + 
			"                c.state_date," + 
			"				 a.wo_id, " +
			"                a.flow_id, " + 
			"                b.bo_type_id, " + 
			"                b.bo_state_name, " + 
			"                date_format(a.create_date, '%y-%m-%d %t') create_date, " + 
			"                g.tache_code, " + 
			"                g.tache_name, " + 
			"                b.flow_name, " + 
			"                b.bo_state, " + 
			"                b.bo_title " + 
			"  from l_bpm_wo_task       a, " + 
			"       l_bpm_bo_flow_inst  b, " + 
			"       s_service_apply   c, " + 
			"       l_bpm_wo_task_exec  d, " + 
			"       bpm_bo_flow_def   f, " + 
			"       bpm_bo_flow_tache g  " + 
			" where a.wo_id = d.wo_id " + 
			"   and c.apply_id = b.bo_id " + 
			"   and a.flow_id = b.flow_id " + 
			"   and b.bo_type_id = f.bo_type_id " + 
			"   and a.wo_state in ('FINISH','FAIL') " +
			"   and a.resp_oper_id = ? "+
			"   and g.bo_type_id = f.bo_type_id " + 
			"   and g.tache_code = d.tache_code " + 
			"   and g.tache_code = a.tache_code" +
			"   and f.type = '"+BPMConsts.FLOW_DEF_TYPE_TASK+"'" +
			"	and d.wo_id = (select max(be.wo_id) from l_bpm_wo_task be where be.flow_id = b.flow_id and be.resp_oper_id = ? )";//我处理过的,最近一次记录
	
	//我申请的
	public String getMyApply = 
			"select a.flow_id," +
			"       a.bo_title," + 
			"       a.flow_name," + 
			"       a.bo_id," + 
			"       a.bo_type_id," + 
			"       a.bo_state_name," + 
			"       a.wo_state," + 
			"       a.wo_id," + 
			"       case" + 
			"         when wo_state = 'READY' then" + 
			"          '就绪'" + 
			"         when wo_state = 'FAIL' then" + 
			"          '审核未通过'" + 
			"         when wo_state = 'DEALING' or wo_state = 'NEW' then" + 
			"          '正在处理'" + 
			"         when wo_state = 'FINISH' then" + 
			"          '审批中'" + 
			"         when wo_state = 'ARCHIVED' then" + 
			"          '完成'" + 
			"       end as tate," + 
			"		a.cur_worker as audit_staff_name," +
			"		a.cur_worker_org as audit_org," +
			"       a.bo_state," + 
			"       b.apply_id," + 
			"       b.apply_code," + 
			"       b.apply_name," + 
			"       b.service_id," + 
			"       b.service_type," + 
			"       b.eff_date," + 
			"       b.exp_date," + 
			"       b.apply_date," + 
			"       b.apply_staff_id," + 
			"       b.org_id," + 
			"       b.state," + 
			"       b.state_date" + 
			"  from (select a.flow_id," + 
			"               a.bo_title," + 
			"               a.flow_name," + 
			"               a.bo_id," + 
			"               a.bo_type_id," + 
			"               a.bo_state_name," + 
			"               a.state_date," + 
			"               b.wo_state," + 
			"               b.wo_id," + 
			"               a.bo_state," + 
			"               b.tache_code," + 
			"				case when a.bo_state_name = '审核未通过，需重新提交' " +
			"					then (select s.staff_name from bpm_wo_task_exec exe,dm_staff s " +
			"						where exe.wo_id=b.wo_id and exe.task_worker = s.staff_id) " +
			"					else (select role_name from dm_role r where r.role_id = c.task_worker) end as cur_worker," +
			"				case when a.bo_state_name = '审核未通过，需重新提交' " +
			"					then (select o.org_name from bpm_wo_task_exec exe,dm_staff s,dm_organization o " +
			"						where exe.wo_id=b.wo_id and exe.task_worker = s.staff_id and s.org_id = o.org_id limit 0, 1) " +
			"					else '-' end as cur_worker_org" +
			"          from bpm_bo_flow_inst a, bpm_wo_task b, bpm_wo_task_exec c" + 
			"         where a.flow_id = b.flow_id" + 
			"           and b.wo_id = c.wo_id" + 
			"           and a.create_oper_id = ?" + 
			"           and b.wo_id in (select max(t.wo_id)" + 
			"                             from bpm_wo_task t" + 
			"                            where t.flow_id = a.flow_id)" + 
			"        union" + 
			"        select a.flow_id," + 
			"               a.bo_title," + 
			"               a.flow_name," + 
			"               a.bo_id," + 
			"               a.bo_type_id," + 
			"               a.bo_state_name," + 
			"               a.state_date," + 
			"               'ARCHIVED' as wo_state," + 
			"               -1 wo_id," + 
			"               a.bo_state," + 
			"               (select tache_code" + 
			"                  from bpm_bo_flow_tache bbft" + 
			"                 where bbft.bo_type_id = a.bo_type_id" + 
			"                   and seq_no = 1) tache_code," + 
			"				(select staff_name from dm_staff s where s.staff_id = b.resp_oper_id limit 0, 1) as cur_worker," +
			"				(select o.org_name from dm_staff s,dm_organization o where " +
			"				s.org_id = o.org_id and s.staff_id = b.resp_oper_id limit 0, 1) as cur_worker_org" +
			"          from l_bpm_bo_flow_inst a, l_bpm_wo_task b, l_bpm_wo_task_exec c" + 
			"         where a.flow_id = b.flow_id" +
			"           and b.wo_id = c.wo_id" + 
			"           and a.create_oper_id = ?" + 
			"           and b.wo_id in (select max(t.wo_id)" + 
			"                             from l_bpm_wo_task t" + 
			"                            where t.flow_id = a.flow_id)" + 
			"       ) a, s_service_apply b" + 
			" where a.bo_id = b.apply_id" + 
			"   and b.apply_staff_id = ?";
}