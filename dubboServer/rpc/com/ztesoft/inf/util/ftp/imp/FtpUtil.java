package com.ztesoft.inf.util.ftp.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import com.ztesoft.inf.util.ftp.IFtpUtil;

public class FtpUtil implements IFtpUtil{

	private  FTPClient ftpClient = null;
	
	private String host = null;
	private int port = 22;
	private String userName = null;
	private String password = null;
	private boolean isConnSuc = false;
	
	private final static Logger log = Logger.getLogger(FtpUtil.class);
	
	public FtpUtil(String host,int port,String userName,String password){
		this.host = host;
		this.password = password;
		this.port = port;
		this.userName = userName;
	}
	
	@Override
	public boolean uploadFile(InputStream is, String ftpPath) {
		this.connect();
		log.info("进行文件上传操作：");
		ftpPath = ftpPath.replace("\\", "/");
		String parentPath = ftpPath.substring(0, ftpPath.lastIndexOf("/"));
		boolean isExistsPath = this.isExistsDirectories(parentPath);
		if(!isExistsPath){
			//目录不存在，创建目录
			this.createPath(parentPath);
		}
		try {
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			boolean result = ftpClient.storeFile(ftpPath, is);
			is.close();
			if(result)
				log.info("文件上传成功");
			else
				log.error("文件上传失败");
			return result;
		} catch (Exception e) {
			log.error("文件上传失败...");
			log.error(e);
			e.printStackTrace();
			return false;
		}  finally{
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
			InputStream is = this.ftpClient.retrieveFileStream(ftpPath);
			if(is == null) throw new RuntimeException("文件输入流为空");
			log.info("文件获取成功");
			return is;
		} catch (Exception e) {
			log.info("ftp上的文件获取失败");
			log.info(e);
			//e.printStackTrace();
			return null;
		}
	}

	private boolean isExistsDirectories(String filePath){
		if(StringUtils.isEmpty(filePath)) return false;
		filePath = filePath.trim();
		if(StringUtils.isEmpty(filePath)) return false;
		if(filePath.charAt(filePath.length()-1) == '/')  filePath = filePath.substring(0, filePath.length()-2);
		filePath = filePath.replace("\\", "/");
		String parentPath = filePath.substring(0, filePath.lastIndexOf("/"));
		String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
		try {
			FTPFile [] files = this.ftpClient.listDirectories(parentPath);
			for(int i=0;i<files.length;i++){
				FTPFile tfile = files[i];
				if(fileName.equals(tfile.getName())) return true;
			}
		} catch (IOException e) {
			return false;
		}
		return false;
	}
	
	private void connect(){
		log.info(new StringBuilder("FTP 开始进行连接，连接目标主机:").append(host).append(":").append(port).append("...."));
		if(this.ftpClient != null){
			log.info("ftpClient 对象已经存在，无须重新获取...");
			return;
		}
		isConnSuc = false;
		ftpClient = new FTPClient();
		try {
			ftpClient.connect(host, port);
			ftpClient.login(userName, password);
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode()))
				throw new RuntimeException("认证失败");
			log.info("FTP 连接成功");
			isConnSuc = true;
		} catch (Exception e) {
			log.error("FTP连接失败，连接错误信息：");
			log.error(e);
			//e.printStackTrace();
		}
		
	}
	
	@Override
	public void destory() {
		log.info("准备销毁对象");
		if(this.ftpClient != null && this.ftpClient.isConnected()){
			log.info("准备关闭ftp连接");
			try {
				this.ftpClient.disconnect();
				log.info("ftp连接已关闭");
			} catch (IOException e) {
				log.error("ftp连接关闭失败");
				log.error(e);
				e.printStackTrace();
				return;
			}
			
		}
		this.ftpClient = null;
		log.info("对象清理成功");
		
	}
	
	//提供静态方法供调用之用
	public static IFtpUtil getFtpUtil(String host,int port,String userName,String password){
		FtpUtil ftpUtil = new FtpUtil(host,port,userName,password);
		try {
			ftpUtil.ftpClient = new FTPClient();
			ftpUtil.ftpClient.connect(host, port);
			ftpUtil.ftpClient.login(userName, password);
			ftpUtil.ftpClient.disconnect();
			return new FtpUtil(host,port,userName,password);
		} catch (Exception e) {
			return new SFtpUtil(host,port,userName,password);
		}
	}

	@Override
	public boolean createPath(String path) {
		
		
		if(StringUtils.isEmpty(path)) return false;
		path = path.trim();
		if(StringUtils.isEmpty(path)) return false;
		if(path.charAt(path.length()-1) == '/')  path = path.substring(0, path.length()-2);
		path = path.replace("\\", "/");
		
		
		this.connect();		
		
		if(this.isExistsDirectories(path)) return true;
		
		log.info(new StringBuilder("准备创建目录：").append(path));
		
		try {
			this.ftpClient.changeWorkingDirectory("/");
			String reallyPath = "";
			String [] paths = path.split("/");
			boolean createSuccess = true;
			for(String tpath : paths){
				if(StringUtils.isEmpty(tpath)){
					if(StringUtils.isEmpty(reallyPath) ) reallyPath="/";
					continue;
				}
				reallyPath += tpath+"/";
				if(this.isExistsDirectories(reallyPath)) continue;
				if(!this.ftpClient.makeDirectory(reallyPath)){
					createSuccess = false;
				}else
					createSuccess = true;
			}
			
			if(createSuccess){
				log.info("目录创建成功");
				return true;
			}else{
				log.error("目录创建失败");
				return false;
			}
				
		} catch (IOException e) {
			log.error("目录创建失败");
			log.error(e);
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean isAvailableFtp() {
		this.connect();
		return isConnSuc;
	}
	
	@Override
	public boolean delFile(String fileDir, String fileName)
			throws  Exception {
		this.connect();
		
		setFileType(FTP.BINARY_FILE_TYPE);
		
		String reallyFilePath = fileDir+"/"+fileName;
		return this.ftpClient.deleteFile(reallyFilePath);
			
	}
	
	private  void setFileType(int fileType) {
		try {
			ftpClient.setFileType(fileType);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private  void closeConnect() {
		try {
			if (ftpClient != null) {
				ftpClient.logout();
				ftpClient.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String gbkToIso8859(Object obj) {
		try {
			if (obj == null)
				return "";
			else
				return new String(obj.toString().getBytes("GBK"), "iso-8859-1");
		} catch (Exception e) {
			return "";
		}
	}
}
