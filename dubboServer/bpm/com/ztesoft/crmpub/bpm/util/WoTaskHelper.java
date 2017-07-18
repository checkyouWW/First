package com.ztesoft.crmpub.bpm.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.crm.business.common.utils.StrTools;
import com.ztesoft.crmpub.bpm.BpmContext;
import com.ztesoft.crmpub.bpm.attr.util.SqlValUtil;
import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.handler.ICallBackHandler;
import com.ztesoft.crmpub.bpm.task.FetchValueClass;
import com.ztesoft.crmpub.bpm.task.IWoTaskEngine;
import com.ztesoft.crmpub.bpm.task.impl.WoTaskEngineImpl;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.model.MBpmBoFlowInst;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTaskExec;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowTache;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmWoDispatchRule;
import com.ztesoft.crmpub.bpm.vo.spec.SBpmWoType;

import appfrm.app.util.ListUtil;
import appfrm.app.util.SeqUtil;
import appfrm.app.util.StrUtil;
import appfrm.app.vo.IVO;
import appfrm.resource.dao.impl.DAO;
import appfrm.resource.dao.impl.DaoUtils;
import exception.CoopException;

/**
 * 工单帮助类
 * 
 * @author lirx
 * 
 */
public class WoTaskHelper {

	/**
	 * 回单时的入口函数（审核也是回单的一种） joshui
	 * 工单回单 正常WO_FINISH /异常WO_FAIL
	 * @param params
	 *            回单运行参数对象
	 * @param callBackHandler
	 *            回调函数
	 * @return boolean
	 */
	public static Map returnWoTask(Map params,
			ICallBackHandler callBackHandler) throws Exception {
		Map retMap = new HashMap();
		Boolean result = false;
		if (params==null || params.size() <=0 ) {
			throw new RuntimeException("传入的回单参数为空,params为空!");
		}

		// 查询流程定义
		MsgBean msg = new MsgBean();
		
		String woId = (String) params.get("wo_id");
		Map flowGrade = (Map) params.get("gradedata");
		
		//如果工单为空
		if( StrUtil.isEmpty(woId) ){
			List<MBpmWoTask> taskList = (List) MBpmWoTask.getDAO().newQuerySQL(
					" bo_id=? and bo_type_id=? ").findByCond(
					(String) params.get("bo_id"),
					(String) params.get("bo_type_id"));
			//TODO 目前先这样做，取第一个工单标识
			if( null != taskList && taskList.size() >= 1 ){
				msg.setWoId(taskList.get(0).wo_id);
			}
		}else{
			msg.setWoId((String) params.get("wo_id"));
		}
		msg.setFlowGrade(flowGrade);//评分Map
		msg.setBoId((String) params.get("bo_id"));// 业务单ID
		msg.setOperId((String) params.get("oper_id"));// 回单人
		msg.setCallBackHandler(callBackHandler);// 业务回调函数
		msg.setMsgAction((String) params.get("action"));// 新建流程动作 WO_FINISH
														// /WO_FAIL
		msg.setMsgType(MsgConsts.MSG_TYPE_TASK);
		msg.setBackToFirst(StringUtil.getStrValue(params, "toFirst"));
		msg.setBackToTacheCode(StringUtil.getStrValue(params, "backToTacheCode"));
		msg.setIsNormal(StringUtil.getStrValue(params, "isNormal"));       //是否正常归档，撤单action用到
		msg.setWithdrawType(StringUtil.getStrValue(params, "withdrawType"));
		msg.setMsgHandleWay(MsgConsts.MSG_HANDLE_SYNC);// 消息同步处理
		msg.setAttrList((List) params.get("attrList"));
		msg.setRespContent((String) params.get("resp_content"));
		msg.setNeed_sms((String)params.get("need_sms"));
		msg.setFlowId((String)params.get("flow_id"));
		msg.setBoTypeId(StrTools.getStrValue(params, "bo_type_id"));
		msg.setTaskTitle(StringUtil.getStrValue(params, "task_title")); // 工单名称，必须要以“-”分隔
		msg.setWorkerType(StringUtil.getStrValue(params, "worker_type"));
		String var_bo_type_id = BpmContext.getVar("bo_type_id"); 
		if(StrTools.isEmpty(var_bo_type_id)){
			BpmContext.putVar("bo_type_id", StrTools.getStrValue(params, "bo_type_id"));
		}
		
		// 2.获取当前流程实例
		MBpmBoFlowInst flowInst = msg.getFlowInst();
		if (flowInst == null) {
			flowInst = (MBpmBoFlowInst) MBpmBoFlowInst.getDAO().findById(msg.getFlowId());
			msg.setFlowInst(flowInst);
		}
        if(flowInst != null){
            BpmContext.putVar(BPMConsts.CTX_VAR.BO_CREATOR, flowInst.create_oper_id);
        }

		IWoTaskEngine woTaskEngine = new WoTaskEngineImpl();
		result = woTaskEngine.putMsg(msg);
		retMap.put("result", result);
		retMap.put("flowId", msg.getFlowId());
		return retMap;

	}

