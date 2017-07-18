package com.ztesoft.dubbo.mp.data.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.dubbo.mp.data.util.BooleanWrap;
import com.ztesoft.dubbo.mp.data.util.ImportValidator;
import com.ztesoft.dubbo.mp.data.util.SrcUtil;
import com.ztesoft.dubbo.mp.data.vo.CDataAblity;
import com.ztesoft.dubbo.mp.data.vo.CDataColumn;
import com.ztesoft.dubbo.mp.data.vo.CDataLan;
import com.ztesoft.dubbo.mp.data.vo.CDataColumnWhere;
import com.ztesoft.dubbo.mp.data.vo.CDataService;
import com.ztesoft.dubbo.mp.data.vo.CDataSrc;
import com.ztesoft.dubbo.mp.data.vo.CDataSrcTable;
import com.ztesoft.dubbo.mp.sys.bo.CacheBO;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.sql.Sql;

import appfrm.app.util.ListUtil;
import appfrm.app.util.SeqUtil;
import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;

@Repository
@SuppressWarnings({"rawtypes","unchecked"})
public class DataMgrDao {
	
	public List getCatalog(String pcatalogId){
		
		if(StringUtil.isNotEmpty(pcatalogId)){
			String sql = Sql.C_DATA_SQLS.get("catalog_sql");
			return DAO.queryForMap(sql, new String[]{"00A","00A",pcatalogId});
		}
		else{
			String sql = Sql.C_DATA_SQLS.get("catalog_sql_all");
			return DAO.queryForMap(sql, new String[]{"00A"});
		}
		
	}
	
	public RpcPageModel getAbilityList(Map params){
		//System.out.println(params);

		int page = 1;
		int pagesize = 10;
		try{
			page = Integer.parseInt(Const.getStrValue(params, "page"));
			pagesize = Integer.parseInt(Const.getStrValue(params, "rows"));
		}catch(Exception e){e.printStackTrace();}
		
		String catalogId = Const.getStrValue(params, "catalog_id");
		String dataName = Const.getStrValue(params, "data_name");
		String dataCode = Const.getStrValue(params, "data_code");
		String firstDivision = Const.getStrValue(params, "first_division");
		String secondDivision = Const.getStrValue(params, "second_division");
		String extractFreq = Const.getStrValue(params, "extract_freq");
		String state = Const.getStrValue(params, "state");
		
		
		StringBuilder sql = new StringBuilder(Sql.C_DATA_SQLS.get("data_ability_list"));
		
		List<String> sqlParams = new ArrayList<String>();
		sqlParams.add("00X");
		
		//补充目录筛选条件
		if(StringUtil.isNotEmpty(catalogId)){
			sql.append("and cds.catalog_id = ? ");
			sqlParams.add(catalogId);
		}
		
		if(StringUtil.isNotEmpty(dataName)){
			sql.append("and cda.data_name like ? ");
			sqlParams.add("%"+dataName+"%");
		}
		
		if(StringUtil.isNotEmpty(dataCode)){
			sql.append("and cda.data_code like ? ");
			sqlParams.add("%"+dataCode+"%");
		}
		
		if(StringUtil.isNotEmpty(extractFreq)){
			sql.append("and csrc.extract_freq = ? ");
			sqlParams.add(extractFreq);
		}
		
		if(StringUtil.isNotEmpty(firstDivision)){
			sql.append("and cda.first_division = ? ");
			sqlParams.add(firstDivision);
		}
		
		if(StringUtil.isNotEmpty(secondDivision)){
			sql.append("and cda.second_division = ? ");
			sqlParams.add(secondDivision);
		}
		
		if(StringUtil.isNotEmpty(state)){
			sql.append("and cds.state = ? ");
			sqlParams.add(state);
		}
		
		
		
		PageModel result = DAO.queryForPageModel(sql.toString(), pagesize, page, sqlParams.toArray(new String[]{}));;
		return PageModelConverter.pageModelToRpc(result);
	}
	
	/**
	 * 获取使用中的申请个数
	 * @return
	 */
	public int getInUsingApplyCount(String serviceId){
		String sql = Sql.C_DATA_SQLS.get("get_in_using_service_count");
		String count = DAO.querySingleValue(sql, new String[]{serviceId});
		if(StringUtil.isEmpty(count) || !StringUtil.isNum(count)) return -1;	//非法数据返回-1，阻止程序认为没有数据了
		else return Integer.parseInt(count);
	}
	
	public void changeServiceState(String serviceId,String newState){
		String sql = Sql.C_DATA_SQLS.get("update_service_state");
		DAO.update(sql, new String[]{newState,serviceId});
	}

	/**
	 * 根据service_id获取数据服务
	 * @param params
	 * @return
	 */
	public Map getDataServiceById(Map params) {
		Map result = new HashMap();
		
		String service_id = Const.getStrValue(params, "service_id");
		if(service_id == null || "".equals(service_id)){
			return result;
		}
		StringBuilder sql = new StringBuilder(Sql.C_DATA_SQLS.get("data_ability_list"));
		sql.append(" and cds.service_id = ?");
		List<String> sqlParams = new ArrayList<String>();
		sqlParams.add("00X");
		sqlParams.add(service_id);
		List list =  DAO.queryForMap(sql.toString(), sqlParams.toArray(new String[]{}));
		
		if(!ListUtil.isEmpty(list)){
			result = (Map) list.get(0);
		}
		return result;
	}
	
	public List getSrcSysList(){
		String sql = Sql.C_DATA_SQLS.get("get_src_sys");
		return DAO.queryForMap(sql, new String[]{});
	}
	
