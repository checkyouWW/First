package com.ztesoft.crmpub.bpm.vo.model;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;
/**
 * 
 * <pre>
 * Title:类中文名称
 * Description: 类功能的描述 
 * </pre>
 * @author caozj  cao.zhijun3@zte.com.cn
 * @date Dec 2, 2014 3:12:34 PM
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
@DBObj(tn = MBpmFlowTemplete.TABLE_CODE)
public class MBpmFlowTemplete extends VO implements IVO{
	 public static final IVOMeta META = IVOMeta.getInstance(MBpmFlowTemplete.class);
	    public static final String TABLE_CODE = "BPM_BO_FLOW_DEF_TEMP";
	    
		public IVOMeta getMeta() {
			return META;
		}
		
		public static IDAO getDAO(){
	        return META.getDAOMeta().getDAO();
	    }
		
		
		@DBField @IDField @RootField
	    public String  flow_templete_id;
		
		@DBField
	    public String  bo_type_id;
		
		@DBField
	    public String  flow_templete_name;
		
		@DBField
	    public String  version_code;
		
		@DBField
	    public String  state;
		
		@DBField
		public String templete_desc;
		
		@DBField(type=DBField.TYPE_DATE)
		public String  create_date;
		
		@DBField(type=DBField.TYPE_DATE)
		public String  state_date;
		
		@DBField
		public String  oper_staff;
		
		@DBField(type=DBField.TYPE_BLOB)
		public byte[]   xml_str;
		
}
