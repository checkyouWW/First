package com.ztesoft.dubbo.mp.audit.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.crm.business.common.utils.ListUtil;
import com.ztesoft.crmpub.bpm.handler.ICallBackHandler;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowDef;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;
import com.ztesoft.dubbo.mp.data.vo.CDataColumn;
import com.ztesoft.dubbo.mp.task.vo.AttachFile;
import com.ztesoft.dubbo.mp.task.vo.STaskInfo;
import com.ztesoft.dubbo.sp.data.vo.MDataChangeNotify;
import com.ztesoft.dubbo.sp.data.vo.SDataColumn;
import com.ztesoft.dubbo.sp.data.vo.SDataDispatch;
import com.ztesoft.dubbo.sp.data.vo.SDataInst;
import com.ztesoft.dubbo.sp.data.vo.SServiceApply;
import com.ztesoft.dubbo.sp.data.vo.SServiceInst;
import com.ztesoft.inf.mp.sys.IFtpService;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.ftp.bean.FtpBean;
import com.ztesoft.inf.util.ftp.imp.FtpUtil;

import appfrm.app.vo.IVO;
import appfrm.resource.dao.impl.DAO;
import exception.CoopException;
import spring.util.SpringContextUtil;

/**
 * 服务申请单修改审批通过处理事件
 */
public class ServiceApplyModFinishHandler implements ICallBackHandler{

	@Override
	public boolean processMsg(MsgBean msg) throws Exception {
		SBpmBoFlowDef bpmBoFlowDef = msg.getFlowDef();
		SBpmBoFlowTache currTache = msg.getFlowTache();
		
		if(bpmBoFlowDef != null && currTache != null){
			String curr_tache_code = currTache.tache_code;
			bpmBoFlowDef.setMsg(msg);
			//获取下一个流程环节			
			SBpmBoFlowTache nextFlowTache = bpmBoFlowDef.getNextTache(curr_tache_code);
			//下一环节不为空，或不被跳过证明还没结束
			if(nextFlowTache != null){
				return true;
			}
		}
		
		String bo_id = msg.getBoId();
		
		if(StringUtils.isBlank(bo_id)){
			return false;
		}

		//更新申请单
		SServiceApply apply = (SServiceApply) SServiceApply.getDAO().findById(bo_id);
		if(apply != null){
			String service_type = apply.service_type;
			String flow_id = msg.getFlowId();
			if(service_type.equals(KeyValues.SERVICE_TYPE_DATA)) {
				synDataApplyInfo(bo_id,flow_id);
			}else if(service_type.equals(KeyValues.SERVICE_TYPE_TASK)) {
				synTaskApplyInfo(bo_id,flow_id);
			}else if(service_type.equals(KeyValues.SERVICE_TYPE_SECURITY)) {
				
			}
			apply.set("state", KeyValues.APPLY_STATE_SUCCESS);
			apply.set("state_date", DateUtil.getFormatedDateTime());
			String[] othReqSupportfields = new String[]{"state","state_date"};
			SServiceApply.getDAO().updateParmamFieldsByIdSQL(othReqSupportfields).update(apply);
		}
		
		//生成服务实例
		String inst_id = SeqUtil.getSeq(SServiceInst.TABLE_CODE, "inst_id");
		
		SServiceInst inst = new SServiceInst();
		Map map = apply.saveToMap();
		inst.readFromMap(map);
		inst.inst_id = inst_id;
		SServiceInst.getDAO().insert(inst);
		
		return true;
	}
	
