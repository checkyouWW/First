package com.ztesoft.dubbo.mp.task.bo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import spring.util.DBUtil;
import appfrm.app.vo.IVO;
import appfrm.resource.dao.impl.DAO;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.dao.DAOUtils;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.RSAUtil;
import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.common.util.SessionHelper;
import com.ztesoft.crm.business.common.utils.ListUtil;
import com.ztesoft.crmpub.bpm.mgr.service.BpmService;
import com.ztesoft.dubbo.mp.task.vo.AttachFile;
import com.ztesoft.dubbo.mp.task.vo.STaskInfo;
import com.ztesoft.dubbo.sp.data.util.ApplyUtil;
import com.ztesoft.dubbo.sp.data.vo.SDataDispatch;
import com.ztesoft.dubbo.sp.data.vo.SDataInst;
import com.ztesoft.dubbo.sp.data.vo.SServiceApply;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.sql.Sql;

import comx.order.inf.IKeyValues;
import exception.CoopException;

@Component
@SuppressWarnings({ "all" })
public class TaskBO {

	@Autowired
	private BpmService bpmService;
	
	public String getApplyCode() {
		// T + 12位填充序列 生成申请单号
		DecimalFormat df = new DecimalFormat("000000000000");
		String task_apply_code = "T" + df.format(Long.parseLong(SeqUtil.getSeq("S_SERVICE_APPLY", "TASK_APPLY_CODE")));
		return task_apply_code;
	}
	
	public String insertRelyFile(Map params) {
		String file_id = SeqUtil.getSeq("attach_file", "file_id");
		
		String action_type = Const.getStrValue(params, "action_type");
		String apply_id = Const.getStrValue(params, "apply_id");
		if(StringUtils.isNotBlank(action_type) && action_type.trim().equals("M") && StringUtils.isNotBlank(apply_id)) {
			//数据写入临时表
			Map fileMap = new HashMap();
			fileMap.put("file_id", file_id);
			fileMap.put("file_name", MapUtils.getString(params, "file_name"));
			fileMap.put("file_location_type","002");
			fileMap.put("file_location",MapUtils.getString(params, "file_location"));
			fileMap.put("table_name","s_task_info");
			fileMap.put("table_pk_name","task_id");
			fileMap.put("table_pk_value","-1");
			fileMap.put("create_date",DAOUtils.getFormatedDate());
			fileMap.put("status",KeyValues.STATE_00A);
			
			ApplyUtil au = new ApplyUtil();
			
			au.insertChangeNotify(fileMap, new AttachFile(), new AttachFile(), AttachFile.TABLE_CODE, file_id, apply_id, "A", "-1");
		}else {
			Map result = new HashMap();
			List sqlParams = new ArrayList();
			sqlParams.add(file_id);
			sqlParams.add(MapUtils.getString(params, "file_name"));
			sqlParams.add("002");
			sqlParams.add(MapUtils.getString(params, "file_location"));
			sqlParams.add("s_task_info");
			sqlParams.add("task_id");
			sqlParams.add("-1");
			sqlParams.add(DAOUtils.getFormatedDate());
			sqlParams.add(KeyValues.STATE_00A);
			DBUtil.getSimpleQuery().excuteUpdate(Sql.S_TASK_SQLS.get("INSERT_ATTACH_FILE_SQL"), sqlParams);
		}
		return file_id;
	}
	
	public boolean deleteRelyFile(List<Map> files) {
		if (ListUtil.isEmpty(files)) {
			return false;
		}
		List sqlParams = new ArrayList();
		for (Map file : files) {
			List list = new ArrayList();
			list.add(MapUtils.getString(file, "file_id"));
			sqlParams.add(list.toArray(new String[] {}));
		}
		DBUtil.getSimpleQuery().batchUpdate(Sql.S_TASK_SQLS.get("DELETE_ATTACH_FILE_SQL"), sqlParams);
		return true;
	}

