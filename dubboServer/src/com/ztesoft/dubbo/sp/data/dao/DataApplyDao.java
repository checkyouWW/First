package com.ztesoft.dubbo.sp.data.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import spring.util.SpringContextUtil;
import appfrm.app.vo.IVO;
import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.DcSystemParamUtil;
import com.ztesoft.common.util.RSAUtil;
import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.common.util.SessionHelper;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.crm.business.common.utils.ListUtil;
import com.ztesoft.crmpub.bpm.mgr.service.BpmService;
import com.ztesoft.dubbo.inf.util.BDPMd5Utils;
import com.ztesoft.dubbo.mp.data.vo.CDataAblity;
import com.ztesoft.dubbo.mp.sys.service.FtpService;
import com.ztesoft.dubbo.se.data.dao.DataScheduleDao;
import com.ztesoft.dubbo.sp.data.util.ApplyUtil;
import com.ztesoft.dubbo.sp.data.vo.SDataColumn;
import com.ztesoft.dubbo.sp.data.vo.SDataDispatch;
import com.ztesoft.dubbo.sp.data.vo.SDataInst;
import com.ztesoft.dubbo.sp.data.vo.SServiceApply;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.ftp.IFtpUtil;
import com.ztesoft.inf.util.ftp.imp.FtpUtil;
import com.ztesoft.inf.util.jdbc.JDBCMeta;
import com.ztesoft.sql.Sql;
import comx.order.inf.IKeyValues;

@Repository
@SuppressWarnings({"rawtypes", "unchecked"})
public class DataApplyDao {

	private static final Logger log = Logger.getLogger(DataApplyDao.class);
	
	/**
	 * 保存申请数据
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public Map saveApply(Map params) throws Exception {
		Map result = new HashMap();
		Map baseData = (Map) params.get("baseData");
		List<Map> dataInstList = (List) params.get("dataInstList");
		
		String apply_id = SeqUtil.getSeq(SServiceApply.TABLE_CODE, "apply_id");
		
		/**@第一步 保存申请单*/
		SServiceApply apply = new SServiceApply();
		apply.readFromMap(baseData);
		String curr_time = DateUtil.getFormatedDateTime();
		apply.apply_id = apply_id;
		apply.service_type = KeyValues.SERVICE_TYPE_DATA;//数据服务
		apply.service_id = StringUtils.isEmpty(apply.service_id)?"":apply.service_id;
		apply.service_id = apply.service_id.split(",")[0];
		apply.apply_date = curr_time;
		apply.state_date = curr_time;
		apply.state = KeyValues.APPLY_STATE_AUDIT; // 00A: 通过 00X: 未通过 00B: 审批中
		SServiceApply.getDAO().insert(apply);
		
		//循环选中的数据列表
		if(dataInstList != null  && dataInstList.size() > 0){
			for(Map map : dataInstList){
				Map dataInstData = (Map) map.get("dataInstData");
				Map dispatchcData = (Map) map.get("dispatchcData");
				
				String data_inst_id = SeqUtil.getSeq(SDataInst.TABLE_CODE, "data_inst_id");
				
				/**@第二步 能力信息*/
				SDataInst dataInst = new SDataInst();
				dataInst.readFromMap(dataInstData);
				dataInst.data_inst_id = data_inst_id;
				dataInst.apply_id = apply_id;
				dataInst.service_type = KeyValues.SERVICE_TYPE_DATA;
				String data_range = dataInst.data_range;
				if(StringUtil.isEmpty(data_range)){
					data_range = SessionHelper.getLanId();
				}
				//hive视图不能出现-号
				if("-1".equals(data_range)){
					data_range = "1";
				}
				dataInst.view_name = dataInst.data_code+"_view"+"_"+data_range+"_"+data_inst_id;
				if(StringUtil.isEmpty(dataInst.rerun_interval) || !StringUtil.isNum(dataInst.rerun_interval))
					dataInst.rerun_interval = DcSystemParamUtil.getSysParamByCache("DEF_INTERVAL");
				createViewSql(dataInst);
				SDataInst.getDAO().insert(dataInst);
				
				
				/**@第三步 分发模式信息*/
				SDataDispatch dispatch = new SDataDispatch();
				String dispatch_id = SeqUtil.getSeq(SDataDispatch.TABLE_CODE, "DISPATCH_ID");
				dispatch.readFromMap(dispatchcData);
				dispatch.dispatch_id = dispatch_id;
				dispatch.data_inst_id = data_inst_id;
				//判断分发模式是不是ftp且是拉取：
				if(KeyValues.DISPATCH_TYPE_FTP.equals(dispatch.dispatch_type) && "pull".equals(dispatch.ftp_data_type)){
					Map getFtpDataMap = new HashMap();
					getFtpDataMap.put("ftp_type", "TEAM");
					getFtpDataMap.put("org_id", apply.org_id);
					List<Map> ftpDataList = SpringContextUtil.getApplicationContext().
							getBean(FtpService.class).getFtpList(getFtpDataMap);
					if(ftpDataList!=null && ftpDataList.size()>0){
						Map ftpDataMap = ftpDataList.get(0);
						dispatch.ftp_def_dir = Const.getStrValue(ftpDataMap, "path");
						dispatch.ftp_ip = Const.getStrValue(ftpDataMap, "ip");
						dispatch.ftp_password = Const.getStrValue(ftpDataMap, "password");
						dispatch.ftp_port = Const.getStrValue(ftpDataMap, "port");
						dispatch.ftp_user = Const.getStrValue(ftpDataMap, "user");
					}
				}
				SDataDispatch.getDAO().insert(dispatch);
				
				/**@第四步 字段及安全信息*/
				List<Map> colList = (List<Map>) map.get("securityCol");
				
				//获取c_src_column is_acct seq字段信息
				String getCSrcColumnInfoSql = "select seq ,is_acct  from c_data_column  where column_id = ?";
				
				if(colList != null){
					for(Map col : colList){
						SDataColumn scol = new SDataColumn();
						scol.readFromMap(col);
						scol.alg_type = Const.getStrValue(col, "dst_algorithm");
						scol.data_inst_id = dataInst.data_inst_id;
						
						//获取c_src_column is_acct seq字段信息
						List<Map> tList = DAO.queryForMap(getCSrcColumnInfoSql.toString(), new String[]{scol.column_id});
						if(!ListUtil.isEmpty(tList)) {
							Map tMap = tList.get(0);
							scol.is_acct = Const.getStrValue(tMap, "is_acct");
							scol.seq = Const.getStrValue(tMap, "seq");
						}
						
						scol.getDao().insert(scol);
					}
				}
			}
		}
		
