package com.ztesoft.dubbo.sp.task.service;


import com.ztesoft.inf.util.RpcPageModel;
import org.springframework.stereotype.Service;
import com.ztesoft.dubbo.sp.task.dao.TaskHallDAO;
import com.ztesoft.inf.sp.task.ITaskHallService;
import com.ztesoft.ioc.LogicInvokerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by kam on 2016/9/2.
 */
@Service
public class TaskHallService implements ITaskHallService {
    private TaskHallDAO getTaskHallDAO() {
        return LogicInvokerFactory.getInstance().getBO(TaskHallDAO.class);
    }


    @Override
    public String test(String string) {
        return string.toUpperCase();
    }

    @Override
    @Transactional
    public RpcPageModel search(Map params) {
        return getTaskHallDAO().search(params);
    }
}
