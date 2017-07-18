package com.ztesoft.dubbo.mp.audit.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import spring.util.SpringContextUtil;
import appfrm.resource.dao.impl.DAO;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.SessionHelper;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.crm.business.common.utils.ListUtil;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.mgr.service.BpmService;
import com.ztesoft.dubbo.sp.data.service.DataApplyService;
import com.ztesoft.dubbo.sp.data.vo.SDataDispatch;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.sql.Sql;

@Repository
@SuppressWarnings({"unchecked","rawtypes" })
public class AuditMgrDao {
	
	@Autowired
	BpmService bpmService;

	/**
	 * 审批信息
	 * @param param
	 * @return
	 * @throws Exception 
	 */
	public Map auditApplyInfo(Map param) throws Exception {
		Map result = new HashMap();
		//判断是否是审批人员
		String staffId = SessionHelper.getStaffId();
		String isAuditSql = " select count(*) from dm_staff_role where staff_id = ? and role_id = ? ";
		String isAudit = DAO.querySingleValue(isAuditSql, new String[]{staffId, "2"});
		if(StringUtils.isNotBlank(isAudit) && Integer.parseInt(isAudit)>0) {
		}else {
			result.put("res", false);
			result.put("res_mess","不是审批人员不能审批");
			return result;
		}
		String apply_id = StringUtil.getStrValue(param, "apply_id");
		if(StringUtils.isNotBlank(apply_id)) {
			String applyStaffId = DAO.querySingleValue("select apply_staff_id from s_service_apply where apply_id = ? ", new String[]{apply_id});
			if(StringUtils.isNotBlank(applyStaffId) && applyStaffId.trim().equals(staffId.trim())) {
				result.put("res", false);
				result.put("res_mess","申请人和审批人员不能为同一个人");
				return result;
			}
		}else {
			result.put("res", false);
			result.put("res_mess","获取申请信息出错");
			return result;
		}
		
		
		String service_type = StringUtil.getStrValue(param, "service_type");
		String type = StringUtil.getStrValue(param, "type"); //type==0 不通过 type==1 通过
		String audit_info = StringUtil.getStrValue(param, "audit_info");
		String bo_type_id = StringUtil.getStrValue(param, "bo_type_id");
		
		String action = MsgConsts.ACTION_WO_FINISH;
		//type==0 不通过 type==1 通过
		if("0".equals(type)) {
			action = MsgConsts.ACTION_WO_FAIL;
		}else if("1".equals(type)) {
			Map r = checkApplyInfo(param);
			if(Const.getStrValue(r, "res").equals("1")) {
				r.put("res", false);
				return r;
			}
		}
		
		// 组装流程data
		List attrList = new ArrayList();
		param.put("attrList", attrList);
		param.put("action", action);
		param.put("resp_content", audit_info);
		param.put("bo_id", apply_id);
		param.put("bo_type_id", bo_type_id);

		boolean flag = true;
		Map res = bpmService.audit(param);
        if(!Const.getStrValue(res, "resCode").equals("0")) {
        	flag = false;
        }
		
		//写日志表
		String insertSql = Sql.SYS_SQLS.get("INSERT_M_APPLY_AUDIT_LOG");
		String currentTime = DateUtil.formatDate(DateUtil.current(), DateUtil.DATE_TIME_FORMAT);
		DAO.update(insertSql, new String[]{apply_id, service_type, SessionHelper.getStaffId(), audit_info, type, currentTime});
		
		result.put("res", flag);
		if(flag) {
			result.put("res_mess","操作成功");
		}else {
			result.put("res_mess","操作失败");
		}
		return result;
	}

	public Map checkApplyInfo(Map m) {
		Map res = new HashMap();
		String applyId = Const.getStrValue(m, "apply_id");
		String bo_type_id = Const.getStrValue(m, "bo_type_id");
		//获取apply信息，包含临时表中数据
		DataApplyService service = (DataApplyService) SpringContextUtil.getBean("dataApplyService");
		m.put("applyId", applyId);
		m.put("bo_type_id", bo_type_id);
		Map applyMap = service.getApplyData(m);
		String serviceType = Const.getStrValue(applyMap, "service_type");
		List<Map> dataInstList = (List<Map>) applyMap.get("ability_list");
		boolean isOk = true;
		if(!ListUtil.isEmpty(dataInstList)) {
			for(Map dataInst : dataInstList) {
				Map dispatchMap = (Map) dataInst.get("dispatch");
				
				//申请单类型为SECURITY，并且选择字段的脱敏算法为RSA的（特殊）没有分发
				if(serviceType.equals(KeyValues.SERVICE_TYPE_SECURITY)) {
					isOk = this.hasNoRsa(dataInst);
				}
				
				if(!isOk) {
					isOk = true;
					continue;
				}
				
				SDataDispatch disp = new SDataDispatch();
				disp.readFromMap(dispatchMap);
				if(StringUtils.isNotBlank(disp.dispatch_type) && disp.dispatch_type.equals("ftp")) {
					if(StringUtils.isNotBlank(disp.ftp_data_type) && disp.ftp_data_type.equals("pull")) {
						if(StringUtils.isBlank(disp.ftp_ip) || StringUtils.isBlank(disp.ftp_port) || StringUtils.isBlank(disp.ftp_user) ||
								StringUtils.isBlank(disp.ftp_password) || StringUtils.isBlank(disp.ftp_def_dir) || StringUtils.isBlank(disp.ftp_split)) {
							isOk = false;
							break;
						}
					}
				}
			}
			
		}
		
		if(isOk) {
			res.put("res","0");
			res.put("res_mess", "可以申请");
		}else {
			res.put("res","1");
			res.put("res_mess", "该申请单存在分发模式为FTP分发、模式为拉取模式的，且没有填写完成，请修改申请单");
		}
		return res;
	}
	
	//判断是否使用了rsa脱敏算法
	private boolean hasNoRsa(Map dataInst) {
		String dataInstId = Const.getStrValue(dataInst, "data_inst_id");
		String sql = "select count(*) from s_data_inst sdi,s_data_column sdc,c_algorithms ca "
				+ " where sdi.data_inst_id = sdc.data_inst_id "
				+ " and sdi.data_inst_id = ?  "
				+ " and ca.algorithm_id = sdc.alg_type "
				+ " and ca.type = 1";
		String count = DAO.querySingleValue(sql, new String[]{dataInstId});
		if(StringUtils.isNotBlank(count) && Integer.parseInt(count) > 0) {
			return false;
		}
		return true;
	}
	
}
