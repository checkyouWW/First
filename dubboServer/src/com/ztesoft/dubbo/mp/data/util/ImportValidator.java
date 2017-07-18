package com.ztesoft.dubbo.mp.data.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spring.util.SpringContextUtil;
import appfrm.resource.dao.impl.DAO;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.dubbo.common.AttrService;
import com.ztesoft.dubbo.mp.data.dao.DataMgrDao;
import com.ztesoft.inf.util.KeyValues;
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ImportValidator {

	
	public ImportValidator(){
		
	}
	
	
	public BooleanWrap checkCatalog(String catalogCode){
		String sql = "select * from c_data_catalog where catalog_code=? and state = ? ";
		List<Map> tlist = DAO.queryForMap(sql, new String[]{catalogCode,KeyValues.STATE_00A});
		if(tlist == null || tlist.size() == 0) return new BooleanWrap(false,"所属目录编码不存在");
		//判断是否叶子目录
		String sql2 = "select * from c_data_catalog where p_catalog_id=? and state=? ";
		List<Map> tlist2 = DAO.queryForMap(sql2, 
				new String[]{Const.getStrValue(tlist.get(0), "catalog_id"),KeyValues.STATE_00A});
		if(tlist2==null || tlist2.size()!=0) return new BooleanWrap(false,"所选目录尚有子目录，无法使用");
		return new BooleanWrap(true);
	}
	
	public BooleanWrap checkSrcInfo(String srcSystem,String srcSchema,String srcTable){
		if(StringUtil.isEmpty(srcSchema) || StringUtil.isEmpty(srcSchema) || StringUtil.isEmpty(srcTable)) 
			return new BooleanWrap(false,"来源平台，来源库，来源表不允许为空");
		//判断srcSystem和srcSchema是否对应得上
		String sql1 = "select * from meta_schema where `owner`=? and schema_code=?";
		List<Map> tlist1 = DAO.queryForMap(sql1, new String[]{srcSystem,srcSchema});
		if(tlist1==null || tlist1.size() == 0) return new BooleanWrap(false,"来源平台与来源库无法对应");
		srcTable = srcTable.replace("${lan_id}", "%");
		//继续判断
		DataMgrDao dmgrDao = SpringContextUtil.getApplicationContext().getBean(DataMgrDao.class);
		Map validat2Map = new HashMap();
		validat2Map.put("schema_code", Const.getStrValue(tlist1.get(0), "schema_code"));
		validat2Map.put("table_code", srcTable);
		Map resultMap = dmgrDao.validateSrcTable(validat2Map);
		if(resultMap==null ) return new BooleanWrap(false,"来源表不存在");
		String tableCount =Const.getStrValue(resultMap, "result_count");
		if(StringUtil.isEmpty(tableCount) || "0".equals(tableCount)) return new BooleanWrap(false,"来源表不存在");
		BooleanWrap returnBoolean = new BooleanWrap(true);
		returnBoolean.setData(resultMap.get("table_list"));
		return returnBoolean ;
	}
	
	public BooleanWrap checkHistoryExtract(String isHistoryExtract,String historyAcct,String extractType){
		
		if(StringUtil.isEmpty(isHistoryExtract)) return new BooleanWrap(false, "无法识别“是否历史数据抽取”");
		if("1".equals(isHistoryExtract)){
			if(StringUtil.isEmpty(historyAcct)) return new BooleanWrap(false,"清填写“抽取历史账期”");
			String pattern = "";
			if(KeyValues.EXTRACT_FREQ_MONTH.equals(extractType)) pattern="yyyyMM";
			else if(KeyValues.EXTRACT_FREQ_DAY.equals(extractType)) pattern="yyyyMMdd";
			if(!this.validateDate(historyAcct, pattern)) return new BooleanWrap(false,"请填写正确的历史账期");
			return  new BooleanWrap(true);
		}else{
			if(StringUtil.isNotEmpty(historyAcct)) return new BooleanWrap(false,"不用历史数据抽取，不需要填历史账期");
		}
		
		return null;
	}
	
	public BooleanWrap checkDateCode(String dataCode){
		if(StringUtil.isEmpty(dataCode)) return new BooleanWrap(false,"数据编码是必填字段");
		DataMgrDao dmgrDao = SpringContextUtil.getApplicationContext().getBean(DataMgrDao.class);
		Map sendMap = new HashMap();
		sendMap.put("data_code", dataCode);
		Map result = dmgrDao.validateDataCode(sendMap);
		Boolean rs = (Boolean) result.get("result");
		if(rs==null || !rs.booleanValue()) return new BooleanWrap(false,"数据编码已存在，不能重复添加");
		return new BooleanWrap(true);
	}
	
	public BooleanWrap checkDivsion(String firstDivsion,String secondDivsion){
		if(StringUtil.isEmpty(firstDivsion)) return new BooleanWrap(false,"一级分区无法识别");
		if(StringUtil.isEmpty(secondDivsion)) return new BooleanWrap(true);
		
		//获取一级分区的详细信息
		AttrService attrService = SpringContextUtil.getApplicationContext().getBean(AttrService.class);
		List<Map> firstAttrList = attrService.getStaticAttr(KeyValues.FIRST_DIVISION_CODE);
		String attrValueId = "";
		for(int i=0;firstAttrList!=null && i<firstAttrList.size();i++){
			Map ttmap = firstAttrList.get(i);
			String attrValue = Const.getStrValue(ttmap, "attr_value");
			if(firstDivsion.equals(attrValue))
				attrValueId = Const.getStrValue(ttmap, "attr_value_id");
		}
		if(StringUtil.isEmpty(attrValueId)) return new BooleanWrap(false);
		
		//获取二级分区信息
		List<Map> secondAttrList = attrService.getStaticAttr(KeyValues.SECOND_DIVISION_CODE);
		String attrValueId2 = "";
		for(int i=0;secondAttrList!=null && i<secondAttrList.size();i++){
			Map ttmap = secondAttrList.get(i);
			String attrValue = Const.getStrValue(ttmap, "attr_value");
			if(secondDivsion.equals(attrValue))
				attrValueId2 = Const.getStrValue(ttmap, "parent_value_id");
		}
		
		if(StringUtil.isEmpty(attrValueId2)) return new BooleanWrap(false);
		if(!attrValueId.equals(attrValueId2)) return new BooleanWrap(false,"一级分区和二级分区的联动关系无法对应");
		return new BooleanWrap(true);
		
	}
	
	public BooleanWrap checkColumn(String schemaCode,String tableCode,List<Map> columns,String algorithmsCode){
		System.out.println(schemaCode+":"+tableCode);
		if(StringUtil.isEmpty(schemaCode) || StringUtil.isEmpty(tableCode))
			return new BooleanWrap(false,"来源库和来源表不能为空");
		if(columns==null || columns.size() == 0)
			return new BooleanWrap(false,"来源字段不能为空");
		List<Map> rcolumnList = new ArrayList<Map>();
		
		List<Map> columnAlgorithmsList = DAO.queryForMap("select * from c_algorithms_field where state=?", new String[]{"00A"});
		
		for(Map tcolumn : columns){
			String columnCode = Const.getStrValue(tcolumn, "column_code");
			String fieldName = Const.getStrValue(tcolumn, algorithmsCode);	//脱敏字段
			//判断数据字段是否存在
			String sql = "select * from meta_columns where column_code =? and schema_code=? and table_code=?";
			List<Map> tcolumnList = DAO.queryForMap(sql, new String[]{columnCode,schemaCode,tableCode});
			if(tcolumnList==null || tcolumnList.size() == 0)
				return new BooleanWrap(false,"字段："+columnCode+" 不存在");
			
			//判断脱敏字段
			if(!this.checkColumnAlgorithms(fieldName, columnAlgorithmsList).getResult()){
				String tips = "字段："+columnCode+"的脱敏字段校验失败："+BooleanWrap.getLASTUSE().getTips("");
				return new BooleanWrap(false,tips);
			}else{
				String fieldId = (String)BooleanWrap.getLASTUSE().getData();
				tcolumnList.get(0).put("algorithm_field_id", fieldId);
			}
			
			rcolumnList.add(tcolumnList.get(0));
		}
		
		BooleanWrap returnWrap = new BooleanWrap(true);
		returnWrap.setData(rcolumnList);
		return returnWrap;
	}
	
	public String getStaticTransData(String value,String staticCode){
		//获取所有静态字段值
		if(StringUtil.isEmpty(value)) return null;
		AttrService attrService = SpringContextUtil.getApplicationContext().getBean(AttrService.class);
		List<Map> attrList = attrService.getStaticAttr(staticCode);
		if(attrList==null || attrList.size() == 0) return null;
		String transValue = "";
		String reallyValue = "";
		for(Map m : attrList){
			String attrValue = Const.getStrValue(m, "attr_value");
			String attrValueName = Const.getStrValue(m, "attr_value_name");
			if(value.equals(attrValue)) reallyValue = attrValue;
			if(value.equals(attrValueName)) transValue = attrValue;
		}
		if(StringUtil.isNotEmpty(reallyValue)) return reallyValue;
		if(StringUtil.isNotEmpty(transValue)) return transValue;
		return null;
	}
	
	public boolean validateDate(String date,String pattern){
		if(StringUtil.isEmpty(date) || StringUtil.isEmpty(pattern)) return false;
		if(date.length() != pattern.length()) return false;
		try{
			DateFormat format = new SimpleDateFormat(pattern);
			Date thisDate = format.parse(date);
			String date2 = format.format(thisDate);
			if(!date.equals(date2)) return false;
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	
	public String getCatalogIdByCode(String catalogCode){
		String sql = "select catalog_id from c_data_catalog where catalog_code = ? and state = ? ";
		return DAO.querySingleValue(sql, new String[]{catalogCode,KeyValues.STATE_00A});
	}
	
	public BooleanWrap checkColumnAlgorithms(String algorithms,List<Map> algorithmsList){
		if(StringUtil.isEmpty(algorithms)) return new BooleanWrap(true);
		if(algorithmsList!=null){
			for(Map tmap:algorithmsList){
				String fieldName = Const.getStrValue(tmap, "field_name");
				if(fieldName.toLowerCase().equals(algorithms.toLowerCase())){
					BooleanWrap returnB = new BooleanWrap(true);
					returnB.setData(Const.getStrValue(tmap, "algorithm_field_id"));
					return returnB;
				}
			}
			return new BooleanWrap(false,"脱敏："+algorithms+" 不存在");
		}else{
			String sql = "select * from c_algorithms_field where state='00A' and field_name = ?  ";
			List<Map> tlist = DAO.queryForMap(sql, new String[]{algorithms});
			if(tlist==null || tlist.size() == 0) return new BooleanWrap(false,"脱敏："+algorithms+" 不存在");
			Map tmap = tlist.get(0);
			BooleanWrap returnB = new BooleanWrap(true);
			returnB.setData(Const.getStrValue(tmap, "algorithm_field_id"));
			return returnB;
		}
	}
	
}
