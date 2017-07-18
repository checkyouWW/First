package com.ztesoft.dubbo.sp.workspace.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import spring.util.SpringContextUtil;
import appfrm.app.vo.PageModel;
import appfrm.resource.dao.impl.DAO;

import com.powerise.ibss.framework.Const;
import com.ztesoft.common.util.DateUtil;
import com.ztesoft.common.util.DcSystemParamUtil;
import com.ztesoft.common.util.PageModelConverter;
import com.ztesoft.common.util.RSAUtil;
import com.ztesoft.common.util.SeqUtil;
import com.ztesoft.common.util.SessionHelper;
import com.ztesoft.common.util.StringUtil;
import com.ztesoft.crm.business.common.utils.ListUtil;
import com.ztesoft.crmpub.bpm.mgr.service.BpmService;
import com.ztesoft.dubbo.inf.util.RsaEncrypt;
import com.ztesoft.dubbo.mp.sys.service.FtpService;
import com.ztesoft.dubbo.sp.data.vo.SDataColumn;
import com.ztesoft.dubbo.sp.data.vo.SDataDispatch;
import com.ztesoft.dubbo.sp.data.vo.SDataInst;
import com.ztesoft.dubbo.sp.data.vo.SDataSecurity;
import com.ztesoft.dubbo.sp.data.vo.SDataSecurityAcct;
import com.ztesoft.dubbo.sp.data.vo.SServiceApply;
import com.ztesoft.inf.util.KeyValues;
import com.ztesoft.inf.util.RpcPageModel;
import com.ztesoft.sql.Sql;

import comx.order.inf.IKeyValues;

@Repository
@SuppressWarnings({"unchecked","rawtypes" })
public class DataUsedApplyDao {

