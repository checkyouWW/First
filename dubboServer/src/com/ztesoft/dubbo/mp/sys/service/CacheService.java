package com.ztesoft.dubbo.mp.sys.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.sys.bo.CacheBO;
import com.ztesoft.inf.mp.sys.ICacheService;
import com.ztesoft.inf.util.RpcPageModel;

/**
 * 定时任务信息管理
 * 
 * @author
 *
 */
@Service("cacheService")
@SuppressWarnings("rawtypes")
public class CacheService implements ICacheService {

	private CacheBO bo = new CacheBO();
	
	@Override
	@Transactional
	public RpcPageModel getAttrList(Map params) {
		return bo.getAttrList(params);
	}
	
	@Override
	@Transactional
	public RpcPageModel getDcSystemParamList(Map params) {
		return bo.getDcSystemParamList(params);
	}
	
	@Override
	@Transactional
	public Map refresh(Map params) {
		return bo.refresh(params);
	}
}
