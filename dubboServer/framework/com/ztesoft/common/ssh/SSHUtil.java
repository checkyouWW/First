package com.ztesoft.common.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.ztesoft.common.util.StringUtil;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * SSH
 * 
 * @author zhou.peiyuan
 * 
 */
public class SSHUtil {

	public static ShellResult exeCmd(String[] cmds, String[] hostNameParams) {
		return exeCmd(cmds, hostNameParams, false, -1);
	}

	public static ShellResult exeCmd(String[] cmds, 
			String[] hostNameParams, boolean ignoreError, long timeOut) {
		ShellResult result = new ShellResult();
		String hostname = hostNameParams[0];
		String username = hostNameParams[1];
		String password = hostNameParams[2];
		int port = 22;
		if(hostNameParams.length>3){
			try{
				port = Integer.parseInt(hostNameParams[3]);
			}catch(Exception e){
				
			}
		}
		
		long startTime = System.currentTimeMillis();
		Connection conn = null;
		try {

			conn = new Connection(hostname,port);
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword(username,
					password);

			if (isAuthenticated == false)
				throw new IOException("Authentication failed. name= "
						+ hostname);

			for (String cmd : cmds) {
				Session sess = null;
				try {
					sess = conn.openSession();
					// sess.requestPTY("vt100", 80, 24, 640, 480, null);
					sess.execCommand(cmd);
					waitResult(sess, result, startTime, timeOut,ignoreError);
				} catch (IOException e) {
					result.setSucess(false);
					e.printStackTrace();
				} finally {
					if (sess != null) {
						sess.close();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.setSucess(false);
		} finally {
			if (conn != null) {
				conn.close();
			}
		} 
		return result;
	}

	/**
	 * 取得输出流
	 * 
	 * @param sess
	 * @param resutl
	 * @throws IOException
	 */
	private static void waitResult(Session sess, ShellResult result,
			long startTime, long timeOut,boolean ignoreError) throws IOException {
		BufferedReader stdoutReader = null;
		BufferedReader stderrReader = null;
		try {
			InputStream stdout = new StreamGobbler(sess.getStdout());
			InputStream stderr = new StreamGobbler(sess.getStderr());
			stdoutReader = new BufferedReader(new InputStreamReader(
					stdout,"UTF-8"));
			stderrReader = new BufferedReader(new InputStreamReader(
					stderr,"UTF-8"));
			char[] arr = new char[512];
			int read;

			StringBuilder outmsg = new StringBuilder();
			while (true) {
				if (timeOut > 0
						&& (System.currentTimeMillis() - startTime) > timeOut) {
				}
				read = stdoutReader.read(arr, 0, arr.length);
				if (read < 0) {
					break;
				}
				outmsg.append(new String(arr, 0, read));
			}
			result.setSucess(true);
			result.setOutMessage(ObjectUtils.customDecode(outmsg.toString()));
			StringBuilder errmsg = new StringBuilder();
			while (true) {
				if (timeOut > 0
						&& (System.currentTimeMillis() - startTime) > timeOut) {
				}
				read = stderrReader.read(arr, 0, arr.length);
				if (read < 0) {
					break;
				}
				errmsg.append(new String(arr, 0, read));
			}
			if(errmsg.length()>0){
				result.setErrMessage(ObjectUtils.customDecode(errmsg.toString()));
				result.setSucess(false);
			}
			
			if(ignoreError){
				result.setSucess(true);
			}
		}catch (IOException e) {
			throw new IOException(e);
		} finally {
			if (stderrReader != null) {
				stderrReader.close();
			}
			if (stdoutReader != null) {
				stdoutReader.close();
			}
		}
	}

	public static void main(String[] args){
		String[] cmds = {
				"ls /etc/vsftpd | grep addvsftp.shh"};
		String[] host = {"10.45.47.15", "root", "pass123"};
		
		ShellResult result = SSHUtil.exeCmd(cmds, host);
		
		System.out.println(result.isSucess());
		System.out.println(result.getOutMessage().trim());
	}
}