	public Map saveDataUsedApply(Map m) throws Exception  {
		Map re = new HashMap();
		String applyName = MapUtils.getString(m, "apply_name");
		String applyCode = MapUtils.getString(m, "apply_code");
		String abilityId = MapUtils.getString(m, "ability_id");
		String serviceId = MapUtils.getString(m, "service_id");
		String serviceType = KeyValues.SERVICE_TYPE_SECURITY;
		String effDate = MapUtils.getString(m, "eff_date");
		String expDate = MapUtils.getString(m, "exp_date");
		String applyDate = DateUtil.formatDate(DateUtil.DATE_TIME_FORMAT);
		String applyStaffId = MapUtils.getString(m, "apply_staff_id");
		String orgId = MapUtils.getString(m, "org_id");
		String state = "00B"; //审批中
		String stateDate = applyDate;
		String account = MapUtils.getString(m, "account"); //账期
		String applyReason = MapUtils.getString(m, "apply_reason");
		
		String dataCode = MapUtils.getString(m, "data_code");
		String lanId = MapUtils.getString(m, "lan_id");
		String oldDataInstId = MapUtils.getString(m, "old_data_inst_id");
		
		Map col = (Map) m.get("col");
		Map other = (Map) m.get("other");
		String apply_id = SeqUtil.getSeq(SServiceApply.TABLE_CODE, "apply_id");
		
		//判断申请单apply_code是否重复
		String isApplyCodeRepeatSql = "select count(*) from s_service_apply where apply_code = ? and service_type = ? ";
		String count = DAO.querySingleValue(isApplyCodeRepeatSql, new String[]{applyCode.trim(),serviceType});
		if(StringUtils.isNotBlank(count) && !count.equals("0")) {
			re.put("res_mess", "申请单号重复，请重新输入");
			re.put("res", false);
			return re;
		}
		//保存s_service_apply
		SServiceApply apply = new SServiceApply();
		apply.apply_id = apply_id;
		apply.apply_code = applyCode;
		apply.apply_name = applyName;
		apply.apply_date = applyDate;
		apply.apply_staff_id = applyStaffId;
		apply.eff_date = effDate;
		apply.exp_date = expDate;
		apply.org_id = orgId;
		apply.service_id = serviceId;
		apply.service_type = serviceType;
		apply.state = state;
		apply.state_date = stateDate;
		apply.apply_reason = applyReason;
		SServiceApply.getDAO().insert(apply);
		
		//保存s_data_inst
		String data_inst_id = SeqUtil.getSeq(SDataInst.TABLE_CODE, "data_inst_id");
		SDataInst dataInst = new SDataInst();
		dataInst.service_type = serviceType;
		dataInst.data_inst_id = data_inst_id;
		dataInst.apply_id = apply_id;
		dataInst.service_id = serviceId;
		dataInst.data_code = dataCode;
		
		if(StringUtil.isEmpty(lanId)){
			lanId = SessionHelper.getLanId();
		}
		//hive视图不能出现-号
		if("-1".equals(lanId)){
			lanId = "1";
		}
		
		dataInst.data_range = lanId;
		dataInst.view_name = dataInst.data_code+"_view"+"_"+lanId+"_"+data_inst_id;
		
		if(StringUtil.isEmpty(dataInst.rerun_interval) || !StringUtil.isNum(dataInst.rerun_interval))
			dataInst.rerun_interval = DcSystemParamUtil.getSysParamByCache("DEF_INTERVAL");
		SDataInst.getDAO().insert(dataInst);
		
		//保存s_data_column
		String column_inst_id = SeqUtil.getSeq(SDataColumn.TABLE_CODE, "column_inst_id");
		SDataColumn dataColumn = new SDataColumn();
		dataColumn.column_id = MapUtils.getString(col, "column_id");
		dataColumn.column_inst_id = column_inst_id;
		dataColumn.data_inst_id = data_inst_id;
		dataColumn.is_dst = MapUtils.getString(col, "is_dst");  //是否使用脱敏算法：0不使用 1使用
		dataColumn.alg_type = MapUtils.getString(col, "dst_algorithm");
		
		//获取c_src_column is_acct seq字段信息
		StringBuffer getCSrcColumnInfoSql = new StringBuffer(" SELECT cdc.seq,cdc.is_acct FROM c_data_column cdc WHERE cdc.column_id = ? ");
		List<Map> tList = DAO.queryForMap(getCSrcColumnInfoSql.toString(), new String[]{dataColumn.column_id});
		if(!ListUtil.isEmpty(tList)) {
			Map tMap = tList.get(0);
			dataColumn.is_acct = Const.getStrValue(tMap, "is_acct");
			dataColumn.seq = Const.getStrValue(tMap, "seq");
		}
		SDataColumn.getDAO().insert(dataColumn);

		//保存s_data_security
		String security_id = SeqUtil.getSeq(SDataSecurity.TABLE_CODE, "security_id");
		SDataSecurity dataSecurity = new SDataSecurity();
		dataSecurity.security_id = security_id;
		dataSecurity.ability_id = abilityId;
		dataSecurity.data_inst_id = data_inst_id;
		dataSecurity.apply_id = apply_id;
		dataSecurity.create_date = DateUtil.formatDate(DateUtil.DATE_TIME_FORMAT);
		dataSecurity.old_data_inst_id = oldDataInstId;
		SDataSecurity.getDAO().insert(dataSecurity);
		
		//保存s_data_security_acct
		String[] accts = account.split(",");
		for(String acct : accts) {
			String acct_id = SeqUtil.getSeq(SDataSecurityAcct.TABLE_CODE, "acct_id");
			SDataSecurityAcct dsAcct = new SDataSecurityAcct();
			dsAcct.acct_id = acct_id;
			dsAcct.security_id = security_id;
			dsAcct.data_acct = acct;
			dsAcct.key_code = RsaEncrypt.encrypt(applyCode+acct);
			SDataSecurityAcct.getDAO().insert(dsAcct);
		}
		//分发模式
		String dispatchType = Const.getStrValue(other, "dispatch_type");
		
		if(StringUtils.isNotBlank(dispatchType)) {
			SDataDispatch sDataDispatch = new SDataDispatch();
			sDataDispatch.dispatch_id = SeqUtil.getSeq("S_DATA_DISPATCH", "DISPATCH_ID");
			sDataDispatch.readFromMap(other);
			sDataDispatch.data_inst_id = data_inst_id;
			
			//判断分发模式是不是ftp且是拉取：
			if(KeyValues.DISPATCH_TYPE_FTP.equals(dispatchType) && "pull".equals(Const.getStrValue(other, "ftp_data_type"))){
				Map getFtpDataMap = new HashMap();
				getFtpDataMap.put("ftp_type", "TEAM");
				getFtpDataMap.put("org_id", apply.org_id);
				List<Map> ftpDataList = SpringContextUtil.getApplicationContext().
						getBean(FtpService.class).getFtpList(getFtpDataMap);
				if(ftpDataList!=null && ftpDataList.size()>0){
					Map ftpDataMap = ftpDataList.get(0);
					sDataDispatch.ftp_def_dir = Const.getStrValue(ftpDataMap, "path");
					sDataDispatch.ftp_ip = Const.getStrValue(ftpDataMap, "ip");
					sDataDispatch.ftp_password = Const.getStrValue(ftpDataMap, "password");
					sDataDispatch.ftp_port = Const.getStrValue(ftpDataMap, "port");
					sDataDispatch.ftp_user = Const.getStrValue(ftpDataMap, "user");
				}
			}
			
			
			sDataDispatch.getDao().insert(sDataDispatch);
		}
		
		
		/**@第五步 启动流程*/
		Map flowData = new HashMap();
		String flow_id = SeqUtil.getSeq("BPM_BO_FLOW_INST", "flow_id");
		flowData.put("bo_type_id", KeyValues.BO_TYPE_ID_SERVICE_APPLY);
		flowData.put("bo_id", apply.apply_id);
		flowData.put("flow_id", flow_id);
		flowData.put("autoFinishBeginWO", "true");
		
		String action_type = IKeyValues.ACTION_TYPE_A;
		String bo_title = apply.apply_name;
		BpmService service = (BpmService) SpringContextUtil.getBean("bpmService");
		boolean is_success = service.startFlow(flowData, action_type, bo_title);
		
		re.put("res", is_success);
		if(is_success) {
			re.put("res_mess", "操作成功");
			re.put("apply_id", apply_id);
		}
		else re.put("res_mess", "操作失败");
		return re;
	}

