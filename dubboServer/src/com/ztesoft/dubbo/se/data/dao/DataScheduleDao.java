package com.ztesoft.dubbo.se.data.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.DcSystemParamUtil;
import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.common.util.SessionHelper;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.dubbo.inf.impl.TenantHandler;
import com.ztesoft.dubbo.inf.util.RsaEncrypt;
import com.ztesoft.dubbo.se.data.vo.ServiceOrder;
import com.ztesoft.dubbo.sp.data.vo.SDataColumn;
import com.ztesoft.dubbo.sp.data.vo.SDataDispatch;
import com.ztesoft.dubbo.sp.data.vo.SDataInst;
import com.ztesoft.inf.se.data.ILoggerService;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.sql.Sql;

import appfrm.app.util.ListUtil;
import appfrm.app.vo.IVO;
import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;
import spring.util.SpringContextUtil;

@Repository
@SuppressWarnings({"rawtypes", "unchecked"})
public class DataScheduleDao {
	
	@Resource
	private ILoggerService logService;
	
	public void calCreateOrderTime(String service_type) throws Exception {
		String sql = Sql.S_DATA_SQLS.get("get_valid_data_inst_l");
		
		List<Map> list = DAO.queryForMap(sql,new String[]{service_type});
		if(list == null){
			return;
		}
		for(int i=0; i<list.size(); i++){
			Map map = (Map) list.get(i);
			SDataInst dataInst = new SDataInst();
			dataInst.readFromMap(map);
			this.calTime(dataInst);
		}
	}
	
	/**
	 * 计算工单的下次执行时间
	 * @param mc
	 * @throws Exception 
	 */
	private void calTime(SDataInst dataInst) throws Exception{
//		String extract_type = dataInst.extract_type;
		String create_order_time = dataInst.create_order_time;
		
//		//如果是一次性提取，则下一执行时间就是默认凌晨0点(非周期只执行一次)
//		if("once".equals(extract_type)){
//			create_order_time = DateUtil.getFormatedDateTime();
//		}
//		//周期性提取
//		else if("period".equals(extract_type)){
//			//下一次执行时间
//		}
		create_order_time = DateUtil.getFormatedDateTime();
		dataInst.set("create_order_time", create_order_time);
		
		String[] fields = (String[]) dataInst.updateFieldSet.toArray(new String[] {});
		SDataInst.getDAO().updateParmamFieldsByIdSQL(fields).update(dataInst);
	}
	
	public List getDataServiceInsts(Map params){
		String service_type = Const.getStrValue(params, "service_type");
		String sql = Sql.S_DATA_SQLS.get("get_valid_data_inst");
		sql += " and not exists(select 1 from service_order s where s.data_inst_id = b.data_inst_id)";
		sql += " and to_days(b.create_order_time) = to_days(now())";//今天处理的
		
		List<String> sqlParams = new ArrayList<String>();
		sqlParams.add(service_type);
		
		List result = DAO.queryForMap(sql, sqlParams.toArray(new String[]{}));
		return result;
	}

	public Map createServiceOrder(Map dataInst) {
		if(dataInst == null || dataInst.isEmpty()){
			return null;
		}
		String curr_time = DateUtil.getFormatedDateTime();
		ServiceOrder order = new ServiceOrder();
		order.readFromMap(dataInst);
		order.create_date = curr_time;
		order.state_date = curr_time;
		order.state = KeyValues.ORDER_STATE_TODO;
		order.re_run_flag = KeyValues.RE_RUN_FLAG_NO;
		
		this.computeAcctTime(dataInst, order);
		
		this.computeWhereSql(dataInst, order);
		
		ServiceOrder.getDAO().insert(order);
		
		return order.saveToMap();
	}
	
