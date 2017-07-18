package com.ztesoft.crmpub.bpm.vo.model.his;

import com.ztesoft.crmpub.bpm.vo.model.MBpmBoChange;

import appfrm.app.annotaion.DBObj;
import appfrm.app.meta.IVOMeta;
import appfrm.resource.dao.IDAO;


@DBObj(tn = MLBpmBoChange.TABLE_CODE)
public class MLBpmBoChange extends MBpmBoChange {
    
    public static final IVOMeta META = IVOMeta.getInstance(MLBpmBoChange.class);
    public static final String TABLE_CODE = "L_BPM_BO_CHANGE";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
}