	public RpcPageModel getApplyPage(Map m) {
		String pageSize = Const.getStrValue(m, "rows");
		String pageIndex = Const.getStrValue(m, "page");
		int ps = 10;
		int pi = 1;
		if(StringUtils.isNotBlank(pageSize)) {
			ps = Integer.parseInt(pageSize);
		}
		if(StringUtils.isNotBlank(pageIndex)) {
			pi = Integer.parseInt(pageIndex);
		}
		
		String staff_id = SessionHelper.getStaffId();
		
		List<String> sqlParams = new ArrayList<String>();
		
		String sql = Sql.BPM_SQLS_LOCAL.get("getMyServiceApply");
		String success_fail_sql = Sql.BPM_SQLS_LOCAL.get("getMyServiceApplySuccFail");
		String l_sql = Sql.BPM_SQLS_LOCAL.get("getMyServiceApplyHis");
		
		sqlParams.add(staff_id);
		sqlParams.add(staff_id);
		sqlParams.add(staff_id);
		
		sql = assembleWhere(sql, sqlParams, m);
		
		sqlParams.add(staff_id);
		sqlParams.add(staff_id);
		
		success_fail_sql = assembleWhere(success_fail_sql, sqlParams, m);
		
		sqlParams.add(staff_id);
		sqlParams.add(staff_id);
		
		l_sql = assembleWhere(l_sql, sqlParams, m);
		
		String all_sql = "select * from (" + sql + " union " + success_fail_sql + " union " + l_sql + ") a "
				+ " GROUP BY a.flow_id "
				+ " order by a.flow_id desc  ";
		PageModel p = DAO.queryForPageModel(all_sql, ps, pi, sqlParams.toArray(new String[]{}));
		
		return PageModelConverter.pageModelToRpc(p);
	}