	private void computeAcctTime(Map dataInst, ServiceOrder order) {
		String acct_time = "";
		String his_acct_time = "";
		
		String data_inst_id = StringUtil.getStrValue(dataInst, "data_inst_id");
		String is_history = StringUtil.getStrValue(dataInst, "is_history");
		String history_acct = StringUtil.getStrValue(dataInst, "history_acct");
		
		Map map = this.getExtractTypeMap(data_inst_id);
		String extract_start_acct = Const.getStrValue(map, "extract_start_acct");
		String extract_freq = StringUtil.getStrValue(map, "extract_freq");
		
		//计算默认日期
		String dateFormatter = "";
		Calendar defcal = Calendar.getInstance();
		defcal.setTime(new Date());
		if(KeyValues.EXTRACT_FREQ_MONTH.equals(extract_freq)){
			dateFormatter = "yyyyMM";
			defcal.add(Calendar.MONTH, -1);
		}
		else{
			dateFormatter = "yyyyMMdd";
			defcal.add(Calendar.DATE, -1);
		}

		acct_time = DateUtil.formatDate(defcal.getTime(), dateFormatter);
		
		//如果不是历史账期抽取，返回默认字段的默认值
		if("1".equals(is_history)){
			his_acct_time = history_acct;
			//返回字段值
			if(StringUtil.isEmpty(his_acct_time)){
				his_acct_time = extract_start_acct;
			}
		}
		
		order.acct_time = acct_time;
		order.his_acct_time = his_acct_time;
	}
	
	private void computeWhereSql(Map dataInst, ServiceOrder order) {
		String create_view_where_sql = StringUtil.getStrValue(dataInst, "create_view_where_sql");
		String select_where_sql = StringUtil.getStrValue(dataInst, "select_where_sql");
		
		Map data = new HashMap();
		data.putAll(dataInst);
		data.putAll(order.saveToMap());
		
		order.create_view_where_sql = this.parseSql(data, create_view_where_sql);
		order.select_where_sql = this.parseSql(data, select_where_sql);
	}
	
	private String parseSql(Map data, String sql){
		Pattern pattern = Pattern.compile("[\\$][{](\\w+)[}]");
		Matcher matcher = pattern.matcher(sql);
		String result = sql;
		while(matcher.find()){
			String match_field = matcher.group();
			String single_field = match_field.substring(match_field.indexOf("{")+1, match_field.indexOf("}"));
			
			String field_value = Const.getStrValue(data, single_field);
			result = result.replaceFirst("\\$\\{"+single_field+"\\}", field_value);
		}
		return result;
	}

	public Map updateServiceOrder(String service_order_id, Map changes) {
		if(StringUtil.isEmpty(service_order_id)){
			return null;
		}
		String is_reRun = Const.getStrValue(changes,"is_reRun");
		changes.remove("is_reRun");
		
		ServiceOrder order = (ServiceOrder) ServiceOrder.getDAO().findById(service_order_id);
		if(order == null){
			return null;
		}
		Set<String> keys = changes.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String key = it.next();
			String value = (String) changes.get(key);
			order.set(key, value);
		}
		String[] fields = (String[]) order.updateFieldSet.toArray(new String[] {});
		ServiceOrder.getDAO().updateParmamFieldsByIdSQL(fields).update(order);

