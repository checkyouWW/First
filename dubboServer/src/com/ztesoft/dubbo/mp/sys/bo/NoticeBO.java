package com.ztesoft.dubbo.mp.sys.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.ztesoft.common.dao.DAOUtils;
import com.ztesoft.common.util.DBUtils;
import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.common.util.SessionHelper;
import com.ztesoft.dubbo.common.AttrService;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.sql.Sql;

import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;
import spring.util.DBUtil;

@SuppressWarnings({ "rawtypes", "all" })
public class NoticeBO {

	/**
	 * 
	 * @param params  根据标题模糊查询公告
	 * @return
	 */

	public List<Map> getNoticeByTitle(Map params){
		String noticeTitle = ((String)params.get("notice_title")).replaceAll(" ", "");
		StringBuilder sql = new StringBuilder();
		String staffId = "1";
		String objId = "1";
		sql.append("select t.notice_id,t.notice_title,t.notice_content,t.notice_range,t.staff_name,t.notice_obj_name,t.notice_obj_id,to_char(t.create_date,'YYYY-MM-DD HH24:MI') create_date,t.staff_id,t.state,t.state_name from ( ");
		sql.append("select notice_id,notice_title,notice_content,state,") ;
		sql.append("(select a.staff_name from dm_staff a where a.staff_id=staff_id) staff_name,");
		sql.append("(select v.attr_value_desc from dc_attr_value v where v.attr_value=notice_range and ");
		sql.append(" v.attr_id = (select attr.attr_id from dc_attribute attr where attr.attr_code='NOTICE_RANGE' and rownum <2) ) notice_range,");
		sql.append("decode(notice_obj_id,'-1','全体', (select a.org_name from dm_organization a where a.org_id=notice_obj_id)) notice_obj_name,");
		sql.append("(select v.attr_value_desc from dc_attr_value v where v.attr_value=state and ");
		sql.append(" v.attr_id = (select attr.attr_id from dc_attribute attr where attr.attr_code='NOTICE_STATE' and rownum <2) ) state_name,");
		sql.append(" notice_obj_id,create_date,staff_id from m_notice ");
		sql.append(" where staff_id=? ");
		sql.append(" union ");
		sql.append("select notice_id,notice_title,notice_content,state,") ;
		sql.append("(select a.staff_name from dm_staff a where a.staff_id=staff_id) staff_name,");
		sql.append("(select v.attr_value_desc from dc_attr_value v where v.attr_value=notice_range and ");
		sql.append(" v.attr_id = (select attr.attr_id from dc_attribute attr where attr.attr_code='NOTICE_RANGE' and rownum <2) ) notice_range,");
		sql.append("decode(notice_obj_id,'-1','全体', (select a.org_name from dm_organization a where a.org_id=notice_obj_id)) notice_obj_name,");
		sql.append("(select v.attr_value_desc from dc_attr_value v where v.attr_value=state and ");
		sql.append(" v.attr_id = (select attr.attr_id from dc_attribute attr where attr.attr_code='NOTICE_STATE' and rownum <2) ) state_name,");
		sql.append(" notice_obj_id,create_date,staff_id from m_notice ");
		sql.append(" where state='1' and notice_obj_id in (?,'-1') ");
		sql.append(") t  ");
		if(StringUtils.isNotEmpty(noticeTitle)){
			sql.append(" where instr(t.notice_title,'"+noticeTitle+"') > 0 ");
		}
		return  DAO.queryForMap(sql.toString(), new String[]{staffId,objId});
	}
	/**
	 * 
	 * @param params  获取所有公告
	 * @return
	 */
	public List<Map> getAllNotice(Map params){
		StringBuilder sql = new StringBuilder();
		String staffId = "";
		String objId = "";
		sql.append("select notice_id,notice_title,notice_content,notice_range,") ;
		sql.append(" notice_obj_id,create_date,staff_id from m_notice ");
		sql.append(" where staff_id=? ");
		sql.append(" union ");
		sql.append("select notice_id,notice_title,notice_content,notice_range,") ;
		sql.append(" notice_obj_id,create_date,staff_id from m_notice ");
		sql.append(" where state='1' and notice_obj_id in (?,'-1') ");
		return  DAO.queryForMap(sql.toString(), new String[]{staffId,objId});
	}
	
