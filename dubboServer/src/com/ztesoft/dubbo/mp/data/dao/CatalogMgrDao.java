package com.ztesoft.dubbo.mp.data.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.crm.business.common.utils.ListUtil;
import com.ztesoft.crm.business.common.utils.MapUtil;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.sql.Sql;

import comx.order.inf.IContext;

@Repository
@SuppressWarnings({"unchecked","rawtypes" })
public class CatalogMgrDao {

	private Connection getBDPConnection() {
		return IContext.getContext().getConnection(KeyValues.DATASOURCE_BDP);
	}
	
	public List getOwnerFromBDP(Map m) {
		String sql = Sql.C_DATA_SQLS.get("bdp_owner_list");
		List list = DAO.queryForMap(getBDPConnection(), sql, new String[]{});
		return list;
	}
	
	public List getSchemasFromBDP(Map m) {
		String sql = Sql.C_DATA_SQLS.get("bdp_schema_list");
		sql = sql + " order by owner ";
		List list = DAO.queryForMap(getBDPConnection(), sql, new String[]{});
		return list;
	}

	public RpcPageModel getTablesFromBDP(Map m) {
		String pageSize = Const.getStrValue(m, "rows");
		String pageIndex = Const.getStrValue(m, "page");
		int ps = 5;
		int pi = 1;
		if(StringUtils.isNotBlank(pageSize)) {
			ps = Integer.parseInt(pageSize);
		}
		if(StringUtils.isNotBlank(pageIndex)) {
			pi = Integer.parseInt(pageIndex);
		}
		String schemaCode = Const.getStrValue(m, "schema_code");
		String tableCode = Const.getStrValue(m, "table_code");
		String sql = Sql.C_DATA_SQLS.get("bdp_table_list");
		List<String> paramList = new ArrayList<String>();
		if(StringUtils.isNotBlank(schemaCode)) {
			sql += " and schema_code = ? ";
			paramList.add(schemaCode);
		}
		if(StringUtils.isNotBlank(tableCode))  {
			sql += " and table_code like ? ";
			paramList.add("%"+tableCode+"%");
		}
		PageModel p = DAO.queryForPageModel(getBDPConnection(), sql, ps, pi, paramList.toArray(new String[]{}));
		return PageModelConverter.pageModelToRpc(p);
	}

	public List getFieldsFromBDP(Map m) {
		String schemaCode = Const.getStrValue(m, "schema_code");
		String tableCode = Const.getStrValue(m, "table_code");
		String sql = Sql.C_DATA_SQLS.get("bdp_field_list");
		List<String> paramList = new ArrayList<String>();
		if(StringUtils.isNotBlank(schemaCode) && StringUtils.isNotBlank(tableCode)) {
			sql += " and lower(schema_code) = lower(?) and lower(table_code) = lower(?) ";
			paramList.add(schemaCode);
			paramList.add(tableCode);
		}else {
			return null;
		}
		List list = DAO.queryForMap(getBDPConnection(), sql, paramList.toArray(new String[]{}));
		return ListUtil.isEmpty(list)?null:list;
	}

	public List getSchemas(Map m) {
		String sql = Sql.C_DATA_SQLS.get("schema_list");
		List list = DAO.queryForMap(sql, new String[]{});
		return list;
	}

	public RpcPageModel getTables(Map m) {
		String pageSize = Const.getStrValue(m, "rows");
		String pageIndex = Const.getStrValue(m, "page");
		int ps = 5;
		int pi = 1;
		if(StringUtils.isNotBlank(pageSize)) {
			ps = Integer.parseInt(pageSize);
		}
		if(StringUtils.isNotBlank(pageIndex)) {
			pi = Integer.parseInt(pageIndex);
		}
		String schemaId = Const.getStrValue(m, "schema_id");
		String tableCode = Const.getStrValue(m, "table_code");
		String state =  Const.getStrValue(m, "state");
		String sql = Sql.C_DATA_SQLS.get("table_list");
		List<String> paramList = new ArrayList<String>();
		if(StringUtils.isNotBlank(schemaId)) {
			sql += " and a.schema_id = ? ";
			paramList.add(schemaId);
		}
		if(StringUtils.isNotBlank(tableCode))  {
			sql += " and a.table_code like ? ";
			paramList.add("%"+tableCode+"%");
		}
		if(StringUtils.isNotBlank(state)) {
			sql += " and a.state = ? ";
			paramList.add(state);
		}
		PageModel p = DAO.queryForPageModel(sql, ps, pi, paramList.toArray(new String[]{}));
		return PageModelConverter.pageModelToRpc(p);
	}

