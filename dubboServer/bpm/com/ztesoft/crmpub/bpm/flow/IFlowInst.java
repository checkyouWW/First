/**
 * 
 */
package com.ztesoft.crmpub.bpm.flow;

import com.ztesoft.crmpub.bpm.vo.MsgBean;



/**
 * 流程实例接口，设计思路采用业务单作为流程载体，即业务单ID 等同 流程ID
 * @author major
 *
 */
public interface IFlowInst {
	public String getFlow_id();  //获取流程对应的业务单ID, 业务单ID 等于  流程ID
	public String getBo_id();  //获取流程对应的业务单ID, 业务单ID 等于   流程ID
	public String getFlow_name() ;  //获取流程名

	
	public String getBo_state() ;
	public void setBo_state(String boState);
	
	public boolean dispatchTaskOrder(MsgBean msgBean) throws Exception;
	public boolean saveMyself();
}
