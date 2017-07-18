package com.ztesoft.common.util;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

@SuppressWarnings("rawtypes")
public class ParamsConfig {

	private String fileName = "/spring/params.properties";
	private Properties params = new Properties();
	private static ParamsConfig paramsConfig = null;

	public ParamsConfig(){
		initParams();
	}

	public static ParamsConfig getInstance(){
		if(paramsConfig == null){
			paramsConfig = new ParamsConfig();
		}
		return paramsConfig;
	}

	public void initParams(){
		try {
			params = this.getConfigFileProperties(fileName);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		Enumeration enu = params.keys();
		String key = "";
		String val = "";
		while(enu.hasMoreElements()){
			key = (String)enu.nextElement();
			val = params.getProperty(key);
			if(val!=null && !"".equals(val)){
				params.put(key, val.trim());
			}
		}
	}

	public String getParamValue(String name){
		return params.getProperty(name);
	}

	@SuppressWarnings("rawtypes")
	public void updateProperty(String paramCode) throws Exception{
		Properties tempProperty = this.getConfigFileProperties(fileName);
		Enumeration enu = tempProperty.keys();
		String key = "";
		String val = "";
		while(enu.hasMoreElements()){
			key = (String)enu.nextElement();
			val = tempProperty.getProperty(key);
			if(val!=null && !"".equals(val)){
				tempProperty.put(key, val.trim());
			}
		}
		String paramValue= tempProperty.getProperty(paramCode);
		if(paramValue!=null){
			params.setProperty(paramCode,paramValue);
		}
	}

	private InputStream getFileInputStream(String fileName) throws Exception {
		return this.getClass().getClassLoader().getResourceAsStream(fileName) ;
	}

	public Properties getConfigFileProperties(String fileName) throws Exception {
		InputStream is = getFileInputStream(fileName) ;
		Properties configFile = new Properties() ;
		configFile.load(is) ;
		is.close() ;

		return configFile ;
	}
}
