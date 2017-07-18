package com.ztesoft.dubbo.sp.data.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

/**
 * 
 * 数据修改临时表
 *
 */
@DBObj(tn = MDataChangeNotify.TABLE_CODE)
public class MDataChangeNotify extends VO implements IVO {
    
	private static final long serialVersionUID = 6565459789215370311L;
	public static final IVOMeta META = IVOMeta.getInstance(MDataChangeNotify.class);
    public static final String TABLE_CODE = "DATA_CHANGE_NOTIFY";
    
	@Override
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
    
    
	@DBField @IDField 
    public String  change_id;
    
    @DBField
    public String  action_type;
    
    @DBField
    public String  owner_inst_id;
    
    @DBField
    public String  table_name;
    
    @DBField
    public String  inst_id;
    
    @DBField
    public String  field_name;
    
    @DBField
    public String  field_value;
    
    @DBField
    public String  old_field_value;
    
    @DBField
    public String tab_attr_id;
    
    @DBField(type=DBField.TYPE_DATE)
    public String  handle_time;
    
    @DBField
    public String  notify_id;
    
    @DBField
    public String  flow_id;
}