	private String assembleWhere(String sql, List<String> sqlParams, Map param) {
		String serviceType = MapUtils.getString(param, "service_type");
		String applyName = MapUtils.getString(param, "apply_name");
		String bo_state = MapUtils.getString(param, "bo_state");
		//是否来自个人查询
		String isOwn = MapUtils.getString(param, "is_own");
		
		if(StringUtils.isNotBlank(serviceType)) {
			sql += " and c.service_type = ? ";
			sqlParams.add(serviceType);
		}
		
		if(StringUtils.isNotBlank(applyName)) {
			sql += " and c.apply_name like ? ";
			sqlParams.add("%"+applyName+"%");
		}
		
		if(StringUtils.isNotBlank(bo_state)) {
			sql += " and b.bo_state = ? ";
			sqlParams.add(bo_state);
		}
		
		if(StringUtils.isNotBlank(isOwn)) {
			sql += " and c.apply_staff_id = ?";
			sqlParams.add(SessionHelper.getStaffId());
		}
		
		return sql;
	}

	public Map getDataUsedApplyInfo(Map m) {
		Map re = new HashMap();
		String applyId = Const.getStrValue(m, "apply_id");
		//获取s_service_apply
		StringBuffer sSql = new StringBuffer();
		sSql.append(" select a.apply_id,a.apply_code,a.apply_name,a.service_id,a.service_type,date_format(a.eff_date,'%Y-%m-%d') eff_date,");
		sSql.append(" date_format(a.exp_date,'%Y-%m-%d') exp_date,a.apply_staff_id,");
		sSql.append(" (select d.staff_name from dm_staff d where d.staff_id = a.apply_staff_id) apply_staff_name,");
		sSql.append(" a.org_id,(select o.org_name from dm_organization o where o.org_id = a.org_id) apply_org_name,");
		sSql.append(" a.state,a.apply_reason ");
		sSql.append(" from s_service_apply a where 1=1 ");
		if(StringUtils.isNotBlank(applyId)) {
			sSql.append(" and a.apply_id = ? ");
			List<Map> sList = DAO.queryForMap(sSql.toString(), new String[]{applyId});
			if(ListUtil.isEmpty(sList) || sList.size()!=1) {
				return null;
			}
			Map ssa = sList.get(0);
			re.put("service_apply",ssa);
		}else {
			return null;
		}
		
		//获取s_data_inst
		StringBuffer dSql = new StringBuffer();
		dSql.append(" select a.data_inst_id,a.apply_id,a.is_history,a.history_acct,t.service_id,t.data_code,t.data_name ");
		dSql.append(" from s_data_inst a left join ");
		dSql.append(" ( select b.service_id,b.data_code,b.data_name,b.ability_id from "); 
		dSql.append(" c_data_ability b ,c_data_service c where b.service_id = c.service_id ");
		dSql.append(" ) t on t.service_id = a.service_id ");
		dSql.append(" where 1=1  ");
		dSql.append(" and a.apply_id = ? ");
		List<Map> dList = DAO.queryForMap(dSql.toString(), new String[]{applyId});
		if(ListUtil.isEmpty(dList) || dList.size()!=1) {
			return re;
		}
		Map dInst = dList.get(0);
		re.put("data_inst", dInst);
		
		//获取s_data_column
		String dataInstId = Const.getStrValue(dInst, "data_inst_id");
		StringBuffer cSql = new StringBuffer();
		cSql.append(" select a.column_inst_id,a.data_inst_id,a.column_id,a.is_dst,a.alg_type,b.column_code,b.column_name,c.algorithm_code,c.algorithm_id ,c.type ");
		cSql.append(" from s_data_column a left join c_data_column b on a.column_id = b.column_id ");
		cSql.append(" left join c_algorithms c on b.dst_algorithm = c.algorithm_id ");
		cSql.append(" where 1=1 ");
		cSql.append(" and a.data_inst_id = ? ");
		List<Map> cList = DAO.queryForMap(cSql.toString(), new String[]{dataInstId});
		re.put("col", (ListUtil.isEmpty(cList) || cList.size()!=1)?null:cList.get(0));
		
		//获取账期
		StringBuffer aSql = new StringBuffer();
		aSql.append(" select sdsa.data_acct,sdsa.acct_id,sdsa.key_code from s_data_security_acct sdsa inner join s_data_security sds on sdsa.security_id = sds.security_id where 1=1 ");
		aSql.append(" and sds.apply_id = ? ");
		List<Map> aList = DAO.queryForMap(aSql.toString(), new String[]{applyId});
		re.put("account",aList);
		
		//获取s_data_dispatch 分发模式
		StringBuffer ddSql = new StringBuffer();
		ddSql.append("select * from s_data_dispatch where data_inst_id = ? ");
		List<Map> ddList = DAO.queryForMap(ddSql.toString(), new String[]{dataInstId});
		
		Map dispatchMap = ListUtil.isEmpty(ddList)?null:ddList.get(0);
		if(dispatchMap!=null && !MapUtils.isEmpty(dispatchMap)) {
			String fp = Const.getStrValue(dispatchMap, "ftp_password");
			String dp = Const.getStrValue(dispatchMap, "db_password");
			if(StringUtils.isNotBlank(fp)) {
				dispatchMap.put("ftp_password", RSAUtil.decrypt(fp));
			}
			if(StringUtils.isNotBlank(dp)) {
				dispatchMap.put("db_password", RSAUtil.decrypt(dp));
			}
		}
		re.put("other", dispatchMap);
		
		return re;
	}

