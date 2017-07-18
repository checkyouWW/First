package com.ztesoft.crmpub.bpm.vo.model.his;

import com.ztesoft.crmpub.bpm.vo.model.MBpmAttrInst;

import appfrm.app.annotaion.DBObj;
import appfrm.app.meta.IVOMeta;
import appfrm.resource.dao.IDAO;


@DBObj(tn = MLBpmAttrInst.TABLE_CODE)
public class MLBpmAttrInst extends MBpmAttrInst {
    
    public static final IVOMeta META = IVOMeta.getInstance(MLBpmAttrInst.class);
    public static final String TABLE_CODE = "l_bpm_attr_inst";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
}