	public boolean saveTask(Map params) throws Exception {
		Map apply = (Map) params.get("apply");
		Map task = (Map) params.get("task");
		Map data_inst = (Map) params.get("data_inst");
		Map dispatch = (Map) params.get("dispatch");
		List<Map> cols = (List<Map>) params.get("cols");
		
		String apply_id = SeqUtil.getSeq("S_SERVICE_APPLY", "APPLY_ID");
		String task_id = SeqUtil.getSeq("S_TASK_INFO", "TASK_ID");
		String data_inst_id = SeqUtil.getSeq("S_DATA_INST", "DATA_INST_ID");
		String dispatch_id = SeqUtil.getSeq("S_DATA_DISPATCH", "DISPATCH_ID");
		
		// 写申请单
		List sqlParams = new ArrayList();
		sqlParams.add(apply_id);
		sqlParams.add(MapUtils.getString(apply, "apply_code"));
		sqlParams.add(MapUtils.getString(apply, "apply_name"));
		sqlParams.add(MapUtils.getString(apply, "service_id"));
		sqlParams.add(KeyValues.SERVICE_TYPE_TASK);
		sqlParams.add(MapUtils.getString(apply, "eff_date"));
		sqlParams.add(MapUtils.getString(apply, "exp_date"));
		sqlParams.add(DAOUtils.getFormatedDate());
		sqlParams.add(SessionHelper.getStaffId());
		sqlParams.add(MapUtils.getString(apply, "apply_reason"));
		sqlParams.add(SessionHelper.getTeamId());
		sqlParams.add(KeyValues.APPLY_STATE_AUDIT);
		sqlParams.add(DAOUtils.getFormatedDate());
		DBUtil.getSimpleQuery().excuteUpdate(Sql.S_TASK_SQLS.get("INSERT_S_SERVICE_APPLY_SQL"), sqlParams);
		
		// 写任务实例、任务附件
		sqlParams.clear();
		sqlParams.add(task_id);
		sqlParams.add(apply_id);
		sqlParams.add(MapUtils.getString(task, "task_name"));
		sqlParams.add(MapUtils.getString(task, "task_code"));
		sqlParams.add(MapUtils.getString(task, "task_type"));
		sqlParams.add(MapUtils.getString(task, "prior"));
		sqlParams.add(MapUtils.getString(task, "run_command"));
		sqlParams.add(MapUtils.getString(task, "task_desc"));
		DBUtil.getSimpleQuery().excuteUpdate(Sql.S_TASK_SQLS.get("INSERT_S_TASK_INFO_SQL"), sqlParams);
		sqlParams.clear();
		List<Map> files = (List<Map>) task.get("files");
		for (Map file : files) {
			List list = new ArrayList();
			list.add(task_id);
			list.add(MapUtils.getString(file, "file_id"));
			sqlParams.add(list.toArray(new String[] {}));
		}
		DBUtil.getSimpleQuery().batchUpdate(Sql.S_TASK_SQLS.get("UPDATE_ATTACH_FILE_SQL"), sqlParams);
		
		// 写能力信息
		sqlParams.clear();	
		sqlParams.add(data_inst_id);
		sqlParams.add(apply_id);
		sqlParams.add(MapUtils.getString(data_inst, "data_code"));
		DBUtil.getSimpleQuery().excuteUpdate(Sql.S_TASK_SQLS.get("INSERT_S_DATA_INST_SQL"), sqlParams);
		
		// 字段信息、实例字段信息
		sqlParams.clear();	
		List sqlParams2 = new ArrayList();
		for (Map col : cols) {
			List list = new ArrayList();
			list.add(SeqUtil.getSeq("C_DATA_COLUMN", "COLUMN_ID"));
			list.add(task_id);
			list.add(MapUtils.getString(col, "column_name"));
			list.add(MapUtils.getString(col, "column_code"));
			list.add(MapUtils.getString(col, "column_type"));
			list.add(StringUtils.isEmpty(MapUtils.getString(col, "column_length")) ? null : MapUtils.getString(col, "column_length"));
			sqlParams.add(list.toArray(new String[] {}));
			
			List instList = new ArrayList();
			instList.add(SeqUtil.getSeq("S_DATA_COLUMN", "COLUMN_INST_ID"));
			instList.add(data_inst_id);
			instList.add(list.get(0));
			sqlParams2.add(instList.toArray(new String[] {}));
		}
		DBUtil.getSimpleQuery().batchUpdate(Sql.S_TASK_SQLS.get("INSERT_C_DATA_COLUMN_SQL"), sqlParams);
		DBUtil.getSimpleQuery().batchUpdate(Sql.S_TASK_SQLS.get("INSERT_S_DATA_COLUMN_SQL"), sqlParams2);
		
		// 写分发信息
		sqlParams.clear();
		sqlParams.add(dispatch_id);
		sqlParams.add(data_inst_id);
		sqlParams.add(MapUtils.getString(dispatch, "dispatch_type"));
		sqlParams.add(MapUtils.getString(dispatch, "ftp_data_type"));
		sqlParams.add(MapUtils.getString(dispatch, "ftp_ip"));
		sqlParams.add(MapUtils.getString(dispatch, "ftp_port"));
		sqlParams.add(MapUtils.getString(dispatch, "ftp_user"));
		sqlParams.add(MapUtils.getString(dispatch, "ftp_password"));
		sqlParams.add(MapUtils.getString(dispatch, "ftp_def_dir"));
		sqlParams.add(MapUtils.getString(dispatch, "ftp_split"));
		sqlParams.add(MapUtils.getString(dispatch, "import_type"));
		sqlParams.add(MapUtils.getString(dispatch, "create_table"));
		sqlParams.add(MapUtils.getString(dispatch, "sqoop_type"));
		sqlParams.add(MapUtils.getString(dispatch, "db_url"));
		sqlParams.add(MapUtils.getString(dispatch, "db_user"));
		sqlParams.add(MapUtils.getString(dispatch, "db_password"));
		sqlParams.add(MapUtils.getString(dispatch, "db_type"));
		sqlParams.add(MapUtils.getString(dispatch, "def_file_name"));
		DBUtil.getSimpleQuery().excuteUpdate(Sql.S_TASK_SQLS.get("INSERT_S_DATA_DISPATCH_SQL"), sqlParams);
		
		// 启动流程
		Map flowData = new HashMap();
		flowData.put("flow_id", SeqUtil.getSeq("BPM_BO_FLOW_INST", "flow_id"));
		flowData.put("bo_id", apply_id);
		flowData.put("bo_type_id", KeyValues.BO_TYPE_ID_SERVICE_APPLY);
		flowData.put("autoFinishBeginWO", "true");
		
		bpmService.startFlow(flowData, IKeyValues.ACTION_TYPE_A, MapUtils.getString(apply, "apply_name"));
		
		return true;
	}
	