	/**
	 * 生成任务单实例
	 * 
	 * @param flowTache
	 * @param msg
	 * @return FlowWorkOrder
	 * @throws Exception 
	 */
	private static MBpmWoTask genWorkOrderTask(String woTypeId,
			MBpmBoFlowInst flowInst, SBpmBoFlowTache flowTache, MsgBean msg) throws Exception {
		String curDbTime = DaoUtils.getDBCurrentTime();

		MBpmWoTask workOrder = new MBpmWoTask();
		workOrder.wo_id = SeqUtil.getInst().getNext("BPM_WO_TASK", "WO_ID");
		workOrder.wo_type_id = woTypeId;
		workOrder.work_type = flowTache.work_type;
		workOrder.task_type = flowTache.task_type;
		// 要兼容之前审核不通过时，工单状态的设置逻辑 joshui
		workOrder.wo_state = BPMConsts.WO_STATE_READY;
		if (MsgConsts.ACTION_WO_FAIL.equals(msg.getMsgAction()) && flowInst.getFlowDef().isFirstTache(workOrder.tache_code) ) {
				workOrder.set("wo_state", BPMConsts.WO_STATE_FAIL);
		}
		workOrder.state_date = curDbTime;
		workOrder.task_title = StrUtil.isNotEmpty(msg.getTaskTitle()) ? msg.getTaskTitle() : msg.getBoTitle() + "-" + flowTache.tache_name;
		workOrder.task_content = msg.getBoTitle();
		workOrder.tache_code = flowTache.tache_code;
		workOrder.tache_name = flowTache.tache_name;
		workOrder.flow_id = flowInst.flow_id;
		workOrder.bo_id = flowInst.bo_id;
		workOrder.bo_type_id = flowInst.bo_type_id;
		workOrder.create_date = curDbTime;
		workOrder.create_oper_id = flowInst.create_oper_id;// 流程创建人
		workOrder.dispatch_date = curDbTime;
		workOrder.dispatch_oper_id = msg.getOperId();// 上一环节处理人
		workOrder.dispatch_tache_code = msg.getWoTask() == null ? "" : msg.getWoTask().tache_code; // 上一环节
		//获取表单中的完成时限
        /*String attrSql="select b.attr_id from bpm_wo_type a ,bpm_template_attr b where a.template_id=b.template_id and b.field_name='deadline' and a.bo_type_id=? and a.tache_code=?";
        String attrId=DAO.querySingleValue(attrSql,new String[]{flowTache.bo_type_id,workOrder.dispatch_tache_code});
        List<HashMap> attrList=msg.getAttrList();
        if(attrList!=null&&attrList.size()>0&&StrUtil.isNotEmpty(attrId)){
            for(Map map:attrList){
                if(StrTools.getStrValue(map,"name").equals(attrId)){
                    workOrder.limit_date=StrTools.getStrValue(map,"value");
                }
            }
        }
        if(StrUtil.isEmpty(workOrder.limit_date)) {
            // 计算工单要求完成时间。其实目前环节与环节类型是一对一的 joshui
            SBpmWoType woType = flowTache.getWorkOrderType();
            if (StrUtil.isNotEmpty(woType.limit_interval_type) && StrUtil.isNotEmpty(woType.limit_interval_value)) {
            	// 根据本环节设置的时长计算
                if ("001".equals(woType.limit_interval_type)) {
                    workOrder.limit_date = WorkDateUtil.getInstance().getOverTime(curDbTime, woType.limit_interval_value);
                } else if ("002".equals(woType.limit_interval_type)) { // 跟其他环节的完成时间一样，比如外包转派
                    // @TODO-joshui 需求暂没明确要求这样
                }
            }
        }*/
		
		workOrder.getDao().insert(workOrder);
		return workOrder;
	}