	public Map updateDataUsedApplyInfo(Map m) {
		Map re = new HashMap();
		String applyId = Const.getStrValue(m, "apply_id");
		String expDate = Const.getStrValue(m, "exp_date");
		String effDate = Const.getStrValue(m, "eff_date");
		String dispatchType = Const.getStrValue(m, "dispatch_type");
		if(StringUtils.isBlank(applyId)) {
			re.put("res",false);
			re.put("res_mess", "获取信息出错");
			return re;
		}
		
		//保存s_service_apply
		StringBuffer saveApplySql = new StringBuffer();
		List<String> pList = new ArrayList<String>();
		saveApplySql.append(" update s_service_apply set apply_id = ?  ");
		pList.add(applyId);
		if(StringUtils.isNotBlank(expDate)) {
			saveApplySql.append(" ,exp_date = ? ");
			pList.add(expDate);
		}
		if(StringUtils.isNotBlank(effDate)) {
			saveApplySql.append(" ,eff_date = ? ");
			pList.add(effDate);
		}
		
		saveApplySql.append(" where apply_id = ? ");
		pList.add(applyId);
		
		DAO.update(saveApplySql.toString(), pList.toArray(new String[]{}));
		
		//保存s_data_dispatch
		String dispatchId = Const.getStrValue(m, "dispatch_id");
		StringBuffer saveDispatchSql = new StringBuffer(" update s_data_dispatch set dispatch_id = ? ");
		List<String> parList = new ArrayList<String>();
		parList.add(dispatchId);
		if(StringUtils.isNotBlank(dispatchType) && StringUtils.isNotBlank(dispatchId)) {
			if(dispatchType.trim().equals(KeyValues.DISPATCH_TYPE_FTP)) {
				String ftpDataType = Const.getStrValue(m, "ftp_data_type");
				if(StringUtils.isNotBlank(ftpDataType) && ftpDataType.trim().equals("pull")) { //拉取才有修改
					String ftpIp = Const.getStrValue(m, "ftp_ip");
					String ftpDefDir = Const.getStrValue(m, "ftp_def_dir");
					String ftpUser = Const.getStrValue(m, "ftp_user");
					String ftpPassword = Const.getStrValue(m, "ftp_password");
					String ftpPort = Const.getStrValue(m, "ftp_port");
					String ftpSplit = Const.getStrValue(m, "ftp_split");
					if(StringUtils.isBlank(ftpPassword)) { //没有修改，不变化，（加密）
						saveDispatchSql.append(",ftp_ip=?,ftp_def_dir=?,ftp_user=?,ftp_port=?,ftp_split=? ");
					}else {
						saveDispatchSql.append(",ftp_password=?,ftp_ip=?,ftp_def_dir=?,ftp_user=?,ftp_port=?,ftp_split=? ");
						parList.add(ftpPassword);
					}
					parList.add(ftpIp);
					parList.add(ftpDefDir);
					parList.add(ftpUser);
					parList.add(ftpPort);
					parList.add(ftpSplit);
				}
			}else if(dispatchType.trim().equals(KeyValues.DISPATCH_TYPE_DB_IMPORT)) {
				String importType = Const.getStrValue(m, "import_type");
				String createTable = StringUtils.isNotBlank(Const.getStrValue(m, "create_table"))?Const.getStrValue(m, "create_table"):null;
				String sqoopType = Const.getStrValue(m, "sqoop_type");
				saveDispatchSql.append(",import_type = ?,create_table="+createTable+",sqoop_type=? ");
				parList.add(importType);
				parList.add(sqoopType);
			}
			saveDispatchSql.append(" where dispatch_id = ? ");
			parList.add(dispatchId);
			DAO.update(saveDispatchSql.toString(), parList.toArray(new String[]{}));
		}else {
			//存在没有分发的信息
//			re.put("res", false);
//			re.put("res_mess","操作失败");		
//			return re;
		}
		
		re.put("res", true);
		re.put("res_mess","操作成功");		
		return re;
	}
	
