package com.ztesoft.dubbo.sp.task.dao;

import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;
import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.sql.Sql;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by kam on 2016/9/2.
 */
public class TaskHallDAO {
    public RpcPageModel search(Map params) {
        PageModel result;
        int pageIndex = Integer.parseInt(MapUtils.getString(params, "page", "1"));
        int pageSize = Integer.parseInt(MapUtils.getString(params, "rows", "10"));

        String text = MapUtils.getString(params, "text");
        String engineType = MapUtils.getString(params, "engineType");
        String order = MapUtils.getString(params, "order");
        String asc = MapUtils.getString(params, "asc");

        String whereClause = "";
        List queryParams = new ArrayList();

        if (StringUtil.isNotEmpty(text)) {
            whereClause += " and cts.task_name like ? ";
            queryParams.add("%" + text + "%");
        }

        if (StringUtil.isNotEmpty(engineType)) {
            whereClause += " and cts.engine_type = ? ";
            queryParams.add(engineType);
        }

        if (StringUtil.isNotEmpty(order)) {
            whereClause += " order by " + order + (asc.equals("asc") ? " asc" : " desc");
        }


        result = DAO.queryForPageModel(Sql.C_TASK_SQLS.get("SEARCH_TASK_LIST") +whereClause, pageSize, pageIndex,queryParams);

        return PageModelConverter.pageModelToRpc(result);
    }

    public static void main(String[] args) {
         String sql = "INSERT INTO `c_task_service` (`service_id`, `task_name`, `task_code`, `engine_type`, `comments`, `instructions`, `state`, `apply_count`, `schedule_count`) VALUES " +
                "(%s, 'task_name_%s', 'task_%s', '%s', 'comments', 'instructionsxxx%s', '00A', '%s', '%s');";
        sql = "INSERT INTO `bdsme`.`dm_staff` (`staff_id`, `org_id`, `staff_code`, `password`, `staff_name`, `gender`, `staff_desc`, `create_date`, `eff_date`, `party_id`, `state`, `exp_date`, `is_manager`, `def_team_id`) " +
                "VALUES ('%s', '1', 'test%s', 'c4ca4238a0b923820dcc509a6f75849b', '测试人员%s', '0', '系统管理员', '2016-07-27 12:51:53', '2016-07-27 12:51:53', NULL, '00A', '', 'T', '17');";

        sql = "INSERT INTO s_service_inst (inst_id, apply_id, apply_code, apply_name, service_id, service_type, eff_date, exp_date, apply_date, apply_staff_id, org_id, state, state_date, last_data_acct, last_data_date) VALUES ('%s', '%s', '%s', 'name_%s', '%s', 'TASK', now(), now(), now(), '1', '2', '00A', now(), '1', now());";
        for(int i=1000;i<1100;i++){
            System.out.println(String.format(sql,i,i,i,i,i));
        }
    }
}
