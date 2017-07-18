package com.ztesoft.common.listener;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.ztesoft.common.util.DcSystemParamUtil;
import com.ztesoft.common.util.ParamsConfig;
import com.ztesoft.dubbo.common.AttrService;

/**
 * 
 * @Description: 系统初始化Servelet,用于Spring容器初始化后
 * @author KING
 * @date 2016年5月20日 下午2:41:02
 * @version V1.0
 */
public class InitServlet implements InitializingBean {

	private static final Logger logger = Logger.getLogger(InitServlet.class);
	
	/**
	 * 服务器启动，加载部分配置参数
	 */
	public void afterPropertiesSet() throws Exception {
		loadDcSystemParam();
		
		loadAttr();
	}

	/**
	 * 加载静态数据
	 */
	private void loadAttr() {
		try {
			logger.info("-----------------静态数据载入");
			AttrService.getInstance().initStaticValue();
			ParamsConfig.getInstance().initParams();
		} catch (Exception e) {
			logger.error("静态数据载入失败: ", e);
		}
	}

	/**
	 * 加载系统配置参数
	 */
	private void loadDcSystemParam() {
		DcSystemParamUtil.loadDcSystemParam();
	}

}