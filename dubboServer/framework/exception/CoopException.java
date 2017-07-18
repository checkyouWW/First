package exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;


public class CoopException extends RuntimeException {
	private static Logger logger = Logger.getLogger(CoopException.class);
	
	public static final String ERROR = "ERROR";
	
	public static final String WORN = "WORN";
	
	public static final String INFO = "INFO";
	
	private String stackMessage = "";
	
	private String excType = "";
	
	public CoopException(String excType,String message, Throwable cause) {
		super(message,cause);
		this.excType = excType;
		stackMessage = getStackTraceAsString();
	}
	
	public CoopException(String excType,Throwable cause) {
		super(cause);
		this.excType = excType;
		stackMessage = getStackTraceAsString();
	}
	
	public String getExcType() {
		return excType;
	}
	
	public String getStackMessage() {
		return stackMessage;
	}

	public final void printStackTrace() {
		printStackTrace(System.out);
	}

	/**
	 * 获取当前堆栈信息
	 * @return String
	 */
	public final String getStackTraceAsString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		printStackTrace(pw);
		return sw.toString();
	}

}