		/**@第五步 启动流程*/
		Map flowData = new HashMap();
		String flow_id = SeqUtil.getSeq("BPM_BO_FLOW_INST", "flow_id");
		flowData.put("bo_type_id", KeyValues.BO_TYPE_ID_SERVICE_APPLY);
		flowData.put("bo_id", apply.apply_id);
		flowData.put("flow_id", flow_id);
		flowData.put("autoFinishBeginWO", "true");
		
		String action_type = IKeyValues.ACTION_TYPE_A;
		String bo_title = apply.apply_name;
		BpmService service = (BpmService) SpringContextUtil.getBean("bpmService");
		boolean is_success = service.startFlow(flowData, action_type, bo_title);
		
		result.put("success", is_success);
		result.put("apply_id", apply.apply_id);
		return result;
	}

	private void createViewSql(SDataInst dataInst) {
		String is_history = dataInst.is_history;
		String history_acct = dataInst.history_acct;
		String data_range = dataInst.data_range;
		String service_id = dataInst.service_id;
		List<IVO> abVos = CDataAblity.getDAO().query(" service_id = ?", service_id);
		CDataAblity ability = null;
		if(!ListUtil.isEmpty(abVos)){
			ability = (CDataAblity) abVos.get(0);
		}
		if(ability == null){
			return;
		}
		//已经不使用这两个字段
		//String lan_division = ability.lan_division;
		//String acct_division = ability.acct_division;
		
		String lan_division = null;
		String acct_division = null;
		
		DataScheduleDao dao = (DataScheduleDao) SpringContextUtil.getBean("dataScheduleDao");
		List<Map> disWhereList = dao.getColumnWhereList(service_id, KeyValues.WHERE_TYPE_DISPATCH);
		if(disWhereList != null){
			for(Map where : disWhereList){
				String column_code = StringUtil.getStrValue(where, "column_code");
				String expression = StringUtil.getStrValue(where, "expression");
				if("${lan_id}".equals(expression)){
					lan_division = column_code;
				}
				else if("${acct_time}".equals(expression)){
					acct_division = column_code;
				}
			}
		}
		
		StringBuffer select_where_sql = new StringBuffer();
		StringBuffer create_view_where_sql = new StringBuffer();
		select_where_sql.append(" where 1 = 1 ");
		//本地网分区
		if(StringUtil.isNotEmpty(lan_division) && StringUtil.isNotEmpty(data_range)){
			if("-1".equals(data_range)){
				//全省,如果要根据本地网拆单的话这里就要改了 TODO
			}
			else {
				select_where_sql.append(" and ").append(lan_division).append(" = ").append(data_range);
			}
		}
		//创建视图的where条件是不需要账期的
		create_view_where_sql.append(select_where_sql);
		
		//账期分区
		if(StringUtil.isNotEmpty(acct_division)){
			if("1".equals(is_history)){
				//账期必须少于等于当前账期
				select_where_sql.append(" and ").append(acct_division).append(" <= ").append("${acct_time}");
				select_where_sql.append(" and ").append(acct_division).append(" >= ").append(history_acct).append(" ");
			}
			else {
				select_where_sql.append(" and ").append(acct_division).append(" = ").append("${acct_time}").append(" ");
			}
		}
		
		dataInst.create_view_where_sql = create_view_where_sql.toString();
		dataInst.select_where_sql = select_where_sql.toString();
	}

	public List getDataColumnList(Map params){
		
		String serviceId = Const.getStrValue(params, "service_id");
		String sql = Sql.C_DATA_SQLS.get("get_column_list");
		List<String> sqlParams = new ArrayList<String>();
		sqlParams.add(serviceId);
		return DAO.queryForMap(sql, sqlParams.toArray(new String[]{}));
		
	}
	
	public Map getApplyData(Map params) {
		ApplyUtil au = new ApplyUtil();
		String applyId = Const.getStrValue(params, "applyId");
		SServiceApply apply = (SServiceApply) SServiceApply.getDAO().findById(applyId);
		String bo_type_id = Const.getStrValue(params, "bo_type_id");
		//bpm_bo_flow_inst flow_id 只能有一个，多了是异常
		String isDataUpdated = Sql.S_DATA_SQLS.get("is_data_apply_update");
		/**
		 * 存在两种现象
		 * 1、bdsp新建申请后，bdmp修改，数据在临时表中，申请类型是（SERVICE_APPLY）
		 * 2、bdsp修改申请（bdmp修改），数据在临时表中，申请类型是（SERVICE_MOD）
		 * 3、修改和申请两种申请类型的申请流程，对同一个申请单来说，只能存在一个
		 * 修改流程的数据比新申请流程数据新，故获取修改流程产生临时数据，没有则获取申请流程的数据
		 */
		String flowId = DAO.querySingleValue(isDataUpdated, new String[]{SServiceApply.TABLE_CODE,applyId,applyId,"apply_id",applyId,applyId,"ACTIVE",KeyValues.BO_TYPE_ID_SERVICE_MOD,SServiceApply.TABLE_CODE});
		if(StringUtils.isBlank(flowId)) {
			flowId = DAO.querySingleValue(isDataUpdated, new String[]{SServiceApply.TABLE_CODE,applyId,applyId,"apply_id",applyId,applyId,"ACTIVE",KeyValues.BO_TYPE_ID_SERVICE_APPLY,SServiceApply.TABLE_CODE});
		}
		
		Map applyMap = apply.saveToMap();
		//提取翻译值
		applyMap.put("org_name", DAO.querySingleValue(Sql.S_DATA_SQLS.get("get_org_name"), new String[]{apply.org_id}));
		applyMap.put("apply_staff_name", DAO.querySingleValue(Sql.S_DATA_SQLS.get("get_staff_name"), new String[]{apply.apply_staff_id}));
		
		List<Map> dataList = null;
		if(StringUtils.isNotBlank(flowId)) {
			au.getDataChangeNotify(applyMap, SServiceApply.TABLE_CODE, applyId, applyId, flowId, new String[]{"M","A"});
			//获取数据详情（原有的，除去临时表中删除的 ）
			dataList = DAO.queryForMap(Sql.S_DATA_SQLS.get("get_data_inst_list_with_del"),new String[]{flowId,SDataInst.TABLE_CODE,apply.apply_id,"data_inst_id",apply.apply_id});
		}else {
			dataList = DAO.queryForMap(Sql.S_DATA_SQLS.get("get_data_inst_list"),new String[]{apply.apply_id});
		}
		
		for(int i=0;dataList!=null && i<dataList.size();i++){
			Map tempData = dataList.get(i);
			//获取data_inst_id;
			String dataInstId = Const.getStrValue(tempData, "data_inst_id");
			//根据dataInstId获取所有字段信息
			List<Map> columnList = DAO.queryForMap(Sql.S_DATA_SQLS.get("get_data_column_list"), new String[]{dataInstId});
			
			//获取分发模式
			Map dispatchMap = null;
			List<Map>dispatchList = DAO.queryForMap(Sql.S_DATA_SQLS.get("get_dispatch_sql"), new String[]{dataInstId});
			if(dispatchList!=null && dispatchList.size()>0)
				dispatchMap = dispatchList.get(0);
			
			if(StringUtils.isNotBlank(flowId)) { //申请单在流程存在，查看是否有存放在临时表中的数据，有则覆盖
				//获取dataInst（修改的）
				au.getDataChangeNotify(tempData, SDataInst.TABLE_CODE, dataInstId,applyId,flowId, new String[]{"M"});
				
				//获取datadispatch
				au.getDataChangeNotify(dispatchMap, SDataDispatch.TABLE_CODE, Const.getStrValue(dispatchMap, "dispatch_id"), dataInstId, flowId, new String[]{"M","A"});
				
				//临时表中获取
				columnList = DAO.queryForMap("select * from data_change_notify_column where data_inst_id = ? and flow_id = ? ", new String[]{dataInstId,flowId});
				
			}
			if(dispatchMap!=null && !MapUtils.isEmpty(dispatchMap)) {
				String fp = Const.getStrValue(dispatchMap, "ftp_password");
				String dp = Const.getStrValue(dispatchMap, "db_password");
				if(StringUtils.isNotBlank(fp)) {
					dispatchMap.put("ftp_password", RSAUtil.decrypt(fp));
				}
				if(StringUtils.isNotBlank(dp)) {
					dispatchMap.put("db_password", RSAUtil.decrypt(dp));
				}
			}
			tempData.put("dispatch", dispatchMap);
			tempData.put("column_list", columnList);
			
		}
		
		if(StringUtils.isNotBlank(flowId)) { //申请单在流程存在，查看是否有存放在临时表中的数据，有则覆盖或增加
			//获取临时表中dataInst（新增的）
			String getServiceIdTmpSql = "select field_value from data_change_notify where action_type = ? and table_name = ? and field_name = ? and flow_id = ? and owner_inst_id = ? ";
			List<Map> serviceIds = DAO.queryForMap(getServiceIdTmpSql, new String[]{"A",SDataInst.TABLE_CODE,"service_id",flowId,applyId});
			if(!ListUtil.isEmpty(serviceIds)) {
				for(Map idmap : serviceIds) {
					Map newDataInstMap = new HashMap();
					Map newDataDisMap = new HashMap();
					
					String serviceId = Const.getStrValue(idmap, "field_value");
					String tmpId = applyId+"_"+serviceId;
					String tmpInstId = tmpId+"_data";
					newDataInstMap.put("data_inst_id",tmpInstId);
					au.getDataChangeNotify(newDataInstMap, SDataInst.TABLE_CODE, tmpInstId,applyId,flowId, new String[]{"A","M"});
					
					//获取datadispatch
					String tmpDisId = tmpId+"_disp";
					au.getDataChangeNotify(newDataDisMap, SDataDispatch.TABLE_CODE, tmpDisId, tmpInstId, flowId, new String[]{"A","M"});
					String newfp = Const.getStrValue(newDataDisMap, "ftp_password");
					String newdp = Const.getStrValue(newDataDisMap, "db_password");
					if(StringUtils.isNotBlank(newfp)) {
						newDataDisMap.put("ftp_password", RSAUtil.decrypt(newfp));
					}
					if(StringUtils.isNotBlank(newdp)) {
						newDataDisMap.put("db_password", RSAUtil.decrypt(newdp));
					}
					newDataDisMap.put("dispatch_id", tmpDisId);
					
					//临时表中获取
					List<Map> newColumnList = DAO.queryForMap("select * from data_change_notify_column where data_inst_id = ? and flow_id = ? ", new String[]{tmpInstId,flowId});
					
					newDataInstMap.put("dispatch", newDataDisMap);
					newDataInstMap.put("column_list", newColumnList);
					
					dataList.add(newDataInstMap);
				}
			}
		}
		
		applyMap.put("ability_list", dataList);
		return applyMap;
	}

	//排除临时表中删除的数据
	private void removeDataInst(List<Map> dataList, List<Map> dataListD) {
		if(!ListUtil.isEmpty(dataListD) && !ListUtil.isEmpty(dataList)) {
			for(Map dataDB : dataList) {
				String dbId = Const.getStrValue(dataDB, "data_inst_id");
				for(Map dataDel : dataListD) {
					String dataDelId = Const.getStrValue(dataDel, "field_value");
					if(dbId.equals(dataDelId)) {
						dataList.remove(dataDB);
						dataListD.remove(dataDel);
						if(ListUtil.isEmpty(dataListD)) break;
					}
				}
				if(ListUtil.isEmpty(dataList)) break;
			}
		}
	}

	public PageModel getDataColumnPageModel(Map params) {
		
		int page = 1;
		int pagesize = 10;
		
		try{
			page = Integer.parseInt(Const.getStrValue(params, "page"));
			pagesize = Integer.parseInt(Const.getStrValue(params, "rows"));
		}catch(Exception e){}
		
		String serviceId = Const.getStrValue(params, "service_id");
		String sql = Sql.C_DATA_SQLS.get("get_column_list");
		List<String> sqlParams = new ArrayList<String>();
		sqlParams.add(serviceId);
		return DAO.queryForPageModel(sql, pagesize, page, new String[]{serviceId});
	}

	public PageModel getApplyDataColumn(Map params) {
		
		int page = 1;
		int pagesize = 10;
		
		try{
			page = Integer.parseInt(Const.getStrValue(params, "page"));
			pagesize = Integer.parseInt(Const.getStrValue(params, "rows"));
		}catch(Exception e){}
		
		//判断数据是否在临时表中
		String applyId = Const.getStrValue(params, "apply_id");
		String flowId = DAO.querySingleValue(Sql.S_DATA_SQLS.get("is_data_apply_update"), 
						new String[]{SServiceApply.TABLE_CODE,applyId,applyId,"apply_id",
						applyId,applyId,"ACTIVE",KeyValues.BO_TYPE_ID_SERVICE_MOD,SServiceApply.TABLE_CODE});
		PageModel p = null;
		String sql = null;
		if(StringUtils.isNotBlank(flowId)) {
			String dataInstId = Const.getStrValue(params, "ability_id");
			sql = Sql.S_DATA_SQLS.get("get_ability_column_list_tmp");
			p = DAO.queryForPageModel(sql, pagesize	, page	, new String[]{dataInstId,flowId});
		}else {
			String abilityId = Const.getStrValue(params, "ability_id");
			sql = Sql.S_DATA_SQLS.get("get_used_ability_column_list");
			
			p = DAO.queryForPageModel(sql, pagesize	, page	, new String[]{abilityId});
		}
		
		
		return p;
	}
	//修改，写入临时表
	public Map updateDataApplyInfoAudit(Map m) {
		ApplyUtil au = new ApplyUtil();
		Map res = new HashMap();
		String applyId = MapUtils.getString(m, "apply_id");
		String flowId = MapUtils.getString(m, "flow_id");
		
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
		
		Map<String,String> baseData = (Map) m.get("baseData"); 
		String effDate = MapUtils.getString(baseData, "eff_date");
		String expDate = MapUtils.getString(baseData, "exp_date");
		
		au.insertMDataChangeNotify(flowId, applyId, applyId, SServiceApply.TABLE_CODE, "eff_date", effDate,"M");
		au.insertMDataChangeNotify(flowId, applyId, applyId, SServiceApply.TABLE_CODE, "exp_date", expDate,"M");
		
		List<Map> dataInstList = (List) m.get("dataInstList");
		if(dataInstList != null  && dataInstList.size() > 0){
			for(Map map : dataInstList) {
				Map dataInstData = (Map) map.get("dataInstData");
				String dataInstId = MapUtils.getString(dataInstData, "data_inst_id");
				String extractType = MapUtils.getString(dataInstData, "extract_type");
				if(StringUtils.isBlank(extractType)) {
					continue;
				}
				if(extractType.equals("period")) {
					String dataRange = MapUtils.getString(dataInstData, "data_range");
					au.insertMDataChangeNotify(flowId, dataInstId, applyId, SDataInst.TABLE_CODE, "data_range", dataRange,"M");
				}else if(extractType.equals("once")) {
					String dataRange = MapUtils.getString(dataInstData, "data_range");
					String historyAcct = MapUtils.getString(dataInstData, "history_acct");
					au.insertMDataChangeNotify(flowId, dataInstId, applyId, SDataInst.TABLE_CODE, "data_range", dataRange,"M");
					au.insertMDataChangeNotify(flowId, dataInstId, applyId, SDataInst.TABLE_CODE, "history_acct", historyAcct,"M");
				}
				
				Map dispatchcData = (Map) map.get("dispatchcData");
				String dispatchId = MapUtils.getString(dispatchcData, "dispatch_id");
				String dispatchType = MapUtils.getString(dispatchcData, "dispatch_type");
				if(StringUtils.isBlank(dispatchType)) {
					continue;
				}
				if(dispatchType.equals("ftp")) {
					String ftpDataType = MapUtils.getString(dispatchcData, "ftp_data_type");
					if(ftpDataType.equals("pull")) {
						String ftpIp = MapUtils.getString(dispatchcData, "ftp_ip");
						String ftpPort = MapUtils.getString(dispatchcData, "ftp_port");
						String ftpUser = MapUtils.getString(dispatchcData, "ftp_user");
						String ftpPassword = MapUtils.getString(dispatchcData, "ftp_password");
						String ftpDefDir = MapUtils.getString(dispatchcData, "ftp_def_dir");
						String ftpSplit = MapUtils.getString(dispatchcData, "ftp_split");
						String defFileName = MapUtils.getString(dispatchcData, "def_file_name");
						au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "ftp_password", ftpPassword,"M");
						au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "ftp_ip", ftpIp,"M");
						au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "ftp_port", ftpPort,"M");
						au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "ftp_user", ftpUser,"M");
						au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "ftp_def_dir", ftpDefDir,"M");
						au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "ftp_split", ftpSplit,"M");
						au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "def_file_name", defFileName, "M");
					}
				}else if(dispatchType.equals("db_import")) {
					String importType = MapUtils.getString(dispatchcData, "import_type");
					String sqoopType = MapUtils.getString(dispatchcData, "sqoop_type");
					au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "import_type", importType,"M");
					au.insertMDataChangeNotify(flowId, dispatchId, dataInstId, SDataDispatch.TABLE_CODE, "sqoop_type", sqoopType,"M");
				}
				
				
				
				List<Map> colList = (List<Map>) map.get("securityCol");
				if(colList != null){
					//删除data_change_notify_column对应数据 ，再插入
					String delChangeColumnSql = "delete from data_change_notify_column where data_inst_id = ? and flow_id = ? ";
					DAO.update(delChangeColumnSql, new String[]{dataInstId,flowId});
					for(Map col : colList){
						//插入data_change_notify_column，此表是s_data_column的临时表，审批通过之后，删除s_data_column表相关数据，将临时表数据存入其中
						String col_is_dst = Const.getStrValue(col, "is_dst");
						if(StringUtils.isNotBlank(col_is_dst) && col_is_dst.trim().equals(KeyValues.NOT_USE_DST_ALGORITHM)) {
							col.put("dst_algorithm", "");
						}
						
						au.insertDataChangeColumn(col, dataInstId, flowId, applyId);
					}
				}
			}
		}
		res.put("success", true);
		return res;
	}

	public Map getApplyCode(Map params) {
		Map returnMap = new HashMap();
		String applyCode = SeqUtil.getSeq("S_SYSTEM_SEQ", "APPLY_CODE");
		if(StringUtils.isEmpty(applyCode)) return returnMap;
		for(;applyCode.length()<8;)
			applyCode = "0"+applyCode;
		returnMap.put("apply_code", KeyValues.APPLY_CODE_PREFIX+applyCode);
		return returnMap;
	}

	public Map validateDispatch(Map params) {
		List<Map> dispatcherList = (List<Map>)params.get("data");
		Map returnMap = new HashMap();
		List<Integer> notPassSite =  Collections.synchronizedList(new ArrayList<Integer>());
		int listSize = dispatcherList==null?0:dispatcherList.size();
		CountDownLatch countDownLatch = new CountDownLatch(listSize);
		ExecutorService exec = Executors.newFixedThreadPool(listSize<10?listSize:10);
		Map resultMap = new HashMap();
		resultMap.put("isValidatePass", true);
		for(int i=0;dispatcherList!=null && i<dispatcherList.size();i++){
			Map thisMap = dispatcherList.get(i);
			exec.submit(new ValidateFtpAndDBRunable(thisMap,i,resultMap,notPassSite,countDownLatch));
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			exec.shutdown();
		}
		
		List<Integer> reallyNotPassSite = new ArrayList<Integer>();
		for(int i=0;notPassSite!=null && i<notPassSite.size();i++){
			Integer tmpI = notPassSite.get(i);
			if(tmpI==null) tmpI = new Integer(0);
			int addSite = 0;
			for(int j=0;j<reallyNotPassSite.size();j++){
				Integer rj = reallyNotPassSite.get(j);
				if(tmpI.intValue() < rj){
					addSite = j;
					break;
				}
			}
			reallyNotPassSite.add(addSite,tmpI);
		}
		
		returnMap.put("isValidatePass", resultMap.get("isValidatePass"));
		returnMap.put("notPassList", reallyNotPassSite);
		return returnMap;
	}
	
	class ValidateFtpAndDBRunable implements Runnable{

		private Map dispatherMap = null;
		private Map resultMap = null;
		private int index = 0;
		private List<Integer> notPassList = null;
		private CountDownLatch latch = null;
		
		public ValidateFtpAndDBRunable(Map dispatherMap,int indx,Map resultMap,List<Integer> notPassList,CountDownLatch latch){
			this.dispatherMap = dispatherMap;
			this.index = indx;
			this.resultMap = resultMap;
			this.notPassList = notPassList;
			this.latch = latch;
			
		}
		
		public void validate() {
			
			String dispatchType = Const.getStrValue(dispatherMap, "dispatch_type");
			String ftpDataType = Const.getStrValue(dispatherMap, "ftp_data_type");
			//如果是ftp推送
			if(KeyValues.DISPATCH_TYPE_FTP.equals(dispatchType) && KeyValues.FTP_DISPATH_PUSH.equals(ftpDataType)){
				int ftpPort = Integer.parseInt(Const.getStrValue(dispatherMap, "ftp_port"));
				String ftpHost = Const.getStrValue(dispatherMap, "ftp_ip");
				String ftpUsr = Const.getStrValue(dispatherMap, "ftp_user");
				String ftpPwd = Const.getStrValue(dispatherMap, "ftp_password");
				IFtpUtil ftpUtil = FtpUtil.getFtpUtil(ftpHost, ftpPort, ftpUsr, ftpPwd);
				if(!ftpUtil.isAvailableFtp()){
					resultMap.put("isValidatePass", false);
					notPassList.add(new Integer(this.index+1));
				}
			}//如果是sqoop模式
			else if(KeyValues.DISPATCH_TYPE_DB_IMPORT.equals(dispatchType)){
				String conStr = Const.getStrValue(dispatherMap, "db_url");
				String dbPwd = Const.getStrValue(dispatherMap, "db_password");
				String dbUsr = Const.getStrValue(dispatherMap, "db_user");
				boolean isSucConn = false;
				try{
					JDBCMeta meta = new JDBCMeta(conStr, dbUsr, dbPwd);
					isSucConn = meta.isAvailableConn();
				}catch(Exception e){
					log.error(e);
					isSucConn = false;
				}
				if(!isSucConn){
					resultMap.put("isValidatePass", false);
					notPassList.add(new Integer(this.index+1));
				}
			}
			
		}

		@Override
		public void run() {
			try{
				this.validate();
				this.latch.countDown();
			}catch(Exception e){
				log.error(e);
				e.printStackTrace();
				this.latch.countDown();
			}
		}
		
	}
	
	
	public Map updateDataApplyInfo(Map params) throws Exception {
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
		BpmService service = (BpmService) SpringContextUtil.getBean("bpmService");
		boolean is_success = service.startFlow(flowData, action_type, bo_title);
		if(is_success) {
			resu = updateDataApplyInfoTmp(params,flow_id);
		}
		return resu;
	}

	//将数据更新到临时表
	public Map updateDataApplyInfoTmp(Map params, String flowId) {
		Map result = new HashMap();
		ApplyUtil au = new ApplyUtil();
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
		
		
		//界面dataInst
		List<Map> dataInstList = (List) params.get("dataInstList");
		//原来dataInst
		List<Map> dataInstListOld = DAO.queryForMap(Sql.S_DATA_SQLS.get("get_data_inst_list"),new String[]{applyId});
		
		/** 操作界面删除数据 */
		for(Map dataInstOld : dataInstListOld) {
			String dataInstIdOld = Const.getStrValue(dataInstOld, "data_inst_id");
			boolean is_deleted = true;
			for(Map dataInst : dataInstList) {
				Map dataInstDataNow = (Map) dataInst.get("dataInstData");
				String dataInstId = Const.getStrValue(dataInstDataNow, "data_inst_id");
				if(StringUtils.isNotBlank(dataInstIdOld) && StringUtils.isNotBlank(dataInstId) && dataInstIdOld.equals(dataInstId)) {
					is_deleted = false;
				}
			}
			if(is_deleted) { //删除的数据只存原有的id
				Map mc = new HashMap();
				mc.put("action_type", "D");
				mc.put("field_name", "data_inst_id");
				mc.put("table_name", SDataInst.TABLE_CODE);
				mc.put("old_field_value", "");
				mc.put("field_value", dataInstIdOld);
				mc.put("owner_inst_id", applyId);
				mc.put("inst_id", dataInstIdOld);
				mc.put("tab_attr_id", "");
				mc.put("notify_id", "");
				mc.put("flow_id", flowId);
				au.insertMDataChangeNotify(mc);
			}
		}
		
		IVO vo = SServiceApply.getDAO().findById(applyId);
		if (vo != null) {
			SServiceApply fromDB = (SServiceApply) vo;
			SServiceApply fromPage = (SServiceApply) vo.cloneObj();
			Map baseData = (Map) params.get("baseData");
			au.insertChangeNotify(baseData,fromPage,fromDB,SServiceApply.TABLE_CODE,fromPage.apply_id,fromPage.apply_id,"M",flowId);
			
			// 循环选中的数据列表
			if (dataInstList != null && dataInstList.size() > 0) {
				for (Map map : dataInstList) {
					Map dataInstData = (Map) map.get("dataInstData");
					Map dispatchcData = (Map) map.get("dispatchcData");
					
					//如果是FTP拉取模式，自动补充数据
					//判断分发模式是ftp且是拉取：
					if(KeyValues.DISPATCH_TYPE_FTP.equals(dispatchcData.get("dispatch_type")) && 
							"pull".equals(dispatchcData.get("ftp_data_type"))){
						Map getFtpDataMap = new HashMap();
						getFtpDataMap.put("ftp_type", "TEAM");
						getFtpDataMap.put("org_id",fromDB.org_id);
						List<Map> ftpDataList = SpringContextUtil.getApplicationContext().
								getBean(FtpService.class).getFtpList(getFtpDataMap);
						if(ftpDataList!=null && ftpDataList.size()>0){
							Map ftpDataMap = ftpDataList.get(0);
							dispatchcData.put("ftp_def_dir",Const.getStrValue(ftpDataMap, "path"));
							dispatchcData.put("ftp_ip",Const.getStrValue(ftpDataMap, "ip"));
							dispatchcData.put("ftp_password",Const.getStrValue(ftpDataMap, "password"));
							dispatchcData.put("ftp_port", Const.getStrValue(ftpDataMap, "port"));
							dispatchcData.put("ftp_user",Const.getStrValue(ftpDataMap, "user"));
						}
					}
					
					List<Map> colList = (List<Map>) map.get("securityCol");
					
					/** 能力信息 */
					String dataInstId = Const.getStrValue(dataInstData, "data_inst_id");
					//407_143_data 通过findById(id),会找到407的对象信息，
					IVO dataInstVO = SDataInst.getDAO().findById(dataInstId);
					boolean hasDataInst = false;
					if(dataInstVO != null) {
						SDataInst tmpDataInst = (SDataInst) dataInstVO;
						String nowDataInstId = tmpDataInst.data_inst_id;
						if(nowDataInstId.equals(dataInstId)) {
							hasDataInst = true;
						}
					}
					if(hasDataInst) {
						//修改的数据，存入临时表
						SDataInst dataInstfromDB = (SDataInst) dataInstVO;
						SDataInst dataInstfromPage = (SDataInst) dataInstVO.cloneObj();
						au.insertChangeNotify(dataInstData,dataInstfromPage,dataInstfromDB,SDataInst.TABLE_CODE,dataInstfromPage.data_inst_id,fromPage.apply_id,"M",flowId);
						
						/** 分发模式信息 */
						List<Map> dispatchList = DAO.queryForMap(Sql.S_DATA_SQLS.get("get_dispatch_sql"), new String[]{dataInstId});
						Map dispatchMap = new HashMap();
						if(dispatchList!=null && dispatchList.size()>0)
							dispatchMap = dispatchList.get(0);
						String dispatchId = Const.getStrValue(dispatchMap, "dispatch_id");
						IVO dispatchVO = SDataDispatch.getDAO().findById(dispatchId);
						if(dispatchVO != null) {
							SDataDispatch dispatchfromDB = (SDataDispatch) dispatchVO;
							SDataDispatch dispatchfromPage = (SDataDispatch) dispatchVO.cloneObj();
							au.insertChangeNotify(dispatchcData,dispatchfromPage,dispatchfromDB,SDataDispatch.TABLE_CODE,dispatchfromPage.dispatch_id,dataInstId,"M",flowId);
						}
						
						/** 字段及安全信息 */
						if (colList != null) {
							for (Map col : colList) {
								//插入data_change_notify_column，此表是s_data_column的临时表，审批通过之后，删除s_data_column表相关数据，将临时表数据存入其中
								au.insertDataChangeColumn(col, dataInstId, flowId, applyId);
							}
						}
						
					}else {//新增的数据，通过拼data_change_notify的inst_id(查找方便)
						
						//新增data_inst
						String serviceId = Const.getStrValue(dataInstData, "service_id");
						String tmpId = applyId+"_"+serviceId;
						String dataInstTmpId = tmpId+"_data";
						
						SDataInst dataInst = new SDataInst();
						dataInst.readFromMap(dataInstData);
						dataInst.data_inst_id = dataInstTmpId;
						dataInst.apply_id = applyId;
						dataInst.service_type = KeyValues.SERVICE_TYPE_DATA;
						dataInst.view_name = dataInst.data_code+"_view"+"_"+DateUtil.getVSOPDate8();
						
						au.insertChangeNotify(dataInstData,dataInst,new SDataInst(),SDataInst.TABLE_CODE,dataInst.data_inst_id,dataInst.apply_id,"A",flowId);
				
						//新增dispatch
						String dispatchTmpId = tmpId+"_disp";
						SDataDispatch dispatch = new SDataDispatch();
						dispatch.readFromMap(dispatchcData);
						dispatch.dispatch_id = dispatchTmpId;
						dispatch.data_inst_id = dataInstTmpId;
						
						au.insertChangeNotify(dispatchcData,dispatch,new SDataDispatch(),SDataDispatch.TABLE_CODE,dispatch.dispatch_id,dispatch.data_inst_id,"A",flowId);
						
						//新增column
						if (colList != null) {
							for (Map col : colList) {
								//插入data_change_notify_column，此表是s_data_column的临时表，审批通过之后，删除s_data_column表相关数据，将临时表数据存入其中
								au.insertDataChangeColumn(col, dataInstTmpId, flowId, applyId);
							}
						}
					}

				}
			}
			result.put("success", true);
			result.put("apply_id", fromPage.apply_id);
		}
		return result;
	}
	
	public Map getDispatchInfo(Map params) {
		String org_id = SessionHelper.getTeamId();
		List<Map> ftpList = DAO.queryForMap(Sql.S_DATA_SQLS.get("get_dispatch_ftp_list"), new String[]{org_id});
		List<Map> dbList = DAO.queryForMap(Sql.S_DATA_SQLS.get("get_dispatch_db_list"), new String[]{org_id});
		if(ftpList!=null && !ListUtil.isEmpty(ftpList)) {
			for(Map m : ftpList) {
				String fp = Const.getStrValue(m, "ftp_password");
				if(StringUtils.isNotBlank(fp)) {
					m.put("ftp_password", RSAUtil.decrypt(fp));
				}
			}
		}else {
			//加入空变量，界面使用
			Map ftp = new HashMap();
			ftp.put("text", "");
			ftp.put("valueText", "");
			ftpList.add(ftp);
		}
		if(dbList!=null && !ListUtil.isEmpty(dbList)) {
			for(Map m : dbList) {
				String fp = Const.getStrValue(m, "db_password");
				if(StringUtils.isNotBlank(fp)) {
					m.put("db_password", RSAUtil.decrypt(fp));
				}
			}
		}
		Map dispInfo = new HashMap();
		dispInfo.put("ftp_list", ftpList);
		dispInfo.put("db_list", dbList);
		return dispInfo;
	}
	
	public Map validateFtpFileName(Map params) {
		
		Map returnMap = new HashMap();
		returnMap.put("state", "failed");
		
		String fileName = Const.getStrValue(params, "file_name");
		
		String dataInstId = Const.getStrValue(params, "data_inst_id");
		
		List<String> sqlParams = new ArrayList<String>();
		sqlParams.add(fileName);
		
		String sql = Sql.S_DATA_SQLS.get("validate_def_file_name");
		String sql2 = Sql.S_DATA_SQLS.get("validate_def_file_name_2");
		
		if(StringUtils.isNotEmpty(dataInstId)){
			sql += " and sdd.data_inst_id != ? ";
			sql2 += " and owner_inst_id != ? ";
			sqlParams.add(dataInstId);
		}
		
		List<Map> rsList = DAO.queryForMap(sql, sqlParams.toArray(new String[]{}));
		
		if(rsList== null  || rsList.size()>0) {
			return returnMap;
		}
		
		//二次判断
		List<Map> rsList2 = DAO.queryForMap(sql2, sqlParams.toArray(new String[]{}));
		
		if(rsList2== null  || rsList2.size()>0) {
			return returnMap;
		}
		
		returnMap.put("state", "success");
		return returnMap;
	}
	
	public boolean canGetPassWd(Map m) {
		String pwd = Const.getStrValue(m, "password");
		String staffCode = SessionHelper.getStaffCode();
		String enPwd = BDPMd5Utils.encrypt(pwd,staffCode);
		String sql = " select count(*) from dm_staff where password = ? and staff_code = ? ";
		String count = DAO.querySingleValue(sql, new String[]{enPwd, staffCode});
		if(StringUtils.isNotBlank(count) && Integer.parseInt(count)>0) {
			return true;
		}else {
			return false;
		}
	}

	public List getCanSelectedDataRange(Map param) {
		String serviceId = Const.getStrValue(param, "service_id");
		
		String sql = "select * from c_data_lan where ability_id in (select ability_id from c_data_ability where service_id = ?)";
		
		List trs = DAO.queryForMap(sql, new String[]{serviceId});
		if(trs!=null && trs.size()>0) {
			Map tmap = new HashMap();
			tmap.put("lan_id", "-1");
			tmap.put("lan_name", "全部");
			trs.add(0, tmap);
			return trs;
		}
		
		String tsql = DAO.querySingleValue("select dc_sql from dc_sql where dc_name=?", new String[]{"DC_LAN"});
		return DAO.queryForMap(tsql, new String[]{});
		
		
		
	}
	
}