	/**
	 * 生成工单执行者
	 * 可以配置多种派单规则，只要满足了派单规则，就生成该派单规则对应的处理人。 modified by joshui
	 * @param next_flowTache
	 * @param msg
	 * @param next_flowTache
	 * @return FlowWorkOrder
	 */
	private static List<MBpmWoTaskExec>  genWorkOrderExecutor(String next_woTypeId, MBpmWoTask next_workOrder,
			SBpmBoFlowTache next_flowTache, MsgBean msg) throws Exception {
		List<MBpmWoTaskExec> execs = new ArrayList<MBpmWoTaskExec>();
		
		List<SBpmWoDispatchRule> ruleL = next_flowTache.getDispatchRule(next_woTypeId);
		if (ruleL.isEmpty()) {
			throw new RuntimeException(
					"dispatch error! 找不到派单规则，请检查派单规则是否配置！wo_type_Id = "
							+ next_woTypeId);
		}
		
		for (SBpmWoDispatchRule dispatchRule : ruleL) {
			String destMethod = dispatchRule.dest_method;
			String destWorkerExpr = dispatchRule.dest_worker;
			String destWorkerType = dispatchRule.dest_worker_type;
			String dispatchWay = dispatchRule.dispatch_way;// 派单方式
			if(StringUtil.isNotEmpty(msg.getWorkerType())){
				//已页面传进来为准
				destWorkerType = msg.getWorkerType();
			}
			
			if (BPMConsts.DISPATCH_WAY_MANUAL.equals(dispatchWay)) { // 人工派单
				if (BPMConsts.VALUE_METHOD.ATTR.equalsIgnoreCase(destMethod)) { // 从界面传入目标执行者的值
					List<HashMap> attrList = msg.getAttrList();
					// destWorkerType的可能值为BPMConsts.WORKER_TYPE_STAFF、WORKER_TYPE_TEAM、WORKER_TYPE_ORG、WORKER_TYPE_STAFF_MULTI等
					for (HashMap<String, String> attrMap : attrList) {
						if (destWorkerExpr.equalsIgnoreCase(attrMap.get("name")) && StrUtil.isNotEmpty(attrMap.get("value"))) {
							if (BPMConsts.WORKER_TYPE_STAFF_MULTI.equals(destWorkerType)) {
								// 多选人员，并行派单，每个人有自己对应的工单 joshui
								String[] valArr = attrMap.get("value").split(BPMConsts.WORKER_TYPE_STAFF_MULTI_SPLIT);
								for (int i = 0; i < valArr.length; i++) {
									execs.add(buildTaskExecutor(valArr[i], destWorkerType, next_workOrder));
								}
							}else{
								execs.add(buildTaskExecutor(attrMap.get("value"), destWorkerType, next_workOrder));
							}
							
						}
					}
				}
			} else if ((BPMConsts.DISPATCH_WAY_AUTO.equals(dispatchWay) || "".equals(dispatchWay))) { // 自动派单
				// 审批不通过时，不需要通过自动方式指定执行人，除非是004类型的路由。
				if(MsgConsts.ACTION_WO_FAIL.equals(msg.getMsgAction()) && 
						!BPMConsts.VALUE_METHOD.ROUTE.equalsIgnoreCase(destMethod)){
					continue;
				}
				
				
				if (BPMConsts.VALUE_METHOD.SQL.equalsIgnoreCase(destMethod)) {
					List<Map> workers = SqlValUtil.fetch(destWorkerExpr);
					for (Map woker : workers) {
						String taskWorker = (String) woker.get("attr_id");
						execs.add(buildTaskExecutor(taskWorker, destWorkerType, next_workOrder));
					}
				} 
				if (BPMConsts.VALUE_METHOD.VAR.equalsIgnoreCase(destMethod)) {
					String taskWorker = BpmContext.getVar(destWorkerExpr);
					execs.add(buildTaskExecutor(taskWorker, destWorkerType, next_workOrder));
				} 
				if (BPMConsts.VALUE_METHOD.CONST.equalsIgnoreCase(destMethod)) {
					String taskWorker = destWorkerType;
					if( "SELF".equals(destWorkerExpr) ){
						taskWorker = msg.getOperId();
					}
					execs.add(buildTaskExecutor(taskWorker, destWorkerType, next_workOrder));
				}  
				if (BPMConsts.VALUE_METHOD.CLASS.equalsIgnoreCase(destMethod)) {
					String className = destWorkerExpr;
					Class clazz = Class.forName(className);
					FetchValueClass fetchValue = (FetchValueClass) clazz.newInstance();
					fetchValue.setMsg(msg);
					String taskWorker = fetchValue.execute(); // 通过BpmContext获取变量值
					execs.add(buildTaskExecutor(taskWorker, destWorkerType, next_workOrder));
				}
				
				if (BPMConsts.VALUE_METHOD.ROUTE.equalsIgnoreCase(destMethod)) { //找目标环节对应的已完成的最新工单执行人，目前只针对route等于004这种情况 joshui
					String sql = 
						"select COUNT(1) AS flag from bpm_tache_route t " +
						"where t.type = ? AND t.bo_type_id = ? AND t.src_tache_code = ? AND t.tar_tache_code = ? AND t.route_id = ?";
					List<Map<String, String>> countL = DAO.queryForMap(sql, new String[]{MsgConsts.BPM_TACHE_ROUTE_TYPE_004, 
							next_workOrder.bo_type_id, msg.getFlowTache().tache_code, next_flowTache.tache_code, destWorkerExpr});
					if (!"0".equals(countL.get(0).get("flag"))) {
						execs.addAll(buildExecutorByTache(next_workOrder, next_flowTache));
					}
					
				}
			}
		}
        // 如果是审核不通过，直接找打回环节的最新工单执行人，旧的代码是这种逻辑，后续也可以做成支持可以选择打回环节的处理人 joshui
        if (execs.isEmpty() && MsgConsts.ACTION_WO_FAIL.equals(msg.getMsgAction())) {
            execs.addAll(buildExecutorByTache(next_workOrder, next_flowTache));
            //return execs;
        }
		return execs;
	}

