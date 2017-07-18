package com.ztesoft.dubbo.mp.audit.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.audit.dao.AuditMgrDao;
import com.ztesoft.inf.mp.audit.IAuditMgrService;


@Service("auditMgrService")
@SuppressWarnings("rawtypes")
public class AuditMgrService implements IAuditMgrService {

	@Resource
	private AuditMgrDao dao;
	
	/**
	 * 审批信息
	 * @param m
	 * @return
	 * @throws Exception 
	 */
	@Override
	@Transactional
	public Map auditApplyInfo(Map m) throws Exception {
		return dao.auditApplyInfo(m);
	}
	
	/**
	 * 判断是否有ftp pull 没填的
	 */
	@Override
	@Transactional
	public Map checkApplyInfo(Map m) {
		return dao.checkApplyInfo(m);
	}
	
}
