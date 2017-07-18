package com.ztesoft.inf.mp.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appfrm.app.vo.PageModel;

@SuppressWarnings({ "rawtypes" })
public interface IOrgMgrService {
	
	public String getAppToken(Map params);
	
	public Map getInitInfo(Map params);
	
	public PageModel queryOrg(HashMap params);
	
	public Map saveOrg(HashMap params) throws Exception;
	
	public Map delOrg(HashMap params) throws Exception;
	
	public PageModel queryOrgMem(HashMap params);
	
	public boolean addOrgMem(HashMap params) throws Exception;
	
	public boolean delOrgMem(HashMap params) throws Exception;
	
	public int setDirector(HashMap params) throws Exception;
	
	public boolean relieveDirector(HashMap params);

	public List queryOrgList(Map params);
	
}