	public RpcPageModel getFields(Map m) {
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
//		String serviceId = Const.getStrValue(m, "service_id");
		String tableId = Const.getStrValue(m, "table_id");
		String sql = Sql.C_DATA_SQLS.get("field_list");
		List<String> paramList = new ArrayList<String>();
//		if(StringUtils.isNotBlank(serviceId)) {
//			sql += " and a.service_id = ? ";
//			paramList.add(serviceId);
//		}
		if(StringUtils.isNotBlank(tableId)) {
			sql += " and a.table_id = ? ";
			paramList.add(tableId);
		}
		PageModel p = DAO.queryForPageModel(sql, ps, pi, paramList.toArray(new String[]{}));
		return PageModelConverter.pageModelToRpc(p);
	}

	public String synSchemaAndSysInfo(Map schemaData) {
		//存入平台信息
		String sysId = null;
		String sysCode = MapUtils.getString(schemaData, "sys_code");
		Map sys = getSysInfo(schemaData);
		if(MapUtil.isEmpty(sys) && StringUtils.isNotBlank(sysCode)) {
			String insertSysInfo = Sql.C_DATA_SQLS.get("insert_sys_info");
			DAO.update(insertSysInfo, new String[]{sysCode,sysCode,""});
			sysId = MapUtils.getString(getSysInfo(schemaData), "sys_id");
		}else {
			sysId = MapUtils.getString(sys, "sys_id");
		}
		
		//存入数据库信息
		String schemaId = null;
		if(StringUtils.isNotBlank(sysId)) {
			String schemaCode = MapUtils.getString(schemaData, "schema_code");
			String schemaName = MapUtils.getString(schemaData, "schema_name");
			Map tmp = new HashMap();
			tmp.put("sys_id", sysId);
			tmp.put("schema_code", schemaCode);
			Map schema = getSchemaInfo(tmp);
			if(MapUtil.isEmpty(schema) && StringUtils.isNotBlank(schemaCode)) {
				String insertSchemaInfo = Sql.C_DATA_SQLS.get("insert_schema_info");
				DAO.update(insertSchemaInfo, new String[]{sysId,schemaCode,schemaName,""});
				schemaId = MapUtils.getString(getSchemaInfo(tmp), "schema_id");
			}else {
				schemaId = MapUtils.getString(schema, "schema_id");
			}
			
		}
		return schemaId;
	}

	
	public void synTableInfo(List<Map> tableData, String schemaId) {
		String insertTable = Sql.C_DATA_SQLS.get("insert_table");
		for(Map table : tableData) {
			String tableCode = MapUtils.getString(table, "table_code");
			String tableName = MapUtils.getString(table, "table_name");
			Map tmp = new HashMap();
			tmp.put("table_code", tableCode);
			tmp.put("schema_id", schemaId);
			Map t = getTableInfo(tmp);
			if(MapUtils.isEmpty(t) && StringUtils.isNotBlank(tableCode)) {
				String createTime = DateUtil.getFormatedDateTime();
				DAO.update(insertTable, new String[]{schemaId,tableCode,tableName,"",createTime});
			}
		}
		
	}

