package com.ztesoft.dubbo.mp.data.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.sql.Sql;

import spring.util.DBUtil;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class TableMappingBO {

	public RpcPageModel queryTableMappings(Map params) {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));

		StringBuffer sql = new StringBuffer(Sql.SYS_SQLS.get("SELECT_TABLE_MAPPING_SQL"));
		List sqlParams = new ArrayList();
		String[] conds = new String[] { "std_table_code", "src_sys_code", "schema_code", "src_table_code" };
		for (String cond : conds) {
			if (StringUtils.isNotEmpty(MapUtils.getString(params, cond))) {
				sql.append(" and ").append(cond).append(" like ?");
				sqlParams.add("%" + MapUtils.getString(params, cond) + "%");
			}
		}
		sql.append(" order by mapping_id desc");
		RpcPageModel result = DBUtil.getSimpleQuery().queryForRpcPageModel(sql.toString(), null, page_size, page_index,
				sqlParams.toArray(new String[] {}));
		return result;
	}
	
	public boolean addMapping(Map params) throws Exception {
		List sqlParams = new ArrayList();
		String mapping_id = SeqUtil.getSeq("TABLE_MAPPING", "MAPPING_ID");
		sqlParams.add(mapping_id);
		sqlParams.add(MapUtils.getString(params, "std_table_code"));
		sqlParams.add(MapUtils.getString(params, "src_sys_code"));
		sqlParams.add(MapUtils.getString(params, "schema_code"));
		sqlParams.add(MapUtils.getString(params, "src_table_code"));
		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("INSERT_TABLE_MAPPING_SQL"), sqlParams);
		return true;
	}

	public boolean editMapping(Map params) throws Exception {
		String mapping_id = MapUtils.getString(params, "mapping_id");
		if (StringUtils.isEmpty(mapping_id)) {
			return false;
		}
		List sqlParams = new ArrayList();
		sqlParams.add(MapUtils.getString(params, "std_table_code"));
		sqlParams.add(MapUtils.getString(params, "src_sys_code"));
		sqlParams.add(MapUtils.getString(params, "schema_code"));
		sqlParams.add(MapUtils.getString(params, "src_table_code"));
		sqlParams.add(mapping_id);
		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("UPDATE_TABLE_MAPPING_SQL"), sqlParams);
		return true;
	}
	
	public boolean delMapping(Map params) throws Exception {
		String mapping_id = MapUtils.getString(params, "mapping_id");
		if (StringUtils.isEmpty(mapping_id)) {
			return false;
		}
		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("DELETE_TABLE_MAPPING_SQL"), new String[] { mapping_id });
		return true;
	}
	
}
