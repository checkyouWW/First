package com.ztesoft.dubbo.mp.task.vo;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

/**
 * 任务实例信息
 *
 */
@DBObj(tn = AttachFile.TABLE_CODE)
public class AttachFile extends VO implements IVO {

	private static final long serialVersionUID = -8335526515550222664L;

	public static final IVOMeta META = IVOMeta.getInstance(AttachFile.class);

	public static final String TABLE_CODE = "attach_file";

	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField @IDField
	public String file_id;
	
	@DBField
	public String file_type;

	@DBField
	public String file_name;

	@DBField
	public String file_location_type;

	@DBField
	public String file_location;

	@DBField
	public String file_blob_content;

	@DBField
	public String table_name;

	@DBField
	public String table_pk_name;

	@DBField
	public String table_pk_value;

	@DBField
	public String table_field_name;

	@DBField
	public String batch_id;

	@DBField
	public String create_date;

	@DBField
	public String create_pos_id;

	@DBField
	public String status;

}
