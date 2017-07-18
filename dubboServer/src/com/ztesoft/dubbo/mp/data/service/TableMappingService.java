package com.ztesoft.dubbo.mp.data.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.data.bo.TableMappingBO;
import com.ztesoft.inf.mp.data.ITableMappingService;
import com.ztesoft.inf.util.RpcPageModel;

@Service
@SuppressWarnings({ "unchecked", "rawtypes" })
public class TableMappingService implements ITableMappingService {
	
	@Autowired
	private TableMappingBO tableMappingBO;

	@Transactional
	@Override
	public RpcPageModel queryTableMappings(Map params) {
		return tableMappingBO.queryTableMappings(params);
	}

	@Transactional
	@Override
	public boolean saveMapping(Map params) throws Exception {
		String action_type = MapUtils.getString(params, "action_type");
		if ("A".equals(action_type)) {
			return tableMappingBO.addMapping(params);
		} else if ("M".equals(action_type)) {
			return tableMappingBO.editMapping(params);
		} else {
			return false;
		}
	}
	
	@Transactional
	@Override
	public boolean delMapping(Map params) throws Exception {
		return tableMappingBO.delMapping(params);
	}
	
}
