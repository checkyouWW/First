package com.ztesoft.crmpub.bpm.task;

import java.util.Map;

import appfrm.app.GlobalIDMng;
import appfrm.app.attr.IAttrInst;
import appfrm.app.vo.IVO;

import com.ztesoft.crm.business.common.utils.StrTools;
import com.ztesoft.crmpub.bpm.vo.MsgBean;

@SuppressWarnings("rawtypes")
public class TacheSkip extends FetchValueClass {
	
	public boolean isChanging(String spec_field_name){
		MsgBean msg = this.msg;
		boolean result = false;
		if(msg == null){
			return result;
		}
		
		IAttrInst attrInst = getAttrInst();
		if(attrInst == null){
			return result;
		}
		
		IVO vo = attrInst.getParent().getVOById(attrInst.get());
		if(vo == null){
			return result;
		}
		IVO dbVo = vo.getDao().findById(vo.getId());
		if(dbVo == null){
			return result;
		}
		String[] fields = spec_field_name.split("/");
		for(String field_name : fields){
			String current_value = vo.get(field_name);
			String db_value = dbVo.get(field_name);
			if(current_value != null && !current_value.equals(db_value)){
				//值已经变化，则不跳过此环节；
				result = true;
			}
		}
		
		
		return result;
	}

	protected IAttrInst getAttrInst() {
		MsgBean msg = this.msg;
		if(msg == null){
			return null;
		}
		Map param = msg.getParam();
		if(param == null){
			return null;
		}
		String obj_id = StrTools.getStrValue(param, "obj_id");
		Object object = GlobalIDMng.getInstance().getObj(obj_id);
		if (!(object instanceof IAttrInst)) return null;
		IAttrInst attrInst = (IAttrInst) object;
		return attrInst;
	}

	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
