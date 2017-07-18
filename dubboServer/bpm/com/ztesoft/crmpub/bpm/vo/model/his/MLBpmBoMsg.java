package com.ztesoft.crmpub.bpm.vo.model.his;

import com.ztesoft.crmpub.bpm.vo.model.MBpmBoMsg;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.resource.dao.IDAO;


@DBObj(tn = MLBpmBoMsg.TABLE_CODE)
public class MLBpmBoMsg extends MBpmBoMsg{
    
    public static final IVOMeta META = IVOMeta.getInstance(MLBpmBoMsg.class);
    public static final String TABLE_CODE = "L_BPM_BO_MSG";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
    
}