	public RpcPageModel getApplyPageDis(Map m) {
		String pageSize = Const.getStrValue(m, "rows");
		String pageIndex = Const.getStrValue(m, "page");
		int ps = 10;
		int pi = 1;
		if(StringUtils.isNotBlank(pageSize)) {
			ps = Integer.parseInt(pageSize);
		}
		if(StringUtils.isNotBlank(pageIndex)) {
			pi = Integer.parseInt(pageIndex);
		}
		String serviceType = MapUtils.getString(m, "service_type");
		String applyName = MapUtils.getString(m, "apply_name");
		String state = MapUtils.getString(m, "state");
		//是否来自个人查询
		String isOwn = MapUtils.getString(m, "is_own");
		String teamId = SessionHelper.getTeamId();
		
		StringBuffer sql = new StringBuffer();
		List<String> pList = new ArrayList<String>();
		sql.append(" select a.apply_id,a.apply_code,a.apply_name,a.service_id,a.service_type,a.eff_date,a.exp_date,a.apply_date,a.apply_staff_id,a.apply_reason,"
				+ "(select d.staff_name from dm_staff d where d.staff_id = a.apply_staff_id ) staff_name,a.org_id,(select o.org_name from dm_organization o where o.org_id = a.org_id ) org_name, "
				+ " a.state,a.state_date ");
		sql.append(" from s_service_apply a where 1=1 ");
		if(StringUtils.isNotBlank(serviceType)) {
			sql.append(" and a.service_type = ? ");
			pList.add(serviceType);
		}
		
		if(StringUtils.isNotBlank(applyName)) {
			sql.append(" and a.apply_name like ? ");
			pList.add("%"+applyName+"%");
		}
		
		if(StringUtils.isNotBlank(state)) {
			sql.append(" and a.state = ? ");
			pList.add(state);
		}
		
		if(StringUtils.isNotBlank(teamId)) {
			sql.append(" and a.org_id = ? ");
			pList.add(teamId);
		}
		
		if(StringUtils.isNotBlank(isOwn)) {
			sql.append(" and a.apply_staff_id = ?");
			pList.add(SessionHelper.getStaffId());
		}
		sql.append(" order by a.state_date desc ");
		PageModel p = DAO.queryForPageModel(sql.toString(), ps, pi, pList.toArray(new String[]{}));
		
		return PageModelConverter.pageModelToRpc(p);
	}

