package com.ztesoft.dubbo.mp.data.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.sql.Sql;

@Repository
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AlgorithmDao {

	public RpcPageModel getAlgorithmPage(Map m) {
		String pageSize = Const.getStrValue(m, "rows");
		String pageIndex = Const.getStrValue(m, "page");
		String name = Const.getStrValue(m, "name");
		String state = Const.getStrValue(m, "state");
		int ps = 10;
		int pi = 1;
		if(StringUtils.isNotBlank(pageSize)) {
			ps = Integer.parseInt(pageSize);
		}
		if(StringUtils.isNotBlank(pageIndex)) {
			pi = Integer.parseInt(pageIndex);
		}
		StringBuffer sql = new StringBuffer();
		List<String> paramsList = new ArrayList<String>();
		String sql1 = Sql.C_DATA_SQLS.get("algothrim_list");
		sql.append(sql1);
		if(StringUtils.isNotBlank(name)) {
			sql.append(" and a.algorithm_name like ? ");
			paramsList.add("%"+name+"%");
		}
		if(StringUtils.isNotBlank(state)){
			sql.append(" and a.state = ? ");
			paramsList.add(state);
		}
		sql.append(" order by a.state_time desc ");
		PageModel p = DAO.queryForPageModel(sql.toString(), ps, pi, paramsList.toArray(new String[]{}));
		return PageModelConverter.pageModelToRpc(p);
	}

	public RpcPageModel getFieldPage(Map m) {
		String pageSize = Const.getStrValue(m, "rows");
		String pageIndex = Const.getStrValue(m, "page");
		String name = Const.getStrValue(m, "name");
		int ps = 10;
		int pi = 1;
		if(StringUtils.isNotBlank(pageSize)) {
			ps = Integer.parseInt(pageSize);
		}
		if(StringUtils.isNotBlank(pageIndex)) {
			pi = Integer.parseInt(pageIndex);
		}
		StringBuffer sql = new StringBuffer();
		List<String> paramsList = new ArrayList<String>();
		sql.append(" select a.algorithm_field_id,a.algorithm_id,c.algorithm_name,a.field_name,a.state,a.func,a.comments,date_format(a.create_time,'%Y-%m-%d %T') create_time,date_format(a.state_time,'%Y-%m-%d %T') state_time from c_algorithms_field a inner join c_algorithms c on a.algorithm_id = c.algorithm_id  where a.state!='00X'  ");
		if(StringUtils.isNotBlank(name)) {
			sql.append(" and a.field_name like ? ");
			paramsList.add("%"+name+"%");
		}
		
		sql.append(" order by a.state_time desc ");
		PageModel p = DAO.queryForPageModel(sql.toString(), ps, pi, paramsList.toArray(new String[]{}));
		
		return PageModelConverter.pageModelToRpc(p);
	}
	
	public Map addAlgorithm(Map m) {
		Map result = new HashMap();
		String alCode = Const.getStrValue(m, "al_code");
		String alName = Const.getStrValue(m, "al_name");
		String alFun = Const.getStrValue(m, "al_fun");
		String alDesc = Const.getStrValue(m, "al_desc");
		String type = Const.getStrValue(m, "type");
		
		String selectSql = "select count(*) from c_algorithms where (algorithm_code = ? or algorithm_name = ?) and state!='00X' ";
		String count = DAO.querySingleValue(selectSql, new String[]{alCode,alName});
		if(Integer.parseInt(count)>0) {
			result.put("res", false);
			result.put("res_mess", "操作失败:算法名称或编码重复，请重试");
			return result;
		}
		StringBuffer sql = new StringBuffer();
		List<String> paramsList = new ArrayList<String>();
		sql.append(" insert into c_algorithms(algorithm_code,algorithm_name,state,type,algorithm_func,comments,create_time,state_time) ");
		sql.append(" values(?,?,'00B',?,?,?,?,?) ");
		paramsList.add(alCode);
		paramsList.add(alName);
		paramsList.add(type);
		paramsList.add(alFun);
		paramsList.add(alDesc);
		String createTime = DateUtil.getFormatedDateTime();
		paramsList.add(createTime);
		paramsList.add(createTime);
		try{
			DAO.update(sql.toString(), paramsList.toArray(new String[]{}));
		}catch(Exception e) {
			result.put("res", false);
			result.put("res_mess", "操作失败:"+e);
			return result;
		}
		result.put("res", true);
		result.put("res_mess", "操作成功");
		return result;
	}

	public Map addField(Map m) {
		Map result = new HashMap();
		String fieName = Const.getStrValue(m, "fie_name");
		String alId = Const.getStrValue(m, "al_in_field_id");
		String fieFun = Const.getStrValue(m, "fie_fun");
		String fieDesc = Const.getStrValue(m, "fie_desc");
		
		String selectSql = "select count(*) from c_algorithms_field where field_name = ? and state!='00X' ";
		String count = DAO.querySingleValue(selectSql, new String[]{fieName});
		if(Integer.parseInt(count)>0) {
			result.put("res", false);
			result.put("res_mess", "操作失败:字段名称重复，请重试");
			return result;
		}
		
		StringBuffer sql = new StringBuffer();
		List<String> paramsList = new ArrayList<String>();
		sql.append(" insert into c_algorithms_field(algorithm_id,field_name,state,func,comments,create_time,state_time) ");
		sql.append(" values(?,?,'00B',?,?,?,?) ");
		paramsList.add(alId);
		paramsList.add(fieName);
		paramsList.add(fieFun);
		paramsList.add(fieDesc);
		String createTime = DateUtil.getFormatedDateTime();
		paramsList.add(createTime);
		paramsList.add(createTime);
		try{
			DAO.update(sql.toString(), paramsList.toArray(new String[]{}));
		}catch(Exception e) {
			result.put("res", false);
			result.put("res_mess", "操作失败:"+e);
			return result;
		}
		result.put("res", true);
		result.put("res_mess", "操作成功");
		return result;
		
	}

	public Map updateAlgorithm(Map m) {
		return null;
		
	}

	public Map updateField(Map m) {
		return null;
		
	}

	public Map downAlgorithm(Map m) {
		Map res = new HashMap();
		String id = Const.getStrValue(m, "id");
		if(StringUtils.isBlank(id)) {
			res.put("res", false);
			res.put("res_mess", "获取信息标识错误");
			return res;
		}
		String sql = " update c_algorithms set state='00S' , state_time=? where algorithm_id=?";
		try{
			String createTime = DateUtil.getFormatedDateTime();
			DAO.update(sql, new String[]{createTime,id});
		}catch(Exception e) {
			res.put("res", false);
			res.put("res_mess", "操作失败:"+e);
			return res;
		}
		res.put("res", true);
		res.put("res_mess", "操作成功");
 		return res;
	}

	public Map downField(Map m) {
		Map res = new HashMap();
		String id = Const.getStrValue(m, "id");
		if(StringUtils.isBlank(id)) {
			res.put("res", false);
			res.put("res_mess", "获取信息标识错误");
			return res;
		}
		String sql = " update c_algorithms_field set state='00S' , state_time=? where algorithm_field_id=?";
		try{
			String createTime = DateUtil.getFormatedDateTime();
			DAO.update(sql, new String[]{createTime,id});
		}catch(Exception e) {
			res.put("res", false);
			res.put("res_mess", "操作失败:"+e);
			return res;
		}
		res.put("res", true);
		res.put("res_mess", "操作成功");
 		return res;
		
	}

	public Map deleteAlgorithm(Map m) {
		Map res = new HashMap();
		String id = Const.getStrValue(m, "id");
		if(StringUtils.isBlank(id)) {
			res.put("res", false);
			res.put("res_mess", "获取信息标识错误");
			return res;
		}
		String sql = " update c_algorithms set state='00X' , state_time=? where algorithm_id=?";
		try{
			String createTime = DateUtil.getFormatedDateTime();
			DAO.update(sql, new String[]{createTime,id});
			m.remove("id");
			m.put("al_id", id);
			deleteFiled(m);
		}catch(Exception e) {
			res.put("res", false);
			res.put("res_mess", "操作失败:"+e);
			return res;
		}
		res.put("res", true);
		res.put("res_mess", "操作成功");
 		return res;
		
	}

	public Map deleteFiled(Map m) {
		Map res = new HashMap();
		String id = Const.getStrValue(m, "id");
		String alId = Const.getStrValue(m, "al_id");
		if(StringUtils.isBlank(id) && StringUtils.isBlank(alId)) {
			res.put("res", false);
			res.put("res_mess", "获取信息标识错误");
			return res;
		}
		List<String> paramsList = new ArrayList<String>();
		String sql = " update c_algorithms_field set state='00X' , state_time=? where 1=1 ";
		String createTime = DateUtil.getFormatedDateTime();
		paramsList.add(createTime);
		if(StringUtils.isNotBlank(id)) {
			sql += " and algorithm_field_id=? ";
			paramsList.add(id);
		}
		if(StringUtils.isNotBlank(alId)) {
			sql += " and algorithm_id=? ";
			paramsList.add(alId);
		}
		try{
			DAO.update(sql, paramsList.toArray(new String[]{}));
		}catch(Exception e) {
			res.put("res", false);
			res.put("res_mess", "操作失败:"+e);
			return res;
		}
		res.put("res", true);
		res.put("res_mess", "操作成功");
 		return res;
		
	}

	public Map upAlgorithm(Map m) {
		Map res = new HashMap();
		String id = Const.getStrValue(m, "id");
		if(StringUtils.isBlank(id)) {
			res.put("res", false);
			res.put("res_mess", "获取信息标识错误");
			return res;
		}
		String sql = " update c_algorithms set state='00A' , state_time=? where algorithm_id=?";
		try{
			String createTime = DateUtil.getFormatedDateTime();
			DAO.update(sql, new String[]{createTime,id});
		}catch(Exception e) {
			res.put("res", false);
			res.put("res_mess", "操作失败:"+e);
			return res;
		}
		res.put("res", true);
		res.put("res_mess", "操作成功");
 		return res;
	}

	public Map upField(Map m) {
		Map res = new HashMap();
		String id = Const.getStrValue(m, "id");
		if(StringUtils.isBlank(id)) {
			res.put("res", false);
			res.put("res_mess", "获取信息标识错误");
			return res;
		}
		String sql = " update c_algorithms_field set state='00A' , state_time=? where algorithm_field_id=?";
		try{
			String createTime = DateUtil.getFormatedDateTime();
			DAO.update(sql, new String[]{createTime,id});
		}catch(Exception e) {
			res.put("res", false);
			res.put("res_mess", "操作失败:"+e);
			return res;
		}
		res.put("res", true);
		res.put("res_mess", "操作成功");
 		return res;
	}

}