	public Map queryTask(Map params) {
		String apply_id = MapUtils.getString(params, "apply_id");
		String task_id = MapUtils.getString(params, "task_id");
		
		if (StringUtils.isEmpty(apply_id) && StringUtils.isEmpty(task_id)) {
			return null;
		}
		
		Map result = new HashMap();
		StringBuffer cond = new StringBuffer();
		List sqlParams = new ArrayList();
		if (StringUtils.isNotEmpty(task_id)) {
			cond.append(" and task_id = ?");
			sqlParams.add(task_id);
		} else {
			cond.append(" and apply_id = ?");
			sqlParams.add(apply_id);
		}
		
		// 任务信息
		List<Map> list = DBUtil.getSimpleQuery()
				.queryForMapListBySql(Sql.S_TASK_SQLS.get("SELECT_S_TASK_INFO_SQL") + cond, sqlParams);
		Map task = list.get(0);
		
		// 申请单信息
		apply_id = StringUtils.isEmpty(apply_id) ? MapUtils.getString(task, "apply_id") : apply_id;
		list = DBUtil.getSimpleQuery().queryForMapListBySql2(Sql.S_TASK_SQLS.get("SELECT_S_SERVICE_APPLY_SQL"),
				new String[] { apply_id });
		if (list.size() == 0) {
			return null;
		}
		Map apply = list.get(0);
		
		// 任务附件
		task_id = StringUtils.isEmpty(task_id) ? MapUtils.getString(task, "task_id") : task_id;
		List<Map> files = DBUtil.getSimpleQuery().queryForMapListBySql2(Sql.S_TASK_SQLS.get("SELECT_ATTACH_FILE_SQL"),
				new String[] { "s_task_info", "task_id", task_id });
		for (Map file : files) {
			String new_file_name = MapUtils.getString(file, "new_file_name");
			file.put("file_name", new_file_name.substring(0, new_file_name.lastIndexOf("_")) + new_file_name.substring(new_file_name.lastIndexOf(".")));
		}
		task.put("files", files);
		
		// 能力信息
		list = DBUtil.getSimpleQuery().queryForMapListBySql2(Sql.S_TASK_SQLS.get("SELECT_S_DATA_INST_SQL"),
				new String[] { apply_id });
		Map data_inst = list.get(0);
		
		// 字段
		String data_inst_id = MapUtils.getString(data_inst, "data_inst_id");
		List<Map> cols = DBUtil.getSimpleQuery().queryForMapListBySql2(Sql.S_TASK_SQLS.get("SELECT_COL_SQL"),
				new String[] { data_inst_id });
		
		// 分发信息
		list = DBUtil.getSimpleQuery().queryForMapListBySql2(Sql.S_TASK_SQLS.get("SELECT_S_DATA_DISPATCH_SQL"),
				new String[] { data_inst_id });
		Map dispatch = list.get(0);
		
		// 调度信息
		String service_id = MapUtils.getString(apply, "service_id");
		Map schedule = new HashMap();
		String shedule_url = DBUtil.getSimpleQuery().querySingleValue(Sql.S_TASK_SQLS.get("SELECT_C_TASK_SCHEDULE_SQL"),
				new String[] { service_id });
		List<Map> task_params = DBUtil.getSimpleQuery().queryForMapListBySql2(Sql.S_TASK_SQLS.get("SELECT_C_TASK_PARAM_SQL"),
				new String[] { service_id });
		StringBuffer shedule_example = new StringBuffer(shedule_url);
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < task_params.size(); i++) {
			temp.append(MapUtils.getString(task_params.get(i), "param_code") + "=xxx&");
		}
		if (temp.length() > 0) {
			temp.deleteCharAt(temp.length() - 1);
			schedule.put("shedule_example", shedule_url + "?" + temp);
		}
		schedule.put("shedule_url", shedule_url);
		schedule.put("shedule_example", temp.length() > 0 ? shedule_url + "?" + temp : shedule_url);
		schedule.put("params", task_params);
		
		
		//从临时表中取数据
		//bpm_bo_flow_inst flow_id 只能有一个，多了是异常
		String isDataUpdated = Sql.S_DATA_SQLS.get("is_data_apply_update");
		/**
		 * 存在两种现象
		 * 1、bdsp新建申请后，bdmp修改，数据在临时表中，申请类型是（SERVICE_APPLY）
		 * 2、bdsp修改申请（bdmp修改），数据在临时表中，申请类型是（SERVICE_MOD）
		 * 3、修改和申请两种申请类型的申请流程，对同一个申请单来说，只能存在一个
		 * 修改流程的数据比新申请流程数据新，故获取修改流程产生临时数据，没有则获取申请流程的数据
		 */
		String flow_id = DAO.querySingleValue(isDataUpdated, new String[]{SServiceApply.TABLE_CODE,apply_id,apply_id,"apply_id",apply_id,apply_id,"ACTIVE",KeyValues.BO_TYPE_ID_SERVICE_MOD,SServiceApply.TABLE_CODE});
		if(StringUtils.isBlank(flow_id)) {
			flow_id = DAO.querySingleValue(isDataUpdated, new String[]{SServiceApply.TABLE_CODE,apply_id,apply_id,"apply_id",apply_id,apply_id,"ACTIVE",KeyValues.BO_TYPE_ID_SERVICE_APPLY,SServiceApply.TABLE_CODE});
		}
		
