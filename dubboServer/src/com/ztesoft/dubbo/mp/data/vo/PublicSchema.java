package com.ztesoft.dubbo.mp.data.vo;

import java.util.List;

import com.ztesoft.common.util.StringUtil;
import com.ztesoft.inf.util.JsonUtil;
import com.ztesoft.inf.util.RedisKeyUtil;
import com.ztesoft.jedis.dao.RedisClient;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.util.ListUtil;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;

@SuppressWarnings("serial")
@DBObj(tn = PublicSchema.TABLE_CODE)
public class PublicSchema extends VO {
	
	public static final IVOMeta META = IVOMeta.getInstance(PublicSchema.class);
	
	public static final String TABLE_CODE = "public_schema";
	
	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO(){
		return META.getDAOMeta().getDAO();
	}
	
	@DBField  @IDField
	public String public_schema_id;
	
	@DBField
	public String owner;
	
	@DBField
	public String schema_code;
	
	@DBField
	public String schema_name;
	
	@DBField
	public String comments;
	
	public static PublicSchema getDefPublicSchema(){
		RedisClient redisClient = new RedisClient();
		String key = RedisKeyUtil.getSysParamByCode("def_public_schema");
		String json = redisClient.get(key);
		if(StringUtil.isNotEmpty(json)){
			PublicSchema res = JsonUtil.fromJson(json, PublicSchema.class);
			return res;
		}
		
		List<IVO> schemas = PublicSchema.getDAO().query(" 1=1");
		if(ListUtil.isEmpty(schemas)){
			return  null;
		}
		PublicSchema schema = (PublicSchema) schemas.get(0);
		String js = JsonUtil.toJson(schema);
		redisClient.set(key, js);
		return schema;
	}
}
