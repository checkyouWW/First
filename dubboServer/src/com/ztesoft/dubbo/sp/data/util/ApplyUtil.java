package com.ztesoft.dubbo.sp.data.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import appfrm.app.meta.IFieldMeta;
import appfrm.app.vo.IVO;
import appfrm.resource.dao.impl.DAO;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.crm.business.common.utils.ListUtil;
import com.ztesoft.crm.business.common.utils.MapUtil;
import com.ztesoft.dubbo.sp.data.vo.MDataChangeNotify;
import com.ztesoft.dubbo.sp.data.vo.MDataColumnChangeNotify;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.sql.Sql;

public class ApplyUtil {
	
	//存入data_change_notify_column 字段临时表
	public void insertDataChangeColumn(Map col, String dataInstId, String flowId, String applyId) {
		String id = SeqUtil.getSeq(MDataColumnChangeNotify.TABLE_CODE, "column_change_id");
		MDataColumnChangeNotify cn = new MDataColumnChangeNotify();
		cn.readFromMap(col);
		cn.alg_type = Const.getStrValue(col, "dst_algorithm");
		cn.data_inst_id = dataInstId;
		cn.column_change_id = id;
		cn.flow_id = flowId;
		cn.apply_id = applyId;
		cn.getDao().insert(cn);
	}
	
	public String insertDataChangeColumnTask(Map col,String flowId, String applyId) {
		String id = SeqUtil.getSeq(MDataColumnChangeNotify.TABLE_CODE, "column_change_id");
		MDataColumnChangeNotify cn = new MDataColumnChangeNotify();
		cn.readFromMap(col);
		cn.column_change_id = id;
		cn.flow_id = flowId;
		cn.apply_id = applyId;
		cn.getDao().insert(cn);
		return id;
	}
	
	//获取界面变化VO
	public Map getChangedVO(IVO fromPage, Map map) {
		Map re = new HashMap();
		List<String> fieldNames = new ArrayList<String>();
		if(!MapUtil.isEmpty(map)) {
			Set<String> set = map.keySet();
			for(String key : set) {
				Object obj = map.get(key);
				if (!(obj instanceof String)) {
					continue;
				}
				String value = (String) obj;
				IFieldMeta field = fromPage.getMeta().getField(key);
				if (field != null && value != null) {
					fromPage.set(key, value);
					fieldNames.add(key);
				}
			}
		}
		re.put("vo", fromPage);
		re.put("fieldNames",fieldNames);
		return re;
	}
	
	//插入对象变化临时表
	public void insertChangeNotify(Map params,IVO fromPage, IVO fromDB, String tableCode, String tableId, String ownInstId, String actionType, String flowId){
		Map changeMap = getChangedVO(fromPage,params);
		fromPage = (IVO) changeMap.get("vo");
		Set<String> updateSet = fromPage.getUpdateFieldSet();
		String[] fieldNames = updateSet.toArray(new String[] {});
		for (String fieldName : fieldNames) {
			String fieldValueNew = fromPage.get(fieldName);
			String fieldValueOld = fromDB.get(fieldName);

			if ((StringUtils.isBlank(fieldValueNew) && StringUtils
					.isBlank(fieldValueOld))
					|| (fieldValueNew != null && fieldValueNew
							.equals(fieldValueOld))
					|| (fieldValueOld != null && fieldValueOld
							.equals(fieldValueNew))) {
				continue;
			}
			
			boolean isExist = this.isExistInDataChangeNotify(tableCode, tableId, fieldName);
			if(isExist) {
				this.deleteDataChangeNotify(tableCode, tableId, fieldName);
			}
			
			Map mc = new HashMap();
			mc.put("action_type", actionType);
			mc.put("field_name", fieldName);
			mc.put("table_name", tableCode);
			mc.put("old_field_value", fieldValueOld);
			mc.put("field_value", fieldValueNew);
			mc.put("owner_inst_id", ownInstId);
			mc.put("inst_id", tableId);
			mc.put("tab_attr_id", "");
			mc.put("notify_id", "");
			mc.put("flow_id", flowId);
			insertMDataChangeNotify(mc);
			
		}
	}
	
