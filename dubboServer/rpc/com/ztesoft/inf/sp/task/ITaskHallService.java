package com.ztesoft.inf.sp.task;

import com.ztesoft.inf.util.RpcPageModel;

import java.util.List;
import java.util.Map;

/**
 * Created by kam on 2016/9/2.
 */
public interface ITaskHallService {
    String test(String string);

    RpcPageModel search(Map params);
}
