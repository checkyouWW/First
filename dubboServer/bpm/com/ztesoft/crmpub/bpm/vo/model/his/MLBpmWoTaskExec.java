package com.ztesoft.crmpub.bpm.vo.model.his;

import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTaskExec;

import appfrm.app.annotaion.DBObj;
import appfrm.app.meta.IVOMeta;
import appfrm.resource.dao.IDAO;


@DBObj(tn = MLBpmWoTaskExec.TABLE_CODE)
public class MLBpmWoTaskExec extends MBpmWoTaskExec {
    
    public static final IVOMeta META = IVOMeta.getInstance(MLBpmWoTaskExec.class);
    public static final String TABLE_CODE = "L_BPM_WO_TASK_EXEC";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
}
