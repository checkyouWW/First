/**
 * BPM公共常量
 * 
 */
package com.ztesoft.crmpub.bpm.consts;

import java.util.HashMap;

/**
 * 
 * @author major
 *
 */
public class BPMConsts { 
	
	public static final String FIRST_TACHE_STATE_NAME = "已保存，待提交";
	
    /**
     * 环节类型
     */
	public static final String TACHE_TYPE_BEGIN = "BEGIN";//开始环节
	public static final String TACHE_TYPE_MIDDLE = "MIDDLE";//中间环节
	public static final String TACHE_TYPE_END = "END";//结束环节
	
	/* 工单动作类型 */
	public static final String WO_ACTION_NORMAL = "0";  //正常回单
	
	
	public static final String WO_ACTION_SUSPEND = "1"; //流程挂起
	
	
	public static final String WO_ACTION_WAKEUP = "2";  //流程解挂
	
	
	public static final String WO_ACTION_DISPATCH = "3";  //工单改派
	
	
	public static final String WO_ACTION_INVALID = "4";  //流程作废

	
	/**
	 * 工单处理状态 WO_STATE
	 */
	public static final String WO_STATE_NEW = "NEW";	
	
	public static final String WO_STATE_READY = "READY";	//工单状态-就绪/已派单（未开始）
	
	public static final String WO_STATE_DEALING = "DEALING";//工单状态-正在处理
	
	public static final String WO_STATE_FINISH = "FINISH";  //工单状态-审核中
	
	public static final String WO_STATE_FAIL= "FAIL";  //工单状态-审核不通过
	
	public static final String WO_STATE_WITHDRAW= "WITHDRAW";  //工单状态-完成
	
	
	public static final String EXEC_STATE_TODO = "TODO";        //处理人状态-待处理
	public static final String EXEC_STATE_FINISH = "FINISH";     //处理人状态- 当前处理人“主动”发起完成工单动作
	public static final String EXEC_STATE_UNFINISHED = "UNFINISHED";     //处理人状态- 未完成，一般是多人审批时候，被人抢先审批
	public static final String EXEC_STATE_FAIL = "FAIL"; 		 //处理人状态-当前处理人“主动”发起不通过动作
	public static final String EXEC_STATE_WITHDRAW = "WITHDRAW"; 		 //处理人状态-当前处理人“主动”发起撤单动作
	public static final String EXEC_STATE_DISPATCH = "DISPATCH"; 		 //处理人状态-当前处理人“主动”发起转派动作，处理人已经转为其他人
	
	/**
	 * 工单执行者类型  WORKER_TYPE
	 * STAFF:员工  ROLE:员工角色 ORG:组织, TEAM:虚拟团队
	 */
    public static final String WORKER_TYPE_STAFF = "STAFF";  //员工
    public static final String WORKER_TYPE_STAFF_MULTI = "STAFF_MULTI";  //员工。并行模式，每个员工有对应的wo_task。joshui
    public static final String WORKER_TYPE_ROLE = "ROLE";    //员工角色
    public static final String WORKER_TYPE_ORG = "ORG";      //组织。用来实现共享模式，这个群组的人都能看到工单，其中一人抢单后，其他人就都看不到了 joshui
    public static final String WORKER_TYPE_TEAM = "TEAM";    //群组。用来实现共享模式，这个群组的人都能看到工单，其中一人抢单后，其他人就都看不到了 joshui
    
    public static final String WORKER_TYPE_STAFF_MULTI_SPLIT = ",";  //员工。并行模式，每个员工有对应的wo_task。joshui
    
    /**
     * 流程大类
     */
    public static final String FLOW_DEF_TYPE_TASK = "TASK";  //普通流程
    public static final String FLOW_DEF_TYPE_ICT = "ICT";  //ICT流程
    
    /**
     * 派单/抄送目标，属性列表值取值方法
     * 派单目标取值方法ATTR/SQL/VAR/CONST：
			ATTR: 属性取值，关联ATTR_ID表界面取值
			SQL: SQL取值，如 select upper_staff from staff where staff_id ={$staff_id}
			VAR: 内置环境变量方式,如对应  
			                  UP_MANAGER 归属的上级经理
			                  OWN_MANAGER 代理商归属认领的渠道经理
			                  OWN_ORG 归属的组织
			                  BO_CREATOR 申请发起人
			CONST:常量
     */
    public static final class VALUE_METHOD {
		/**
		 * 取值方法
		 */
		public static final String ATTR= "ATTR";//属性取值 
		