	public RpcPageModel getDataInstApplyList(Map m) {
		String pageSize = Const.getStrValue(m, "rows");
		String pageIndex = Const.getStrValue(m, "page");
		int ps = 10;
		int pi = 1;
		if(StringUtils.isNotBlank(pageSize)) {
			ps = Integer.parseInt(pageSize);
		}
		if(StringUtils.isNotBlank(pageIndex)) {
			pi = Integer.parseInt(pageIndex);
		}
		
		String dataName = Const.getStrValue(m, "data_name");
		
		String sql = "SELECT "
				+ " so.service_order_id, "
				+ " ssi.apply_id, "
				+ " ssi.apply_code, "
				+ " ssi.apply_name, "
				+ " sdi.data_inst_id, "
				+ " cda.data_code,"
				+ " cda.data_name, "
				+ " cda.ability_id, "
				+ " cda.service_id,"
				+ " ssi.service_type,"
				+ " cds.extract_freq "
				+ " FROM "
				+ " service_order so, "
				+ " s_service_inst ssi, "
				+ " s_data_inst sdi, "
				+ " s_service_apply ssa, "
				+ " c_data_ability cda,"
				+ " c_data_src cds "
				+ " WHERE "
				+ " so.service_inst_id = ssi.inst_id "
				+ " AND so.data_inst_id = sdi.data_inst_id "
				+ " AND ssi.apply_id = ssa.apply_id "
				+ " AND so.state = ? "
				+ " AND ssa.state = ? "
				+ " AND cda.service_id = sdi.service_id"
				+ " AND ssi.service_type != ? "
				+ " AND cda.service_id = cds.service_id";
		
		List<String> pList = new ArrayList<String>();
		
		pList.add(KeyValues.ORDER_STATE_SUCCESS);
		pList.add(KeyValues.APPLY_STATE_SUCCESS);
		pList.add(KeyValues.SERVICE_TYPE_SECURITY);
		
		if(StringUtils.isNotBlank(dataName)) {
			sql += " AND cda.data_name like ? " ;
			pList.add("%"+dataName+"%");
		}
		
		sql	+= " order by so.service_order_id desc ";
		
		PageModel p = DAO.queryForPageModel(sql, ps, pi, pList.toArray(new String[]{}));
		return PageModelConverter.pageModelToRpc(p);
	}
	
	public List getDataInstDataColumn(Map m) { 
		String oldDataInstId = Const.getStrValue(m, "old_data_inst_id");
		String isDst = MapUtils.getString(m, "is_dst");
		String isFromSafe = MapUtils.getString(m, "isFromSafe");
		List<String> pList = new ArrayList<String>();
		
		StringBuffer sql = new StringBuffer(" SELECT "
				+ " g.column_id, "
				+ " g.column_code, "
				+ " g.column_name, "
				+ " g.is_dst, "
				+ " g.dst_algorithm, "
				+ " g.ability_id, "
				+ " ca.algorithm_code, "
				+ " ca.type AS algorithm_type, "
				+ " g.service_id "
				+ " FROM "
				+ " ( "
				+ " SELECT "
				+ " sdc.column_id, "
				+ " cdc.column_code, "
				+ " cdc.column_name, "
				+ " cdc.column_type, "
				+ " sdc.is_dst, "
				+ " sdc.alg_type AS dst_algorithm, "
				+ " tt.* "
				+ " FROM "
					+ " s_data_column sdc "
				+ " INNER JOIN c_data_column cdc ON sdc.column_id = cdc.column_id "
				+ " INNER JOIN s_data_inst sdi ON sdc.data_inst_id = sdi.data_inst_id "
				+ " AND sdi.data_inst_id = ? "
				+ " LEFT JOIN ( "
				+ " SELECT "
				+ " 	cda.* "
				+ " FROM "
				+ " 	c_data_ability cda "
				+ " INNER JOIN c_data_service cds ON cda.service_id = cds.service_id "
				+ " AND cds.state = '00A' "
				+ " ) tt ON cdc.service_id = tt.service_id "
				+ " ) g "
				+ " LEFT JOIN c_algorithms ca ON ca.algorithm_id = g.dst_algorithm where 1=1 ");
		
		pList.add(oldDataInstId);
		if(StringUtils.isNotBlank(isDst)) {
			sql.append(" and g.is_dst=? ");
			pList.add(isDst);
		}
		//安全数据申请中，只要md5和rsa类型的算法
		if(StringUtils.isNotBlank(isFromSafe)) {
			sql.append(" and ( ca.type = ? or ca.type = ? ) ");
			pList.add(KeyValues.ALG_TYPE_0);
			pList.add(KeyValues.ALG_TYPE_1);
		}
		List list = DAO.queryForMap(sql.toString(), pList.toArray(new String[]{}));
		return ListUtil.isEmpty(list)?null:list;
	}
	
}
