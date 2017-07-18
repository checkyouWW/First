package com.ztesoft.inf.mp.audit;

import java.util.Map;


/**
 * 后台数据管理  我的审核
 * @author chen.xinwu
 *
 */
@SuppressWarnings("rawtypes")
public interface IAuditMgrService {

	//审核信息
	Map auditApplyInfo(Map m) throws Exception;

	//判断是否有ftp pull 没填的
	Map checkApplyInfo(Map m);
	
}
