/**
 * 
 */
package com.ztesoft.crmpub.bpm.consts;

/**
 * @author li.jh
 *
 */
public class MsgConsts {
	/**
	 * 消息类型
	 */
	public static final String MSG_TYPE_FLOW = "FLOW";//新建流程动作
	
	public static final String MSG_TYPE_TASK = "TASK";//更新流程动作
		
	/******消息动作***/
	public static final String ACTION_NEW_FLOW = "NEW_FLOW";//新建流程动作
	
	public static final String ACTION_UPDATE_FLOW = "UPDATE_FLOW";//更新流程动作
	
	public static final String ACTION_INVALID_FLOW = "INVALID_FLOW";//作废流程动作
	
	public static final String ACTION_WO_NEW = "WO_NEW"; // 工单派单
	
	public static final String ACTION_WO_DISPATCH = "WO_DISPATCH"; // 工单派单
	
	public static final String ACTION_WO_FINISH = "WO_FINISH";		// 工单正常完成回单
	
	public static final String ACTION_WO_FAIL = "WO_FAIL";   //工单异常回单
	
	public static final String ACTION_WO_WITHDRAW = "WO_WITHDRAW";   //工单撤单
	

	
	/******消息处理方式****/
    public static final String MSG_HANDLE_SYNC = "SYNC";  //同步处理消息
	
	public static final String MSG_HANDLE_ASYN = "ASYN";  //异步处理消息
	
	/******消息处理结果****/
	public static final String BPM_SUCCESS = "0";//流程创建成功
	
	public static final String RESULT_UNHANDLE = "-999";//消息没处理
	
	public static final String NEW_FLOW_SUCCESS = "1";//流程创建成功
	public static final String NEW_FLOW_SUCCESS_NAME = "流程创建成功";
	
	public static final String UPDATE_FLOW_SUCCESS = "2";//流程更新成功
	public static final String UPDATE_FLOW_SUCCESS_NAME = "流程更新成功";
	
	public static final String FINISH_FLOW_SUCCESS = "3";//流程竣工成功
	public static final String FINISH_FLOW_SUCCESS_NAME = "流程竣工成功";
	
	public static final String INVALID_FLOW_SUCCESS = "4";//流程作废成功
	public static final String INVALID_FLOW_SUCCESS_NAME = "流程作废成功";
	//0:需要工单协同 1：不需要工单协同
	public static final String IS_SYN = "0";
	public static final String IS_ASY = "1";
	
	
	public static final String RESULT_FAIL = "0";
	public static final String BPM_TACHE_ROUTE_TYPE_001 = "001"; //环节路由表的路由类型字段，模板字段类型，模板字段值匹配后，跳转到目标环节
	public static final String BPM_TACHE_ROUTE_TYPE_002 = "002"; //环节路由表的路由类型字段，SQL类型，SQL返回true，跳转到目标环节
	public static final String BPM_TACHE_ROUTE_TYPE_003 = "003"; //环节路由表的路由类型字段，java类类型，java返回true，跳转到目标环节
	public static final String BPM_TACHE_ROUTE_TYPE_004 = "004"; //环节路由表的路由类型字段，可以理解为匹配任何值/任何情况，直接跳转到目标环节，适合外派转派后，回到初始派单环节
	public static final String BPM_TACHE_ROUTE_TYPE_005 = "005"; //环节路由表的路由类型字段，目标环节填-1，审批不通过时用到，根据实例找到离本环节最近的、在环节定义表里比本环节次序号小的环节
}
