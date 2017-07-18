package com.ztesoft.inf.sp.workspace;

import java.util.List;
import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;



/**
 * 个人工作台--安全数据使用申请
 * @author chen.xinwu
 *
 */
@SuppressWarnings("rawtypes")
public interface IDataUsedApplyService {

	//保存数据安全使用申请
	Map saveDataUsedApply(Map m) throws Exception;

	//获取申请单列表（审批--查数据库和流程）
	RpcPageModel getApplyPage(Map m);
	
	//获取申请单列表（审批--直接查数据库）
	RpcPageModel getApplyPageDis(Map m);

	//获取安全数据使用申请详情
	Map getDataUsedApplyInfo(Map m);

	//更新安全数据使用申请信息（审批）
	Map updateDataUsedApplyInfo(Map m);
	
	//获取与申请单关联的datacode
	RpcPageModel getDataInstApplyList(Map m);

	//获取s_data_inst-->s_data_column
	List getDataInstDataColumn(Map m);

	
	
}
