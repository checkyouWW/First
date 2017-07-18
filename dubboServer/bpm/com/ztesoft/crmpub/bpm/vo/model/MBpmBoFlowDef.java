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
 * Description: 类功能的描述  流程定义类
 * </pre>
 * @author caozj  cao.zhijun3@zte.com.cn
 * @date Dec 1, 2014 4:15:03 PM
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
@DBObj(tn = MBpmBoFlowDef.TABLE_CODE)
public class MBpmBoFlowDef extends VO implements IVO{
    public static final IVOMeta META = IVOMeta.getInstance(MBpmBoFlowDef.class);
    public static final String TABLE_CODE = "BPM_BO_FLOW_DEF";
    
	public IVOMeta getMeta() {
		return META;
	}
	
	public static IDAO getDAO(){
        return META.getDAOMeta().getDAO();
    }
	
	
	@DBField @IDField @RootField
    public String  bo_type_id;
	
	@DBField
	public String  table_code;
	
	@DBField
	public String  table_pk_col;
	
	@DBField
	public String  flow_name;
	
	@DBField
	public String  bo_class;
	
	@DBField
	public String  bo_desc;
	
	@DBField
	public String  bo_url;
	
	@DBField
	public String  lan_id;
	
	@DBField
	public String  region_id;
	
	@DBField
	public String  state;
	
	@DBField(type=DBField.TYPE_DATE)
	public String  create_date;
	
	@DBField(type=DBField.TYPE_DATE)
	public String  state_date;
	
	@DBField
	public String  oper_staff;
	
	@DBField
	public String  create_handler;
	
	@DBField
	public String  type;
	
	@DBField
	public String  flow_dir_id;
	
}
