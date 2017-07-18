/**
 * 
 */
package com.ztesoft.common.ssh;

/**
 * <pre>
 * 类名：[ShellResult.java]: 
 * 
 * 描述：
 * 
 * 版本：v 1.0.0.0
 * 
 * 创建时间：2014 Apr 10, 2014 10:30:26 AM
 * 
 * 
 * 开发人员：李了一
 * 
 * 修改记录：
 *     修改时间：
 *     修改人员：
 *     修改原因：
 * 
 * </pre>
 */
public class ShellResult {
	private boolean sucess;
	private String outMessage = "";
	private String errMessage = "";
	
	public boolean isSucess() {
		return sucess;
	}
	public void setSucess(boolean sucess) {
		this.sucess = sucess;
	}
	public String getOutMessage() {
		return outMessage;
	}
	public void setOutMessage(String outMessage) {
		this.outMessage = outMessage;
	}
	public String getErrMessage() {
		return errMessage;
	}
	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}
	
}
