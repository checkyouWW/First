package com.ztesoft.dubbo.sp.workspace.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.common.util.SessionHelper;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.mgr.service.BpmService;
import com.ztesoft.crmpub.bpm.vo.model.MBpmBoFlowInst;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;
import com.ztesoft.crmpub.bpm.vo.model.his.MLBpmBoFlowInst;
import com.ztesoft.dubbo.sp.data.vo.SServiceApply;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.ResultMap;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.sql.Sql;

import comx.order.inf.IKeyValues;
import appfrm.app.util.ListUtil;
import appfrm.app.vo.IVO;
import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;
import spring.util.SpringContextUtil;

/**
 * Created by kam on 2016/9/8.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class WorkSpaceDAO {
	
    /* 我的数据服务 */
    public RpcPageModel myDataServiceList(Map params) {
        PageModel result;
        int pageIndex = Integer.parseInt(MapUtils.getString(params, "page", "1"));
        int pageSize = Integer.parseInt(MapUtils.getString(params, "rows", "10"));

        String dataName = MapUtils.getString(params, "dataName");
        String dataCode = MapUtils.getString(params, "dataCode");
//        String acctType = MapUtils.getString(params, "acctType");
//        String dataAcctStart = MapUtils.getString(params, "dataAcctStart");
//        String dataAcctEnd = MapUtils.getString(params, "dataAcctEnd");

        String extractFreq = MapUtils.getString(params, "extractFreq");
        
        String whereClause = "";
        List queryParams = new ArrayList();
        queryParams.add(SessionHelper.getStaffId());
        queryParams.add(SessionHelper.getTeamId());

        if (StringUtil.isNotEmpty(dataName)) {
            whereClause += " and cda.data_name like ? ";
            queryParams.add("%" + dataName + "%");
        }

        if (StringUtil.isNotEmpty(dataCode)) {
            whereClause += " and cda.data_code like ? ";
            queryParams.add("%" + dataCode + "%");
        }

        if(StringUtil.isNotEmpty(extractFreq)) {
        	whereClause += " and cds.extract_freq = ? ";
        	queryParams.add(extractFreq);
        }
        
//        if (StringUtil.isNotEmpty(acctType)) {
//            whereClause += " and ssislog.acct_type like ? ";
//            queryParams.add(acctType);
//        }
//
//        if (StringUtil.isNotEmpty(dataAcctStart)) {
//            whereClause += " and ssislog.data_acct >= ? ";
//            queryParams.add(dataAcctStart.replaceAll("-", ""));
//        }
//
//        if (StringUtil.isNotEmpty(dataAcctEnd)) {
//            whereClause += " and ssislog.data_acct <= ? ";
//            queryParams.add(dataAcctEnd.replaceAll("-", ""));
//        }

        String sql = Sql.S_DATA_SQLS.get("SQL_MY_DATA_SERVICE") + whereClause;
        result = DAO.queryForPageModel(sql, pageSize, pageIndex, queryParams);
        return PageModelConverter.pageModelToRpc(result);
    }

    /* 我的任务服务 */
    public RpcPageModel myTaskServiceList(Map params) {
        PageModel result;
        int pageIndex = Integer.parseInt(MapUtils.getString(params, "page", "1"));
        int pageSize = Integer.parseInt(MapUtils.getString(params, "rows", "10"));

        String text = MapUtils.getString(params, "text");
        String id = MapUtils.getString(params, "id");

        String whereClause = "";
        List queryParams = new ArrayList();
        queryParams.add(SessionHelper.getStaffId());
        queryParams.add(SessionHelper.getTeamId());

        if (StringUtil.isNotEmpty(text)) {
            whereClause += " and (a.task_code like ? or a.task_name like ?) ";
            queryParams.add("%" + text + "%");
            queryParams.add("%" + text + "%");
        }

        if (StringUtil.isNotEmpty(id)) {
            whereClause += " and b.service_id = ? ";
            queryParams.add( id );
        }


        List<Map> tenant = DAO.queryForMap(Sql.S_DATA_SQLS.get("SQL_MY_TENANT_INFO") , SessionHelper.getStaffId(), SessionHelper.getTeamId());
        String tenantName = "";
        String tenentCode = "";
        if (tenant.size() > 0) {
            tenantName = (String) tenant.get(0).get("tenant_name");
            tenentCode = (String) tenant.get(0).get("tenant_code");
            if (tenantName == null) tenantName = "";
            if (tenentCode == null) tenentCode = "";
        }

        String sql = Sql.S_DATA_SQLS.get("MY_TASK_SERVICE_SQL") + whereClause;
        result = DAO.queryForPageModel(sql, pageSize, pageIndex, queryParams);
        for (Object m : result.getList()) {
            ((Map) m).put("tenent_code", tenentCode);
            ((Map) m).put("tenant_name", tenantName);
        }
        return PageModelConverter.pageModelToRpc(result);
    }


    public static int getOccur(String src, String find) {
        int o = 0;
        int index = -1;
        while ((index = src.indexOf(find, index)) > -1) {
            ++index;
            ++o;
        }
        return o;
    }

    public RpcPageModel myTeamMembers(Map params) {
        PageModel result = new PageModel();
        int pageIndex = Integer.parseInt(MapUtils.getString(params, "page", "1"));
        int pageSize = Integer.parseInt(MapUtils.getString(params, "rows", "10"));

        /* 搜索姓名或工号 */
        String text = MapUtils.getString(params, "text");
        String teamId = SessionHelper.getTeamId();

        String whereClause = "";
        List queryParams = new ArrayList();

        queryParams.add(teamId);

        if (StringUtil.isNotEmpty(text)) {
            whereClause += " and (ds.staff_name like ? or ds.staff_code like ?) ";
            queryParams.add("%" + text + "%");
            queryParams.add("%" + text + "%");
        }

        result = DAO.queryForPageModel(Sql.S_DATA_SQLS.get("SQL_MY_TEAM") + whereClause, pageSize, pageIndex, queryParams);
        return PageModelConverter.pageModelToRpc(result);
    }

    public List myTeamMembers() {
        return DAO.queryForMap(Sql.S_DATA_SQLS.get("SQL_MY_TEAM_MENBER_SIMPLE"), SessionHelper.getTeamId());
    }

    public int getAdminNumber(String orgId) {
        String sql = Sql.S_DATA_SQLS.get("SQL_TEAM_ADMIN_NUMBER");
        return DAO.queryForMap(sql, orgId).size();
    }

    /* 检查当前账户是否该组织的管理员 */
    public boolean checkIsAdmin(String orgId, String staffId) {
        String whereClause = "";
        List queryParams = new ArrayList();

        String result;

        if (StringUtil.isNotEmpty(orgId)) {
            whereClause += " and mtm.org_id = ? ";
            queryParams.add(orgId);
        } else return false;

        if (StringUtil.isNotEmpty(staffId)) {
            whereClause += " and vs.staff_id = ? ";
            queryParams.add(staffId);
            result = DAO.querySingleValue(Sql.S_DATA_SQLS.get("SQL_CHECK_MY_TEAM_ADMIN") + whereClause, (String[]) queryParams.toArray(new String[queryParams.size()]));
            if (result.equals("T")) return true;
        }
        return false;
    }

    /* 检查某账户是否已存在于组织中 */
    public boolean checkExists(String orgId, String staffId) {
        String whereClause = "";
        List queryParams = new ArrayList();
        List result;

        if (StringUtil.isNotEmpty(orgId)) {
            whereClause += " and mtm.org_id = ? ";
            queryParams.add(orgId);
        } else return false;

        if (StringUtil.isNotEmpty(staffId)) {
            whereClause += " and vs.staff_id = ? ";
            queryParams.add(staffId);
            result = DAO.queryForMap(Sql.S_DATA_SQLS.get("SQL_CHECK_MY_TEAM_ADMIN") + whereClause, (String[]) queryParams.toArray(new String[queryParams.size()]));
            if (result.size() > 0) return true;
        }
        return false;
    }

    /**
     * 通过团队编号和真实工号来查找虚拟工号
     * @param teamId
     * @param staffId
     * @return
     */
    public String getVRStaffIdByStaffId(String teamId, String staffId) {
        String vrStaffId = DAO.querySingleValue(Sql.S_DATA_SQLS.get("SQL_GET_VRSTAFF_BY_STAFF"), new String[]{teamId, staffId});
        return vrStaffId;
    }

    /* 删除成员 */
    public void deleteMenbers(String teamId, String staffId) {
        DAO.update(Sql.S_DATA_SQLS.get("SQL_DELETE_MY_TEAM_MENBER"), teamId, staffId);
    }

    /* 设置管理员 */
    public void setAdmin(String isDirector, String teamId, String vrStaffId) {

        DAO.update(Sql.S_DATA_SQLS.get("SQL_REMOVE_ALL_TEAM_ADMIN"), teamId);
        DAO.update(Sql.S_DATA_SQLS.get("SQL_UPDATE_MY_TEAM_ADMIN"), new String[]{isDirector, teamId, vrStaffId});
    }

    /* 向团队中添加成员 */
    public void addMenber(String teamId, String vrStaffId) {
        DAO.update(Sql.S_DATA_SQLS.get("SQL_INSERT_MY_TEAM"), teamId, vrStaffId, "F");
    }


    /* 我的申请单 */
    public RpcPageModel myApplyList(Map params) {
        int pageIndex = Integer.parseInt(MapUtils.getString(params, "page", "1"));
        int pageSize = Integer.parseInt(MapUtils.getString(params, "rows", "10"));
        String staffId = SessionHelper.getStaffId();
        String teamId = SessionHelper.getTeamId();
        String type = MapUtils.getString(params, "type");
        String text = MapUtils.getString(params, "text");
        String auditState = MapUtils.getString(params, "auditState");

        String whereClause = "";
        List queryParams = new ArrayList();

        queryParams.add(staffId);
        queryParams.add(staffId);
        queryParams.add(staffId);
        String sql = Sql.BPM_SQLS_LOCAL.get("getMyApply");


        /*恢复申请模式*/
        if ("recover".equals(type)) {
            whereClause += " and ( b.state=? or b.state=? )";
            queryParams.add("00C");
            queryParams.add("00X");
            whereClause += " and b.service_id  in (select service_id from c_data_service where state='00A' ) ";
        }

        if (StringUtil.isNotEmpty(auditState)) {
            whereClause += " and a.bo_state=? ";
            queryParams.add(auditState);
        }
        
        if (StringUtil.isNotEmpty(teamId)) {
            whereClause += " and b.org_id = ? ";
            queryParams.add(teamId);
        }

        /* 申请单ID或名称 */
        if (StringUtil.isNotEmpty(text)) {
            whereClause += " and ( b.apply_code=? or b.apply_name like ? )";
            queryParams.add(text);
            queryParams.add("%" + text + "%");
        }
        
        whereClause += " order by a.flow_id desc";

     
        
        return PageModelConverter.pageModelToRpc(DAO.queryForPageModel(sql + whereClause, pageSize, pageIndex, queryParams));
    }

    public SServiceApply getApplyById(String applyId) {
    	SServiceApply apply = (SServiceApply) SServiceApply.getDAO().findById(applyId);
        return apply;
    }

    /* 取消申请 */
	public Map cancelApply(Map params) throws Exception {
        String staffId = SessionHelper.getStaffId();
        String applyId = MapUtils.getString(params, "applyId");

        SServiceApply apply = getApplyById(applyId);
        if (apply == null
                || !staffId.equals(apply.get("apply_staff_id")) )
            return new ResultMap().failed().msg("申请单无效");
        if (!KeyValues.APPLY_STATE_AUDIT.equals(apply.get("state"))) return new ResultMap().failed().msg("只能取消审核中的申请单");
        
        // 组装流程data
        BpmService bpmService = (BpmService) SpringContextUtil.getBean("bpmService");
        List attrList = new ArrayList();
        params.put("attrList", attrList);
        params.put("action", MsgConsts.ACTION_WO_WITHDRAW);
        params.put("resp_content", "取消申请");
        params.put("bo_id", applyId);

        bpmService.audit(params);

        return new ResultMap().success();
    }

    /* 恢复申请 */
    public Map recoverApply(Map params) throws Exception {
        String staffId = SessionHelper.getStaffId();
        String applyId = MapUtils.getString(params, "applyId");
        
        SServiceApply apply = getApplyById(applyId);
        
        if (apply == null
                || !staffId.equals(apply.get("apply_staff_id")) )
            return new ResultMap().failed().msg("申请单无效");
        
        String state = (String) apply.get("state");
        if (!KeyValues.APPLY_STATE_WITHDRAW.equals(state) && !KeyValues.APPLY_STATE_FAIL.equals(state))
            return new ResultMap().failed().msg("只能撤恢复已撤销或审批失败的申请单");
        
        //如果是撤销的
		if(KeyValues.APPLY_STATE_WITHDRAW.equals(state)){
			List<IVO> list = MLBpmBoFlowInst.getDAO().query(" bo_id = ? and bo_type_id = ?", applyId, KeyValues.BO_TYPE_ID_SERVICE_APPLY);
			if(ListUtil.isEmpty(list)){
				return new ResultMap().failed().msg("申请单无效");
			}
			MLBpmBoFlowInst flowInst = (MLBpmBoFlowInst) list.get(0);
			//恢复工单的历史记录
			flowInst.resumeOrder();
			params.putAll(flowInst.saveToMap());
		}
		
		else if(KeyValues.APPLY_STATE_FAIL.equals(state)){
			List<IVO> list = MBpmBoFlowInst.getDAO().query(" bo_id = ? and bo_type_id = ?", applyId, KeyValues.BO_TYPE_ID_SERVICE_APPLY);
			if(ListUtil.isEmpty(list)){
				return new ResultMap().failed().msg("申请单无效");
			}
			MBpmBoFlowInst flowInst = (MBpmBoFlowInst) list.get(0);
			params.putAll(flowInst.saveToMap());
		}
		else {
			return new ResultMap().failed().msg("申请单校验状态有误");
		}
		
		String flow_id = (String) params.get("flow_id");
		//这个sql是根据create_date asc排序的
		List<MBpmWoTask> taskList = (List) MBpmWoTask.getDAO().query("flow_id = ?", flow_id);
		
		if(!ListUtil.isEmpty(taskList)){
			MBpmWoTask task = null;
			for(int i = taskList.size()-1; i>=0; i--){
				MBpmWoTask newFirstTask = taskList.get(i);
				if(BPMConsts.TACHE_CODE_NEW_REQ.equals(newFirstTask.tache_code)){
					task = newFirstTask;
					break;
				}
			}
			params.put("wo_id", task.wo_id);
			params.put("wo_type_id", task.wo_type_id);
			params.put("tache_code", task.tache_code);
		}
		
		//重新提交
		String action = MsgConsts.ACTION_WO_FINISH;
		BpmService bpmService = (BpmService) SpringContextUtil.getBean("bpmService");
		List attrList = new ArrayList();
		params.put("attrList", attrList);
		params.put("action", action);
		params.put("resp_content", "恢复申请");
		params.put("bo_id", apply.apply_id);
		
		bpmService.audit(params);
		
		//更新申请单
		if(apply != null){
			apply.set("state", KeyValues.APPLY_STATE_AUDIT);
			apply.set("state_date", DateUtil.getFormatedDateTime());
			String[] othReqSupportfields = (String[]) apply.updateFieldSet.toArray(new String[] {});
			SServiceApply.getDAO().updateParmamFieldsByIdSQL(othReqSupportfields).update(apply);
		}
		
        return new ResultMap().success();
    }
    
    public Map revokeApply(Map params) throws Exception {
        String staffId = SessionHelper.getStaffId();
        String applyId = MapUtils.getString(params, "apply_id");
        String boState = MapUtils.getString(params, "bo_state");

        SServiceApply apply = getApplyById(applyId);
        if (apply == null
                || !staffId.equals(apply.get("apply_staff_id")) )
            return new ResultMap().failed().msg("申请单无效");
        if (!KeyValues.APPLY_STATE_SUCCESS .equals(apply.get("state")) || !KeyValues.BO_STATE_END.equals(boState))  {
        	return new ResultMap().failed().msg("只能撤销审核通过的申请单");
        }else {
        	String curDate = DateUtil.getFormatedDateTime();
			DAO.update("update s_service_apply set state = ?, state_date = ? where apply_id = ? ", new String[]{KeyValues.APPLY_STATE_AUDIT,curDate,applyId});
        }
        	
        
        Map flowData = new HashMap();
		String flow_id = SeqUtil.getSeq("BPM_BO_FLOW_INST", "flow_id");
		flowData.put("bo_type_id", KeyValues.BO_TYPE_ID_SERVICE_CANCEL);
		flowData.put("bo_id", apply.apply_id);
		flowData.put("flow_id", flow_id);
		flowData.put("autoFinishBeginWO", "true");
		
		String action_type = IKeyValues.ACTION_TYPE_A;
		String bo_title = apply.apply_name;
		BpmService service = (BpmService) SpringContextUtil.getBean("bpmService");
		boolean is_success = service.startFlow(flowData, action_type, bo_title);
		
		if(is_success) {
			return new ResultMap().success();
		}else {
			return new ResultMap().failed().msg("申请失败");
		}
    }
}