	//同步任务申请数据
	public void synTaskApplyInfo(String apply_id, String flow_id) throws Exception {
		this.synApplyInfo(apply_id, flow_id, KeyValues.SERVICE_TYPE_TASK);
		List<IVO> taskInfoList = STaskInfo.getDAO().query(" apply_id = ? ", apply_id);
		String taskId = null;
		if(taskInfoList != null && !ListUtil.isEmpty(taskInfoList)) {
			STaskInfo taskInfo = (STaskInfo) taskInfoList.get(0);
			taskId = taskInfo.task_id;
		}else {
			throw new CoopException(CoopException.ERROR,"获取申请单信息错误！", null);
		}
		//同步task_info数据
		List<MDataChangeNotify> taskInfoCN = (List)MDataChangeNotify.getDAO().query(
				" owner_inst_id = ? and inst_id = ? and table_name = ? and action_type = ? and flow_id = ? ", 
				apply_id, taskId, STaskInfo.TABLE_CODE, "M",flow_id);
		if(taskInfoCN != null && !taskInfoCN.isEmpty()){
			IVO vo = STaskInfo.getDAO().findById(taskId);
			if(vo == null){
				throw new CoopException(CoopException.INFO, "获取申请单信息错误!", null);
			}
			STaskInfo taskInfofromChange = (STaskInfo) vo.cloneObj();//变更后的数据
			updateData(taskInfoCN, taskInfofromChange);
			deleteTmpDataChange(apply_id,taskId,STaskInfo.TABLE_CODE,null);
		}
		
		
		//同步附件信息
		String getAttachChangedSql = "select inst_id from data_change_notify where table_name = ? and owner_inst_id = ? and flow_id = ? group by inst_id,action_type order by action_type asc ";
		List<Map> fileIdsChanged = DAO.queryForMap(getAttachChangedSql, new String[]{AttachFile.TABLE_CODE,apply_id,flow_id});
		Map instFileMap = new HashMap();//order by action asc 'A' first, 'D' later
		for(Map fileTmp : fileIdsChanged) {
			String fileId = Const.getStrValue(fileTmp, "inst_id");
			List<MDataChangeNotify> fileCN = (List) MDataChangeNotify.getDAO().query(
					" owner_inst_id = ? and inst_id = ? and table_name = ? and flow_id = ? ", apply_id,fileId,AttachFile.TABLE_CODE,flow_id);
			
			if(fileCN != null && !ListUtil.isEmpty(fileCN)) {
				String action_type = fileCN.get(0).action_type;
				AttachFile af = new AttachFile();
				voChange(fileCN,af);
				if(StringUtils.isNotBlank(action_type) && action_type.equals("A")) {
					String file_id = SeqUtil.getSeq("attach_file", "file_id");
					af.file_id = file_id;
					af.table_pk_value = taskId;
					AttachFile.getDAO().insert(af);
					instFileMap.put(fileId, file_id);
				}else if(StringUtils.isNotBlank(action_type) && action_type.equals("D")) {
					IFtpService ftpService = (IFtpService) SpringContextUtil.getBean("ftpService");
					FtpBean bean = ftpService.getDefTaskFtp();
					
					String host_name = bean.ip;
					String port = String.valueOf(bean.port);
					String user_name = bean.user;
					String password = bean.password;
					String file_location = bean.password;
					FtpUtil ftp = new FtpUtil(host_name, Integer.parseInt(port), user_name, password);
					
					ftp.delFile(file_location, af.file_location);
					af.file_id = fileId;
					AttachFile.getDAO().deleteById(af);
				}
				deleteTmpDataChange(apply_id,fileId,AttachFile.TABLE_CODE,action_type);
			}
		}
	}

	//同步数据申请数据
	public void synDataApplyInfo(String apply_id, String flow_id) {
		this.synApplyInfo(apply_id, flow_id, KeyValues.SERVICE_TYPE_DATA);
	}
	
