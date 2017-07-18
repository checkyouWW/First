package com.ztesoft.dubbo.mp.task.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ztesoft.common.dao.DAOUtils;
import com.ztesoft.common.util.DBUtils;
import com.ztesoft.crm.business.common.utils.ListUtil;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.sql.Sql;

import spring.util.DBUtil;

@Component
@SuppressWarnings({ "all" })
public class TaskScheduleBO {
	
	public Map queryServiceOrder(Map params) {
		String task_id = MapUtils.getString(params, "task_id");
		
		List sqlParams = new ArrayList();
		StringBuffer cond = new StringBuffer();
		if (StringUtils.isNotEmpty(task_id)) {
			cond.append(" and service_inst_id = ?");
			sqlParams.add(task_id);
		}
		cond.append(" order by create_date desc");
		List<Map> list = DBUtil.getSimpleQuery()
				.queryForMapListBySql(Sql.S_TASK_SQLS.get("SELECT_SERVICE_ORDER_SQL") + cond, sqlParams);
		return ListUtil.isEmpty(list) ? null : list.get(0);
	}
	
	public String insertServiceOrder(Map params) {
		List sqlParams = new ArrayList();
		sqlParams.add(null);
		sqlParams.add(MapUtils.getString(params, "data_inst_id"));
		sqlParams.add(MapUtils.getString(params, "service_inst_id"));
		sqlParams.add(MapUtils.getString(params, "service_id"));
		sqlParams.add(MapUtils.getString(params, "service_type"));
		sqlParams.add(MapUtils.getString(params, "apply_id"));
		sqlParams.add(null);
		sqlParams.add(DAOUtils.getFormatedDate());
		sqlParams.add(DAOUtils.getFormatedDate());
		sqlParams.add(KeyValues.ORDER_STATE_TODO);
		sqlParams.add(null);
		DBUtil.getSimpleQuery().excuteUpdate(Sql.S_TASK_SQLS.get("INSERT_SERVICE_ORDER_SQL"), sqlParams);
		
		String service_order_id = DBUtil.getSimpleQuery().querySingleValue("select LAST_INSERT_ID()", new String[] {});
		return service_order_id;
	}
	
	public boolean updateServiceOrder(Map<String, String> params) {
		String service_order_id = MapUtils.getString(params, "service_order_id");
		if (StringUtils.isEmpty(service_order_id)) {
			return false;
		}
		List sqlParams = new ArrayList();
		
		StringBuffer cols = new StringBuffer();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if ("service_order_id".equalsIgnoreCase(entry.getKey())) {
				continue;
			}
			if (entry.getKey().indexOf("_date") > -1) {
				cols.append(entry.getKey() + " = " + DBUtils.to_date(2) + ", ");
			} else {
				cols.append(entry.getKey() + " = ?, ");
			}
			sqlParams.add(entry.getValue());
		}
		if (cols.length() > 0 ) {
			cols.append("state_date = " + DBUtils.to_date(2) + " ");
			sqlParams.add(DAOUtils.getFormatedDate());
		} else {
			return false;
		}
		sqlParams.add(service_order_id);
		StringBuffer sql = new StringBuffer();
		sql.append("update service_order set ");
		sql.append(cols);
		sql.append("where service_order_id = ?");

		DBUtil.getSimpleQuery().excuteUpdate(sql.toString(), sqlParams);
		return true;
	}

}
