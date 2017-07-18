package com.ztesoft.inf.mp.data;

import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;

@SuppressWarnings({ "unchecked", "rawtypes" })
public interface ITableMappingService {

	public RpcPageModel queryTableMappings(Map params);

	boolean saveMapping(Map params) throws Exception;

	boolean delMapping(Map params) throws Exception;

}