	/**
	 * 目标环节已执行U哦，找目标环节对应的已完成的最新工单执行人。目标环节可能有多条bpm_wo_task，所以需要取max
	 * @param next_workOrder
	 * @param next_flowTache
	 * Author : joshui
	 * Date ：2014-11-26
	 */
	private static List<MBpmWoTaskExec> buildExecutorByTache(MBpmWoTask next_workOrder, SBpmBoFlowTache next_flowTache) {
		List<MBpmWoTaskExec> execs = new ArrayList<MBpmWoTaskExec>();
		
		String sql = 
			"select a.task_worker, a.worker_type from bpm_wo_task_exec a where a.worker_type in('STAFF','STAFF_POS') AND a.wo_id = ( " +
			"       SELECT MAX(t.wo_id) FROM bpm_wo_task t WHERE T.wo_id <> ? AND t.flow_id = ? AND t.tache_code = ? " + 
			")";
		List<Map<String, String>> workerL = DAO.queryForMap(sql, new String[]{next_workOrder.wo_id, next_workOrder.flow_id, next_flowTache.tache_code});
		for (Map<String, String> worker : workerL) {
			execs.add(buildTaskExecutor(worker.get("task_worker"), worker.get("worker_type"), next_workOrder));
		}
		
		return execs;
	}

