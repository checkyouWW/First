package com.ztesoft.crmpub.bpm.vo.model;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@DBObj(tn = MBpmTacheRoute.TABLE_CODE)
public class MBpmTacheRoute extends VO implements IVO{
	 	public static final IVOMeta META = IVOMeta.getInstance(MBpmTacheRoute.class);
	    public static final String TABLE_CODE = "BPM_TACHE_ROUTE";
	    
		public IVOMeta getMeta() {
			return META;
		}
		
		public static IDAO getDAO(){
	        return META.getDAOMeta().getDAO();
	    }
		
		@DBField @IDField @RootField
	    public String  route_id;
		
		@DBField 
	    public String  bo_type_id;
		
		
		@DBField 
	    public String  src_tache_code;
		
		@DBField 
	    public String  tar_tache_code;
		
		@DBField
		public String  attr_id;
		
		@DBField
		public String  attr_value;
		
		@DBField
		public String  type;

		@DBField
		public String  sql_str;
		
		@DBField
		public String  class_str;
		
		@DBField
		public String  state;
		
		@DBField(type=DBField.TYPE_DATE)
		public String  state_date;
		
		@DBField(type=DBField.TYPE_DATE)
		public String  create_date;
		
}