		public static final String SQL = "SQL"; //SQL取值

		public static final String VAR = "VAR"; //内置环境变量方式
		
		public static final String CONST = "CONST";//常量取值
		
		public static final String CLASS = "CLASS";//类取值
		
		public static final String ROUTE = "ROUTE";//环节路由表004方式，回退到目标环节，直接找目标环节的工单执行人 joshui
    }
    
    /**
     * 
     * 内置环境(线程)变量  
     * @author major
     *
     */
    public static final class CTX_VAR {

		public static final String UP_MANAGER= "UP_MANAGER";	//当前操作工号的上级经理
		
		public static final String OWN_MANAGER = "OWN_MANAGER"; //代理商归属认领的渠道经理
		
		public static final String OWN_ORG = "OWN_ORG"; 		//当前操作工号的组织
		
		public static final String BO_CREATOR = "BO_CREATOR";	//申请发起人
		
		public static final String SELECT_LAN_ID = "SELECT_LAN_ID";//页面选择的本地网（区分登录本地网）
		
		public static final String DUTY_ATTR = "DUTY_ATTR";//责任属性
		
		//public static final String IS_FLOW_SKIP = "0";//是否跳过环节
    }
    
    
	/**
	 * 环节跳过条件类型
	 */
	public static final String TACHE_SKIP_COND_TYPE_SQL = "001";//SQL计算方式 
	
	public static final String TACHE_SKIP_COND_TYPE_CLASS = "002";//JAVA类
	
	/**
	 * 环节任务类型 TASK_TYPE
		SINGLE_TASK或空：一个环节只对应一个工单 
		MULTI_TASK_AND：一个环节对应多个工单，并且所有的工单都完成时该环节才完成      
		MULTI_TASK_OR：一个环节对应多个工单，只要一个环节完成则该环节完成  
		是在实例化工单的时候，如果有多个派单的目标，则实例化多个工单实例（目前只支持采用相同的工单类型）
	 */ 
	public static final String TASK_TYPE_SINGLE_TASK ="SINGLE_TASK";
	public static final String TASK_TYPE_MULTI_TASK_AND ="MULTI_TASK_AND";
	public static final String TASK_TYPE_MULTI_TASK_OR ="MULTI_TASK_OR";
	
	//以下为AP CRM扩展 具体化
	public static final String TASK_TYPE_CONSTRUCT = "C1";  //施工单	
	public static final String TASK_TYPE_FEE = "C2";  //收费单	
	public static final String TASK_TYPE_CALLBACK = "C3";  //回访单	
	public static final String TASK_TYPE_FIX = "C4";  //维护保障单	
	public static final String TASK_TYPE_COMPLAIN = "C5";  //客户投诉单
	
	
	
	
	
	/**
	 * 环节派单方式
	 */ 
	public static final String DISPATCH_WAY_AUTO = "AUTO"; //自动派单 "A"
	
	public static final String DISPATCH_WAY_MANUAL = "MANUAL"; //人工派单 "M"
	
	
	/**
	 * 环节工作类型
	 */
	public static final String WORK_TYPE_PROC = "DEAL"; //处理环节  PROC
	
	public static final String WORK_TYPE_AUDIT = "AUDIT"; //审批环节
	
	public static final String WORK_TYPE_AUDIT_EDIT = "AUDIT_EDIT"; //审批+编辑环节
	
	/**
	 * 环节编码
	 */
	public static final String TACHE_CODE_NEW_REQ = "10"; //新建环节，以前为NEW_REQ
	
	public static final String TACHE_CODE_CONSTRUCT = "CONSTRUCT"; //施工环节
	
	public static final String TACHE_CODE_FEE = "FEE"; //施工环节
	
	public static final String TACHE_CODE_CALLBACK = "CALLBACK"; //施工环节
	
	
	/**
	 * 流程模板状态
	 */
	public static final String FLOW_TEMP_STATE_CREATE = "00C"; //新建
	public static final String FLOW_TEMP_STATE_ACTIVE = "00A"; //激活
	public static final String FLOW_TEMP_STATE_FAIL =   "00X"; //失效
	
