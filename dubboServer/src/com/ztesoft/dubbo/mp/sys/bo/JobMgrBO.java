package com.ztesoft.dubbo.mp.sys.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.dubbo.mp.sys.vo.QuartzScheduleJob;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.sql.Sql;

import appfrm.app.meta.IFieldMeta;
import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;

/**
 * 菜单管理
 * 
 * @author
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class JobMgrBO {
	
	public RpcPageModel getQuartzScheduleJobs(Map params) {
		int pageIndex = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int pageSize = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String sql = "select * from quartz_schedule_job";
		List<String> sqlParams = new ArrayList<String>();
		PageModel page = DAO.queryForPageModel(sql, pageSize, pageIndex, sqlParams);
		return PageModelConverter.pageModelToRpc(page);
	}

	public Map addJob(Map params) {
		// 执行插入
		Map result = new HashMap();
		try {
			QuartzScheduleJob job = new QuartzScheduleJob();
			job.readFromMap(params);
			QuartzScheduleJob.getDAO().insert(job);
			
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "新增成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "新增失败!");
		}
		return result;
	}

	public Map editJob(Map params) {
		String job_id = StringUtil.getStrValue(params, "job_id");
		
		// 执行更新
		Map result = new HashMap();
		try {
			QuartzScheduleJob job = (QuartzScheduleJob) QuartzScheduleJob.getDAO().findById(job_id);
			if(job != null){
				Set<String> sets = params.keySet();
				Iterator it = sets.iterator();
				while(it.hasNext()){
					String key = (String) it.next();
					String value = (String) params.get(key);
					IFieldMeta field = QuartzScheduleJob.META.getField(key);
					if(field == null){
						continue;
					}
					job.set(key, value);
				}
				
				String[] fields = (String[]) job.updateFieldSet.toArray(new String[] {});
				QuartzScheduleJob.getDAO().updateParmamFieldsByIdSQL(fields).update(job);
			}
			
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "修改成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "修改失败!");
		}
		return result;
	}

	public Map deleteJob(Map params) {
		String job_id = StringUtil.getStrValue(params, "job_id");
		Map result = new HashMap();
		
		// 执行删除
		try {
			QuartzScheduleJob job = (QuartzScheduleJob) QuartzScheduleJob.getDAO().findById(job_id);
			QuartzScheduleJob.getDAO().deleteById(job);
			result.put(KeyValues.STATUSSIGN, KeyValues.SUCCESS);
			result.put(KeyValues.MSGSIGN, "删除成功!");
		} catch (Exception e) {
			e.printStackTrace();
			result.put(KeyValues.STATUSSIGN, KeyValues.FAIL);
			result.put(KeyValues.MSGSIGN, "删除失败!");
		}
		return result;
	}

	public PageModel getLogList(Map params) {
		
		int page = 1,pagesize = 10;
		try{
			page = Integer.parseInt(Const.getStrValue(params, "page"));
			pagesize = Integer.parseInt(Const.getStrValue(params, "rows"));
		}catch(Exception e){};
		
		String sql = Sql.S_DATA_SQLS.get("get_schedule_log_list");
		return DAO.queryForPageModel(sql, pagesize, page, new String[]{});
	}

}