	public List getSrcSchemaList(Map params){
		String sys_code = Const.getStrValue(params, "sys_code");	//来源平台过滤条件
		
		String sql = Sql.C_DATA_SQLS.get("get_src_schema");
		List<String> sqlParams = new ArrayList<String>();
		//sql += " and a.schema_type = 'HIVE'";
		if(StringUtil.isNotEmpty(sys_code)){
			sql += " and b.sys_code = ? ";
			sqlParams.add(sys_code);
		}
				
		return DAO.queryForMap(sql, sqlParams.toArray(new String[]{}));
		
	}
	
	public List getSrcTableList(Map params){
		
		String srcSys = Const.getStrValue(params, "src_sys");
		
		String schemaId = Const.getStrValue(params, "schema_id");
		String tableCode = Const.getStrValue(params, "table_code");
		
		StringBuilder sql = null;
		if(KeyValues.SRC_SYS_HD.equalsIgnoreCase(srcSys))
			sql = new StringBuilder(Sql.C_DATA_SQLS.get("get_src_table"));
		else{
			sql = new StringBuilder(Sql.C_DATA_SQLS.get("get_omp_src_table"));
		}
		List<String> sqlParams = new ArrayList<String>();
		
		if(StringUtil.isNotEmpty(schemaId)){
			sql.append(" and mt.schema_code = ? ");
			sqlParams.add(schemaId);
		}
		
		if(StringUtil.isNotEmpty(tableCode)){
			sql.append(" and lower(mt.table_code) like ? ");
			sqlParams.add("%"+tableCode.toLowerCase() + "%");
		}
		
		List list = DAO.queryForMap(sql.append(" order by mt.table_code asc").toString(), sqlParams.toArray(new String[]{}));
		return list;
		
	}
	