	/**
	 * 构造任务单执行者
	 * 
	 * @param workOrder
	 * @param msg
	 * @return FlowWorkOrderExecutor
	 */
	private static MBpmWoTaskExec buildTaskExecutor(String taskWorker,
			String workerType, MBpmWoTask workOrder) {
		if (StrUtil.isEmpty(taskWorker)) {
			throw new RuntimeException("创建任务执行者记录失败，原因：传入的处理人taskWorker为空！" );
		 }		
		 
		 MBpmWoTaskExec taskExecutor = new MBpmWoTaskExec();
	     taskExecutor.wo_id = workOrder.wo_id;
	     taskExecutor.bo_id = workOrder.bo_id;//业务单ID
	     taskExecutor.bo_type_id = workOrder.bo_type_id;//业务单类型
	     taskExecutor.work_type = workOrder.work_type;//工作类型
	     taskExecutor.task_type = workOrder.task_type;//任务类型
	     taskExecutor.task_title = workOrder.task_title;//任务标题
	     taskExecutor.task_content = workOrder.task_content;//任务内容
	     taskExecutor.tache_code = workOrder.tache_code;//环节编码
	     taskExecutor.tache_name = workOrder.tache_name;//环节名称
	     taskExecutor.flow_id = workOrder.flow_id;//流程实例ID
	     taskExecutor.exec_state = BPMConsts.WO_STATE_READY; // 执行人状态 joshui
	     String curDate = DaoUtils.getDBCurrentTime();
	     taskExecutor.dispatch_date = curDate;//派单时间
	     taskExecutor.dispatch_oper_id = workOrder.dispatch_oper_id;//派单人 = 上一环节处理人) 这个地方可能有问题，应该取当前回单人 @TODO-joshui
		 taskExecutor.task_worker = taskWorker;//工单指定者标识
		 taskExecutor.worker_type_original = workerType; //原始的工单执行者类型 joshui
		 taskExecutor.worker_type = BPMConsts.WORKER_TYPE_STAFF_MULTI.equals(workerType) ? BPMConsts.WORKER_TYPE_STAFF : workerType; //工单执行者类型 modified by joshui
		 //taskExecutor.getDao().insert(taskExecutor);
		  
	     return taskExecutor;
	  }
	
	 /**
     *更新工单执行人状态
      * xu.zhaomin
     */
    public static void updateWorkTaskExecState(String task_exec_id,String state) throws Exception{
        if (StrUtil.isEmpty(state)) {
            throw new RuntimeException("更新工单状态失败，原因：state为空！" );
        }
        MBpmWoTaskExec exec=(MBpmWoTaskExec)MBpmWoTaskExec.getDAO().findById(task_exec_id);
        if(exec!=null) {
            exec.set("exec_date", DateUtil.getFormatedDateTime());
            exec.set("exec_state", state);
            exec.getDAO().updateParmamFieldsByIdSQL(exec.updateFieldSet.toArray(new String[]{})).update(exec);
        }
    }

    /**
     * 根据参数生成工单执行人
     * xu.zhaomin
     * @param params
     */
    public static void genWorkTeamExec(Map params){
        String wo_id=(String)params.get("wo_id");
        String taskWorker=(String)params.get("task_worker");
        String workerType=(String)params.get("worker_type");
        if (StrUtil.isEmpty(wo_id)) {
            throw new RuntimeException("生成工单失败，原因：wo_id为空！" );
        }
        if (StrUtil.isEmpty(taskWorker)) {
            throw new RuntimeException("生成工单失败，原因：task_worker为空！" );
        }
        if (StrUtil.isEmpty(workerType)) {
            throw new RuntimeException("生成工单失败，原因：worker_type为空！" );
        }
        MBpmWoTask workOrder=(MBpmWoTask)MBpmWoTask.getDAO().findById(wo_id);
        if(workOrder!=null) {
            MBpmWoTaskExec taskExec=buildTaskExecutor(taskWorker, workerType, workOrder);
            taskExec.getDao().insert(taskExec);
        }
    }

