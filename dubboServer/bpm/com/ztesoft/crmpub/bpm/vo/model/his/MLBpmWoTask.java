package com.ztesoft.crmpub.bpm.vo.model.his;

import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;

import appfrm.app.annotaion.DBObj;
import appfrm.app.meta.IVOMeta;
import appfrm.resource.dao.IDAO;


@DBObj(tn = MLBpmWoTask.TABLE_CODE)
public class MLBpmWoTask extends MBpmWoTask {
    
    public static final IVOMeta META = IVOMeta.getInstance(MLBpmWoTask.class);
    public static final String TABLE_CODE = "L_BPM_WO_TASK";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
}