	public Map synFieldInfo(List<Map> fieldData, String schemaId) {
		Map res = new HashMap();
		String insertField = "";
		String tableId = null;
		boolean hasAcctCode = false;
		int co = 0;
		Map accMap = getBdpAccountCode(null);
		String acctMonthCode = MapUtils.getString(accMap, "acct_month_code");
		String acctDayCode = MapUtils.getString(accMap, "acct_day_code");
		int acctCount = 0;
		
		String tableCode = null;
		if(!ListUtil.isEmpty(fieldData)) {
			Map field_t = fieldData.get(0);
			tableCode = MapUtils.getString(field_t, "table_code");
			if(StringUtils.isBlank(tableId) && StringUtils.isNotBlank(tableCode) && StringUtils.isNotBlank(schemaId)) {
				Map t = new HashMap();
				t.put("table_code", tableCode);
				t.put("schema_id", schemaId);
				Map table = getTableInfo(t);
				tableId = MapUtils.getString(table, "table_id");
			}
			
			if(StringUtils.isNotBlank(tableId)) {
				//判断表中是否有账期编码
				hasAcctCode = hasAcctCode(tableId);
				//判断字段中是否有账期编码
				for(Map fie:fieldData) {
					String columnCode = MapUtils.getString(fie, "column_code");
					if(columnCode.trim().equals(acctDayCode.trim()) || columnCode.trim().equals(acctMonthCode.trim())) {
						acctCount++;
						fie.put("is_acct", "1");
					}else {
						fie.put("is_acct", "0");
					}
				}
				if((hasAcctCode && acctCount==0) || (!hasAcctCode && acctCount==1 )) {
					for(Map field:fieldData) {
						String fieldCode = MapUtils.getString(field, "column_code");
						String fieldName = MapUtils.getString(field, "column_name");
						String fieldLength = MapUtils.getString(field, "length");
						String fieldType = MapUtils.getString(field, "column_type");
						String seq = MapUtils.getString(field, "seq");
						String isAcct = MapUtils.getString(field, "is_acct");
						Map tmp = new HashMap();
						tmp.put("field_code", fieldCode);
						tmp.put("table_id", tableId);
						Map f = getFieldInfo(tmp);
						if(MapUtils.isEmpty(f) && StringUtils.isNotBlank(fieldCode)) {
							if(StringUtils.isBlank(fieldLength)) {
								insertField = "insert into c_src_column(table_id,column_code,column_name,column_type,comments,state,seq,is_acct) values(?,?,?,?,?,'00A',?,?)";
								DAO.update(insertField, new String[]{tableId,fieldCode,fieldName,fieldType,"",seq,isAcct});
							}else {
								insertField = "insert into c_src_column(table_id,column_code,column_name,column_type,column_length,comments,state,seq,is_acct) values(?,?,?,?,?,?,'00A',?,?)";
								DAO.update(insertField, new String[]{tableId,fieldCode,fieldName,fieldType,fieldLength,"",seq,isAcct});
							}
						}
					}
				}
				if(hasAcctCode && acctCount!=0) {
					res.put("mess", "表中已存在账期编码："+acctMonthCode+"或"+acctDayCode+"，账期编码必须且只能存在一个，请重试");
					res.put("re_flag", false);
				}
				
				if(!hasAcctCode && acctCount!=1) {
					res.put("mess", "账期编码："+acctMonthCode+"或"+acctDayCode+"，账期编码必须且只能存在一个，请重试");
					res.put("re_flag", false);
				}
			}
		}
		
		return res;
	}
	//判断本地表中是否已同步账期编码
	public boolean hasAcctCode(String tableId) {
		String sql = " select count(*) from c_src_column "
				 + " where table_id = ? "
				 + " and is_acct = 1 and state !='00X'";
		String count = DAO.querySingleValue(sql, new String[]{tableId});
		try {
			int ct = Integer.parseInt(count);
			if(ct>0) return true;
			else return false;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//获取平台信息
	private Map getSysInfo(Map m) {
		String sysCode = MapUtils.getString(m, "sys_code");
		String selectSysInfo = Sql.C_DATA_SQLS.get("select_sys_nfo");
		List<String> pList = new ArrayList<String>();
		if(StringUtils.isNotBlank(sysCode)) {
			selectSysInfo += " and sys_code=? ";
			pList.add(sysCode);
			List<Map> sys = DAO.queryForMap(selectSysInfo, pList.toArray(new String[]{}));
			if(ListUtil.isEmpty(sys)) {
				return null;
			}else {
				Map re = sys.get(0);
				return MapUtil.isEmpty(re)?null:re;
			}
		}
		return null;
	}
	
	//获取库信息
	private Map getSchemaInfo(Map m) {
		String schemaCode = MapUtils.getString(m, "schema_code");
		String sysId = MapUtils.getString(m, "sys_id");
		String selectSchema = Sql.C_DATA_SQLS.get("select_schema");
		List<String> pList = new ArrayList<String>();
		if(StringUtils.isNotBlank(sysId)) {
			selectSchema += " and sys_id = ? ";
			pList.add(sysId);
		}
		if(StringUtils.isNotBlank(schemaCode)) {
			selectSchema += " and schema_code = ? ";
			pList.add(schemaCode);
		}
		List<Map> schema = DAO.queryForMap(selectSchema, pList.toArray(new String[]{}));
		if(ListUtil.isEmpty(schema)) {
			return null;
		}else {
			Map re = schema.get(0);
			return MapUtil.isEmpty(re)?null:re;
		}
	}
	
	//获取表信息
	private Map getTableInfo(Map m) {
		String tableCode = MapUtils.getString(m, "table_code");
		String schemaId = MapUtils.getString(m, "schema_id");
		String selectTable = Sql.C_DATA_SQLS.get("select_table");
		List<String> pList = new ArrayList<String>();
		if(StringUtils.isNotBlank(schemaId)) {
			selectTable += " and a.schema_id = ? ";
			pList.add(schemaId);
		}
		if(StringUtils.isNotBlank(tableCode)) {
			selectTable += " and a.table_code = ? ";
			pList.add(tableCode);
		}
		List<Map> table = DAO.queryForMap(selectTable, pList.toArray(new String[]{}));
		if(ListUtil.isEmpty(table)) {
			return null;
		}else {
			Map re = table.get(0);
			return MapUtil.isEmpty(re)?null:re;
		}
	}
	
	//获取字段细信息
	private Map getFieldInfo(Map m) {
		String tableId = MapUtils.getString(m, "table_id");
		String fieldCode = MapUtils.getString(m, "field_code");
		String selectField = Sql.C_DATA_SQLS.get("select_field");
		List<String> pList = new ArrayList<String>();
		if(StringUtils.isNotBlank(tableId)) {
			selectField += " and a.table_id = ? ";
			pList.add(tableId);
		}
		if(StringUtils.isNotBlank(fieldCode)) {
			selectField += " and a.column_code = ? ";
			pList.add(fieldCode);
		}
		List<Map> fields = DAO.queryForMap(selectField, pList.toArray(new String[]{}));
		if(ListUtil.isEmpty(fields)) {
			return null;
		}else {
			Map re = fields.get(0);
			return MapUtil.isEmpty(re)?null:re;
		}
	}

	public void deleteTable(String tableId) {
		String sql = Sql.C_DATA_SQLS.get("delete_table");
		sql += " and table_id = ? ";
		DAO.update(sql, new String[]{tableId});
	}
	
	public void deleteField(String fieldId) {
		String sql = Sql.C_DATA_SQLS.get("delete_field");
		sql += " and column_id = ? ";
		DAO.update(sql, new String[]{fieldId});
	}

	public void modifyTableInfo(Map m) {
		String tableId = MapUtils.getString(m, "table_id");
		String tableName = MapUtils.getString(m, "table_name");
		StringBuffer sql = new StringBuffer("update c_src_table set table_id = ? ");
		List<String> pList = new ArrayList<String>();
		pList.add(tableId);
		if(StringUtils.isNotBlank(tableName)) {
			sql.append(" ,table_name = ? ");
			pList.add(tableName);
		}
		sql.append(" where table_id = ? ");
		pList.add(tableId);
		DAO.update(sql.toString(), pList.toArray(new String[]{}));
	}

	public void modifyColumnInfo(Map m) {
		String columnId = MapUtils.getString(m, "column_id");
		String comments = MapUtils.getString(m, "comments");
		StringBuffer sql = new StringBuffer("update c_src_column set column_id = ? ");
		List<String> pList = new ArrayList<String>();
		pList.add(columnId);
		if(StringUtils.isNotBlank(comments)) {
			sql.append(",comments = ? ");
			pList.add(comments);
		}
		sql.append(" where column_id = ? ");
		pList.add(columnId);
		DAO.update(sql.toString(), pList.toArray(new String[]{}));
	}

	public Map getBdpAccountCode(Map m) {
		String sql = "select param_val from dc_system_param where param_code = ? ";
		String acct_month_code = DAO.querySingleValue(sql, new String[]{"BDP_ACCT_MONTH"});
		String acct_day_code = DAO.querySingleValue(sql, new String[]{"BDP_ACCT_DAY"});
		Map re = new HashMap();
		re.put("acct_month_code", acct_month_code);
		re.put("acct_day_code", acct_day_code);
		return re;
	}
}
