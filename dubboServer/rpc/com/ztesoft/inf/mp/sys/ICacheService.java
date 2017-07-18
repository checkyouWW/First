package com.ztesoft.inf.mp.sys;

import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;

/**
 * 缓存管理服务
 * @author lwt
 *
 */
public interface ICacheService {
	
	public RpcPageModel getAttrList(Map params);
	
	public RpcPageModel getDcSystemParamList(Map params);
	
	public Map refresh(Map params);
}
