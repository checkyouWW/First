package com.ztesoft.dubbo.mp.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import appfrm.app.util.ListUtil;
import appfrm.resource.dao.impl.DAO;

import com.ztesoft.dubbo.mp.data.dao.CatalogMgrDao;
import com.ztesoft.inf.mp.data.ICatalogMgrService;
import com.ztesoft.inf.util.RpcPageModel;


@Service("catalogMgrService")
@SuppressWarnings("rawtypes")
public class CatalogMgrService implements ICatalogMgrService {

	@Resource
	private CatalogMgrDao dao;
	
	@Override
	@Transactional
	public List getOwnerFromBDP(Map m) {
		return dao.getOwnerFromBDP(m);
	}
	
	@Override
	@Transactional
	public List getSchemasFromBDP(Map m) {
		return dao.getSchemasFromBDP(m);
	}

	@Override
	@Transactional
	public RpcPageModel getTablesFromBDP(Map m) {
		return dao.getTablesFromBDP(m);
	}

	@Override
	@Transactional
	public List getFieldsFromBDP(Map m) {
		return dao.getFieldsFromBDP(m);
	}

	@Override
	@Transactional
	public List getSchemas(Map m) {
		return dao.getSchemas(m);
	}

	@Override
	@Transactional
	public RpcPageModel getTables(Map m) {
		return dao.getTables(m);
	}

	@Override
	@Transactional
	public RpcPageModel getFields(Map m) {
		return dao.getFields(m);
	}

	@Override
	@Transactional
	public Map synInfo(Map m) {
		
		Map schemaData = (Map) m.get("schema_data");
		List<Map> tableData = (List<Map>) m.get("table_data");
		List<Map> fieldData = (List<Map>) m.get("field_data");
		
		Map mes = new HashMap();
		String schemaId = null;
		//同步平台和库信息
		if(MapUtils.isNotEmpty(schemaData)) {
			schemaId = dao.synSchemaAndSysInfo(schemaData);
		}
		//同步表信息
		if(StringUtils.isNotBlank(schemaId)) {
			if(!ListUtil.isEmpty(tableData)) {
				dao.synTableInfo(tableData,schemaId);
			}
			mes.put("schema_id", schemaId);
		}else {
			mes.put("mess", "获取库表信息异常");
		}
		//同步字段信息
		if(StringUtils.isNotBlank(schemaId)) {
			if(!ListUtil.isEmpty(fieldData)) {
				Map re = dao.synFieldInfo(fieldData,schemaId);
				mes.putAll(re);
			}
		}else {
			mes.put("mess", "获取库表信息异常");
		}
		return mes;
	}
	
	@Override
	@Transactional
	public void deleteTables(Map m) {
		List<Map> tables = (List<Map>) m.get("tables");
		for(Map table : tables) {
			String tableId = MapUtils.getString(table, "table_id");
			if(StringUtils.isNotBlank(tableId)) {
				dao.deleteTable(tableId);
			}
		}
	}
	
	@Override
	@Transactional
	public void deleteField(Map m) {
		String fieldId = MapUtils.getString(m, "column_id");
		if(StringUtils.isNotBlank(fieldId)) {
			dao.deleteField(fieldId);
		}
	}

	@Override
	@Transactional
	public void modifyTableInfo(Map m) {
		dao.modifyTableInfo(m);
	}
	
	@Override
	@Transactional
	public void modifyColumnInfo(Map m) {
		dao.modifyColumnInfo(m);
	}
	
	@Override
	@Transactional
	public Map getBdpAccountCode(Map m) {
		return dao.getBdpAccountCode(m);
	}
}