	public void synApplyInfo(String apply_id, String flow_id, String service_type) {
		//更新s_service_apply
		List<MDataChangeNotify> applyCN = (List)MDataChangeNotify.getDAO().query(
				" owner_inst_id = ? and inst_id = ? and table_name = ? and action_type = ? and flow_id = ? ", 
				apply_id, apply_id, SServiceApply.TABLE_CODE, "M",flow_id);
		if(applyCN != null && !applyCN.isEmpty()){
			IVO vo = SServiceApply.getDAO().findById(apply_id);
			if(vo == null){
				throw new CoopException(CoopException.INFO, "申请单ID："+apply_id+"无对应申请数据", null);
			}
			SServiceApply applyfromChange = (SServiceApply) vo.cloneObj();//变更后的数据
			updateData(applyCN, applyfromChange);
			deleteTmpDataChange(apply_id,apply_id,SServiceApply.TABLE_CODE,null);
		}
		
		//更新s_data_inst
		//获取临时表中的ids
		String getDataInstIdsChangedSql = "select DISTINCT inst_id from data_change_notify where table_name = ? and owner_inst_id = ? and flow_id = ? ";
		List<Map> dataInstIdsChanged = DAO.queryForMap(getDataInstIdsChangedSql, new String[]{SDataInst.TABLE_CODE,apply_id,flow_id});
		
		for(Map dataInstTmp : dataInstIdsChanged) {
			String dataInstId = MapUtils.getString(dataInstTmp,"inst_id");
			List<MDataChangeNotify> dataInstCN = (List)MDataChangeNotify.getDAO().query(
					" owner_inst_id = ? and inst_id = ? and table_name = ? and flow_id = ? ", 
					apply_id, dataInstId, SDataInst.TABLE_CODE, flow_id);
			if(dataInstCN != null && !dataInstCN.isEmpty()){
				String action_type = dataInstCN.get(0).action_type;
				if(action_type.equals("A")) {//data_inst 新增
					String data_inst_id = SeqUtil.getSeq(SDataInst.TABLE_CODE, "data_inst_id");
					SDataInst dataInst = new SDataInst();
					voChange(dataInstCN,dataInst);
					dataInst.data_inst_id = data_inst_id;
					dataInst.apply_id = apply_id;
					dataInst.service_type = KeyValues.SERVICE_TYPE_DATA;
					dataInst.view_name = dataInst.data_code+"_view"+"_"+DateUtil.getVSOPDate8();
					SDataInst.getDAO().insert(dataInst);
					deleteTmpDataChange(apply_id,dataInstId,SDataInst.TABLE_CODE,null);
					
					String tmpId = dataInstId.replace("_data", "");
					String dispatchIdTmp = tmpId+"_disp";
							
					//新增dispatch
					List<MDataChangeNotify> dispatchCN = (List)MDataChangeNotify.getDAO().query(
							" owner_inst_id = ? and inst_id = ? and table_name = ? and flow_id = ? ", 
							dataInstId, dispatchIdTmp, SDataDispatch.TABLE_CODE, flow_id);
					SDataDispatch dispatch = new SDataDispatch();
					String dispatch_id = SeqUtil.getSeq(SDataDispatch.TABLE_CODE, "DISPATCH_ID");
					voChange(dispatchCN,dispatch);
					dispatch.dispatch_id = dispatch_id;
					dispatch.data_inst_id = data_inst_id;
					SDataDispatch.getDAO().insert(dispatch);
					deleteTmpDataChange(dataInstId,dispatchIdTmp,SDataDispatch.TABLE_CODE,null);
					
					//新增column
					synSDataColumn(dataInst.data_inst_id,dataInstId, flow_id,service_type);
				}else if(action_type.equals("M")) {//data_inst 修改
					IVO vo = SDataInst.getDAO().findById(dataInstId);
					if(vo == null){
						throw new CoopException(CoopException.INFO, "申请单数据实例ID："+dataInstId+"无对应实例数据", null);
					}
					SDataInst dataInstfromChange = (SDataInst) vo.cloneObj();//变更后的数据
					updateData(dataInstCN, dataInstfromChange);
					deleteTmpDataChange(apply_id,dataInstId,SDataInst.TABLE_CODE,null);
					
					String dispatchId = DAO.querySingleValue("select dispatch_id from s_data_dispatch where data_inst_id = ? ", new String[]{dataInstId});
					IVO voDis = SDataDispatch.getDAO().findById(dispatchId);
					if(voDis == null){
						throw new CoopException(CoopException.INFO, "申请单数据分发ID："+dispatchId+"无对应分发数据", null);
					}
					List<MDataChangeNotify> dispatchCN = (List)MDataChangeNotify.getDAO().query(
							" owner_inst_id = ? and inst_id = ? and table_name = ? and flow_id = ? ", 
							dataInstId, dispatchId, SDataDispatch.TABLE_CODE, flow_id);
					SDataDispatch disfromChange = (SDataDispatch) voDis.cloneObj();//变更后的数据
					updateData(dispatchCN, disfromChange);
					deleteTmpDataChange(dataInstId,dispatchId,SDataDispatch.TABLE_CODE,null);
					
					//同步column
					synSDataColumn(dataInstId,dataInstId, flow_id,service_type);
				}else if(action_type.equals("D")) {//data_inst 删除
					DAO.update("delete from s_data_inst where data_inst_id = ? ", new String[]{dataInstId});
					DAO.update("delete from s_data_dispatch where data_inst_id = ? ", new String[]{dataInstId});
					DAO.update("delete from s_data_column where data_inst_id = ? ", new String[]{dataInstId});
					if(StringUtils.isNotBlank(service_type) && service_type.equals(KeyValues.SERVICE_TYPE_TASK)) {
						DAO.update("delete from c_data_column where column_id in (select column_id from s_data_column where data_inst_id = ? ) ", new String[]{dataInstId});
					}
					MDataChangeNotify.getDAO().deleteById(dataInstCN.get(0));
				}
			}
		}
	}
	
	
	//删除临时表数据 data_change_notify_column
	public void deleteTmpDataChangeColumn(String data_inst_id, String flow_id) {
		String deleCsql = "delete from data_change_notify_column where data_inst_id = ? and flow_id = ? ";
		DAO.update(deleCsql, new String[]{data_inst_id,flow_id});
	}
	
