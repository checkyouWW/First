package com.ztesoft.dubbo.mp.data.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.dubbo.mp.data.bo.DataSynOrderBO;
import com.ztesoft.inf.mp.data.IDataSynOrderService;
import com.ztesoft.inf.util.RpcPageModel;

@Service
@SuppressWarnings({ "rawtypes" })
public class DataSynOrderService implements IDataSynOrderService {
	
	@Autowired
	private DataSynOrderBO dataSynOrderBO;

	@Override
	@Transactional
	public RpcPageModel querySynOrderItems(Map params) {
		return dataSynOrderBO.querySynOrderItems(params);
	}
	
	@Override
	@Transactional
	public List querySynOrdersByState(String state, int max) {
		List result = dataSynOrderBO.querySynOrdersByState(state, max);
		return result;
	}
	
	@Override
	@Transactional
	public List querySynOrderItemsByState(String state, int max) {
		List result = dataSynOrderBO.querySynOrderItemsByState(state, max);
		return result;
	}

	@Override
	@Transactional
	public RpcPageModel querySynOrderItemLogs(Map params) {
		return dataSynOrderBO.querySynOrderItemLogs(params);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createSynOrders(Map params) {
		dataSynOrderBO.createSynOrders(params);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createSynOrderItems(Map params) {
		dataSynOrderBO.createSynOrderItems(params);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map updateSynOrder(String order_id, Map changes) {
		return dataSynOrderBO.updateSynOrder(order_id, changes);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map updateSynOrderItem(String order_item_id, Map changes) {
		return dataSynOrderBO.updateSynOrderItem(order_item_id, changes);
	}
	
	@Override
	@Transactional
	public boolean messageReady(Map params) {
		return dataSynOrderBO.messageReady(params);
	}
	
	@Override
	@Transactional
	public boolean isStaticTable(Map params){
		return dataSynOrderBO.isStaticTable(params);
	}
	
	@Override
	@Transactional
	public boolean allSuccess(String order_id) {
		return dataSynOrderBO.allSuccess(order_id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void writeBigDataMsg(Map params) {
		dataSynOrderBO.writeBigDataMsg(params);
	}
	
	@Override
	@Transactional
	public boolean hasSyn(Map synOrderItem){
		boolean result = dataSynOrderBO.hasSyn(synOrderItem);
		return result;
	}

	@Override
	@Transactional
	public void reSyn(String order_item_id) {
		dataSynOrderBO.reSyn(order_item_id);
	}

	@Override
	@Transactional
	public Map getDbToHiveParam(Map params) {
		return dataSynOrderBO.getDbToHiveParam(params);
	}

	@Override
	@Transactional
	public Map getDataSynOrderDetails(String orderId) {
		return dataSynOrderBO.getDataSynOrderDetails(orderId);
	}

	@Transactional
	@Override
	public List<Map> getDataColumnByDataCode(String dataCode) {
		return dataSynOrderBO.getDataColumnByDataCode(dataCode);
	}

	@Override
	@Transactional
	public Map getColumnTypeTransMap() {
		return dataSynOrderBO.getColumnTypeTransMap();
	}

}
