package com.ztesoft.crmpub.bpm.vo.spec;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@DBObj(tn = SBpmTacheSkipDef.TABLE_CODE)
public class SBpmTacheSkipDef extends VO implements IVO {
	public final static IVOMeta META = IVOMeta.getInstance(SBpmTacheSkipDef.class);
	public final static String TABLE_CODE = "bpm_tache_skip_def";
	@Override
	public IVOMeta getMeta() {
		// TODO Auto-generated method stub
		return META;
	}

	public static IDAO getDAO() {
		return META.getDAOMeta().getDAO();
	}
	
	@DBField
	@IDField
	@RootField
	private String bo_type_id;
	@DBField
	private String tache_code;
	@DBField
	private String skip_type;
	@DBField
	private String skip_desc;
	@DBField
	private String skip_rule_type;
	@DBField
	private String skip_rule_data;
	@DBField
	private String cur_tache_code;
	@DBField
	private String next_tache_code;
	public String getBo_type_id() {
		return bo_type_id;
	}

	public void setBo_type_id(String bo_type_id) {
		this.bo_type_id = bo_type_id;
	}

	public String getTache_code() {
		return tache_code;
	}

	public void setTache_code(String tache_code) {
		this.tache_code = tache_code;
	}

	public String getSkip_type() {
		return skip_type;
	}

	public void setSkip_type(String skip_type) {
		this.skip_type = skip_type;
	}

	public String getSkip_desc() {
		return skip_desc;
	}

	public void setSkip_desc(String skip_desc) {
		this.skip_desc = skip_desc;
	}

	public String getSkip_rule_type() {
		return skip_rule_type;
	}

	public void setSkip_rule_type(String skip_rule_type) {
		this.skip_rule_type = skip_rule_type;
	}

	public String getSkip_rule_data() {
		return skip_rule_data;
	}

	public void setSkip_rule_data(String skip_rule_data) {
		this.skip_rule_data = skip_rule_data;
	}

	public String getCur_tache_code() {
		return cur_tache_code;
	}

	public void setCur_tache_code(String cur_tache_code) {
		this.cur_tache_code = cur_tache_code;
	}

	public String getNext_tache_code() {
		return next_tache_code;
	}

	public void setNext_tache_code(String next_tache_code) {
		this.next_tache_code = next_tache_code;
	}
}
