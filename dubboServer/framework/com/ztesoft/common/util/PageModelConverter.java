package com.ztesoft.common.util;

import com.ztesoft.inf.util.RpcPageModel;
import appfrm.app.vo.PageModel;

/**
 * PageModel转换器
 * @author lwt
 *
 */
public class PageModelConverter {
	
	/**
	 * 从appfrm PageModel转换到统一RpcPageModel
	 * @param model
	 * @return
	 */
	public static RpcPageModel pageModelToRpc(PageModel model){
		RpcPageModel result = new RpcPageModel();
		result.setList(model.getList());
		result.setPageCount(model.getPageCount());
		result.setPageIndex(model.getPageIndex());
		result.setPageSize(model.getPageSize());
		result.setTotal(model.getTotal());
		result.setTotalCount(model.getTotalCount());
		result.setRows(model.getRows());
		return result;
	}
}
