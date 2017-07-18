package com.ztesoft.dubbo.sp.workspace.service;

import com.ztesoft.common.util.SessionHelper;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.inf.util.ResultMap;
import com.ztesoft.dubbo.mp.sys.bo.OrgMgrBO;
import com.ztesoft.dubbo.mp.sys.bo.StaffBO;
import com.ztesoft.dubbo.sp.workspace.dao.WorkSpaceDAO;
import com.ztesoft.inf.sp.workspace.IWorkSpaceService;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.ioc.LogicInvokerFactory;

import spring.util.DBUtil;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kam on 2016/9/8.
 */
@Service
public class WorkSpaceService implements IWorkSpaceService {
    private WorkSpaceDAO getTaskHallDAO() {
        return LogicInvokerFactory.getInstance().getBO(WorkSpaceDAO.class);
    }

    @Autowired
    protected StaffBO staffBO;
    @Autowired
    private OrgMgrBO orgMgrBO;
    
    /**
     * 获取我的数据服务列表
     */
    @Override
    @Transactional
    public RpcPageModel myDataServiceList(Map params) {
        return getTaskHallDAO().myDataServiceList(params);
    }

    /**
     * 获取我的数据服务列表
     */
    @Override
    @Transactional
    public RpcPageModel myTaskServiceList(Map params) {
        return getTaskHallDAO().myTaskServiceList(params);
    }

    /**
     * 查询我的团队成员
     *
     * @param params
     * @return
     */
    @Override
    @Transactional
    public RpcPageModel myTeamMenbers(Map params) {
        return getTaskHallDAO().myTeamMembers(params);
    }

    /**
     * 查询我的团队成员
     *
     * @param params
     * @return
     */
    @Override
    @Transactional
    public List myTeamMenbersId(Map params) {
        return getTaskHallDAO().myTeamMembers();
    }

    /**
     * 删除我的团队成员
     *
     * @param params
     * @return
     * @throws Exception 
     */
    @Override
    @Transactional
    public Map deleteMenbers(Map params) throws Exception {
        HashMap new_params = new HashMap();
        new_params.put("org_id", SessionHelper.getTeamId());
        new_params.put("staff_id", MapUtils.getString(params, "staff_id"));
        new_params.put("staff_name", MapUtils.getString(params, "staff_name"));
        new_params.put("vr_staff_id", MapUtils.getString(params, "vr_staff_id"));
        boolean success = orgMgrBO.delOrgMem(new_params);
        if (!success) {
        	return new ResultMap().failed().msg("删除成员失败");
        }
        
        return new ResultMap().success();

    }

    /**
     * 设置我的团队管理员
     *
     * @param params
     * @return
     * @throws Exception 
     */
    @Override
    @Transactional
    public Map setAdmin(Map params) throws Exception {
        String staffId = MapUtils.getString(params, "staffId");
        String teamId = SessionHelper.getTeamId();
        String isDirector = MapUtils.getString(params, "isDirector", "F");
        
        if (orgMgrBO.checkDirectorExist(teamId)) {
        	return new ResultMap().failed().msg("只能设置一个团队负责人");
        }
        
        HashMap new_params = new HashMap();
        new_params.put("org_id", SessionHelper.getTeamId());
        new_params.put("staff_id", MapUtils.getString(params, "staff_id"));
        new_params.put("staff_name", MapUtils.getString(params, "staff_name"));
        new_params.put("vr_staff_id", MapUtils.getString(params, "vr_staff_id"));
        int success = orgMgrBO.setDirector(new_params);

        if (success != 0) {
        	return new ResultMap().failed().msg("设置团队负责人失败");
        }
        
        return new ResultMap().success();

    }

    /**
     * 向团队中添加成员
     * @param params
     * @return
     * @throws Exception 
     */
    @Override
    @Transactional
    public Map addMenber(Map params) throws Exception {
        List<String> staffIds = (List<String>) params.get("staffIds");
        String teamId = SessionHelper.getTeamId();
        
        HashMap new_params = new HashMap();
		List<Map> staffs = new ArrayList<Map>();
		for (String staff_id : staffIds) {
			List<Map> list = DBUtil.getSimpleQuery().queryForMapListBySql2(
					"select staff_id, staff_name from dm_staff where staff_id = ?", new String[] { staff_id });
			if (list.size() > 0) {
				staffs.add(list.get(0));
			}
		}
		new_params.put("org_id", teamId);
		new_params.put("staffs", staffs);
		boolean success = orgMgrBO.addOrgMem(new_params);
		if (!success) {
			return new ResultMap().failed().msg("添加成员失败");
		}
        return new ResultMap().success().msg("");
    }

    /**
     * 我的申请
     * @param params
     * @return
     */
    @Override
    @Transactional
    public RpcPageModel myApply(Map params) {
        return getTaskHallDAO().myApplyList(params);
    }
    /**
     * 恢复申请
     * @param params
     * @return
     * @throws Exception 
     */
    @Override
    @Transactional
    public Map recoverApply(Map params) throws Exception {
        return getTaskHallDAO().recoverApply(params);
    }

    /**
     * 取消
     * @param params
     * @return
     * @throws Exception 
     */
    @Override
    @Transactional
    public Map cancelApply(Map params) throws Exception {
        return getTaskHallDAO().cancelApply(params);
    }
    
    /**
     * 撤销
     * @param params
     * @return
     * @throws Exception 
     */
    @Override
    @Transactional
    public Map revokeApply(Map params) throws Exception {
        return getTaskHallDAO().revokeApply(params);
    }

}
