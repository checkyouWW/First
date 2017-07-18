package com.ztesoft.inf.mp.data;

import java.util.List;
import java.util.Map;

import com.ztesoft.inf.util.RpcPageModel;

@SuppressWarnings({ "rawtypes" })
public interface IDataSynOrderService {

	public RpcPageModel querySynOrderItems(Map params);
	
	public List querySynOrdersByState(String state, int max);
	
	public List querySynOrderItemsByState(String state, int max);

	public RpcPageModel querySynOrderItemLogs(Map params);

	/**
	 * 创建同步工单
	 * @param params
	 */
	public void createSynOrders(Map params);
	
	/**
	 * 创建同步工单项
	 * @param order
	 */
	public void createSynOrderItems(Map order);
	
	/**
	 * 更新工单状态
	 * @param order_id
	 * @param changes
	 * @return
	 */
	public Map updateSynOrder(String order_id, Map changes);
	
	/**
	 * 更新工单状态
	 * @param order_id
	 * @param changes
	 * @return
	 */
	public Map updateSynOrderItem(String order_item_id, Map changes);

	/**
	 * 判断ODS和EDW消息表是否就绪
	 * @param params
	 * @return
	 */
	public boolean messageReady(Map params);
	
	/**
	 * 判断订单的所有订单项是否都成功同步
	 * @param order_id
	 * @return
	 */
	public boolean allSuccess(String order_id);

	/**
	 * 如果所有来源表都成功了,那么最终写big_data_msg表
	 * @param params
	 */
	public void writeBigDataMsg(Map params);
	
	/**
	 * 根据消息表判断表是否已经同步了
	 * @param synOrderItem
	 * @return
	 */
	public boolean hasSyn(Map synOrderItem);
	
	/**
	 * 重新同步数据
	 * @param order_item_id
	 */
	public void reSyn(String order_item_id);
	
	/**
	 * 根据订单获取db to hive的必要参数
	 * @param params
	 * @return
	 */
	public Map getDbToHiveParam(Map params);
	
	/**
	 * 获取data_syn_order表的细节
	 * @param orderId
	 * @return
	 */
	public Map getDataSynOrderDetails(String orderId);
	
	/**
	 * 获取data_syn_order表的列详情
	 * @param dataCode
	 * @return
	 */
	public List<Map> getDataColumnByDataCode(String dataCode);

	/**
	 * 获取字段类型的翻译值
	 * @return
	 */
	public Map getColumnTypeTransMap();

	boolean isStaticTable(Map params);
}