		if(StringUtils.isNotBlank(flow_id)) {
			ApplyUtil au = new ApplyUtil();
			//获取apply临时表数据，并覆盖
			au.getDataChangeNotify(apply, SServiceApply.TABLE_CODE, apply_id, apply_id, flow_id, new String[]{"M","A"});
			
			//获取task临时表数据，并覆盖
			au.getDataChangeNotify(task, STaskInfo.TABLE_CODE, task_id, apply_id, flow_id, new String[]{"M","A"});
			
			//获取data_inst临时表数据，并覆盖
			au.getDataChangeNotify(data_inst, SDataInst.TABLE_CODE, data_inst_id, apply_id, flow_id, new String[]{"M","A"});
			
			//获取dispatch临时表数据，并覆盖
			String dispatch_id = Const.getStrValue(dispatch, "dispatch_id");
			au.getDataChangeNotify(dispatch, SDataDispatch.TABLE_CODE, dispatch_id, data_inst_id, flow_id, new String[]{"M","A"});
			String newfp = Const.getStrValue(dispatch, "ftp_password");
			
			//获取cols临时表数据，并覆盖
			String getColsTmpSql = "select * from data_change_notify_column where data_inst_id = ? and flow_id = ? ";
			cols = DAO.queryForMap(getColsTmpSql, new String[]{data_inst_id,flow_id});
			
			//获取任务附件信息
			
			String getFileIdTmpSql = "select field_value as file_id from data_change_notify where table_name = ? and owner_inst_id = ? and flow_id = ? and action_type = ? and field_name = ? ";
			List<Map> fileIdsAdd = DAO.queryForMap(getFileIdTmpSql, new String[]{AttachFile.TABLE_CODE,apply_id,flow_id,"A","file_id"});
			List<Map> fileIdsDel = DAO.queryForMap(getFileIdTmpSql, new String[]{AttachFile.TABLE_CODE,apply_id,flow_id,"D","file_id"});
			
			//获取没有删除的file
			List<List<Map>> newList = new ArrayList<List<Map>>();
			newList.add(files);
			newList.add(fileIdsAdd);

			removeDeleteFile(files,fileIdsDel);
			removeDeleteFile(fileIdsAdd,fileIdsDel);
					
			
			//获取新增的附件
			if(!ListUtil.isEmpty(fileIdsAdd)) {
				for(Map file : fileIdsAdd) {
					String file_id = Const.getStrValue(file, "file_id");
					au.getDataChangeNotify(file, AttachFile.TABLE_CODE, file_id, apply_id, flow_id, new String[]{"A"});
					file.put("new_file_name", Const.getStrValue(file, "file_name"));
					files.add(file);
				}
			}
			
			for (Map file : files) {
				String new_file_name = MapUtils.getString(file, "new_file_name");
				file.put("file_name", new_file_name.substring(0, new_file_name.lastIndexOf("_")) + new_file_name.substring(new_file_name.lastIndexOf(".")));
			}
			task.put("files", files);
			
		}
		
