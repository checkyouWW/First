package com.ztesoft.inf.util.ftp;

import java.io.File;
import java.io.InputStream;

public interface IFtpUtil {

	boolean uploadFile(InputStream is,String ftpPath);
	boolean uploadFile(File file,String ftpPath);
	InputStream downFtpFile(String ftpPath);
	void destory();
	boolean createPath(String path);
	boolean isAvailableFtp();
	boolean delFile(String fileDir, String fileName) throws Exception;
	
}
