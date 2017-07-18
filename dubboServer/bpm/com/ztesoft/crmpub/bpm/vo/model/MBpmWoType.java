package com.ztesoft.crmpub.bpm.vo.model;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@DBObj(tn = MBpmWoType.TABLE_CODE)
public class MBpmWoType extends VO implements IVO{
	 	public static final IVOMeta META = IVOMeta.getInstance(MBpmWoType.class);
	    public static final String TABLE_CODE = "BPM_WO_TYPE";
	    
		public IVOMeta getMeta() {
			return META;
		}
		
		public static IDAO getDAO(){
	        return META.getDAOMeta().getDAO();
	    }
		
		@DBField @IDField @RootField
	    public String  wo_type_id;
		
		@DBField
		public String  bo_type_id;
		
		@DBField
		public String  tache_code;
		
		@DBField
		public String  tache_name;
		
		@DBField
		public String  template_id;
		
		@DBField
		public String  deal_way;
		
		@DBField
		public String  finsh_event_handler;
		
		
		@DBField
		public String  fail_event_handler;
		
		
		@DBField
		public String  pause_event_handler;
		
		
		@DBField
		public String  resume_event_handler;
		
		
		@DBField
		public String  transfer_event_handler;
		
		@DBField
		public String  need_sms;
		
		@DBField
		public String  limit_interval_type;
		

		@DBField
		public String  limit_interval_value;
		
}