		//解析密码
		if(dispatch!=null && !MapUtils.isEmpty(dispatch)) {
			String fp = MapUtils.getString(dispatch, "ftp_password");
			String dp = MapUtils.getString(dispatch, "db_password");
			if(StringUtils.isNotBlank(fp)) {
				dispatch.put("ftp_password", RSAUtil.decrypt(fp));
			}
			if(StringUtils.isNotBlank(dp)) {
				dispatch.put("db_password", RSAUtil.decrypt(dp));
			}
		}
		
		result.put("apply", apply);
		result.put("task", task);
		result.put("data_inst", data_inst);
		result.put("cols", cols);
		result.put("dispatch", dispatch);
		result.put("schedule", schedule);
		
		return result;
	}
	
	//修改任务申请（审批bdmp）
	public Map updateTaskApplyAudit(Map m) {
		ApplyUtil au = new ApplyUtil();
		Map result = new HashMap();
		Map<String,String> applyInfo = (Map) m.get("apply_info");
		String applyId = Const.getStrValue(applyInfo, "apply_id");
		String flowId = Const.getStrValue(m, "flow_id");
		List<Map> cols = (List<Map>) m.get("cols");
		
		//此操作为判断是否操作了临时表（data_change_notify_column,data_change_notify）
		Map appMc = new HashMap();
		appMc.put("action_type", "M");
		appMc.put("field_name", "apply_id");
		appMc.put("table_name", SServiceApply.TABLE_CODE);
		appMc.put("old_field_value", "");
		appMc.put("field_value", applyId);
		appMc.put("owner_inst_id", applyId);
		appMc.put("inst_id", applyId);
		appMc.put("tab_attr_id", "");
		appMc.put("notify_id", "");
		appMc.put("flow_id", flowId);
		au.insertMDataChangeNotify(appMc);
		
		
		String effDate = Const.getStrValue(applyInfo, "eff_date");
		String expDate = Const.getStrValue(applyInfo, "exp_date");
		au.insertMDataChangeNotify(flowId, applyId, applyId, SServiceApply.TABLE_CODE, "eff_date", effDate,"M");
		au.insertMDataChangeNotify(flowId, applyId, applyId, SServiceApply.TABLE_CODE, "exp_date", expDate,"M");
		
		//修改目标表分发配置（目前只用修改该项）
		String getDispatchIdSql = "select sdd.dispatch_id,sdi.data_inst_id from "
				+ " s_data_dispatch sdd,s_data_inst sdi,"
				+ " s_service_apply ssa "
				+ " where sdd.data_inst_id = sdi.data_inst_id "
				+" and ssa.apply_id = sdi.apply_id and sdi.apply_id = ? ";
		List<Map> idList = DAO.queryForMap(getDispatchIdSql, new String[]{applyId});
		if(ListUtil.isEmpty(idList)) {
			result.put("res", false);
			result.put("res_mess", "获取申请信息失败");
			return result;
		}
		Map idsMap = idList.get(0);
		String dispatchId = Const.getStrValue(idsMap, "dispatch_id");
		String dataInstId = Const.getStrValue(idsMap, "data_inst_id");
		
		//保存datainstId数据临时表
		Map dataMc = new HashMap();
		dataMc.put("action_type", "M");
		dataMc.put("field_name", "data_inst_id");
		dataMc.put("table_name", SDataInst.TABLE_CODE);
		dataMc.put("old_field_value", "");
		dataMc.put("field_value", dataInstId);
		dataMc.put("owner_inst_id", applyId);
		dataMc.put("inst_id", dataInstId);
		dataMc.put("tab_attr_id", "");
		dataMc.put("notify_id", "");
		dataMc.put("flow_id", flowId);
		au.insertMDataChangeNotify(dataMc);
		
		Map<String,String> dispatchInfo = (Map) m.get("dispatch_info");
		String dispatchType = Const.getStrValue(dispatchInfo, "dispatch_type");
		if(dispatchType.equals(KeyValues.DISPATCH_TYPE_FTP)) {
			String ftpDataType = Const.getStrValue(dispatchInfo, "ftp_data_type");
			if(ftpDataType.equals("pull")) { //只对拉取信息做修改
				String ftpIp = Const.getStrValue(dispatchInfo, "ftp_ip");
				String ftpPort = Const.getStrValue(dispatchInfo, "ftp_port");
				String ftpPassword = Const.getStrValue(dispatchInfo, "ftp_password");
				String ftpDefDir = Const.getStrValue(dispatchInfo, "ftp_def_dir");
				String ftpSplit = Const.getStrValue(dispatchInfo, "ftp_split");
				String ftpUser = Const.getStrValue(dispatchInfo, "ftp_user");
				String defFileName = Const.getStrValue(dispatchInfo, "def_file_name");
				au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "ftp_password", ftpPassword,"M");
				au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "ftp_ip", ftpIp,"M");
				au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "ftp_port", ftpPort,"M");
				au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "ftp_user", ftpUser,"M");
				au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "ftp_def_dir", ftpDefDir,"M");
				au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "ftp_split", ftpSplit,"M");
				au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "def_file_name", defFileName,"M");
			}
			
		}else if(dispatchType.equals(KeyValues.DISPATCH_TYPE_DB_IMPORT)) {
			String importType = MapUtils.getString(dispatchInfo, "import_type");
			String sqoopType = MapUtils.getString(dispatchInfo, "sqoop_type");
			au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "import_type", importType,"M");
			au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "sqoop_type", sqoopType,"M");
		}
		
		//获取taskId和dataInstId
		//任务申请中的s_task_info与s_service_apply，s_data_inst与s_service_apply是一一对应
		List<IVO> taskList = STaskInfo.getDAO().query(" apply_id = ? ", applyId);
		STaskInfo taskInfo = null;
		if(taskList!=null && !ListUtil.isEmpty(taskList)) {
			taskInfo = (STaskInfo) taskList.get(0);
		}else {
			throw new CoopException(CoopException.ERROR, "获取申请单信息出错" ,null);
		}
		List<IVO> dataInstList = SDataInst.getDAO().query(" apply_id = ? ", applyId);
		SDataInst dataInst = null;
		if(dataInstList!=null && !ListUtil.isEmpty(dataInstList)) {
			dataInst = (SDataInst) dataInstList.get(0);
		}else {
			throw new CoopException(CoopException.ERROR, "获取申请单信息出错", null);
		}
		
		//写入界面的字段列表信息
		//删除data_change_notify_column数据，data_change_notify_column表的数据与界面的一致，后面通过后移植复制
		DAO.update("delete from data_change_notify_column where flow_id = ? ", new String[]{flowId});
		for (Map col : cols) {
			//写字段信息c_data_column和s_data_column对应临时表数据 data_change_notify_column数据
			Map changeColumn = new HashMap();
			changeColumn.put("service_id",taskInfo.task_id);
			changeColumn.put("column_code",Const.getStrValue(col, "column_code"));
			changeColumn.put("column_name",Const.getStrValue(col, "column_name"));
			changeColumn.put("column_type",Const.getStrValue(col, "column_type"));
			changeColumn.put("column_length",StringUtils.isEmpty(MapUtils.getString(col, "column_length")) ? null : MapUtils.getString(col, "column_length"));
			changeColumn.put("data_inst_id", dataInst.data_inst_id);
			changeColumn.put("column_id", "-1");
			
			au.insertDataChangeColumnTask(changeColumn, flowId, applyId);
		}
		
		
		result.put("res", true);
		result.put("res_mess", "操作成功");
		return result;
	}

	//修改任务申请（bdsp）
	public Map updateTaskApplyInfo(Map params) throws Exception {
		ApplyUtil au = new ApplyUtil();
		Map resu = new HashMap();
		String applyId = Const.getStrValue(params, "apply_id");
		
		IVO vo = SServiceApply.getDAO().findById(applyId);
		SServiceApply apply = (SServiceApply) vo;
		if(!apply.state.equals(KeyValues.APPLY_STATE_SUCCESS)) {
			resu.put("success", false);
			resu.put("apply_id", apply.apply_id);
			resu.put("mess","审批通过的申请单才能修改");
			return resu;
		}else {
			String curDate = DateUtil.getFormatedDateTime();
			DAO.update("update s_service_apply set state = ?, state_date = ? where apply_id = ? ", new String[]{KeyValues.APPLY_STATE_AUDIT,curDate,applyId});
		}
		
		
		Map flowData = new HashMap();
		String flow_id = SeqUtil.getSeq("BPM_BO_FLOW_INST", "flow_id");
		flowData.put("bo_type_id", KeyValues.BO_TYPE_ID_SERVICE_MOD);
		flowData.put("bo_id", applyId);
		flowData.put("flow_id", flow_id);
		flowData.put("autoFinishBeginWO", "true");
		
		String action_type = IKeyValues.ACTION_TYPE_A;
		String bo_title = DAO.querySingleValue("select apply_name from s_service_apply where apply_id = ? ", new String[]{applyId});
		boolean is_success = bpmService.startFlow(flowData, action_type, bo_title);
		if(is_success) {
			resu = updateTaskApplyInfoTmp(params,flow_id);
		}
		return resu;
	}

	//修改的数据先写入临时表，审批通过后同步到实例表
	private Map updateTaskApplyInfoTmp(Map params, String flowId) {
		ApplyUtil au = new ApplyUtil();
		Map result = new HashMap();
		String applyId = Const.getStrValue(params, "apply_id");
		
		//此操作为判断是否操作了临时表（data_change_notify_column,data_change_notify）
		Map appMc = new HashMap();
		appMc.put("action_type", "M");
		appMc.put("field_name", "apply_id");
		appMc.put("table_name", SServiceApply.TABLE_CODE);
		appMc.put("old_field_value", "");
		appMc.put("field_value", applyId);
		appMc.put("owner_inst_id", applyId);
		appMc.put("inst_id", applyId);
		appMc.put("tab_attr_id", "");
		appMc.put("notify_id", "");
		appMc.put("flow_id", flowId);
		
		au.insertMDataChangeNotify(appMc);
		
		
		Map apply = (Map) params.get("apply");
		Map task = (Map) params.get("task");
		Map data_inst = (Map) params.get("data_inst");
		Map dispatch = (Map) params.get("dispatch");
		List<Map> cols = (List<Map>) params.get("cols");
		
		// 写申请单
		IVO vo = SServiceApply.getDAO().findById(applyId);
		if (vo != null) {
			SServiceApply fromDB = (SServiceApply) vo;
			SServiceApply fromPage = (SServiceApply) vo.cloneObj();
			au.insertChangeNotify(apply,fromPage,fromDB,SServiceApply.TABLE_CODE,fromPage.apply_id,fromPage.apply_id,"M",flowId);
			
			// 获取s_task_info task_id by apply_id
			List<IVO> vos = STaskInfo.getDAO().query(" apply_id = ? ", applyId);
			String taskId = null;
			if(!ListUtil.isEmpty(vos)) {
				IVO taskVO = vos.get(0);
				if(taskVO != null) {
					STaskInfo taskFromDB = (STaskInfo) taskVO;
					STaskInfo taskFromPage = (STaskInfo) taskVO.cloneObj();
					taskId = taskFromPage.task_id;
					au.insertChangeNotify(task, taskFromPage, taskFromDB, STaskInfo.TABLE_CODE, taskFromPage.task_id, taskFromPage.apply_id, "M", flowId);
				
					// 写任务实例、任务附件  更新附件变化数据的flow_id

					//界面现有任务附件
					List<Map> files = (List<Map>) task.get("files");
					//界面删除的任务附件f
					List<Map> dele_file_ids = (List<Map>) params.get("dele_file_ids");
					updateAttachTmp(files,flowId,applyId);
					updateAttachTmp(dele_file_ids,flowId,applyId);
					
				}
			}
			
			
			// 写能力信息
			List<IVO> dataInstVOs = SDataInst.getDAO().query(" apply_id = ? ", applyId);
			String data_inst_id = null;
			if(!ListUtil.isEmpty(dataInstVOs)) {
				IVO dataInstVO = dataInstVOs.get(0);
				if(dataInstVO != null) {
					SDataInst instFromDB = (SDataInst) dataInstVO;
					SDataInst instFromPage = (SDataInst) dataInstVO.cloneObj();
					data_inst_id = instFromDB.data_inst_id;
					au.insertChangeNotify(data_inst, instFromPage, instFromDB, SDataInst.TABLE_CODE, instFromPage.data_inst_id, instFromPage.apply_id, "M", flowId);
					
					//此处是为了给获取到临时表中data_inst_id 最终获取到columns
					Map instMc = new HashMap();
					instMc.put("action_type", "M");
					instMc.put("field_name", "data_inst_id");
					instMc.put("table_name", SDataInst.TABLE_CODE);
					instMc.put("old_field_value", "");
					instMc.put("field_value", data_inst_id);
					instMc.put("owner_inst_id", applyId);
					instMc.put("inst_id", data_inst_id);
					instMc.put("tab_attr_id", "");
					instMc.put("notify_id", "");
					instMc.put("flow_id", flowId);
					au.insertMDataChangeNotify(instMc);

					
				}
			}
			
			// 字段信息、实例字段信息
			List sqlParams2 = new ArrayList();
			for (Map col : cols) {
				//写字段信息c_data_column和s_data_column对应临时表数据 data_change_notify_column数据
				Map changeColumn = new HashMap();
				changeColumn.put("service_id",taskId);
				changeColumn.put("column_code",Const.getStrValue(col, "column_code"));
				changeColumn.put("column_name",Const.getStrValue(col, "column_name"));
				changeColumn.put("column_type",Const.getStrValue(col, "column_type"));
				changeColumn.put("column_length",StringUtils.isEmpty(MapUtils.getString(col, "column_length")) ? null : MapUtils.getString(col, "column_length"));
				changeColumn.put("data_inst_id", data_inst_id);
				changeColumn.put("column_id", "-1");
				
				au.insertDataChangeColumnTask(changeColumn, flowId, applyId);
			}
			
			// 写分发信息
			List<IVO> dispVOs = SDataDispatch.getDAO().query(" data_inst_id = ? ", data_inst_id);
			if(!ListUtil.isEmpty(dispVOs)) {
				IVO dispVO = dispVOs.get(0);
				if(dispVO != null) {
					SDataDispatch dispFromDB = (SDataDispatch) dispVO;
					SDataDispatch dispFromPage = (SDataDispatch) dispVO.cloneObj();
					au.insertChangeNotify(dispatch, dispFromPage, dispFromDB, SDataDispatch.TABLE_CODE, dispFromPage.dispatch_id, dispFromPage.data_inst_id, "M", flowId);
				}
			}
			result.put("apply_id", fromPage.apply_id);
			result.put("success", true);
		}
		return result;
	}

	//设置临时表中附件信息flow_id
	private void updateAttachTmp(List<Map> files, String flowId, String applyId) {
		List sqlParams = new ArrayList();
		String updateDataChangeTmpAttachSql = " update data_change_notify set flow_id = ? where owner_inst_id = ? and table_name = ? and inst_id = ? ";
		for (Map file : files) {
			List list = new ArrayList();
			list.add(flowId);
			list.add(applyId);
			list.add(AttachFile.TABLE_CODE);
			list.add(MapUtils.getString(file, "file_id"));
			sqlParams.add(list.toArray(new String[] {}));
		}
		DBUtil.getSimpleQuery().batchUpdate(updateDataChangeTmpAttachSql, sqlParams);
	}
	
	public Map insertMDataChangeNotify(Map params) {
		ApplyUtil au = new ApplyUtil();
		au.insertMDataChangeNotify(params);
		return null;
	}
	
	private void removeDeleteFile(List<Map> files, List<Map> fileIdsDel) {
		if(files==null || ListUtil.isEmpty(files)) return;
		if(fileIdsDel==null || ListUtil.isEmpty(fileIdsDel)) return;
		int[] deleteIndex = new int[files.size()];
		List<Integer> deleteIndexList = new ArrayList<Integer>();
		for(int i=0; i<files.size(); i++) {
			String file_id = Const.getStrValue(files.get(i), "file_id");
			if(StringUtils.isBlank(file_id)) continue;
			for(int j=0; j<fileIdsDel.size(); j++) {
				String file_id_d = Const.getStrValue(fileIdsDel.get(j), "file_id");
				if(StringUtils.isNotBlank(file_id_d) && file_id.equals(file_id_d)) {
					deleteIndexList.add(i);
				}
			}
		}
		for(Integer indexI : deleteIndexList) {
			if(indexI!=null) {
				files.remove(indexI.intValue());
			}
		}
		
		
	}
}