	public RpcPageModel getSrcColumn(Map params){
		String srcSys = Const.getStrValue(params, "src_sys");
		
		String tableCode = Const.getStrValue(params, "table_code");
		String schemaCode = Const.getStrValue(params, "schema_code");
		String columnCode = Const.getStrValue(params, "column_code");
		String keyword = Const.getStrValue(params, "keyword");
		List<String> selected = (List<String>)params.get("has_selected");
		int page = 1;
		int pagesize = 10;
		try{
			page = Integer.parseInt(Const.getStrValue(params, "page"));
			pagesize = Integer.parseInt(Const.getStrValue(params, "rows"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		//根据table_code获取其中任意一个table_id (table_code是变量，可能指向多张表，而这些表的表结构从业务上是一致的，所以这里查找第一张表)
		String sql = "";
		if(KeyValues.SRC_SYS_HD.equals(srcSys))
			sql = Sql.C_DATA_SQLS.get("validate_src_table");
		else
			sql = Sql.C_DATA_SQLS.get("validate_omp_src_table");
		tableCode  = tableCode.replace("%", "[0-9]+").toLowerCase();
		if(StringUtil.isEmpty(schemaCode) || StringUtil.isEmpty(tableCode)){
			return null;
		}
		List<Map> tableList = DAO.queryForMap(sql, new String[]{schemaCode,tableCode});
		
		if(tableList == null || tableList.size() == 0){
			PageModel pm = new PageModel();
			pm.setList(new ArrayList());
			pm.setTotal(0);
			return PageModelConverter.pageModelToRpc(pm);
		}else{
			String reallyTableCode = tableCode;
			if(tableList.size() > 0){
				Map tableMap = tableList.get(0);
				reallyTableCode = Const.getStrValue(tableMap, "table_code");
			}
			StringBuilder sql2 = null;
			if(KeyValues.SRC_SYS_HD.equals(srcSys))
				sql2 = new StringBuilder(Sql.C_DATA_SQLS.get("get_src_column")).append(" and lower(csc.table_code) = ? ");
			else
				sql2 = new StringBuilder(Sql.C_DATA_SQLS.get("get_omp_src_column")).append(" and lower(csc.table_code) = ? ");
			
			sql2.append("and csc.schema_code = ? ");
			
			List<String> sqlParams = new ArrayList<String>();
			sqlParams.add(reallyTableCode.toLowerCase());
			sqlParams.add(schemaCode);
			if(StringUtil.isNotEmpty(columnCode)){
				sql2.append( " and lower(csc.column_code) like ? ");
				sqlParams.add("%"+columnCode.toLowerCase()+"%");
			}
			
			if(StringUtil.isNotEmpty(keyword)){
				keyword = "%"+keyword+"%";
				sql2.append(" and (csc.column_code like ? or csc.column_name like ? or csc.column_desc like ? )");
				sqlParams.add(keyword);
				sqlParams.add(keyword);
				sqlParams.add(keyword);
			}
			
			if(selected != null && selected.size()>0){
				sql2.append(" and csc.column_id not in (");
				for(int i=0;i<selected.size();i++){
					String column_id = selected.get(i);
					if(i!=0) sql2.append(",");
					sql2.append("?");
					sqlParams.add(column_id);
				}
				sql2.append(" ) ");
			}
			
			StringBuffer sql3 = new StringBuffer();
			sql3.append(" select * from ( ");
			sql3.append(sql2);
			sql3.append(" ) t ");
			sql3.append(" left join c_algorithms ca on t.desensitization_rule = ca.algorithm_id ");
			
			sql3.append(" order by seq asc ");
			
			PageModel returnModel = DAO.queryForPageModel(sql3.toString(), pagesize, page, 
					sqlParams.toArray(new String[]{}));
			
			if(!KeyValues.SRC_SYS_HD.equalsIgnoreCase(srcSys)){
				List<Map> dataList = returnModel.getList();
				List<Map> bdpAttrTypeTrans = DAO.queryForMap(Sql.C_DATA_SQLS.get("bdp_opt_col_type_trans"), new String[]{});
				Map transMap = new HashMap();
				for(Map tmap : bdpAttrTypeTrans){
					transMap.put(tmap.get("attr_value"), tmap.get("attr_value_desc"));
				}
				for(Map map : dataList){
					String transData = (String)transMap.get(map.get("column_type"));
					if(StringUtil.isNotEmpty(transData))
						map.put("column_type", transData);
				}
			}
			
			return PageModelConverter.pageModelToRpc(returnModel);
		}
	}
	
	public List getSrcColumnList(Map params){
		String srcSys = Const.getStrValue(params, "src_sys");
		
		String tableCode = Const.getStrValue(params, "table_code");
		String schemaCode= Const.getStrValue(params, "schema_code");
		
		String sql = "";
		
		if(KeyValues.SRC_SYS_HD.equalsIgnoreCase(srcSys))
			sql = "select * from meta_columns where schema_code = ? and table_code = ?  order by table_code asc";
		else{
			sql = "select * from omp_metadata_table_columns where schema_code = ? and table_code = ?  order by table_code asc";
			sql = "select t1.*,t2.attr_value_desc as column_type_t from ("+sql+") t1,(select * from bdp_attribute_value where attr_id = '32033') t2 where t1.column_type=t2.attr_value";
		}
		
		return DAO.queryForMap(sql, new String[]{schemaCode,tableCode});
	}
	
	public Map validateSrcTable(Map params){
		String srcSys = Const.getStrValue(params, "src_sys");
		String schemaCode = Const.getStrValue(params, "schema_code");
		String tableCode = Const.getStrValue(params, "table_code");
		if(StringUtil.isEmpty(schemaCode)) schemaCode =  Const.getStrValue(params, "schema_id");
		List<Map> dataList  = null;
		String count = "0";
		if(KeyValues.SRC_SYS_HD.equalsIgnoreCase(srcSys)){
			String sql = Sql.C_DATA_SQLS.get("validate_src_table");
			tableCode = tableCode.replace("%", "[0-9]+").toLowerCase();
			dataList = DAO.queryForMap(sql, new String[]{schemaCode,tableCode});
			count = dataList==null?"0":dataList.size()+"";
		}else{
			String sql = Sql.C_DATA_SQLS.get("validate_omp_src_table");
			tableCode = tableCode.replace("%", "[0-9]+");
			dataList = DAO.queryForMap(sql, new String[]{schemaCode.toLowerCase(),tableCode.toLowerCase()});
			count = dataList==null?"0":dataList.size()+"";
		}
		
		Map returnMap = new HashMap();
		returnMap.put("result_count", count);
		returnMap.put("table_list", dataList);
		return returnMap;
	}
	
	public List getAlgorithmsList(Map params){
		String sql = Sql.C_DATA_SQLS.get("get_algorithms");
		return DAO.queryForMap(sql, new String[]{KeyValues.STATE_00A});
	}
	
	public Map addService(Map params){
		Map srcMap = (Map)params.get("src_data");
		Map ablityMap = (Map) params.get("ablity_data");
		List<Map> columnsList = (List<Map>) params.get("column_data");
		String staffId = Const.getStrValue(params, "dubbo_staff_id");
		String catalogId = Const.getStrValue(params, "catalog_id");
		
		//将数据插入数据服务表
		CDataService service = new CDataService();
		String service_id = SeqUtil.getInst().getNext(CDataService.TABLE_CODE, CDataService.PK_ID);
		service.apply_count = "0";
		service.catalog_id = catalogId;
		service.create_time = DateUtil.getFormatedDateTime();
		service.dispatch_count = "0";
		service.operator = staffId;
		service.query_count = "0";
		service.service_id = service_id;
		service.service_name = Const.getStrValue(ablityMap, "data_name");
		service.state = KeyValues.STATE_00B;
		service.getDao().insert(service);
		
		CDataSrc src = new CDataSrc();
		src.readFromMap(srcMap);
		src.service_id = service.service_id;
		src.src_id = SeqUtil.getInst().getNext(CDataSrc.TABLE_CODE, CDataSrc.PK_ID);
		src.getDao().insert(src);
		
		List<Map> srcTables = new ArrayList<Map>();
		//获取所有涉及到的实际表，并且将它们写到c_data_src_table（数据来源关联表）中
		if(KeyValues.SELECT_TABLE_BYFILL.equals(src.select_table_type)){
			String srcTableSql = "";
			if(KeyValues.SRC_SYS_HD.equals(src.src_sys_code))
				srcTableSql = Sql.C_DATA_SQLS.get("validate_src_table");
			else
				srcTableSql = Sql.C_DATA_SQLS.get("validate_omp_src_table");
			
			String filterValue = Const.getStrValue(srcMap, "filter_input");
			String schemaId = Const.getStrValue(srcMap, "src_schema_id");
			filterValue = filterValue.replace("%", "[0-9]+");
			srcTables = DAO.queryForMap(srcTableSql, new String[]{schemaId.toLowerCase(),filterValue.toLowerCase()});
		}else{
			//如果是从界面选择的话，查找这些数据并添加上去
			if(srcMap.get("src_table_code_array") != null)
				srcTables = (List<Map>) srcMap.get("src_table_code_array");
		}
		
		//循环将有需要的表加到C_DATA_SRC_TABLE表
		for(Map srcTableM : srcTables){
			CDataSrcTable srcTable = new CDataSrcTable();
			srcTable.service_id = service.service_id;
			srcTable.src_id = src.src_id;
			srcTable.src_schema_code = src.src_schema_code;
			srcTable.src_sys_code = src.src_sys_code;
			srcTable.src_table_code = Const.getStrValue(srcTableM,"table_code");
			srcTable.src_table_id = SeqUtil.getInst().getNext(CDataSrcTable.TABLE_CODE, CDataSrcTable.PK_ID);
			srcTable.getDao().insert(srcTable);
			
			//更新状态
			//DAO.update(Sql.C_DATA_SQLS.get("update_src_table_status"), new String[]{KeyValues.STATE_00A,Const.getStrValue(srcTableM, "table_id")});
			
		}
		
		//将记录加到C_DATA_ABILITY表
		CDataAblity ablity = new CDataAblity();
		ablity.readFromMap(ablityMap);
		ablity.ability_id = SeqUtil.getInst().getNext(CDataAblity.TABLE_CODE, CDataAblity.PK_ID);
		ablity.service_id = service.service_id;
		ablity.getDao().insert(ablity);
		
		//从c_data_ability中提取数据弄到c_data_lan表中
		List<Map> dataLanList = (List<Map>) ablityMap.get("defined_lan_list");
		if(dataLanList!=null){
			for(Map dataLanMap :dataLanList ){
				CDataLan dataLan = new CDataLan();
				dataLan.readFromMap(dataLanMap);
				dataLan.ability_id = ablity.ability_id;
				dataLan.state = KeyValues.STATE_00A;
				dataLan.getDao().insert(dataLan);
			}
		}
		
		//从c_data_ability中提取数据弄到c_data_column_where表中
		List<Map> whereFieldList = (List<Map>) ablityMap.get("where_map");
		if(whereFieldList!=null){
			for(Map whereMap : whereFieldList){
				String create_new_partition = StringUtil.getStrValue(whereMap, "create_new_partition");
				if(StringUtil.isEmpty(create_new_partition)){
					create_new_partition = KeyValues.CREATE_NEW_PARTITION_NO;
				}
				CDataColumnWhere otherField = new CDataColumnWhere();
				otherField.readFromMap(whereMap);
				otherField.ability_id = ablity.ability_id;
				otherField.state = KeyValues.STATE_00A;
				otherField.service_id = service.service_id;
				otherField.create_new_partition = create_new_partition;
				otherField.new_partition_code = null;
				otherField.getDao().insert(otherField);
			}
		}
		
		//循环所有字段，并添加到数据服务字段信息表
		//获取c_src_column is_acct seq字段信息
		for(int i=0;columnsList!=null && i<columnsList.size();i++){
			Map tmap = columnsList.get(i);
			CDataColumn cc = new CDataColumn();
			cc.readFromMap(tmap);
			cc.service_id = service.service_id;
			cc.column_id = SeqUtil.getInst().getNext(CDataColumn.TABLE_CODE, CDataColumn.PK_ID);
			cc.dst_algorithm = Const.getStrValue(tmap, "algorithms");
			cc.is_acct = Const.getStrValue(tmap, "is_acct");
			cc.seq = Const.getStrValue(tmap, "seq");
			if(StringUtil.isEmpty(cc.seq)){
				cc.seq = String.valueOf(i+1);
			}
			cc.is_dst = Const.getStrValue(tmap, "is_desensitization");
			if(cc.is_dst.trim().equals("1")) {//脱敏，保存脱敏id
				cc.dst_algorithm = Const.getStrValue(tmap, "desensitization_rule");
			}
			if(StringUtil.isEmpty(cc.dst_algorithm)){
				cc.is_dst = KeyValues.NOT_USE_DST_ALGORITHM;
				cc.dst_algorithm = "1";//补充一个值以便能插入成功
			}
			else
				cc.is_dst = KeyValues.USE_DST_ALGORITHM;
			cc.src_column_id = Const.getStrValue(tmap, "column_id");
			
			//获取c_src_column is_acct seq字段信息
			/*
			List<Map> tmList=  DAO.queryForMap(getCSrcColumnInfoSql.toString(), new String[]{cc.src_column_id});
			if(!ListUtil.isEmpty(tmList)) {
				Map tMap = tmList.get(0);
				cc.is_acct = Const.getStrValue(tMap, "is_acct");
				cc.seq = Const.getStrValue(tMap, "seq");
			}
			*/
			
			if(StringUtil.isEmpty(cc.src_column_id) && srcTables.size()>1)
				cc.src_column_id = "1";
			
			if(StringUtil.isEmpty(cc.column_length))
				cc.column_length = "-1";
			
			cc.getDao().insert(cc);
			if(KeyValues.NOT_USE_DST_ALGORITHM.equals(cc.is_dst)){
				DAO.update(Sql.C_DATA_SQLS.get("set_column_dst_null"), new String[]{cc.column_id});
			}
		}
		
		DAO.update(Sql.C_DATA_SQLS.get("update_column_length_tonull"), new String[]{service.service_id});
		
		//多个表的时候，将src_column_id字段置空
		if(srcTables!=null && srcTables.size()>1)
			DAO.update(Sql.C_DATA_SQLS.get("set_src_column_null"), new String[]{service.service_id});
		
		Map returnMap = new HashMap();
		returnMap.put("state", KeyValues.RESPONSE_SUCCESS);
		returnMap.put("service_id", service_id);
		return returnMap;
	}

	public List getDataColumn(Map m) {
		String abilityId = MapUtils.getString(m, "ability_id");
		String isDst = MapUtils.getString(m, "is_dst");
		String isFromSafe = MapUtils.getString(m, "isFromSafe");
		StringBuffer sql = new StringBuffer(Sql.C_DATA_SQLS.get("select_data_column"));
		List<String> pList = new ArrayList<String>();
		if(StringUtils.isNotBlank(abilityId)) {
			sql.append(" and g.ability_id = ? ");
			pList.add(abilityId);
		}
		if(StringUtils.isNotBlank(isDst)) {
			sql.append(" and g.is_dst=? ");
			pList.add(isDst);
		}
		//安全数据申请中，只要md5和rsa类型的算法
		if(StringUtils.isNotBlank(isFromSafe)) {
			sql.append(" and ( ca.type = ? or ca.type = ? ) ");
			pList.add(KeyValues.ALG_TYPE_0);
			pList.add(KeyValues.ALG_TYPE_1);
		}
		List list = DAO.queryForMap(sql.toString(), pList.toArray(new String[]{}));
		return ListUtil.isEmpty(list)?null:list;
	}
	
	/**
	 * 将c_src_table表的数据从共享变成非共享
	 * @param serviceId
	 * @return
	 */
	public void disabledSrcTableState(String serviceId){
		
		List<Map> srcTable = DAO.queryForMap(Sql.C_DATA_SQLS.get("get_c_data_src_table"), new String[]{serviceId});
		if(srcTable == null || srcTable.size() == 0) return;
		
		//获取来源平台和来源库
		List<Map> srcDetailsList = DAO.queryForMap(Sql.C_DATA_SQLS.get("get_data_src_details"), new String[]{serviceId});
		if(srcDetailsList == null || srcDetailsList.size() == 0) return;
		Map srcDetails = srcDetailsList.get(0);
		String sysCode = Const.getStrValue(srcDetails, "src_sys_code");
		String schemaCode = Const.getStrValue(srcDetails, "src_schema_code");
		
		for(Map tmap:srcTable){
			String tableCode = Const.getStrValue(tmap, "src_table_code");
			
			//获取使用了这一个表的当前是有效状态的服务条数
			String countSql = Sql.C_DATA_SQLS.get("get_use_target_table_count")+" and cds.service_id != ?";
			String usingCount = DAO.querySingleValue(countSql, new String[]{sysCode,schemaCode,schemaCode,tableCode,serviceId});
			
			//已经没其它服务在使用这个表，将他设成非共享状态
			//这个已经用不上了 TODO 确定后注释代码删掉
			/*
			if("0".equals(usingCount)){
				String tableId = DAO.querySingleValue(Sql.C_DATA_SQLS.get("get_src_table_id"), new String[]{sysCode,schemaCode,tableCode});
				DAO.update(Sql.C_DATA_SQLS.get("update_src_table_status"), new String[]{KeyValues.STATE_00B,tableId});
			}
			*/
			
		}
		
	}

	public Map validateDataCode(Map m) {
		String dataCode = Const.getStrValue(m, "data_code");
		Map returnResult = new HashMap();
		returnResult.put("result", false);
		if(StringUtils.isEmpty(dataCode))  return returnResult;
		dataCode = dataCode.trim();
		String sql = Sql.C_DATA_SQLS.get("validate_data_code");
		String count = DAO.querySingleValue(sql, new String[]{dataCode});
		if(!"0".equals(count)) return returnResult;
		else{
			returnResult.put("result", true);
			return returnResult;
		}
	}

	public Map getAccountColumn(Map m) {
		String tableCode = Const.getStrValue(m, "table_code");
		String schemaId = Const.getStrValue(m, "schema_id");
		
		String validationSql = Sql.C_DATA_SQLS.get("validate_src_table");
		
		
		tableCode  = tableCode.replace("%", "[0-9]+");
		List<Map> tableList = DAO.queryForMap(validationSql, new String[]{tableCode,schemaId});
		
		if(tableList==null || tableList.size()==0) return null;
		
		Map targetTable = tableList.get(0);
		String tableId = Const.getStrValue(targetTable, "table_id");
		
		String sql = Sql.C_DATA_SQLS.get("get_account_column");
		List<Map> result = DAO.queryForMap(sql, new String[]{tableId});
		if(result == null || result.size() == 0) return null;
		return result.get(0);
	}

	public Map importData(Map params) {
		Map returnMap = new HashMap();
		ImportValidator validator = new ImportValidator();
		returnMap.put("state", KeyValues.RESPONSE_FAILED);
		if(params == null){
			returnMap.put("tips", "对象数据无法解析");
			return returnMap;
		}
		List<Map> columnList = (List<Map>) params.get("column_list");
		Map baseInfo = (Map) params.get("base_info");
		if(columnList==null || baseInfo==null || columnList.size()==0 ){
			returnMap.put("tips", "对象数据无法解析");
			return returnMap;
		}
		
		//真正进行转换的操作
		Map baseInfoColTrans = this.getColTransMap(KeyValues.XLS_IMPORT_TYPE1);
		Map columnColTrans = this.getColTransMap(KeyValues.XLS_IMPORT_TYPE2);
		
		//columnList添加属性
		for(Map tmap:columnList){
			tmap.put("column_code", tmap.get(columnColTrans.get("字段编码")));
		}
		
		//获取并校验归属目录
		String belongCatalogCode = (String)baseInfo.get(baseInfoColTrans.get("归属目录编码"));
		if(!validator.checkCatalog(belongCatalogCode).getResult()){
			returnMap.put("tips", BooleanWrap.getLASTUSE().getTips("归属目录不存在"));
			return returnMap;
		}
		//获取并校验来源平台，来源库，来源表
		String srcSystem = (String) baseInfo.get(baseInfoColTrans.get("数据来源平台"));
		String srcSchema = (String) baseInfo.get(baseInfoColTrans.get("来源库"));
		String srcTable = (String) baseInfo.get(baseInfoColTrans.get("来源表"));
		if(!validator.checkSrcInfo(srcSystem, srcSchema, srcTable).getResult()){
			returnMap.put("tips", BooleanWrap.getLASTUSE().getTips("来源信息（来源平台、来源库，来源表）无法对应，请检查"));
			return returnMap;
		}
		
		List<Map> reallyTableList = (List<Map>) BooleanWrap.getLASTUSE().getData();
		
		//获取并校验抽取频率
		String extractType = validator.getStaticTransData(
				(String) baseInfo.get(baseInfoColTrans.get("抽取频率")),KeyValues.EXTRACT_FREQ_CODE);
		if(StringUtil.isEmpty(extractType)){
			returnMap.put("tips", "抽取频率 无法识别，清输入正确的抽取频率");
			return returnMap;
		}
		
		//输入并校验历史数据抽取数据
		String isHistory = validator.getStaticTransData(
				(String)baseInfo.get(baseInfoColTrans.get("是否历史数据抽取")),KeyValues.YES_OR_NOT_CODE);
		String historyAcctBegin = (String) baseInfo.get(baseInfoColTrans.get("抽取历史账期（起）")) ;
		if(!validator.checkHistoryExtract(isHistory, historyAcctBegin, extractType).getResult()){
			returnMap.put("tips", BooleanWrap.getLASTUSE().getTips("历史账期校验失败，请检查是否历史账期提取、历史账期字段"));
			return returnMap;
		}
		
		//获取并校验数据名称
		String dataName = (String) baseInfo.get(baseInfoColTrans.get("数据名称"));
		if(StringUtil.isEmpty(dataName)){
			returnMap.put("tips", "请填写数据名称");
			return returnMap;
		}
		
		//获取并校验数据编码
		String dataCode = (String) baseInfo.get(baseInfoColTrans.get("数据编码"));
		if(!validator.checkDateCode(dataCode).getResult()){
			returnMap.put("tips", BooleanWrap.getLASTUSE().getTips("数据编码校验不通过"));
			return returnMap;
		}
		
		//获取并校验一级分区，二级分区
		String firstDivsion = validator.getStaticTransData(
				(String) baseInfo.get(baseInfoColTrans.get("一级分区")),KeyValues.FIRST_DIVISION_CODE);
		String secondDivsion = validator.getStaticTransData(
				(String) baseInfo.get(baseInfoColTrans.get("二级分区")), KeyValues.SECOND_DIVISION_CODE);
		if(StringUtil.isEmpty(secondDivsion) && StringUtil.isNotEmpty((String)baseInfo.get(baseInfoColTrans.get("二级分区")))){
			returnMap.put("tips","二级分区无法识别");
			return returnMap;
		}
		if(!validator.checkDivsion(firstDivsion, secondDivsion).getResult()){
			returnMap.put("tips", BooleanWrap.getLASTUSE().getTips("一级分区、二级分区无法正确识别"));
			return returnMap;
		}
		
		//获取并校验数据提供时间
		String beginSupTime = validator.getStaticTransData(
				(String) baseInfo.get(baseInfoColTrans.get("提供时间（开始）")),KeyValues.DISPATH_TIME_CODE_PRE+extractType);
		String endSupTime = validator.getStaticTransData(
				(String) baseInfo.get(baseInfoColTrans.get("提供时间（结束）")),KeyValues.DISPATH_TIME_CODE_PRE+extractType);
		if(StringUtil.isEmpty(beginSupTime) || StringUtil.isEmpty(endSupTime)){
			returnMap.put("tips", "提供时间无法识别");
			return returnMap;
		}
		if(beginSupTime.compareTo(endSupTime)>=0){
			returnMap.put("tips", "开始提供时间必须早于结束提供时间");
			return returnMap;
		}
		
		//获取描述
		String comments= (String)baseInfo.get(baseInfoColTrans.get("描述"));
		if(StringUtil.isEmpty(comments)){
			returnMap.put("tips", "描述不能为空");
			return returnMap;
		}
		
		String algorithmsCode = (String)columnColTrans.get("脱敏字段");
		
		//校验所有字段
		if(!validator.checkColumn(srcSchema, Const.getStrValue(reallyTableList.get(0),"table_code"), columnList,algorithmsCode).getResult()){
			returnMap.put("tips", BooleanWrap.getLASTUSE().getTips("字段校验失败"));
			return returnMap;
		}
		
		List<Map> allColumnList = (List<Map>) BooleanWrap.getLASTUSE().getData();		
		
		/**所有检验通过，构造函数并进行添加**/
		String staffId = Const.getStrValue(params, "dubbo_staff_id");
		//首先添加c_data_service表
		CDataService service = new CDataService();
		String service_id = SeqUtil.getInst().getNext(CDataService.TABLE_CODE, CDataService.PK_ID);
		service.apply_count = "0";
		service.catalog_id = validator.getCatalogIdByCode(belongCatalogCode);
		service.create_time = DateUtil.getFormatedDateTime();
		service.dispatch_count = "0";
		service.operator = staffId;
		service.query_count = "0";
		service.service_id = service_id;
		service.service_name =dataName;
		service.state = KeyValues.STATE_00B;
		service.getDao().insert(service);
		
		//然后添加CDataSrc表
		CDataSrc src = new CDataSrc();
		src.service_id = service.service_id;
		src.src_id = SeqUtil.getInst().getNext(CDataSrc.TABLE_CODE, CDataSrc.PK_ID);
		src.extract_freq = extractType;
		src.extract_start_acct = historyAcctBegin;
		src.is_hist_extract = isHistory;
		src.select_table_type = srcTable.indexOf("${lan_id}")==-1?
					KeyValues.SELECT_TABLE_SELECTED:KeyValues.SELECT_TABLE_BYFILL;
		src.src_schema_code = srcSchema;
		src.src_sys_code = srcSystem;
		src.src_table_code = srcTable;
		src.getDao().insert(src);
		
		//循环将有需要的表加到C_DATA_SRC_TABLE表
		for(Map srcTableM : reallyTableList){
			CDataSrcTable reallyTable = new CDataSrcTable();
			reallyTable.service_id = service.service_id;
			reallyTable.src_id = src.src_id;
			reallyTable.src_schema_code = src.src_schema_code;
			reallyTable.src_sys_code = src.src_sys_code;
			reallyTable.src_table_code = Const.getStrValue(srcTableM,"table_code");
			reallyTable.src_table_id = SeqUtil.getInst().getNext(CDataSrcTable.TABLE_CODE, CDataSrcTable.PK_ID);
			reallyTable.getDao().insert(reallyTable);
			//更新状态
			//DAO.update(Sql.C_DATA_SQLS.get("update_src_table_status"), new String[]{KeyValues.STATE_00A,Const.getStrValue(srcTableM, "table_id")});
		}
		
		//将记录加到C_DATA_ABILITY表
		CDataAblity ablity = new CDataAblity();
		ablity.ability_id = SeqUtil.getInst().getNext(CDataAblity.TABLE_CODE, CDataAblity.PK_ID);
		ablity.service_id = service.service_id;
		ablity.begin_dispath_time = beginSupTime;
		ablity.end_dispath_time = endSupTime;
		ablity.comments = comments;
		ablity.data_code = dataCode;
		ablity.data_name = dataName;
		ablity.first_division = firstDivsion;
		ablity.is_same_src = "0";
		ablity.second_division = secondDivsion;
		ablity.getDao().insert(ablity);
		
		for(int i=0;allColumnList!=null && i<allColumnList.size();i++){
			Map thisColumn = allColumnList.get(i);
			CDataColumn cdataColumn = new CDataColumn();
			cdataColumn.column_code = Const.getStrValue(thisColumn, "column_code");
			cdataColumn.column_id = SeqUtil.getInst().getNext(CDataColumn.TABLE_CODE, CDataColumn.PK_ID);
			cdataColumn.column_length = Const.getStrValue(thisColumn, "length");
			cdataColumn.column_name = Const.getStrValue(thisColumn, "column_name");
			cdataColumn.column_type = Const.getStrValue(thisColumn, "column_type");
			cdataColumn.comments = Const.getStrValue(thisColumn, "column_desc");
			String algorithmFieldId = Const.getStrValue(thisColumn, "algorithm_field_id");
			cdataColumn.is_acct = "0";
			if(StringUtil.isEmpty(algorithmFieldId)){
				cdataColumn.is_dst = KeyValues.NOT_USE_DST_ALGORITHM;
				cdataColumn.dst_algorithm = "";
			}
			else{
				cdataColumn.is_dst = KeyValues.USE_DST_ALGORITHM;
				cdataColumn.dst_algorithm = algorithmFieldId;
			}
			cdataColumn.seq = Const.getStrValue(params, "seq");
			cdataColumn.service_id = src.service_id;
			cdataColumn.src_column_id = Const.getStrValue(thisColumn, "column_id");
			
			if(StringUtil.isEmpty(cdataColumn.src_column_id) && reallyTableList.size()>1)
				cdataColumn.src_column_id = "1";
			
			boolean updateLengthToNull = false;
			if(StringUtil.isEmpty(cdataColumn.column_length)){
				cdataColumn.column_length = "-1";
				updateLengthToNull = true;
			}
			
			if(StringUtil.isEmpty(cdataColumn.dst_algorithm))
				cdataColumn.dst_algorithm = "1";
			
			cdataColumn.getDao().insert(cdataColumn);
			if(KeyValues.NOT_USE_DST_ALGORITHM.equals(cdataColumn.is_dst)){
				DAO.update(Sql.C_DATA_SQLS.get("set_column_dst_null"), new String[]{cdataColumn.column_id});
			}
			
			if(updateLengthToNull)
				DAO.update("update c_data_column set column_length = null where column_id=?",new String[]{cdataColumn.column_id});
			
		}
		
		//多个表的时候，将src_column_id字段置空
		if(reallyTableList.size()>1)
			DAO.update(Sql.C_DATA_SQLS.get("set_src_column_null"), new String[]{service.service_id});
		
		returnMap.put("state", KeyValues.RESPONSE_SUCCESS);
		
		return returnMap;
	}
	
	private Map getColTransMap(String batchTypeId){
		Map returnMap = new HashMap();
		String sql = "select field_name,cname from batch_type_field where batch_type_id = ?";
		List<Map> transList = DAO.queryForMap(sql, batchTypeId);
		for(int i=0;transList!=null && i<transList.size();i++){
			Map tmap = transList.get(i);
			returnMap.put(tmap.get("cname"), tmap.get("field_name"));
		}
		return returnMap;
	}
	
	private String getFieldCode(String fieldName,String batchTypeId){
		if(StringUtil.isEmpty(fieldName) || StringUtil.isEmpty(batchTypeId)) return "";
		String sql = "select field_name from batch_type_field where batch_type_id = ? and cname=?";
		String returnStr = DAO.querySingleValue(sql, new String[]{batchTypeId,fieldName});
		if(StringUtil.isEmpty(returnStr)) return "";
		return returnStr;
	}

	public PageModel getImportResult(Map params) {
		String batchId = Const.getStrValue(params, "batch_id");
		String isOnlyFailed = Const.getStrValue(params, "only_failed");
		
		int page = 1;
		int pagesize = 10;
		try{
			page = Integer.parseInt(Const.getStrValue(params, "page"));
			pagesize = Integer.parseInt(Const.getStrValue(params, "rows"));
		}catch(Exception e){e.printStackTrace();}
		
		if(StringUtil.isEmpty(batchId)) return new PageModel();
		
		
		String col1 = this.getFieldCode("数据名称", KeyValues.XLS_IMPORT_TYPE1);
		String col2 = this.getFieldCode("数据编码", KeyValues.XLS_IMPORT_TYPE1);
		
		String sql = "select bir.state,bir.comments,bir."+col1+" as data_name,bir."+col2+" as data_code "
				+ "from batch_import_rows bir where batch_id=? ";
		List<String> sqlParams = new ArrayList<String>();
		sqlParams.add(batchId);
		if(StringUtil.isNotEmpty(isOnlyFailed)){
			sql += "and state !=? ";
			sqlParams.add(KeyValues.STATE_00A);
		}
		return DAO.queryForPageModel(sql, pagesize, page, sqlParams.toArray(new String[]{}));
	}

	public List<Map> getPartitionInfo(Map m) {
		String table_code = Const.getStrValue(m, "table_code");
		String schema_code = Const.getStrValue(m, "schema_code");
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from meta_partition where 1=1 and table_code = ? and schema_code = ? ");
		List<Map> list = DAO.queryForMap(sql.toString(), new String[]{table_code,schema_code});
		return list;
	}

	public Map insertSrcLib(Map params) {
		
		String sys_code = Const.getStrValue(params, "sys_code");
		String schema_code = Const.getStrValue(params, "schema_code");
		String owner = Const.getStrValue(params, "owner");
		String schema_name = schema_code;
		String sql = "insert into src_lib(sys_code,schema_code,schema_name,OMP_OWNER) values(?, ?, ?,?)";
		DAO.update(sql, sys_code, schema_code, schema_name,owner);
		
		//刷新静态数据缓存
		CacheBO cache = new CacheBO();
		params.put("type", "attr");
		params.put("value", "SYN_SRC_LIB");
		cache.refresh(params);
		
		Map result = new HashMap();
		result.put("success", true);
		return result;
	}

	public Map deleteSrcLib(Map params) {
		String sys_code = Const.getStrValue(params, "sys_code");
		String schema_code = Const.getStrValue(params, "schema_code");
		String sql = "delete from src_lib where sys_code = ? and schema_code = ?";
		DAO.update(sql, sys_code, schema_code);
		
		//刷新静态数据缓存
		CacheBO cache = new CacheBO();
		params.put("type", "attr");
		params.put("value", "SYN_SRC_LIB");
		cache.refresh(params);
		
		Map result = new HashMap();
		result.put("success", true);
		return result;
	}
	
	public List queryMetaSystem(Map params) {
		String srcSystem = Const.getStrValue(params, "srcSystem");
		if(StringUtil.isEmpty(srcSystem)) return new ArrayList();
		//获取来源数据
		String sql = SrcUtil.getSrcSchemaSql(srcSystem);
		List list = DAO.queryForMap(sql);
		return list;
	}

	public List queryMetaSchema(Map params) {
		String srcSys = Const.getStrValue(params, "srcSys");
		if(KeyValues.SRC_SYS_HD.equalsIgnoreCase(srcSys)){
			String owner = StringUtil.getStrValue(params, "owner");
			String sql = "select * from meta_schema a "
					+ "where not exists(select 1 from src_lib b where a.schema_code = b.schema_code)";
			List<String> sqlParams = new ArrayList<String>();
			if(StringUtil.isNotEmpty(owner)){
				sql += " and owner = ?";
				sqlParams.add(owner);
			}
			List list = DAO.queryForMap(sql, sqlParams.toArray(new String[]{}));
			return list;
		}else{
			String owner = StringUtil.getStrValue(params, "owner");
			String sql = "select * from omp_datasource_protocol a "
					+ "where not exists(select 1 from src_lib b where a.schema_code = b.schema_code)";
			List<String> sqlParams = new ArrayList<String>();
			if(StringUtil.isNotEmpty(owner)){
				sql += " and schema_code = ?";
				sqlParams.add(owner);
			}
			List<Map> list = DAO.queryForMap(sql, sqlParams.toArray(new String[]{}));
			for(Map tmap:list){
				tmap.put("schema_name", tmap.get("user_name"));
				tmap.put("schema_code", tmap.get("user_name"));
			}
			return list;
			
		}
	}

	public List getAllRRLanList(Map params) {
		String sql = "select * from rr_lan_all order by lan_id asc";
		
		return DAO.queryForMap(sql, new String[]{});
	}
	
}
