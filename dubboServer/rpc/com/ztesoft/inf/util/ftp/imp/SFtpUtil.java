package com.ztesoft.inf.util.ftp.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.ztesoft.inf.util.ftp.IFtpUtil;

public class SFtpUtil implements IFtpUtil {

	private String host = null;
	private int port = 22;
	private String userName = null;
	private String password = null;
	private ChannelSftp sftp = null;
	private static final Logger log = Logger.getLogger(SFtpUtil.class);
	private boolean isConnSuc = false;
	
	public SFtpUtil(String host,int port,String userName,String password){
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}
	
	@Override
	public boolean uploadFile(InputStream is, String ftpPath) {
		this.connect();
		String parentPath = null;
		
		ftpPath = ftpPath.replace("\\", "/");
		parentPath = ftpPath.substring(0, ftpPath.lastIndexOf("/"));
		if(!this.isExistsDirectories(parentPath)){
			//目录不存在，创建目录
			this.createPath(parentPath);
		}
		
		log.info("进行文件上传操作：");
		try {
			this.sftp.put(is,ftpPath);
			log.info("文件上传成功");
			return true;
		} catch (Exception e) {
			log.error("文件上传失败");
			log.error(e);
			if(e.getMessage().equals("No such file"))
				log.error("目录不存在");
			e.printStackTrace();
			return false;
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean uploadFile(File file, String ftpPath) {
		log.info(new StringBuilder("准备上传文件：").append(file.getAbsolutePath()));
		try {
			InputStream is = new FileInputStream(file);
			return this.uploadFile(is, ftpPath);
		} catch (Exception e) {
			log.error("文件上传失败...");
			log.error(e);
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public InputStream downFtpFile(String ftpPath) {
		log.info(new StringBuilder("准备获取文件：").append(host).append(":").append(port).append("/").append(ftpPath));
		this.connect();
		try {
			InputStream is = sftp.get(ftpPath);
			if(is == null) throw new RuntimeException("文件输入流为空");
			log.info("文件获取成功");
			return is;
		} catch (Exception e) {
			log.info("sftp上的文件获取失败");
			log.info(e);
			//e.printStackTrace();
			return null;
		}
	}
	
	private void connect(){
		log.info(new StringBuilder("SFTP 开始进行连接，连接目标主机:").append(host).append(":").append(port).append("...."));
		if(this.sftp != null){
			log.info("ChannelSftp 对象已经存在，无须重新获取...");
			return;
		}
		isConnSuc = false;
		try {
			JSch jsch = new JSch();
			jsch.getSession(userName, host, port);
			Session session = jsch.getSession(userName, host, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();  
			Channel channel = session.openChannel("sftp");  
			channel.connect(); 
			this.sftp = (ChannelSftp) channel;
			if(this.sftp == null) throw new RuntimeException("连接成功后sftp对象为空");
			isConnSuc = true;
			log.info("SFTP 连接成功");
		} catch (Exception e) {
			log.error("SFTP连接失败，连接错误信息：");
			log.error(e);
			//e.printStackTrace();
		} 
		
	}

	@Override
	public void destory() {
		log.info("准备销毁对象");
		if(this.sftp!=null && this.sftp.isConnected()){
			log.info("准备关闭sftp连接");
			this.sftp.disconnect();
			log.info("sftp连接已关闭");
		}
		this.sftp = null;
		log.info("对象清理成功");
	}

	@Override
	public boolean createPath(String path) {
		log.info(new StringBuilder("准备创建目录：").append(path));
		this.connect();
		try {
			path = path.trim();
			path = path.replace("\\", "/");
			String [] paths = path.split("/");
			String tempPath = "";
			for(String tpath : paths){
				if(StringUtils.isEmpty(tpath)) continue;
				tempPath +="/"+ tpath;
				if(!this.isExistsDirectories(tempPath))
					this.sftp.mkdir(tempPath);
			}
			log.info("目录创建成功");
			return true;
		} catch (SftpException e) {
			log.error("目录创建失败");
			log.error(e);
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean isExistsDirectories(String filePath){
		try {
			this.sftp.cd(filePath);
			return true;
		} catch (SftpException e1) {
			return false;
		}
	}

	@Override
	public boolean isAvailableFtp() {
		this.connect();
		return isConnSuc;
	}

	@Override
	public boolean delFile(String fileDir, String fileName) throws Exception {
		this.connect();
		if(this.isExistsDirectories(fileDir)){
			this.sftp.cd(fileDir);
			this.sftp.rm(fileName);
		}
		this.destory();
		return false;
	}

}
