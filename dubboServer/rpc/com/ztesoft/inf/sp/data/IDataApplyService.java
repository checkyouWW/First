package com.ztesoft.inf.sp.data;

import java.util.List;
import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;

/**
 * 数据申请服务接口
 * @author lwt
 *
 */
@SuppressWarnings("rawtypes")
public interface IDataApplyService {
	
	/**
	 * 保存申请数据
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public Map saveApply(Map params) throws Exception;

	/**
	 * 获取整份列表数据，不分页
	 * @param params
	 * @return
	 */
	List getDataColumnList(Map params);

	/**
	 * 获取订单数据
	 * @param params
	 * @return
	 */
	Map getApplyData(Map params);

	RpcPageModel getDataColumnPageModel(Map params);

	RpcPageModel getApplyDataColumn(Map params);
	
	/**
	 * 修改数据申请（审批）
	 * @param m
	 * @return
	 */
	Map updateDataApplyInfoAudit(Map m);

	/**
	 * 获取申请单号
	 * @param params
	 * @return
	 */
	Map getApplyCode(Map params);

	Map validateDispatch(Map params);

	/**
	 * 修改数据申请（所有状态）
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public Map updateDataApplyInfo(Map params) throws Exception;

	/**
	 * 获取分发过的信息
	 * @param params
	 * @return
	 */
	Map getDispatchInfo(Map params);
	
	/**
	 * 获取接口机FTP信息
	 * @param m
	 * @return
	 */
	Map getInteDispatchInfo(Map m);
	
	Map validateFtpFileName(Map params);

	/**
	 * 验证登录人密码
	 * @param m
	 * @return
	 */
	boolean canGetPassWd(Map m);

	List getCanSelectedDataRange(Map param);
}