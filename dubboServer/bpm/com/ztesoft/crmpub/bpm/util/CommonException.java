package com.ztesoft.crmpub.bpm.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CommonException {
	/**
	 * 取异常消息
	 * @Title: getException
	 * @Description: TODO
	 * @author chen.yiwan
	 * @param e
	 * @return
	 * @throws
	 */
	public static String getException(Exception e)
	{
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw, true));
		StringBuffer sb = sw.getBuffer();
		return sb.toString();
	}
}
