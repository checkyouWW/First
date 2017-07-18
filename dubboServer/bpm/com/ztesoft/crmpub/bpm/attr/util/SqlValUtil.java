package com.ztesoft.crmpub.bpm.attr.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import appfrm.app.util.ListUtil;
import appfrm.resource.dao.impl.DAO;

import com.ztesoft.crmpub.bpm.BpmContext;

public class SqlValUtil {
	
	
	public static Object[] format( String originSql ){

		if( null == originSql ){
			return null;
		}
		
		List<String> paramList = new ArrayList<String>();
		
		Pattern pattern = Pattern.compile("[\\$][{](\\w+)[}]");
		Matcher matcher = pattern.matcher(originSql); 
		while (matcher.find()) { 
			paramList.add((String)BpmContext.getVar(matcher.group(1)));
		}
		
		originSql = originSql.replaceAll("[\\$][{](\\w+)[}]", "?");
		
		return new Object[]{originSql,paramList};
	}

	
	public static List<Map> fetch(String sqlText) {
		
		Object[] results = SqlValUtil.format(sqlText);
		if( null != results ){
			List<String> params = (List<String>)results[1];
			
			List<Map> resultDatas = DAO.queryForMap((String)results[0], (String[])params.toArray(new String []{}));
			
			if( !ListUtil.isEmpty(resultDatas) ){
				return resultDatas;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
	public static void main(String[] args) {
		
		BpmContext.putVar("STAFF_ID", "admin");
		BpmContext.putVar("CITY_ID", "200");
		
		Object[] results = SqlValUtil.format("select upper_staff from staff where staff_id = ${STAFF_ID} and  city_id = ${CITY_ID} and  bo_id = ${STAFF_ID}");
		System.out.println(results.toString());
	}

}