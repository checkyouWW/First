package com.ztesoft.dubbo.mp.main.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.Data;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ztesoft.common.util.SessionHelper;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.inf.mp.sys.IMainService;
import com.ztesoft.sql.Sql;

import appfrm.resource.dao.impl.DAO;
/**
 * 
* @ClassName: MainServer 
* @Description: 主页的相关接口 
* @author chenminghua
* @date 2016年8月31日 上午9:43:33 
*
 */
@Service("MainServer")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MainService implements IMainService {
	
	
	/**
	 * 
	* @author chenminghua   
	* @date 2016年9月1日 上午10:21:53 
	* @Title: getIndicatorNum 
	* @Description: 获取关键指标数据 
	* @param @param params  空的
	* @param @return    设定文件 
	* @return Object    返回类型 
	* @throws
	 */
	@Transactional
	@Override
	public Object getIndicatorNum(Map params){
		
		Map map = new HashMap();
		/*map.put("attr_value", rs.getString(1));
		map.put("attr_value_name", rs.getString(2));*/
		
		//1、已开放数据服务数量：统计c_data_service中上架状态的记录数；
		String a[] =  new String [0];
		String sql = "select count(*) FROM c_data_service where state='00A'";
		String outsideNum = DAO.querySingleValue(sql,a);
		
		map.put("outsideNum", outsideNum);
		//2、累计数据申请次数：统计s_service_apply中审批通过状态的数据服务记录数；
		sql = "SELECT count(*) from s_service_apply WHERE state='00A'";
		
		String dataApplyNum = DAO.querySingleValue(sql,a);
		
		map.put("dataApplyNum", dataApplyNum);
		//3、累计数据分发次数：根据s_service_inst、s_service_inst_dispatch_log统计数据分发次数
		sql = Sql.S_DATA_SQLS.get("get_total_schedule_count");
		
		String dataGiveAwayNum = DAO.querySingleValue(sql,a);
		
		map.put("dataGiveAwayNum", dataGiveAwayNum);
		//4  累计任务申请次数：统计s_service_apply中审批通过状态的任务服务记录数；
		sql ="select count(*) from s_task_info ,s_service_apply "+
				" WHERE s_service_apply.apply_id=s_task_info.apply_id "
				+ " AND s_service_apply.state='00A'";
		String taskApplyNum = DAO.querySingleValue(sql,a);
		map.put("taskApplyNum", taskApplyNum);
		
		//5 累计任务调度次数：统计s_task_info的记录数；
		sql = Sql.S_DATA_SQLS.get("get_total_schedule_count");
		String taskScheduledNum = DAO.querySingleValue(sql,new String[]{});
		map.put("taskScheduledNum", taskScheduledNum);
		
		return map;
	}
	
	
	/**
	 * 
	* @author chenminghua   
	* @date 2016年9月1日 上午10:25:51 
	* @Title: getNotice 
	* @Description: 获取公告 
	* @param @param params  num 0 获取所有公告   其他数字  获取 对应的最新条目 
	* @param @return    设定文件 
	* @return Object    返回类型 
	* @throws
	 */
	@Transactional
	@Override
	public Object getNotices(Map params){

		int pageSize ,pageNo;
		String staff_id ;
		
		String total;
		
		Map rereturnMat = new HashMap();
		

		try{
			staff_id = StringUtil.getStrValue(params, "dubbo_staff_id");
			pageSize=Integer.parseInt(StringUtil.getStrValue(params, "pageSize"));
			pageNo=Integer.parseInt(StringUtil.getStrValue(params, "pageNo"));
		}catch (Exception e) {
			rereturnMat.put("status", false);
			rereturnMat.put("info", "查询公告时,参数错误");
			return rereturnMat;
		}
		String teamId="";
		teamId = SessionHelper.getTeamId();//团队id

		String sql ="select count(a.notice_id) from ( select M_NOTICE.* from 	"
				+ "			 M_NOTICE where NOTICE_RANGE ='1'"
				+ "							and STATE = '1' "
				+ "				union"
				+ "			select M_NOTICE.* from"
				+ "			 M_NOTICE,M_NOTICE_OBJ ,M_TEAM_MEMBER "
				+ "					where NOTICE_RANGE ='2'"
				+ "					and M_NOTICE_OBJ.NOTICE_ID=M_NOTICE.notice_id"
				+ "					and M_NOTICE_OBJ.NOTICE_OBJ_ID = M_TEAM_MEMBER.org_id"
				+ "					and M_TEAM_MEMBER.ORG_ID = ? "
			    + "					and M_NOTICE.STATE = '1'"
				+ "			 )a"
				+ "			 order by STATE_DATE DESC ";
		
		total = DAO.querySingleValue(sql, new String[]{teamId});
		
		
		 sql ="select a.*,"
				+ "(select d.staff_name from dm_staff d where d.staff_id = a.staff_id) staff_name"
		 		+ " from ( select M_NOTICE.* from "
				+ " M_NOTICE where NOTICE_RANGE ='1'"
				+ "				   and STATE = '1'"
				+ " union"
				+ " select M_NOTICE.* from"
				+ " M_NOTICE,M_NOTICE_OBJ ,M_TEAM_MEMBER "
				+ "	where NOTICE_RANGE ='2'"
				+ "		and M_NOTICE_OBJ.NOTICE_ID=M_NOTICE.notice_id"
				+ "		and M_NOTICE_OBJ.NOTICE_OBJ_ID = M_TEAM_MEMBER.org_id"
				+ "		and M_TEAM_MEMBER.ORG_ID = ? "
			    + "		and M_NOTICE.STATE = '1'"
				+ " )a"
				+ " order by STATE_DATE DESC "
				+ " limit "+((pageNo-1) *pageSize)+"," +pageSize;
		
		List list =DAO.queryForMap(sql, new String[]{teamId});
		
		
		
		rereturnMat.put("total", total);
		rereturnMat.put("pageNo", pageNo);
		rereturnMat.put("pageSize", pageSize);
		rereturnMat.put("data", list);
		rereturnMat.put("status", true);
		
		return rereturnMat;
	}
	
	@Transactional
	@Override
	public Object getApplyGiveAwayCount(Map params){
		 List dateArray = getPassedSevenDays();//时间列表
		//查询每个团队最近7天每天申请分发的次数
		String sql ="SELECT count(*) num,date_format(s_service_inst_dispatch_log.create_date,'%Y-%m-%d') date,"
				+ " s_data_dispatch.dispatch_type"
				+ "	 FROM s_service_inst,s_service_inst_dispatch_log,s_data_dispatch"
				+ "				 WHERE s_service_inst.inst_id=s_service_inst_dispatch_log.inst_id"
				+ "				AND s_service_inst_dispatch_log.dispatch_id=s_data_dispatch.dispatch_id"
				+ "  			AND DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= s_service_inst_dispatch_log.create_date"
				+ "				AND DATE_SUB(CURDATE(), INTERVAL 1 DAY) >= date(s_service_inst_dispatch_log.create_date)"
				+ "					and s_data_dispatch.dispatch_type=?"
				+ "			GROUP BY date"
				+ "				 ORDER BY s_service_inst_dispatch_log.create_date ASC";
		
		String type ="ftp";
		List<Map> list =DAO.queryForMap(sql, new String[]{type});
		
		//处理查询结果   [{id,name,data[{date,num}]},]
		List<Map> returnList = new ArrayList();//数据列表  
		Map rereturnMat = new HashMap();
		//map.put("taskScheduledNum", taskScheduledNum);

		Map dataMap = new HashMap();
		dataMap = (Map) getReturnData(list,dateArray,"FTP");
		returnList.add(dataMap);
		
		
		type = "db_import";
		list =DAO.queryForMap(sql, new String[]{type});
		dataMap = (Map) getReturnData(list,dateArray,"数据库导入");
		returnList.add(dataMap);
		
		

		rereturnMat.put("dateArray", dateArray);
		rereturnMat.put("lineDataList", returnList);
		return rereturnMat;
	}
	
										//  查询结果             时间结果         		  类型
	private Object getReturnData(List<Map> list,List dateArray,String type){//注意 …………该函数有多处调用 
		Map dataMap = new HashMap();
		List dateList = Arrays.asList("0", "0","0","0","0","0","0");
		String applyDate="" ,num="";
		for(int j=0;j<list.size();j++){ //时间从小到大排序
			
			applyDate = (String) list.get(j).get("date");
			num = (String) list.get(j).get("num");

			
			for(int k=0;k<dateArray.size();k++){
				if(applyDate.equals(dateArray.get(k))){
					dateList.set(k,num);
					break;
				}					
			}
		}
		dataMap.put("type", type);
		dataMap.put("data", dateList);
		
		
		return dataMap;
	}
	
	private List getPassedSevenDays() {  
       List dateArray =new ArrayList();
        Date date=new Date();
        date.setTime(date.getTime()-24*7*60*60*1000);//setDate(date.getDate() - 7);
        
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        
        String time="";    
        for(int i=0;i<7;i++){	
        	time = format.format(date);
        	dateArray.add(time);
        	//System.out.println(time);
        	date.setTime(date.getTime()+24*1*60*60*1000);
        }
        return dateArray;
    }  
	
	
	@SuppressWarnings("null")
	@Transactional
	@Override
	//8 任务调度次数
	public Object getTaskScheduleCount(Map params) {
		 List dateArray = getPassedSevenDays();//时间列表
		//查询每个团队最近7天每天申请分发的次数
		 
		String sql = Sql.S_TASK_SQLS.get("get_schedule_task_statis");
		
		List<Map> list =DAO.queryForMap(sql, new String[]{});
		
		//处理查询结果   [{id,name,data[{date,num}]},]
		List<Map> returnList = new ArrayList();//数据列表  
		Map rereturnMat = new HashMap();
		//map.put("taskScheduledNum", taskScheduledNum);

		Map dataMap = new HashMap();
		dataMap = (Map) getReturnData(list,dateArray,"调度次数");
		returnList.add(dataMap);		
		//returnList.add(dataMap);
		rereturnMat.put("dateArray", dateArray);
		rereturnMat.put("lineDataList", returnList);
		return rereturnMat;
	}
	
	@SuppressWarnings("null")
	@Transactional
	@Override
	public Object getDataGeveAwayTop10(Map params){
		
		String sql = "SELECT service_name name,DATA_SERV_DISPATCH_TOP_N.DISPATCH_AMOUNT count,DATA_SERV_DISPATCH_TOP_N.service_id id ,rank,ACCT"
				+ " FROM c_data_service,DATA_SERV_DISPATCH_TOP_N"
				+ " where DATA_SERV_DISPATCH_TOP_N.SERVICE_ID= c_data_service.service_id"
				+ " 	AND ACCT=DATE_FORMAT(NOW(),'%Y%m%d')"
				+ " ORDER BY DATA_SERV_DISPATCH_TOP_N.DISPATCH_AMOUNT DESC LIMIT 10";
		List<Map> list =DAO.queryForMap(sql, new String[]{});
		
		return list;
	}
	
	@SuppressWarnings("null")
	@Transactional
	@Override
	public Object getTaskScheduleTop10(Map params){
		
		String sql = "SELECT task_name name,TASK_SERV_SCHEDULE_TOP_N.SCHEDULE_AMOUNT count,TASK_SERV_SCHEDULE_TOP_N.service_id id ,rank,ACCT"
				+ " FROM c_task_service,TASK_SERV_SCHEDULE_TOP_N"
				+ " where TASK_SERV_SCHEDULE_TOP_N.SERVICE_ID= c_task_service.service_id"
				+ " 	AND ACCT=DATE_FORMAT(NOW(),'%Y%m%d')"
				+ " ORDER BY TASK_SERV_SCHEDULE_TOP_N.SCHEDULE_AMOUNT DESC LIMIT 10";
		List<Map> list =DAO.queryForMap(sql, new String[]{});
		
		return list;
	}
}