	//插入表data_change_notify 数据变化临时表
	public void insertMDataChangeNotify(Map map) {
		String table_code = Const.getStrValue(map, "table_name");
		String inst_id = Const.getStrValue(map, "inst_id");
		String field_name = Const.getStrValue(map, "field_name");
		if(isExistInDataChangeNotify(table_code,inst_id,field_name)) {
			deleteDataChangeNotify(table_code, inst_id, field_name);
		}
		MDataChangeNotify changeNotify = new MDataChangeNotify();
		String id = SeqUtil.getSeq(MDataChangeNotify.TABLE_CODE, "change_id");
		changeNotify.readFromMap(map);
		changeNotify.change_id = id;
		changeNotify.handle_time = DateUtil.getFormatedDateTime();
		changeNotify.getDao().insert(changeNotify);
	}
	
	//获取data_change_notify数据 inst_id --> tableId own_inst_id --> applyId
	public void getDataChangeNotify(Map m, String tableCode, String instId ,String ownInstId,String flowId, String[] actionTypes) {
		String sql = Sql.S_DATA_SQLS.get("get_data_change_notify");
		if(actionTypes!=null && actionTypes.length>0) {
			sql += " and ( ";
			List<String> pList = new ArrayList<String>();
			for(int i=0; i<actionTypes.length; i++) {
				sql += " action_type = '"+actionTypes[i].trim()+"'";
				pList.add(actionTypes[i].trim());
				if(i!=actionTypes.length-1) {
					sql += " or ";
				}
			}
			sql += ")";
		}
		List<Map> l = DAO.queryForMap(sql, new String[]{tableCode,ownInstId,instId,flowId});
		for(Map t: l) {
			String fieldName = Const.getStrValue(t, "field_name");
			String fieldValue = Const.getStrValue(t, "field_value");
			m.put(fieldName, fieldValue);
		}
	}
	
	/**
	 * 一个实例数据对应的字段日志是否存在
	 * 
	 * @param table_name
	 * @param inst_id
	 * @param field_name
	 * @return
	 */
	public boolean isExistInDataChangeNotify(String table_name, String inst_id,
			String field_name) {
		List<IVO> list = MDataChangeNotify
				.getDAO()
				.newQuerySQL(
						" lower(table_name) = ? and inst_id = ? and lower(field_name) = ?")
				.findByCond(table_name.toLowerCase(), inst_id,
						field_name.toLowerCase());
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 删除日志
	 * 
	 * @param table_name
	 * @param inst_id
	 * @param field_name
	 */
	public void deleteDataChangeNotify(String table_name, String inst_id,
			String field_name) {
		String sql = "delete from data_change_notify  where lower(table_name) = ? and inst_id = ? and lower(field_name) = ?";
		DAO.update(sql, new String[] { table_name.toLowerCase(), inst_id,
				field_name.toLowerCase() });
	}
	
	public void insertMDataChangeNotify(String flow_id,String inst_id,String owner_inst_id, String table_code,String field_name,String field_value,String action_type) {
		if(isExistInDataChangeNotify(table_code,inst_id,field_name)) {
			deleteDataChangeNotify(table_code, inst_id, field_name);
		}
		Map tm = new HashMap();
		tm.put("action_type", action_type);
		tm.put("field_name", field_name);
		tm.put("table_name", table_code);
		tm.put("old_field_value", "");
		tm.put("field_value", field_value);
		tm.put("owner_inst_id", owner_inst_id);
		tm.put("inst_id", inst_id);
		tm.put("tab_attr_id", "");
		tm.put("notify_id", "");
		tm.put("flow_id", flow_id);
		this.insertMDataChangeNotify(tm);
	}
		
	
}