	/**
	 * 属性值取值方式
	 * add by lidongsheng
	 */
	public static final class ATTR_VAL_FETCH_TYPE {
		public static final String UP_MANAGER = "UP_MANAGER"; //归属的上级经理
		public static final String OWN_MANAGER = "OWN_MANAGER"; //代理商归属认领的渠道经理 
		public static final String SQL = "SQL"; //SQL
		public static final String CLASS = "CLASS"; //CLASS
	}
	
	
	/**
	 * 流程实例(业务单)状态
	 * @author major
	 *
	 */
	public static final class BO_STATE {
		/**
		 * 流程实例状态
		 */
		public static final String NEW= "NEW";//流程实例新建状态  
		
		public static final String ACTIVITY = "ACTIVE";//流程实例激活状态  0

		public static final String SUSPEND = "SUSPEND";//流程实例挂起状态  1
		
		public static final String INVALID = "INVALID";//流程实例作废状态  2
		
		public static final String END = "END";//流程实例结束状态 9
		
		/**
		 * 流程实例状态名称
		 */
		public static final String NEW_NAME = "新建";//流程实例激活状态
		
		public static final String ACTIVITY_NAME = "激活";//流程实例激活状态

		public static final String SUSPEND_NAME = "挂起";//流程实例挂起状态 
		
		public static final String INVALID_NAME = "作废";//流程实例作废状态 
		
		public static final String STATE_END_NAME = "归档";//流程实例结束状态 
		
		public static final String WITHDRAW_NAME = "撤单";//流程实例撤单
	
	}

public static HashMap  flowStateMap=new HashMap(); //流程流转消息
	
	
	public static HashMap  flowInstStateMap=new HashMap(); //流程实例状态
	
	public static HashMap  flowInstStateNameMap=new HashMap(); //流程实例状态

	
	
	
	
	
	
	static{
		   flowStateMap.put(WO_ACTION_NORMAL,MsgConsts.ACTION_UPDATE_FLOW);
		   flowStateMap.put(WO_ACTION_SUSPEND,MsgConsts.ACTION_UPDATE_FLOW);
		   flowStateMap.put(WO_ACTION_WAKEUP,MsgConsts.ACTION_UPDATE_FLOW);
		   flowStateMap.put(WO_ACTION_DISPATCH,MsgConsts.ACTION_UPDATE_FLOW);
		   
		   
		   flowInstStateMap.put(WO_ACTION_NORMAL, BPMConsts.BO_STATE.ACTIVITY);
		   flowInstStateMap.put(WO_ACTION_SUSPEND,BPMConsts.BO_STATE.SUSPEND);
		   flowInstStateMap.put(WO_ACTION_WAKEUP,BPMConsts.BO_STATE.ACTIVITY);
		   flowInstStateMap.put(WO_ACTION_DISPATCH,BPMConsts.BO_STATE.ACTIVITY);
		   flowInstStateMap.put(WO_ACTION_INVALID,BPMConsts.BO_STATE.INVALID);
		   
		   
		   flowInstStateNameMap.put(WO_ACTION_NORMAL,BPMConsts.BO_STATE.ACTIVITY_NAME);
		   flowInstStateNameMap.put(WO_ACTION_SUSPEND,BPMConsts.BO_STATE.SUSPEND_NAME);
		   flowInstStateNameMap.put(WO_ACTION_WAKEUP,BPMConsts.BO_STATE.ACTIVITY_NAME);
		   flowInstStateNameMap.put(WO_ACTION_DISPATCH,BPMConsts.BO_STATE.ACTIVITY_NAME);
		   flowInstStateNameMap.put(WO_ACTION_INVALID,BPMConsts.BO_STATE.INVALID_NAME);
	}
	
	
	/**
	 * 获取消息类型
	 * @param workItemType
	 * @return string
	 */
	public static String getMsgType(String workItemType){
		   return  (String)flowStateMap.get(workItemType);
	}
	
	/**
	 * 获取流程实例状态
	 * @param workItemType
	 * @return
	 */
	public static String getFlowInstState(String workItemType){
		   return  (String)flowInstStateMap.get(workItemType);
	}

	/**
	 * 获取流程实例状态名称
	 * @param workItemType
	 * @return
	 */
	public static String getFlowInstStateName(String workItemType){
		   return  (String)flowInstStateNameMap.get(workItemType);
	}
	
}