     /**
     * 生成工单，并按派单规则、输入的派单目标进行派单
     * 新增动作、完成动作调用 joshui
     * @param flowInst
     * @param nextTache
     * @param msg
     * @throws Exception
     */
	public static void genWorkerOrderAndDispatch(MBpmBoFlowInst flowInst,
			SBpmBoFlowTache nextTache, MsgBean msg) throws Exception {	
		SBpmWoType nextWoType = nextTache.getWorkOrderType();
		List<MBpmWoTaskExec> execs = new ArrayList<MBpmWoTaskExec>();
		List<MBpmWoTask> tasks = new ArrayList<MBpmWoTask>();
		
		// 按照目前的实现，环节与woType是1对1关系 joshui
	    MBpmWoTask nextWorkOrder = genWorkOrderTask(nextWoType.wo_type_id, flowInst, nextTache, msg);
	    execs = genWorkOrderExecutor(nextWoType.wo_type_id, nextWorkOrder, nextTache, msg);
	    if(ListUtil.isEmpty(execs)){
	    	throw new CoopException(CoopException.INFO, "处理人不能为空！", null);
	    }
	    
	    tasks.add(nextWorkOrder);
	    msg.setWoId(nextWorkOrder.wo_id);

		/* 
		 * 应该是先生成执行人，再生成工单。
	     * 在共享模式下，一个环节一条工单，一个群组的人看到的是同一个工单，但只要群组内某个人抢单然后回单后，就走到了下一环节。
	     * 在并行模式下，一个环节多条工单，好几个人看到各自对应的工单，只有所有人都回完各自的工单，才能走到下一环节。
	     * 一般模式下，一个环节一条工单，也就是之前老的流程处理方式，一个环节只会生成一条工单，可能会有多个执行人，有一人回单后，就继续流转到下环节。
	       @TODO-joshui
	    */
		// 保存工单执行人
		for (MBpmWoTaskExec exec : execs) {
			// 如果是并行模式，每个执行人都需要有对应的工单，且工单的类型为BPMConsts.TASK_TYPE_MULTI_TASK_AND
			if (BPMConsts.WORKER_TYPE_STAFF_MULTI.equals(exec.worker_type_original)) {
				if (!BPMConsts.TASK_TYPE_MULTI_TASK_AND.equals(nextWorkOrder.task_type)) {
					nextWorkOrder.task_type = BPMConsts.TASK_TYPE_MULTI_TASK_AND;
				}else{
					MBpmWoTask task = new MBpmWoTask();
					task.readFromMap(nextWorkOrder.saveToMap());
					task.task_type = BPMConsts.TASK_TYPE_MULTI_TASK_AND;
					task.wo_id = SeqUtil.getInst().getNext("BPM_WO_TASK", "WO_ID");
					
					tasks.add(task);
					exec.wo_id = task.wo_id;
				}
				
				exec.task_type = BPMConsts.TASK_TYPE_MULTI_TASK_AND;
			}
			
			exec.getDao().insert(exec);
		}
		
		//保存工单
		for (MBpmWoTask task : tasks) {
			//task.getDao().insert(task);
		}
		
		flowInst.getWorkOrders().addAll(tasks);
	}
	
	/**
	 * 拽回工单，上一个环节工单，上一个环节工单处理人，不走派单规则表
	 * @param flowInst
	 * @param nextTache
	 * @param msg
	 * @throws Exception
	 */
	public static void genWorkerOrderByOper(MBpmBoFlowInst flowInst,
			SBpmBoFlowTache nextTache, MsgBean msg) throws Exception {	
		List<IVO> workOrderTypes = nextTache.getWorkOrderTypes();
		String taskWorker = msg.getTaskWorker();
		
		if (StringUtil.isEmpty(taskWorker)) {
			throw new RuntimeException("处理人taskWorkder不能为空！");
		}

		for(IVO vo:workOrderTypes ){
			SBpmWoType nextWoType = (SBpmWoType) vo;
			String wo_type_id = nextWoType.wo_type_id;
			MBpmWoTask woTask = genWorkOrderTask(wo_type_id, flowInst, nextTache, msg);

			List<SBpmWoDispatchRule> rule = nextTache.getDispatchRule(wo_type_id);
			if (rule.isEmpty()) {
				throw new RuntimeException(
						"dispatch error! 找不到派单规则，请检查派单规则是否配置！wo_type_id = "
						+ wo_type_id);
			}

			String destWorkerType = null;
			
			if(StringUtil.isNotEmpty(msg.getWorkerType())){
				//已页面传进来为准
				destWorkerType = msg.getWorkerType();
			}
			else {
				for (SBpmWoDispatchRule dispatchRule : rule) {
					destWorkerType = dispatchRule.dest_worker_type;
					break;
				}
			}
			
			MBpmWoTaskExec taskExec=buildTaskExecutor(taskWorker, destWorkerType, woTask);
			taskExec.getDao().insert(taskExec);
			
			flowInst.getWorkOrders().add(woTask);
			msg.setWoId(woTask.wo_id);
		}
	}


}
