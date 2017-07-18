package com.ztesoft.dubbo.mp.data.bo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.dao.DAOUtils;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.DcSystemParamUtil;
import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.dubbo.mp.data.vo.DataSynOrder;
import com.ztesoft.dubbo.mp.data.vo.DataSynOrderItem;
import com.ztesoft.dubbo.mp.data.vo.PublicSchema;
import com.ztesoft.dubbo.se.data.dao.DataScheduleDao;
import com.ztesoft.dubbo.se.data.dao.LoggerDao;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.sql.Sql;

import appfrm.app.util.ListUtil;
import appfrm.app.vo.IVO;
import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;
import spring.util.DBUtil;
import spring.util.SpringContextUtil;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class DataSynOrderBO {
	
	public RpcPageModel querySynOrderItems(Map params) {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));

		StringBuffer sql = new StringBuffer();
		sql.append("select a.src_table_code,a.src_owner,a.exec_date,a.src_sys_code,a.order_item_id,");
		sql.append("a.inf_resp_id,a.lan_id,a.finish_date,a.acct_time,a.src_schema_code,a.state,a.create_date,a.order_id, b.table_code std_table_code ");
		sql.append(" from data_syn_order_item a");
		sql.append(" left join data_syn_order b on a.order_id = b.order_id");
		sql.append(" where 1=1");
		List sqlParams = new ArrayList();
		String[] conds = new String[] { "src_sys_code", "src_schema_code", "src_table_code", "acct_time", "state" };
		for (String cond : conds) {
			if (StringUtils.isNotEmpty(MapUtils.getString(params, cond))) {
				sql.append(" and a.").append(cond).append(" like ?");
				sqlParams.add("%" + MapUtils.getString(params, cond) + "%");
			}
		}
		
		sql.append(" order by a.order_item_id desc");
		PageModel page = DAO.queryForPageModel(sql.toString(), page_size, page_index, sqlParams);
		RpcPageModel result = PageModelConverter.pageModelToRpc(page);
		//RpcPageModel result = DBUtil.getSimpleQuery().queryForRpcPageModel(sql.toString(), null, page_size, page_index, sqlParams.toArray(new String[] {}));
		return result;
	}
	
	public List querySynOrdersByState(String state, int max) {
		StringBuffer sql = new StringBuffer(DataSynOrder.getDAO().newQuerySQL("1=1 ").getSQLString());
		
		List<String> sqlParams = new ArrayList<String>();
		sql.append(" and state = ?");
		sqlParams.add(state);
		sql.append(" limit 0,");
		sql.append(max);
		List result = DAO.queryForMap(sql.toString(), sqlParams.toArray(new String[]{}));
		return result;
	}
	
	public List querySynOrderItemsByState(String state, int max) {
		StringBuffer where = new StringBuffer();
		
		List<String> sqlParams = new ArrayList<String>();
		where.append(" state = ?");
		sqlParams.add(state);
		
		String message_ready_switch = DcSystemParamUtil.getSysParamByCache("MESSAGE_READY_SWITCH");
		if(!"NO_VALID".equalsIgnoreCase(message_ready_switch)){
			where.append(" and (exists(" +
					"select 1 from ods_data_msg o " +
					"where system_id = data_syn_order_item.src_sys_code " +
					"and (owner = data_syn_order_item.src_owner or data_syn_order_item.src_owner is null) " +
					"and lower(table_code) = lower(data_syn_order_item.src_table_code) " +
					"and acct_month = data_syn_order_item.acct_time " +
					"and msg_flag = 'T' " +
					") " +
					"or exists(" +
					"select 1 from data_syn_order d, c_data_ability a " +
					"where d.service_id = a.service_id and a.is_static_table = '1' and d.order_id = data_syn_order_item.order_id " +
					")" +
					")");
		}
		
		where.append(" order by exec_date asc");//优先未处理过的
		
		where.append(" limit 0,");
		where.append(max);
		
		
		String sql = DataSynOrderItem.getDAO().newQuerySQL(where.toString()).getSQLString();
		List result = DAO.queryForMap(sql, sqlParams.toArray(new String[]{}));
		return result;
	}
	
	public RpcPageModel querySynOrderItemLogs(Map params) {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));

		StringBuffer sql = new StringBuffer(Sql.SYS_SQLS.get("SELECT_DATA_SYN_ORDER_LOG_SQL"));
		List sqlParams = new ArrayList();
		if (StringUtils.isNotEmpty(MapUtils.getString(params, "order_item_id"))) {
			sql.append(" and order_item_id = ? ");
			sqlParams.add(MapUtils.getString(params, "order_item_id"));
		}
		sql.append(" order by create_date asc");
		RpcPageModel result = DBUtil.getSimpleQuery().queryForRpcPageModel(sql.toString(), null, page_size, page_index,
				sqlParams.toArray(new String[] {}));
		return result;
	}
	
	public void createSynOrders(Map params) {
		Calendar defcal = Calendar.getInstance();
		defcal.setTime(new Date());
		defcal.add(Calendar.MONTH, -1);
		String last_month = DateUtil.formatDate(defcal.getTime(), "yyyyMM");
		Calendar defcalDay = Calendar.getInstance();
		defcalDay.setTime(new Date());
		defcalDay.add(Calendar.DATE, -1);
		String last_day = DateUtil.formatDate(defcalDay.getTime(), "yyyyMMdd");
		
		String sys_code = "HD_PUBLIC";
		
		PublicSchema schema = PublicSchema.getDefPublicSchema();
		if(schema == null){
			throw new RuntimeException("请配置公共库 public_schema");
		}
		String schema_code = schema.schema_code;//这里取公共库的库编码
		String owner = schema.owner;//公共库的协议
		
		String sql = Sql.C_DATA_SQLS.get("GET_SYN_TABLES");
		sql += " and not exists(select 1 from data_syn_order d "
				+ "where d.service_id = a.service_id and d.acct_time in(?, ?))";
		
		List<Map> syn_tables = DBUtil.getSimpleQuery().queryForMapListBySql2(sql,
				new String[] {last_month, last_day});
		List sqlParams = new ArrayList();
		for (Map m : syn_tables) {
			String table_code = MapUtils.getString(m, "data_code");//这里是界面选择数据编码,其实就等于标准表名
			String extract_freq = MapUtils.getString(m, "extract_freq");
			
			String service_id = MapUtils.getString(m, "service_id");
			String src_sys_code = MapUtils.getString(m, "src_sys_code");
			String src_owner = MapUtils.getString(m, "src_owner");
			String src_schema_code = MapUtils.getString(m, "src_schema_code");
			String src_table_code = MapUtils.getString(m, "src_table_code");
			
			String acct_time = null;
			if (KeyValues.EXTRACT_FREQ_MONTH.equals(extract_freq)) {
				acct_time = last_month;
			} else if (KeyValues.EXTRACT_FREQ_DAY.equals(extract_freq)) {
				acct_time = last_day;
			}
			
			List list = new ArrayList();
			list.add(SeqUtil.getSeq("DATA_SYN_ORDER", "ORDER_ID"));
			list.add(sys_code);
			list.add(schema_code);
			list.add(table_code);
			list.add(acct_time);
			list.add(DAOUtils.getFormatedDate());
			list.add(null);
			list.add(null);
			list.add(KeyValues.SYN_ORDER_STATE_001);
			
			list.add(owner);
			list.add(service_id);
			list.add(src_sys_code);
			list.add(src_owner);
			list.add(src_schema_code);
			list.add(src_table_code);
			
			sqlParams.add(list.toArray(new String[] {}));

			if (sqlParams.size() > 100) {
				DBUtil.getSimpleQuery().batchUpdate(Sql.SYS_SQLS.get("INSERT_DATA_SYN_ORDER_SQL"), sqlParams);
				sqlParams.clear();
			}
		}
		if (sqlParams.size() > 0) {
			DBUtil.getSimpleQuery().batchUpdate(Sql.SYS_SQLS.get("INSERT_DATA_SYN_ORDER_SQL"), sqlParams);
		}
	}
	
	public void createSynOrderItems(Map order) {
		String order_id = MapUtils.getString(order, "order_id");
		String sys_code = MapUtils.getString(order, "sys_code");
		String schema_code = MapUtils.getString(order, "schema_code");
		String table_code = MapUtils.getString(order, "table_code");
		String acct_time = MapUtils.getString(order, "acct_time");
		String service_id = MapUtils.getString(order, "service_id");
		
		List<IVO> vos = DataSynOrderItem.getDAO().query(" order_id = ? and acct_time = ?", order_id, acct_time);
		if(!ListUtil.isEmpty(vos)){
			return;
		}
		
		// 根据转换关系得出来源的表信息
		String src_sys_code = MapUtils.getString(order, "src_sys_code");
		String src_owner = MapUtils.getString(order, "src_owner");
		String src_schema_code = MapUtils.getString(order, "src_schema_code");
		String src_table_code = MapUtils.getString(order, "src_table_code");
		String lan_id = "-1";
		
		String mappingSql = "select src_sys_code,schema_code,src_table_code,lan_id "
				+ "from table_mapping where std_sys_code = ? and std_schema_code = ? and std_table_code = ?";
		List<Map> list = DAO.queryForMap(mappingSql, sys_code, schema_code, table_code);
		if(!ListUtil.isEmpty(list)){
			for(Map mapping : list){
				src_table_code = MapUtils.getString(mapping, "src_table_code");
				lan_id = MapUtils.getString(mapping, "lan_id");
				
				this.createSynOrderItem(order, src_sys_code, src_owner, src_schema_code, src_table_code, lan_id);
			}
		}
		else {
			//没有映射转换关系的话
			
			String lan_division = null;
			DataScheduleDao dao = (DataScheduleDao) SpringContextUtil.getBean("dataScheduleDao");
			List<Map> synWhereList = dao.getColumnWhereList(service_id, KeyValues.WHERE_TYPE_SYN);
			if(synWhereList != null){
				for(Map where : synWhereList){
					String column_code = StringUtil.getStrValue(where, "column_code");
					String expression = StringUtil.getStrValue(where, "expression");
					String create_new_partition = StringUtil.getStrValue(where, "create_new_partition");
					if("${lan_id}".equals(expression) && KeyValues.CREATE_NEW_PARTITION_YES.equals(create_new_partition)){
						lan_division = column_code;
					}
				}
			}
			//动态表名  或者 是按本地网分区的(因为是分区字段,所以要逐个本地网同步才不会报错)
			if(src_table_code.indexOf("${lan_id}")!=-1 || lan_division != null){
				String lan_sql = "select c.lan_id from c_data_lan c,c_data_ability a "
						+ "where c.ability_id = a.ability_id and a.service_id = ?";
				List<Map> lanList = DAO.queryForMap(lan_sql, service_id);
				
				if(ListUtil.isEmpty(lanList)){
					lanList = DAO.queryForMap("select lan_id from rr_lan");
				}
				
				String temp_src_table_code = src_table_code;
				for(Map lan : lanList){
					lan_id = StringUtil.getStrValue(lan, "lan_id");
					src_table_code = temp_src_table_code.replace("${lan_id}", lan_id);
					
					this.createSynOrderItem(order, src_sys_code, src_owner, src_schema_code, src_table_code, lan_id);
				}
			}
			else if(src_table_code.indexOf("${acct_time}")!=-1){
				src_table_code = src_table_code.replace("${acct_time}", acct_time);
				this.createSynOrderItem(order, src_sys_code, src_owner, src_schema_code, src_table_code, lan_id);
			}
			else {
				//直接取源表
				this.createSynOrderItem(order, src_sys_code, src_owner, src_schema_code, src_table_code, lan_id);
			}
		}
	}
	
	private void createSynOrderItem(Map order, 
			String src_sys_code, String src_owner, String src_schema_code, String src_table_code, String lan_id) {
		DataSynOrderItem item = new DataSynOrderItem();
		item.readFromMap(order);
		item.order_item_id = SeqUtil.getSeq(DataSynOrderItem.TABLE_CODE, DataSynOrderItem.PK_ID);
		item.src_sys_code = src_sys_code;
		item.src_owner = src_owner;
		item.src_schema_code = src_schema_code;
		item.src_table_code = src_table_code;
		item.lan_id = lan_id;
		item.create_date = DateUtil.getFormatedDateTime();
		item.state = KeyValues.SYN_ORDER_ITEM_STATE_001;
		item.re_run_flag = KeyValues.RE_RUN_FLAG_NO;
		DataSynOrderItem.getDAO().insert(item);
	}
	
	public Map updateSynOrder(String order_id, Map changes) {
		if(StringUtil.isEmpty(order_id)){
			return null;
		}
		
		DataSynOrder order = (DataSynOrder) DataSynOrder.getDAO().findById(order_id);
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
		DataSynOrder.getDAO().updateParmamFieldsByIdSQL(fields).update(order);

		return null;
	}
	
	public Map updateSynOrderItem(String order_item_id, Map changes) {
		if(StringUtil.isEmpty(order_item_id)){
			return null;
		}
		
		DataSynOrderItem orderItem = (DataSynOrderItem) DataSynOrderItem.getDAO().findById(order_item_id);
		if(orderItem == null){
			return null;
		}
		Set<String> keys = changes.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String key = it.next();
			String value = (String) changes.get(key);
			orderItem.set(key, value);
		}
		String[] fields = (String[]) orderItem.updateFieldSet.toArray(new String[] {});
		DataSynOrderItem.getDAO().updateParmamFieldsByIdSQL(fields).update(orderItem);

		return null;
	}

	public boolean messageReady(Map params) {
		String message_ready_switch = DcSystemParamUtil.getSysParamByCache("MESSAGE_READY_SWITCH");
		if("NO_VALID".equalsIgnoreCase(message_ready_switch)){
			return true;
		}
		String src_owner = StringUtil.getStrValue(params, "src_owner");
		String src_sys_code = StringUtil.getStrValue(params, "src_sys_code");
		String src_table_code = StringUtil.getStrValue(params, "src_table_code");
		String acct_time = StringUtil.getStrValue(params, "acct_time");
		String lan_id = StringUtil.getStrValue(params, "lan_id");
		
		String sql = "select 1 from ods_data_msg "
				+ "where system_id = ? "
				+ "and owner = ? "
				+ "and lower(table_code) = lower(?) "
				+ "and acct_month = ? " //这里的acct_month其实是账期不一定指月份
				+ "and latn_id = ? "
				+ "and msg_flag = 'T'";
		
		List list = DAO.queryForMap(sql, src_sys_code, src_owner, src_table_code, acct_time, lan_id);
		if(ListUtil.isEmpty(list)){
			return false;
		}
		
		return true;
	}
	
	public boolean allSuccess(String order_id) {
		String sql = "select * from data_syn_order_item where order_id = ?";
		List<String> sqlParams = new ArrayList<String>();
		sqlParams.add(order_id);
		sqlParams.add(KeyValues.SYN_ORDER_STATE_100);
		List<Map> list = DAO.queryForMap(sql + " and state <> ?", sqlParams.toArray(new String[]{}));
		//存在状态不等于同步成功的则认为失败
		if(!ListUtil.isEmpty(list)){
			return false;
		}
		else {
			// 如果所有来源表都成功了,那么最终写big_data_msg表
			/**
			List<Map> successList = DAO.queryForMap(sql + " and state = ?", sqlParams.toArray(new String[]{}));
			Map order = this.getDataSynOrderDetails(order_id);
			
			for(Map orderItem : successList){
				orderItem.put("order", order);
				this.writeBigDataMsg(orderItem);
			}**/
		}
		return true;
	}

	public void writeBigDataMsg(Map params) {
		Map order = (Map) params.get("order");
		if(order == null){
			String order_id = StringUtil.getStrValue(params, "order_id");
			order = this.getDataSynOrderDetails(order_id);
		}
		
		String system_id = "99";
		String owner = StringUtil.getStrValue(order, "owner");
		String table_code = StringUtil.getStrValue(order, "table_code");
		
		String acct_time = StringUtil.getStrValue(params, "acct_time");
		String latn_id = StringUtil.getStrValue(params, "lan_id");
		String task_name = table_code;
		
		String sql = "insert into big_data_msg(system_id, owner, table_code, acct_month, latn_id, task_name, state_date, msg_flag, comments) "
				+ "values(?, ?, ?, ?, ?, ?, now(), 'T', '系统自动同步')";
		DAO.update(sql, system_id, owner, table_code, acct_time, latn_id, task_name);
	}

	public void reSyn(String order_item_id) {
		//先更新为待处理状态
		Map changes = new HashMap();
		changes.put("state", KeyValues.SYN_ORDER_ITEM_STATE_001);
		changes.put("re_run_flag", KeyValues.RE_RUN_FLAG_YES);
		changes.put("exec_date", DateUtil.getFormatedDateTime());
		
		this.updateSynOrderItem(order_item_id, changes);
		
		// 日志
		LoggerDao dao = new LoggerDao();
		changes.put("order_item_id", order_item_id);
		changes.put("state", "100");
		changes.put("log_desc", "执行重新同步操作");
		dao.writeSynLog(changes);
	}

	public Map getDbToHiveParam(Map params) {
		Map result = new HashMap();
		
		String order_item_id = StringUtil.getStrValue(params, "order_item_id");
		String order_src_table_code = StringUtil.getStrValue(params, "src_table_code");

		String sql = Sql.C_DATA_SQLS.get("GET_SYN_TABLES");
		sql += " and exists(select 1 from data_syn_order o,data_syn_order_item oi "
				+ "where o.service_id = a.service_id and o.order_id = oi.order_id and oi.order_item_id = ?)";
		
		List list = DAO.queryForMap(sql, order_item_id);
		if(!ListUtil.isEmpty(list)){
			result = (Map) list.get(0);
			String src_sys_code = StringUtil.getStrValue(result, "src_sys_code");
			String src_schema_code = StringUtil.getStrValue(result, "src_schema_code");
			String service_id = StringUtil.getStrValue(result, "service_id");
			if("HD".equalsIgnoreCase(src_sys_code)){
				
			}
			else {
				// 大数据平台对应的协议
				String protocol_sql = 
						"select uri,user_name,password,schema_type,sys_code,sys_name "
						+ "from omp_datasource_protocol where schema_code = ?";
				List protocolList = DAO.queryForMap(protocol_sql, src_schema_code);
				if(!ListUtil.isEmpty(protocolList)){
					Map protocol = (Map) protocolList.get(0);
					result.putAll(protocol);
				}
				
				//这里取是因为src_table_code = wid_serv_mon_${lan_id}但是实际的是wid_serv_mon_1100这种情况
				String columns_sql = "select c.column_code,c.column_name,o.id as src_column_id "
						+ "from c_data_column c left join omp_metadata_table_columns o "
						+ "on o.column_code = c.column_code "
						+ "and lower(o.table_code) = ? "
						+ "and lower(o.schema_code) = ? "
						+ "where c.service_id = ?";
				List columnList = DAO.queryForMap(columns_sql, order_src_table_code.toLowerCase(), src_schema_code.toLowerCase(), service_id);
				result.put("columnList", columnList);
			}
		}
		
		return result;
	}

	public Map getDataSynOrderDetails(String orderId) {
		String sql = "select * from data_syn_order where order_id=?";
		List<Map> rsList =  DAO.queryForMap(sql, orderId);
		if(rsList==null || rsList.size()==0) return new HashMap();
		else return rsList.get(0);
	}

	public List<Map> getDataColumnByDataCode(String dataCode) {
		String sql = "select * from c_data_column  where service_id in (select service_id from c_data_ability where data_code = ?)";
		return  DAO.queryForMap(sql, dataCode);
	}

	public Map getColumnTypeTransMap() {
		String sql = "select * from bdp_attribute_value where attr_id = '32133'";
		List<Map> rsList1 = DAO.queryForMap(sql, new String[]{});
		Map transMap = new HashMap();
		if(rsList1!=null){
			for(Map tMap : rsList1){
				transMap.put(tMap.get("attr_value_desc"), tMap.get("attr_value"));
			}
		}
		transMap.put("日期", "TIMESTAMP");
		transMap.put("日期时间", "TIMESTAMP");
		transMap.put("字符", "STRING");
		transMap.put("BLOB", "STRING");
		transMap.put("CLOB", "STRING");
		transMap.put("数字", "DOUBLE");
		return transMap;
	}

	/**
	 * 是否静态表
	 * @param params
	 * @return
	 */
	public boolean isStaticTable(Map params) {
		String orderId = Const.getStrValue(params, "order_id");
		String sql = Sql.S_DATA_SQLS.get("get_dataability_by_orderId");
		List<Map> rs = DAO.queryForMap(sql, new String[]{orderId});
		if(rs==null || rs.size() == 0) return false;
		String isStaticTable = Const.getStrValue(rs.get(0), "is_static_table");
		if("1".equals(isStaticTable)) return true;
		return false;
	}
	
	/**
	 * 根据消息表判断表是否已经同步了
	 * @param synOrderItem
	 * @return
	 */
	public boolean hasSyn(Map synOrderItem){
		boolean is_static_table = (Boolean) synOrderItem.get("is_static_table");
		Map order = (Map) synOrderItem.get("order");
		
		String system_id = "99";
		String owner = StringUtil.getStrValue(order, "owner");
		String table_code = StringUtil.getStrValue(order, "table_code");
		
		String acct_time = StringUtil.getStrValue(synOrderItem, "acct_time");
		String latn_id = StringUtil.getStrValue(synOrderItem, "lan_id");
		
		List<String> sqlParams = new ArrayList<String>();
		String sql = "select 1 from big_data_msg "
				+ "where system_id = ? "
				+ "and owner = ? "
				+ "and table_code = ? ";
		sqlParams.add(system_id);
		sqlParams.add(owner);
		sqlParams.add(table_code);
		if(!is_static_table){
			sql += "and acct_month = ? "
				+ "and latn_id = ?";
			sqlParams.add(acct_time);
			sqlParams.add(latn_id);
		}
		
		List list = DAO.queryForMap(sql, sqlParams.toArray(new String[]{}));
		boolean result = false;
		if(!ListUtil.isEmpty(list)){
			result = true;
		}
		return result;
	}
}
