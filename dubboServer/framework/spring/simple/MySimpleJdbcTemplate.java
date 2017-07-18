package spring.simple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import spring.util.DBUtil;
import appfrm.app.vo.PageModel;

import com.ztesoft.common.Constants;
import com.ztesoft.common.util.DBUtils;
import com.ztesoft.common.util.ParamsConfig;
import com.ztesoft.crm.business.common.utils.StrTools;
import com.ztesoft.inf.util.RpcPageModel;



/**
 * 锟斤拷展锟斤拷SimpleJdbcTemplate
 * MySimpleJdbcTemplate.java Class Name : MySimpleJdbcTemplate.java<br>
 * Description :<br>
 * Copyright 2010 ztesoft<br>
 * Date : 2012-7-24<br>
 * 
 * Last Modified :<br>
 * Modified by :liu.yuming 锟斤拷捉锟斤拷锟斤拷锟轿拷盏锟斤拷斐ｏ拷锟斤拷锟斤拷锟絥ull值锟斤拷锟斤拷锟斤拷呖沾锟� 2013-07-25<br>
 * Version : 1.0<br>
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MySimpleJdbcTemplate extends SimpleJdbcTemplate {

	public MySimpleJdbcTemplate(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
		super(namedParameterJdbcTemplate);
	}
	
	
//
//	/**
//	 * 锟斤拷锟接凤拷页锟斤拷询
//	 * @param sql
//	 * @param pageSize
//	 * @param pageIndex
//	 * @param args
//	 * @return
//	 * Author by zhou.jundi
//	 */
//	public PageModel queryForPageModel(String sql, int pageSize, int pageIndex,
//			Object... args) {
//		return queryForPageModel(sql, null, pageSize, pageIndex, args);
//	}
	
	/**
	 * 分页查询
	 * 
	 * @param sql
	 * @param pageSize
	 * @param pageIndex
	 * @param args
	 * @return Author by zhou.jundi
	 */
	public PageModel queryForPageModel(String sql, String countSql, int pageSize, int pageIndex, Object... args) {
		int startIndex = (pageIndex - 1) * pageSize + 1;
		int endIndex = startIndex + pageSize;
		
		if (StringUtils.isEmpty(countSql)) {
			countSql = " select count(*) count from ( " + sql + " ) c_table";// 计算总记录数
		}
		
		String pageSql = null;
		if (Constants.DB_TYPE_ORACLE.equals(DBUtils.DB_TYPE)) {
			pageSql = "select * from (select my_table.*,rownum as my_rownum from( " + sql
					+ " ) my_table where rownum< " + endIndex + ") where my_rownum>= " + startIndex;
		} else if (Constants.DB_TYPE_MYSQL.equals(DBUtils.DB_TYPE)) {
			pageSql = sql + " limit " + pageSize * (pageIndex - 1) + "," + pageSize;
		}

		int count = DBUtil.getSimpleQuery().queryForInt(countSql, args);

		PageModel pageModel = new PageModel();
		pageModel.setTotal(count);
		pageModel.setPageIndex(pageIndex);
		pageModel.setPageSize(pageSize);
		pageModel.setRows(this.queryForList(pageSql, args));

		return pageModel;
	}

	/**
	 * 分页查询
	 * 
	 * @param sql
	 * @param pageSize
	 * @param pageIndex
	 * @param args
	 * @return Author by zhou.jundi
	 */
	public RpcPageModel queryForRpcPageModel(String sql, String countSql, int pageSize, int pageIndex, Object... args) {
		int startIndex = (pageIndex - 1) * pageSize + 1;
		int endIndex = startIndex + pageSize;
		
		if (StringUtils.isEmpty(countSql)) {
			countSql = " select count(*) count from ( " + sql + " ) c_table";// 计算总记录数
		}
		
		String pageSql = null;
		if (Constants.DB_TYPE_ORACLE.equals(DBUtils.DB_TYPE)) {
			pageSql = "select * from (select my_table.*,rownum as my_rownum from( " + sql
					+ " ) my_table where rownum< " + endIndex + ") where my_rownum>= " + startIndex;
		} else if (Constants.DB_TYPE_MYSQL.equals(DBUtils.DB_TYPE)) {
			pageSql = sql + " limit " + pageSize * (pageIndex - 1) + "," + pageSize;
		}

		int count = DBUtil.getSimpleQuery().queryForInt(countSql, args);

		RpcPageModel pageModel = new RpcPageModel();
		pageModel.setTotal(count);
		pageModel.setPageIndex(pageIndex);
		pageModel.setPageSize(pageSize);
		pageModel.setRows(this.queryForList(pageSql, args));

		return pageModel;
	}


	/**
	 * Considers an Object array passed into a varargs parameter as
	 * collection of arguments rather than as single argument.
	 */
	protected Object[] getArguments(Object[] varArgs) {
		if (varArgs.length == 1 && varArgs[0] instanceof Object[]) {
			return (Object[]) varArgs[0];
		}
		else {
			return varArgs;
		}
	}
	
	public String querySingleValue(String sql  , List params){
		String[] param = (String[]) params.toArray(new String[]{});
		return this.querySingleValue(sql, param);
	}
	
	public Map queryMapBySql(String sql, String[] sqlParams){
		Map map = null;
		try{
			map = super.queryForMap(sql, sqlParams);
		}catch(EmptyResultDataAccessException e){
			
		}
		return map;
	}
	
	public String querySingleValue(String sql, String[] params) {
		String result = "" ;
		try{
			result = (String)this.queryForObject(sql , String.class, params);
		}catch(EmptyResultDataAccessException e){
			
		}catch(IncorrectResultSizeDataAccessException ie){
			try {
				Object obj = this.queryForList(sql, params);
				if(obj != null){
					List<Map> results = (List<Map>) obj;
					if(results.size() > 0){
						Map map = results.get(0);
						result = StrTools.getStrValue(results.get(0), (String) map.keySet().iterator().next());
					}
				}
			} catch (Exception e) {
				
			}
		}
		return result;
	}
	
	public int executeUpdate(String sql) {
		return this.update(sql, new HashMap());
	}
	
	public int excuteUpdate(String sql, List sqlParams){
		return this.update(sql, sqlParams.toArray());
	}
	
	public int excuteUpdate(String sql,  String[] sqlParams){
		return this.update(sql, sqlParams );
	}
	
	public List queryForMapListBySql2(String sql, String[] sqlParams) {
		List list = null;
		try{
			list = this.queryForList(sql, sqlParams);
		}catch(EmptyResultDataAccessException e){
			
		}
		return list;
	}
	
	public List queryForMapListBySql(String sql, List sqlParams) {
		List list = null;
		try{
			list = this.queryForList(sql, sqlParams.toArray());
		}catch(EmptyResultDataAccessException e){
			
		}
		return list;
	}
	
	/**
	 * 锟斤拷捉锟斤拷锟截空硷拷录锟届常 liu.yuming 2013-08-04
	 * @return
	 */
	public Map<String,Object> queryForMap(String sql){
		Map<String,Object> map = null;
		try{
			map = super.queryForMap(sql,new String[]{});
		}catch(EmptyResultDataAccessException e){
			
		}catch(IncorrectResultSizeDataAccessException ie){
			try {
				Object obj = this.queryForList(sql);
				if(obj != null){
					List<Map> results = (List<Map>) obj;
					if(results.size() > 0){
						return results.get(0);
					}
				}
			} catch (Exception e) {
				
			}
		}
		return map;
	}
	
	/**
	 * 锟斤拷捉锟斤拷锟截空硷拷录锟届常 liu.yuming 2013-08-04
	 * @return
	 */
	public Map<String,Object> queryForMap(String sql, Object...args ){
		Map<String,Object> map = null;
		try{
			map = super.queryForMap(sql,args);
		}catch(EmptyResultDataAccessException e){
			
		}catch(IncorrectResultSizeDataAccessException ie){
			try {
				Object obj = this.queryForList(sql, args);
				if(obj != null){
					List<Map> results = (List<Map>) obj;
					if(results.size() > 0){
						return results.get(0);
					}
				}
			} catch (Exception e) {
				
			}
		}
		return map;
	}
}
