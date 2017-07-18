package com.ztesoft.dubbo.mp.data.util;

import com.ztesoft.common.util.StringUtil;

public class SrcUtil {

	
	public static  String getSrcSchemaSql(String srcSystem){
		if(StringUtil.isEmpty(srcSystem)) throw new RuntimeException("来源平台【协议】，不能为空");
		if("HD".equalsIgnoreCase(srcSystem)){
			return "select * from meta_system";
		}else if("EDW".equalsIgnoreCase(srcSystem)){
			return " select * from omp_datasource_protocol where type='1' ";
		}else if("ODS".equalsIgnoreCase(srcSystem)){
			return " select * from omp_datasource_protocol where type='0' ";
		}
		
		throw new RuntimeException("无法正确识别来源平台【协议】");
	}
	
	
}