	public RpcPageModel queryNotice(HashMap params) {
		int page_index = Integer.parseInt(MapUtils.getString(params, "page", "1"));
		int page_size = Integer.parseInt(MapUtils.getString(params, "rows", "5"));
		String notice_title = MapUtils.getString(params, "notice_title");

		StringBuffer sql = new StringBuffer(Sql.SYS_SQLS.get("SELECT_M_NOTICE_SQL"));

		List sqlParams = new ArrayList();
		if (StringUtils.isNotEmpty(notice_title)) {
			sql.append(" and a.notice_title like ? ");
			sqlParams.add("%" + notice_title + "%");
		}
		sql.append(" order by a.create_date desc");
		RpcPageModel result = DBUtil.getSimpleQuery().queryForRpcPageModel(sql.toString(), null, page_size, page_index,
				sqlParams.toArray(new String[] {}));
		return result;
	}

	public boolean insertNotice(Map params) {

		List sqlParams = new ArrayList();
		String notice_id = SeqUtil.getSeq("m_notice", "notice_id");
		String notice_range = MapUtils.getString(params, "notice_range");
		sqlParams.add(notice_id);
		sqlParams.add(MapUtils.getString(params, "notice_title"));
		sqlParams.add(MapUtils.getString(params, "notice_content"));
		sqlParams.add(notice_range);
		sqlParams.add(DAOUtils.getFormatedDate());
		sqlParams.add(SessionHelper.getStaffId());
		sqlParams.add(DAOUtils.getFormatedDate());
		sqlParams.add(KeyValues.NOTICE_STATE_0);
		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("INSERT_M_NOTICE_SQL"), sqlParams);
		
		if (KeyValues.NOTICE_RANGE_2.equals(notice_range)) {
			sqlParams.clear();
			sqlParams.add(notice_id);
			sqlParams.add(MapUtils.getString(params, "notice_obj_id"));
			DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("INSERT_M_NOTICE_OBJ_SQL"), sqlParams);
		}

		return true;
	}

	public boolean updateNotice(Map params){
		String notice_id = MapUtils.getString(params, "notice_id");
		if (StringUtils.isEmpty(notice_id)) {
			return false;
		}
		String new_notice_range = MapUtils.getString(params, "notice_range");
		String notice_range = DBUtil.getSimpleQuery()
				.querySingleValue("select notice_range from m_notice where notice_id = ?", new String[] { notice_id });
		
		List sqlParams = new ArrayList();
		sqlParams.add(MapUtils.getString(params, "notice_title"));
		sqlParams.add(MapUtils.getString(params, "notice_content"));
		sqlParams.add(MapUtils.getString(params, "notice_range"));
		sqlParams.add(DAOUtils.getFormatedDate());
		sqlParams.add(notice_id);

		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("UPDATE_M_NOTICE_SQL"), sqlParams);

		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("DELETE_M_NOTICE_OBJ_SQL"), new String[] { notice_id });
		
		if (KeyValues.NOTICE_RANGE_2.equals(new_notice_range)) {
			sqlParams.clear();
			sqlParams.add(notice_id);
			sqlParams.add(MapUtils.getString(params, "notice_obj_id"));
			DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("INSERT_M_NOTICE_OBJ_SQL"), sqlParams);
		}

		return true;
	}
	
	public boolean deleteNotice(Map params){
		String notice_id = MapUtils.getString(params, "notice_id");
		if (StringUtils.isEmpty(notice_id)) {
			return false;
		}
		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("DELETE_M_NOTICE_SQL"), new String[] { notice_id });
		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("DELETE_M_NOTICE_OBJ_SQL"), new String[] { notice_id });
		return true;
	}

	public boolean updateNoticeState(Map params) {
		String notice_id = MapUtils.getString(params, "notice_id");
		String notice_state = MapUtils.getString(params, "notice_state");
		if (StringUtils.isEmpty(notice_id)) {
			return false;
		}
		DBUtil.getSimpleQuery().excuteUpdate(Sql.SYS_SQLS.get("UPDATE_M_NOTICE_STATE_SQL"),
				new String[] { notice_state, DAOUtils.getFormatedDate(), notice_id });
		return true;
	}
	
}
