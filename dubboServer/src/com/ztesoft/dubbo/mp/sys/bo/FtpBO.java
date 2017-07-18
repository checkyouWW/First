package com.ztesoft.dubbo.mp.sys.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.RSAUtil;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.dubbo.mp.sys.vo.OrgFtpRel;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.ftp.bean.FtpBean;

import appfrm.app.util.ListUtil;
import appfrm.resource.dao.impl.DAO;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class FtpBO {

	/**
	 * 获取ftp服务器列表
	 * @param params
	 * @return
	 */
	public List getFtpList(Map params) {
		String ftp_type = StringUtil.getStrValue(params, "ftp_type");
		String org_id = StringUtil.getStrValue(params, "org_id");
		String is_decrypt = StringUtil.getStrValue(params, "is_decrypt");
		String sql = "select * from ftp_server f where state = '00A'";
		List<String> sqlParams = new ArrayList<String>();
		
		if(KeyValues.FTP_TYPE_TEAM.equals(ftp_type) && StringUtil.isNotEmpty(org_id)){
			sql = "select f.ip,f.port,r.* from ftp_server f, org_ftp_rel r "
					+ "where f.state = '00A' "
					+ "and f.ftp_type = ? "
					+ "and r.ftp_id = f.ftp_id "
					+ "and r.state = '00A' "
					+ "and r.org_id = ?";
			sqlParams.add(ftp_type);
			sqlParams.add(org_id);
		}
		else if(StringUtil.isNotEmpty(ftp_type)){
			sql += " and f.ftp_type = ?";
			sqlParams.add(ftp_type);
		}
		List result = DAO.queryForMap(sql, sqlParams.toArray(new String[]{}));
		
		if("1".equals(is_decrypt)){
			for(int i=0;result!=null && i<result.size();i++){
				Map tmap = (Map) result.get(i);
				String tpwd = Const.getStrValue(tmap, "password");
				tmap.put("password",RSAUtil.decrypt(tpwd));
			}
		}
		
		return result;
	}
	
	/**
	 * 团队ftp列表
	 * @param org_id
	 * @return
	 */
	public List getOrgFtpList(String org_id) {
		String ftp_type = KeyValues.FTP_TYPE_TEAM;
		Map params = new HashMap();
		params.put("org_id", org_id);
		params.put("ftp_type", ftp_type);
		List list = this.getFtpList(params);
		return list;
	}
	
	/**
	 * 获取团队默认ftp
	 * @param org_id
	 * @return
	 */
	public FtpBean getDefOrgFtp(String org_id) {
		List list = this.getOrgFtpList(org_id);
		FtpBean bean = new FtpBean();
		if(!ListUtil.isEmpty(list)){
			Map map = (Map) list.get(0);
			bean = this.mapToFtpBean(map);
		}
		return bean;
	}
	
	/**
	 * 获取默认接口机ftp
	 * @param org_id
	 * @return
	 */
	public FtpBean getDefInfFtp() {
		String ftp_type = KeyValues.FTP_TYPE_INF;
		Map params = new HashMap();
		params.put("ftp_type", ftp_type);
		List list = this.getFtpList(params);
		
		FtpBean bean = new FtpBean();
		if(!ListUtil.isEmpty(list)){
			Map map = (Map) list.get(0);
			bean = this.mapToFtpBean(map);
		}
		return bean;
	}
	
	/**
	 * 获取任务默认ftp
	 * @param org_id
	 * @return
	 */
	public FtpBean getDefTaskFtp() {
		String ftp_type = KeyValues.FTP_TYPE_TASK;
		Map params = new HashMap();
		params.put("ftp_type", ftp_type);
		List list = this.getFtpList(params);
		
		FtpBean bean = new FtpBean();
		if(!ListUtil.isEmpty(list)){
			Map map = (Map) list.get(0);
			bean = this.mapToFtpBean(map);
		}
		return bean;
	}
	
	private FtpBean mapToFtpBean(Map map){
		FtpBean bean = new FtpBean();
		String port_str = StringUtil.getStrValue(map, "port");
		bean.ip = StringUtil.getStrValue(map, "ip");
		try {
			bean.port = Integer.parseInt(port_str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		bean.user = StringUtil.getStrValue(map, "user");
		bean.password = StringUtil.getStrValue(map, "password");
		bean.path = StringUtil.getStrValue(map, "path");
		return bean;
	}

	/**
	 * 保存团队ftp关联信息
	 * @param params
	 * @return
	 */
	public Map saveOrgFtpRel(Map params) {
		Map result = new HashMap();
		
		OrgFtpRel rel = new OrgFtpRel();
		rel.readFromMap(params);
		rel.state = "00A";
		rel.create_date = DateUtil.getFormatedDateTime();
		OrgFtpRel.getDAO().insert(rel);
		
		result.put("success", "true");
		return result;
	}
}