		if(StringUtils.isNotBlank(is_reRun) && is_reRun.equals("1")) {
			if(logService==null) logService = SpringContextUtil.getApplicationContext().getBean(ILoggerService.class);
			
			Map<String,Object> sendMap = new HashMap<String,Object>();
			sendMap.put("service_order_id", order.service_order_id);
			sendMap.put("alert_type", KeyValues.ALERT_TYPE_RERUN);
			sendMap.put("result", KeyValues.SCHEDULE_LOG_RESULT_NORMAL);
			sendMap.put("log_desc", "执行重跑操作");
			sendMap.put("details_msg", "执行重跑操作");
			sendMap.put("step_name", "执行重跑操作");
			
			//写数据库日志
			logService.writeLog(sendMap);
		}
		return null;
	}

	public List getServiceOrders(String state, String service_type) {
		
		StringBuffer sql = new StringBuffer("select distinct so.* from service_order so,s_data_inst sdi ");
		sql.append(" where so.data_inst_id=sdi.data_inst_id and so.state = ? ");
		//获取默认的重跑间隔
		String defRerunInterval =  DcSystemParamUtil.getSysParamByCache("DEF_INTERVAL");
		List<String> sqlParams = new ArrayList<String>();
		sqlParams.add(state);
		if(StringUtil.isNotEmpty(service_type)){
			sql.append(" and so.service_type = ? ");
			sqlParams.add(service_type);
		}
		/*下面是补充各种可以跑的条件*/
		sql.append(" and ( ");
		sql.append(" so.create_date = so.state_date ");	//第一次跑的话必须是可以执行的
		sql.append(" or so.re_run_flag = '"+KeyValues.RE_RUN_FLAG_YES+"' ");	//如果是强制重跑的话必须要进行重跑
		sql.append(" or so.state != ? ");	//不是初始状态的都可以跑
		//等待重跑的时间满足
		sql.append(" or now() >= DATE_ADD(so.state_date,INTERVAL + ifnull(sdi.rerun_interval,"+defRerunInterval+") SECOND) ");
		sql.append(" ) ");
		sqlParams.add(KeyValues.ORDER_STATE_TODO);		
		List list = DAO.queryForMap(sql.toString(), sqlParams.toArray(new String[]{}));
		return list;
	}
	
	public Map getSDataInst(Map params) {
		Map dataInstMap = new HashMap();
		if(params == null || params.isEmpty()){
			return dataInstMap;
		}
		String data_inst_id = StringUtil.getStrValue(params, "data_inst_id");
		if(StringUtil.isEmpty(data_inst_id)){
			return dataInstMap;
		}
		SDataInst dataInst = (SDataInst) SDataInst.getDAO().findById(data_inst_id);
		if(dataInst == null){
			return dataInstMap;
		}
		
		dataInstMap = dataInst.saveToMap();
		
		//数据服务实例字段信息
		List<IVO> columnVos = SDataColumn.getDAO().query("data_inst_id = ?", data_inst_id);
		
		if(!ListUtil.isEmpty(columnVos)){
			List<Map> columns = new ArrayList<Map>();
			for(IVO vo : columnVos){
				columns.add(vo.saveToMap());
			}
			dataInstMap.put("data_column", columns);
		}
		
		//数据服务实例字段信息
		List<IVO> dispatchVos = SDataDispatch.getDAO().query("data_inst_id = ?", data_inst_id);

		if(!ListUtil.isEmpty(dispatchVos)){
			IVO vo = dispatchVos.get(0);
			dataInstMap.put("data_dispatch", vo.saveToMap());
		}
				
		return dataInstMap;
	}

	public boolean messageReady(Map dataInst) {
		String message_ready_switch = DcSystemParamUtil.getSysParamByCache("MESSAGE_READY_SWITCH");
		if("NO_VALID".equalsIgnoreCase(message_ready_switch)){
			return true;
		}
		String dataInstId = Const.getStrValue(dataInst, "data_inst_id");
		String getParam = "SELECT "
				+" cdsrc.*, sdi.create_order_time,so.service_order_id,so.org_id, "
				+" sdi.data_inst_id,so.lan_num,so.acct_time,sdi.data_code "
				+" FROM "
				+" s_data_inst sdi, "
				+" c_data_service cds, "
				+" c_data_src cdsrc,service_order so  "
				+" WHERE "
				+" sdi.service_id = cds.service_id "
				+" AND cdsrc.service_id = cds.service_id "
				+" and so.data_inst_id = sdi.data_inst_id "
				+" and sdi.data_inst_id = ? ";
		List<Map> pList =  DAO.queryForMap(getParam, new String[]{dataInstId});
		if(ListUtil.isEmpty(pList)) {
			return false;
		}
		Map param = pList.get(0);
		String serviceOrderId = Const.getStrValue(param, "service_order_id");
		String acctTime = Const.getStrValue(param, "acct_time");
		String acct = acctTime;
		if(StringUtils.isBlank(acctTime)) {
			acct = this.getPrevAcct(serviceOrderId);
		}
		String tableCode = Const.getStrValue(param, "data_code");
		
		String lanId = Const.getStrValue(dataInst, "data_range");
		String lanNum = Const.getStrValue(param, "lan_num");
		
		if(StringUtils.isBlank(acct) || StringUtils.isBlank(tableCode)) {
			return false;
		}
		
		String count = "0";
		String sql = "select count(distinct latn_id) from big_data_msg "
				+" where "
				+" lower(table_code) = lower(?) and acct_month = ? and msg_flag = ? ";
		
		if(StringUtils.isBlank(lanId)) {
			lanId = "-1";
		}
		
		if(!"-1".equals(lanId)){ //不是全省数据，单个区县id
			sql += "and (latn_id = ? or latn_id = -1)";
			count = DAO.querySingleValue(sql, new String[]{tableCode,acct,"T",lanId});
			if(StringUtils.isNotBlank(count) && Integer.parseInt(count)>0) {
				return true;
			}
		}else { //全省数据
			int theCount = 14;
			if(StringUtils.isNotBlank(lanNum)) {
				try {
					theCount = Integer.parseInt(lanNum);
				}catch(Exception e) {
					theCount = 14;
				}
			}
			
			count = DAO.querySingleValue(sql, new String[]{tableCode,acct,"T"});
			if(StringUtils.isNotBlank(count) && Integer.parseInt(count)>=theCount) { //全省数据个数大于全部区县个数
				return true;
			}else { //latn_id = -1，存在-1的全省数据
				count = DAO.querySingleValue(sql+" and latn_id = ? ", new String[]{tableCode,acct,"T","-1"});
				if(StringUtils.isNotBlank(count) && Integer.parseInt(count)>0) {
					return true;
				}
			}
		}
		
		return false;
	}

	public Map getDispatchData(String dataInstId) {
		String sql = Sql.S_DATA_SQLS.get("get_dispatch_data");
		List<Map> dataList = DAO.queryForMap(sql, new String[]{dataInstId});
		if(dataList == null || dataList.size() == 0) return null;
		return dataList.get(0);		
	}

	public Map getTenantInfo(Map order) {
		String staff_id = StringUtil.getStrValue(order, "apply_staff_id");
		String org_id = StringUtil.getStrValue(order, "org_id");
		
		String sql = Sql.SYS_SQLS.get("GET_TENANT_CODE");
		List list = DAO.queryForMap(sql, staff_id, org_id);
		Map result = new HashMap();
		if(!ListUtil.isEmpty(list)){
			result = (Map) list.get(0);
			String password = StringUtil.getStrValue(result, "password");
			
			password = RsaEncrypt.encrypt(password);
			result.put("password", password);
		}
		return result;
	}

	public String getViewName(String serviceOrderId) {
		String sql = Sql.S_DATA_SQLS.get("get_hive_view_name");
		return DAO.querySingleValue(sql, new String[]{serviceOrderId});
	}

	public void updateCreateOrderTime(String serviceOrderId,String newTime) {
		String sql = Sql.S_DATA_SQLS.get("update_datainst_createtime");
		DAO.update(sql, new String[]{newTime,serviceOrderId});
	}

	public String getOrgSchema(String org_id) {
		String sql = Sql.SYS_SQLS.get("GET_ORG_SCHEMA");
		String result = DAO.querySingleValue(sql, new String[]{org_id});
		return result;
	}

	public List<Map> getDataColumns(Map m) {
		String data_inst_id = StringUtil.getStrValue(m, "data_inst_id");
		String sql = " SELECT "
				+" t.*, caf.algorithm_func as func "
				+" FROM "
				+" ( "
				+" 	SELECT "
				+" 		cdc.column_code, "
				+"		cst.table_code,"
				+" 		cda.data_code, "
				+" 		sdc.is_dst, "
				+" 		sdc.alg_type, "
				+" 		css.schema_code,"
				+" 		cdc.column_type,"		
				+" 		sdc.seq,"
				+"		sdc.is_acct"
				+" 	FROM "
				+" 		c_data_column cdc, "
				+" 		s_data_column sdc, "
				+" 		meta_columns csc, "
				+" 		meta_tables cst, "
				+"		meta_schema css, "
				+"      c_data_ability cda, "
				+"		c_data_src cds"
				+" 	WHERE "
				+" 		cdc.column_id = sdc.column_id "
				+" 	AND csc.column_id = cdc.src_column_id "
				+" 	AND csc.table_code = cst.table_code "
				+" 	AND cst.schema_code = css.schema_code "
				+"  AND cda.service_id = cdc.service_id "
				+"  AND cda.service_id = cds.service_id "
				+"  AND cds.src_schema_code = css.schema_code "
				+" 	AND sdc.column_inst_id IN ( "
				+" 		SELECT "
				+" 			sdc1.column_inst_id "
				+" 		FROM "
				+" 			s_data_inst sdi1, "
				+" 			s_data_column sdc1 "
				+" 		WHERE "
				+" 			sdi1.data_inst_id = sdc1.data_inst_id "
				+" 		AND sdi1.data_inst_id = ? "
				+" 	) "
				+" ) t "
				+" LEFT JOIN c_algorithms caf ON t.alg_type = caf.algorithm_id order by t.seq asc ";
		
		String omp_sql = " SELECT "
				+" t.*, '' as func "
				+" FROM "
				+" ( "
				+" 	SELECT "
				+" 		cdc.column_code, "
				+"		cst.table_code,"
				+" 		cda.data_code, "
				+" 		sdc.is_dst, "
				+" 		sdc.alg_type, "
				+" 		css.schema_code,"
				+" 		cdc.column_type,"		
				+" 		sdc.seq,"
				+"		sdc.is_acct"
				+" 	FROM "
				+" 		c_data_column cdc, "
				+" 		s_data_column sdc, "
				+" 		omp_metadata_table_columns csc, "
				+" 		omp_metadata_table cst, "
				+"		omp_datasource_protocol css, "
				+"      c_data_ability cda, "
				+"		c_data_src cds"
				+" 	WHERE "
				+" 		cdc.column_id = sdc.column_id "
				+" 	AND csc.column_id = cdc.src_column_id "
				+" 	AND csc.table_code = cst.table_code "
				+" 	AND cst.schema_code = css.schema_code "
				+"  AND cda.service_id = cdc.service_id "
				+"  AND cda.service_id = cds.service_id "
				+"  AND cds.src_schema_code = css.schema_code "
				+" 	AND sdc.column_inst_id IN ( "
				+" 		SELECT "
				+" 			sdc1.column_inst_id "
				+" 		FROM "
				+" 			s_data_inst sdi1, "
				+" 			s_data_column sdc1 "
				+" 		WHERE "
				+" 			sdi1.data_inst_id = sdc1.data_inst_id "
				+" 		AND sdi1.data_inst_id = ? "
				+" 	) "
				+" ) t "
				+" order by t.seq asc ";
		
		List result = DAO.queryForMap(omp_sql, new String[]{data_inst_id});
		if(ListUtil.isEmpty(result)){
			result = DAO.queryForMap(sql, new String[]{data_inst_id});
		}
		return result;
	}
	
	public String getPrevAcct(String serviceOrderId) {
		//首先获取默认数据
		Map defaultValue = this.getAcctColumnMap(serviceOrderId);
		if(defaultValue==null) defaultValue = new HashMap();
		//计算默认日期
		String dateFormatter = "";
		Calendar defcal = Calendar.getInstance();
		defcal.setTime(new Date());
		if(KeyValues.EXTRACT_FREQ_MONTH.equals(Const.getStrValue(defaultValue, "extract_freq"))){
			dateFormatter = "yyyyMM";
			defcal.add(Calendar.MONTH, -1);
		}
		else{
			dateFormatter = "yyyyMMdd";
			defcal.add(Calendar.DATE, -1);
		}
		
		String defaultReturn = DateUtil.formatDate(defcal.getTime(), dateFormatter);
		return defaultReturn;
	}
	
	
	public Map getAcctColumnMap(String serviceOrderId) {
		
		List<Map> rsList = DAO.queryForMap(Sql.S_DATA_SQLS.get("get_acct_column"), new String[]{serviceOrderId});
		
		if(rsList == null || rsList.size() == 0) return null;
		return rsList.get(0);
	}

	public List<Map> getDataColumnList(String serviceOrderId) {
		return DAO.queryForMap(Sql.S_DATA_SQLS.get("get_inst_column_list"), new String[]{serviceOrderId});
	}

	public Map getExtractTypeMap(String dataInstId) {
		String sql = Sql.S_DATA_SQLS.get("get_extract_freq");
		List<Map> listMap = DAO.queryForMap(sql, new String[]{dataInstId});
		if(listMap==null || listMap.size()<1) return null;
		else return listMap.get(0);
	}

	public RpcPageModel getServiceOrderPage(Map m) {
		
		String pageSize = Const.getStrValue(m, "rows");
		String pageIndex = Const.getStrValue(m, "page");
		int ps = 10;
		int pi = 1;
		if(StringUtils.isNotBlank(pageSize)) {
			ps = Integer.parseInt(pageSize);
		}
		if(StringUtils.isNotBlank(pageIndex)) {
			pi = Integer.parseInt(pageIndex);
		}
		String serviceType = StringUtil.getStrValue(m, "service_type");
		String serviceOrderState = StringUtil.getStrValue(m, "service_order_state");
		String applyName = StringUtil.getStrValue(m, "apply_name");
		String acctTime = StringUtil.getStrValue(m, "acct_time");
		String dataCode = StringUtil.getStrValue(m, "data_code");
		String dataRange = StringUtil.getStrValue(m, "data_range");
		
		String staffId = StringUtil.getStrValue(m, "dubbo_staff_id");
		String isGetSelfData = StringUtil.getStrValue(m, "get_self_data");
		
		StringBuffer sql = new StringBuffer(Sql.S_DATA_SQLS.get("get_service_order_list"));
		List<String> pList = new ArrayList<String>();
		if(StringUtils.isNotBlank(applyName)) {
			sql.append(" and ssi.apply_name like ? ");
			pList.add("%"+applyName+"%");
		}
		if(StringUtils.isNotBlank(serviceType)) {
			sql.append(" and ssi.service_type = ? ");
			pList.add(serviceType);
		}
		if(StringUtils.isNotBlank(serviceOrderState)) {
			sql.append(" and so.state = ? ");
			pList.add(serviceOrderState);
		}
		if(StringUtils.isNotBlank(acctTime)) {
			sql.append(" and so.acct_time = ? ");
			pList.add(acctTime);
		}
		if(StringUtils.isNotBlank(dataCode)) {
			sql.append(" and sdi.data_code like ? ");
			pList.add("%"+dataCode+"%");
		}
		if(StringUtils.isNotBlank(dataRange)) {
			if(dataRange.trim().equals("-1")) {
				sql.append(" and ( sdi.data_range = '' or sdi.data_range is null ) ");
			}else {
				sql.append(" and sdi.data_range = ? ");
				pList.add(dataRange);
			}
		}
		
		//如果只获取自己的数据的话，增加过滤条件
		if("1".equals(isGetSelfData)){ //bdsp
			sql.append(" and ssi.apply_staff_id = ? ");
			pList.add(staffId);
		} else { //bdmp
			boolean isManager = SessionHelper.isManager();
			if(!isManager) {
				sql.append(" and ssi.apply_staff_id = ? ");
				pList.add(staffId);
			}
		}
		
		
		
		sql.append(" order by so.state_date desc ");
		
		PageModel p = DAO.queryForPageModel(sql.toString(), ps, pi, pList.toArray(new String[]{}));
		return PageModelConverter.pageModelToRpc(p);
	}


	public Map getSDataInstByServiceOrderId(String serviceOrderId) {
		String sql = Sql.S_DATA_SQLS.get("get_sdata_inst_byserviceid");
		List<Map> dataList = DAO.queryForMap(sql, new String[]{serviceOrderId});
		if(dataList==null || dataList.size() == 0) return null;
		else return dataList.get(0);
	}

	
	public RpcPageModel getServiceOrderLog(Map m) {
		String pageSize = Const.getStrValue(m, "rows");
		String pageIndex = Const.getStrValue(m, "page");
		int ps = 10;
		int pi = 1;
		if(StringUtils.isNotBlank(pageSize)) {
			ps = Integer.parseInt(pageSize);
		}
		if(StringUtils.isNotBlank(pageIndex)) {
			pi = Integer.parseInt(pageIndex);
		}
		String serviceOrderId = Const.getStrValue(m, "service_order_id");
		String sql = "select ssisl.* from service_order so, s_service_inst_schedule_log ssisl "
				+" where  "
				+" so.service_order_id = ssisl.service_order_id "
				+" and so.service_order_id = ? order by ssisl.log_id asc ";
		PageModel p = DAO.queryForPageModel(sql, ps, pi, new String[]{serviceOrderId});
		return PageModelConverter.pageModelToRpc(p);
	}

	/**
	 * 视图授权给团队负责人
	 * @param map
	 * @param order 
	 */
	public Map viewPrivToDirector(Map dataInst, Map order) {
		Map result = new HashMap();
		result.put("success", false);
		String org_id = StringUtil.getStrValue(order, "org_id");
		String org_schema_code = this.getOrgSchema(org_id);
		String view_name = StringUtil.getStrValue(dataInst, "view_name");
		if(StringUtil.isEmpty(view_name)){
			return result;
		}
		//找到团队的负责人
		List roleList = this.getTeamDirectorRole(org_id);
		if(ListUtil.isEmpty(roleList)){
			return result;
		}
		
		Map role = (Map) roleList.get(0);
		
		// 调用接口绑定租户角色权限，报错直接抛出不要拦截
		String type = "grant"; // 类型 grant:授予 revoke:解除
		String role_code = StringUtil.getStrValue(role, "role_code");
		String object_type = KeyValues.BD_PRIV_29;//HDFS目录：24   HDFS 文件：25  HIVE表：23  HBASE表：12 队列：21 视图: 29
		String object_ids = org_schema_code + "." + view_name;
		String role_type = KeyValues.BDP_DEF_ROLE_TYPE;
		
		TenantHandler handler = new TenantHandler();
		handler.genBdpStaffHeaders();
		try {
			result = handler.tenantRolePrivilegeRel(type, role_code, object_type, object_ids, role_type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	public String getDataServiceFileName(String serviceOrderId) {
		String sql = Sql.S_DATA_SQLS.get("get_def_filename_by_serviceorder");
		List<Map> data = DAO.queryForMap(sql, new String[]{serviceOrderId});
		if(data ==null || data.size()==0) return null;
		Map tmapData = data.get(0);
		String dataCode = Const.getStrValue(tmapData, "data_code");
		String applyId = Const.getStrValue(tmapData, "apply_id");
		String dataRange = Const.getStrValue(tmapData, "data_range");
		String acctTime = Const.getStrValue(tmapData, "acct_time");
		String defFileCode = Const.getStrValue(tmapData, "def_file_code");
		if(StringUtil.isEmpty(dataRange)){
			dataRange = "-1";
		}
		tmapData.put("lan_id", dataRange);
		
		if(StringUtil.isEmpty(defFileCode)){
			return dataCode+"_"+applyId+"_"+acctTime+"_"+dataRange;
		}
		
		Pattern pattern = Pattern.compile("[\\$][{](\\w+)[}]");
		Matcher matcher = pattern.matcher(defFileCode);
		String newFieldCode = defFileCode;
		while(matcher.find()){
			String match_field = matcher.group();
			String single_field = match_field.substring(match_field.indexOf("{")+1, match_field.indexOf("}"));
			
			String field_value = Const.getStrValue(tmapData, single_field);
			newFieldCode = newFieldCode.replaceFirst("\\$\\{"+single_field+"\\}", field_value);
		}
				
		return newFieldCode;
	}
	
	/**
	 * 获取团队负责人租户角色
	 * @param org_id
	 * @return
	 */
	private List getTeamDirectorRole(String org_id){
		StringBuffer role_sql = new StringBuffer(Sql.SYS_SQLS.get("QUERY_BD_ROLE_REL_STAFF_SQL"));
		role_sql.append(" and a.org_id = ? and a.is_director = ? order by c.role_code");
		List roleList = DAO.queryForMap(role_sql.toString(), org_id, KeyValues.IS_TEAM_DIRECTOR_T);
		return roleList;
	}

	public Map getLanColumnData(String service_order_id) {
		String sql = Sql.S_DATA_SQLS.get("get_lan_column_code");
		String sql2 = Sql.S_DATA_SQLS.get("get_lan_data_range_inst");
		String lanColumnCode = DAO.querySingleValue(sql, new String[]{service_order_id});
		if(StringUtil.isEmpty(lanColumnCode)) return new HashMap();
		String lanValue = DAO.querySingleValue(sql2, new String[]{service_order_id}) ;
		Map returnMap = new HashMap();
		returnMap.put("lan_column_code", lanColumnCode);
		returnMap.put("lan_value", lanValue);
		return returnMap;
	}

	public String getLanId(String org_id) {
		if(StringUtils.isBlank(org_id)) return null;
		String sql = "select lan_id from dm_organization where org_id = ? ";
		return DAO.querySingleValue(sql, new String[]{org_id});
	}

	public List<Map> getCleanFtpFileList() {

		String sql = Sql.S_DATA_SQLS.get("clean_ftp_file_list");
		String paramsVal = DcSystemParamUtil.getSysParamByCache(KeyValues.CLEAN_FTP_FILE_INTERVAL);
		sql += " and create_date<date_sub(now(), interval "+paramsVal+" hour) ";
		String reallySql = "select t1.*,sdd.ftp_ip,sdd.ftp_port,sdd.ftp_password,sdd.ftp_user from ("+sql+")t1 "
				+ "left join s_data_dispatch sdd on t1.dispatch_id = sdd.dispatch_id";
		return DAO.queryForMap(reallySql, new String[]{});
	}

	public void updateCleanFtpLogState(String logId, String newstate) {
		String sql = Sql.S_DATA_SQLS.get("update_dispatch_log_state");
		DAO.update(sql, newstate,logId);
	}

	public boolean isStaticTable(Map dataInst) {
		String serviceId = Const.getStrValue(dataInst, "service_id");
		String sql = "select * from c_data_ability where service_id =?";
		List<Map> tList = DAO.queryForMap(sql, new String[]{serviceId});
		
		if(tList==null || tList.size() == 0) return false;
		
		if("1".equals(tList.get(0).get("is_static_table"))) return true;
		
		return false;
	}

	public List getColumnWhereList(String service_id, String where_type) {
		if(StringUtil.isEmpty(service_id)){
			return new ArrayList();
		}
		String sql = "select * from c_data_column_where where service_id = ? ";
		List<String> sqlParams = new ArrayList<String>();
		sqlParams.add(service_id);
		if(StringUtil.isNotEmpty(where_type)){
			sql += " and where_type = ?";
			sqlParams.add(where_type);
		}
	
		List<Map> list = DAO.queryForMap(sql, sqlParams.toArray(new String[]{}));
		return list;
	}
	
	public String getColumnWhereValueBySynOrderItem(Map synOrderItem, String expression) {
		String acct_time = StringUtil.getStrValue(synOrderItem, "acct_time");
		String lan_id = StringUtil.getStrValue(synOrderItem, "lan_id");
		
		String result = this.getColumnWhereValue(acct_time, lan_id, expression);
		return result;
	}
	
	public String getColumnWhereValue(Map serviceOrder, String expression) {
		String service_order_id = Const.getStrValue(serviceOrder, "service_order_id");
		String acct_time = StringUtil.getStrValue(serviceOrder, "acct_time");
		
		String lan_id = DAO.querySingleValue("select d.data_range from s_data_inst d,service_order s"
				+ " where d.data_inst_id = s.data_inst_id and s.service_order_id = ?", new String[]{service_order_id});
		
		String result = this.getColumnWhereValue(acct_time, lan_id, expression);
		return result;
	}

	private String getColumnWhereValue(String acct_time, String lan_id, String expression) {
		if("${area_id}".equals(expression)){
			if(StringUtil.isEmpty(lan_id) || "-1".equals(lan_id)) {
				return "";
			}
			String area_id = DAO.querySingleValue("select area_id from rr_lan_all where lan_id = ? ", new String[]{lan_id});
			return area_id;
		}
		else if("${lan_id}".equals(expression)){
			if(StringUtil.isEmpty(lan_id) || "-1".equals(lan_id)) {
				return "";
			}
			return lan_id;
		}
		else if("${acct_time}".equals(expression)){
			return acct_time;
		}
		else if("${partition_id}".equals(expression)){
			if(StringUtil.isEmpty(acct_time)) {
				return "";
			}
			int partition_id = (Integer.valueOf(acct_time).intValue()%100+Integer.valueOf(acct_time.substring(0, 4)).intValue()%2*12);
			return partition_id+"";
		}
		else {
			return "";
		}
	}

	
}
