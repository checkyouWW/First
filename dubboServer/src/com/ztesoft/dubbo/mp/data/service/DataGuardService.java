package com.ztesoft.dubbo.mp.data.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.data.dao.DataGuardDao;
import com.ztesoft.inf.mp.data.IDataGuardService;
import com.ztesoft.inf.util.RpcPageModel;


@Service("dataGuardService")
@SuppressWarnings("rawtypes")
public class DataGuardService implements IDataGuardService {

	@Resource
	private DataGuardDao dao;
	
	@Override
	@Transactional
	public RpcPageModel getDataGuards(Map m) {
		return dao.getDataGuards(m);
	}

	@Override
	@Transactional
	public Map getDataGuardInfo(Map m) {
		return dao.getDataGuardInfo(m);
	}
	@Override
	@Transactional
	public void saveServiceInstAlert(Map m) {
		dao.saveServiceInstAlert(m);
	}
	
}
