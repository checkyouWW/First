package com.ztesoft.common.listener;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.ztesoft.common.util.SessionHelper;

/**
 * 对dubbo协议的请求进行拦截\过滤
 * @author lwt
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DubboFilter implements Filter {
	
	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		Object[] args = invocation.getArguments();
		if(args != null && args.length > 0){
			Object first = args[0];
			if(first instanceof Map){
				//创建线程变量
				Map map = (Map) first;
				Map staffMap = new HashMap();
				staffMap.put("staff_id", map.get("dubbo_staff_id"));
				staffMap.put("consumer_session_id", map.get("consumer_session_id"));
				SessionHelper.createSessionLocal(staffMap);
			}
		}
		
		return invoker.invoke(invocation);
	}

}