	//删除临时表数据 data_change_notify
	public void deleteTmpDataChange(String ownerInstId, String instId, String tableName, String actionType ) {
		String deleDSql = "delete from data_change_notify where owner_inst_id = ? and inst_id = ? and table_name = ? ";
		if(StringUtils.isNotBlank(actionType)) {
			deleDSql += " and action_type = ? ";
			DAO.update(deleDSql, new String[]{ownerInstId,instId,tableName,actionType});
		}else {
			DAO.update(deleDSql, new String[]{ownerInstId,instId,tableName});
		}
	}
	
	//同步临时表data_change_notify_column
	private void synSDataColumn(String data_inst_id ,String data_inst_id_tmp, String flow_id, String service_type) {
		DAO.update("delete from s_data_column where data_inst_id = ? ", new String[]{data_inst_id});
		List<Map> columnList = DAO.queryForMap("select * from data_change_notify_column where data_inst_id = ? and flow_id = ? ", new String[]{data_inst_id_tmp,flow_id});
		if(columnList != null){
			for(Map col : columnList){
				if(StringUtils.isNotBlank(service_type) && service_type.equals(KeyValues.SERVICE_TYPE_TASK)) {
					CDataColumn ccol = new CDataColumn();
					ccol.readFromMap(col);
					String ccol_id = SeqUtil.getSeq(CDataColumn.TABLE_CODE, "COLUMN_ID");
					ccol.column_id = ccol_id;
					ccol.getDao().insert(ccol);;
					
					SDataColumn scol = new SDataColumn();
					scol.readFromMap(col);
					scol.data_inst_id = data_inst_id;
					scol.column_id = ccol_id;
					scol.column_inst_id = SeqUtil.getSeq("S_DATA_COLUMN", "COLUMN_INST_ID");
					scol.getDAO().insert(scol);
				}else {
					SDataColumn scol = new SDataColumn();
					scol.readFromMap(col);
					scol.data_inst_id = data_inst_id;
					scol.getDao().insert(scol);
				}
			}
		}
		deleteTmpDataChangeColumn(data_inst_id_tmp,flow_id);
	}
	
	
	private void voChange(List<MDataChangeNotify> changeNotifys,IVO fromChange) {
		for(MDataChangeNotify changeNotify: changeNotifys){
			String field_name = changeNotify.field_name;
			String field_value = changeNotify.field_value;
			if(StringUtils.isNotBlank(field_name) && StringUtils.isNotBlank(field_value))
				if((StringUtils.isNotBlank(fromChange.get(field_name)) && !fromChange.get(field_name).equals(field_value))
						|| StringUtils.isBlank(fromChange.get(field_name))) {
					fromChange.set(field_name, field_value);
				}
		}
	}
	
	private void updateData(List<MDataChangeNotify> changeNotifys, IVO fromChange) {
		voChange(changeNotifys,fromChange);
		
		Set<String> updateSet = fromChange.getUpdateFieldSet();
		if(updateSet != null){
			fromChange.getDao().updateParmamFieldsByIdSQL(updateSet.toArray(new String[]{})).update(fromChange);
		}
	}
}
