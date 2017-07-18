package com.ztesoft.crmpub.bpm.vo.spec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ztesoft.common.util.StringUtil;
import com.ztesoft.crmpub.bpm.attr.util.SqlValUtil;
import com.ztesoft.crmpub.bpm.consts.BPMConsts;
import com.ztesoft.crmpub.bpm.consts.MsgConsts;
import com.ztesoft.crmpub.bpm.task.FetchValueClass;
import com.ztesoft.crmpub.bpm.vo.MsgBean;
import com.ztesoft.crmpub.bpm.vo.model.MBpmWoTask;
import com.ztesoft.inf.util.KeyValues;

import appfrm.app.annotaion.DBField;
import appfrm.app.annotaion.DBObj;
import appfrm.app.annotaion.IDField;
import appfrm.app.annotaion.NameField;
import appfrm.app.annotaion.RootField;
import appfrm.app.meta.IVOMeta;
import appfrm.app.util.ListUtil;
import appfrm.app.util.StrUtil;
import appfrm.app.vo.IVO;
import appfrm.app.vo.impl.VO;
import appfrm.resource.dao.IDAO;
import appfrm.resource.dao.impl.DAO;

@DBObj(tn = SBpmBoFlowDef.TABLE_CODE)
public class SBpmBoFlowDef extends VO implements IVO {

	public static final IVOMeta META = IVOMeta.getInstance(SBpmBoFlowDef.class);
	public static final String TABLE_CODE = "bpm_bo_flow_def";

	@Override
	public IVOMeta getMeta() {
		return META;
	}

	public static IDAO getDAO() {
		return META.getDAOMeta().getDAO();
	}

	@DBField
	@IDField
	@RootField
	public String bo_type_id;

	@DBField
	public String table_code;

	@DBField
	public String table_pk_col;

	@DBField
	@NameField
	public String flow_name;

	@DBField
	public String bo_class;

	@DBField
	public String bo_desc;

	@DBField
	public String template_id;

	@DBField
	public String bo_url;

	@DBField
	public String lan_id;

	@DBField
	public String region_id;

	@DBField
	public String state;

	@DBField(type = DBField.TYPE_DATE)
	public String create_date;

	@DBField(type = DBField.TYPE_DATE)
	public String state_date;

	@DBField
	public String oper_staff;
	
	@DBField
	public String create_handler;

	private List<IVO> flowTaches = null;// 流程环节
	//private List<IVO> tacheSkipDefs = null;// 环节跳转

	private MsgBean msg = null;
	
	public MsgBean getMsg() {
		return msg;
	}

	public void setMsg(MsgBean msg) {
		this.msg = msg;
	}

	/**
	 * 从数据库加载流程的所有的环节
	 */
	public void loadFlowTaches() {
		if (flowTaches == null) {
			this.flowTaches = SBpmBoFlowTache.getDAO().query(" bo_type_id=? order by SEQ_NO asc", this.bo_type_id);
		}

		if (ListUtil.isEmpty(flowTaches)) {
			throw new RuntimeException("加载流程环节规格数据失败！原因 :流程环节未配置，bo_type_id = " + bo_type_id);
		}

	}
	/**
	public void loadTacheSkipDef(){
		if(tacheSkipDefs == null){
			this.tacheSkipDefs = SBpmTacheSkipDef.getDAO().query(" bo_type_id=? ", this.bo_type_id);
		}
		if(ListUtil.isEmpty(tacheSkipDefs)){
			throw new RuntimeException("加载环节跳转规格数据失败！原因 :环节跳转未配置，bo_type_id = " + bo_type_id);
		}
	}*/

	/**
	 * 获取开始环节
	 * @return
	 * @throws Exception 
	 */
	public SBpmBoFlowTache getBeginTahce() throws Exception {
		if (flowTaches == null) {
			loadFlowTaches();
		}
		SBpmBoFlowTache beginTache=null;
		for (IVO vo : flowTaches) {
			SBpmBoFlowTache flowTache = (SBpmBoFlowTache) vo;
			if(this.isSkip(flowTache) && BPMConsts.TACHE_TYPE_BEGIN.equalsIgnoreCase(flowTache.tache_type)) {
				beginTache = this.getNextTache(flowTache.tache_code);
				break;
			}
			if (BPMConsts.TACHE_TYPE_BEGIN.equalsIgnoreCase(flowTache.tache_type)) {
				beginTache = flowTache;
				break;
			}
		}
		
		if (beginTache==null){
			beginTache = (SBpmBoFlowTache)flowTaches.get(0);
		}
		return beginTache;
	}

	/**
	 * 获取下一个环节规格
	 * @param tacheCode
	 * @return SBpmBoFlowTache
	 * @throws Exception
	 */
	public SBpmBoFlowTache getNextTache(String tacheCode) throws Exception {
		SBpmBoFlowTache nextTache = getNextTacheByRoute(this.bo_type_id, tacheCode);
		if (nextTache == null) {
			nextTache = this.getNextTacheBySeq(tacheCode);
		}
		
		if (nextTache != null && isSkip(nextTache)) {
			nextTache = getNextTache(nextTache.tache_code);
		}
		
		return nextTache;
	}

	/**
	 * 根据环节路由找到下一环节。后续应该去掉com.ztesoft.crmpub.bpm.vo.spec.SBpmBoFlowDef.isSkip(SBpmBoFlowTache)函数，统一在本方法实现。
	 * 如果下一环节是本环节，就是内部转派了。
	 * 只有回单时，才让调用这个函数，因为路由跟回单数据、运行时数据息息相关，在规格成调用这个方法就没啥意义了 joshui 2014-12-19
	 * @param src_tacheCode
	 * @return
	 * Author : joshui
	 * Date ：2014-11-24
	 * @throws Exception 
	 */
	private SBpmBoFlowTache getNextTacheByRoute(String bo_type_id, String src_tacheCode) throws Exception {
		/* 
		 * 在回单的时候，才需要用到路由，回单时，msg不为空，msg动作不为空
		   保证当前是在回单以后，加这个条件主要是为了递归调用时，堆栈溢出。是为了兼容老方法com.ztesoft.crm.chn.org.bo.BpmFlowBO.putLastTache(SBpmBoFlowDef)，
		   这个方法在模板初始化的时候就调用了，里面有递归调用，应该要重构
		*/
		if (this.msg == null || this.msg.getMsgAction() == null) {
			return null;
		}
		
		// 查询环节路由表。后续路由表的规格数据可以提前加载到内存。再通过动作类型action_type过滤一下(要么为空。要么两边的动作类型相等)。
		StringBuilder sql = new StringBuilder(
			"SELECT route_id, bo_type_id, src_tache_code, tar_tache_code, TYPE, attr_id, attr_value, sql_str, class_str, state, state_date, create_date, seq " +
			"FROM bpm_tache_route t " + 
			"WHERE (t.action_type is null or t.action_type = ?) and t.bo_type_id = ? AND t.src_tache_code = ? AND t.state = ? order by t.seq ");
		List<String> params = new ArrayList<String>();
		params.add(this.msg.getMsgAction());
		params.add(bo_type_id);
		params.add(src_tacheCode);
		params.add(KeyValues.STATE_00A);
		List<Map<String, String>> routeList = DAO.queryForMap(sql.toString(), params.toArray(new String[]{}));
		
		//目前只处理有限的几种类型，后续将之前在环节表配置的skip_cond_expr兼容进来
		for (Map<String, String> routeMap : routeList) {
			// 为了兼容老的审批不通过逻辑，之前的审批不通过逻辑不是通过配置实现的，是通过JSP页面特殊传参，默认返回上一环节。因此只考虑004的情况
			/*if ( MsgConsts.ACTION_WO_FAIL.equals(this.msg.getMsgAction()) && !MsgConsts.BPM_TACHE_ROUTE_TYPE_004.equals(routeMap.get("type"))) { 
				continue;
			}*/
			
			// 根据工单实例，找到最新的上一实例环节。比如外部转派，处理完后，要回到转派环节
			if (MsgConsts.BPM_TACHE_ROUTE_TYPE_004.equals(routeMap.get("type"))) { 
				return getTacheByCode(routeMap.get("tar_tache_code"));
			}
			// 模板字段
			if (MsgConsts.BPM_TACHE_ROUTE_TYPE_001.equals(routeMap.get("type"))) { 
				for (HashMap<String, String> attrMap : this.getMsg().getAttrList()) {
					if (attrMap.get("name").equals(routeMap.get("attr_id")) && attrMap.get("value").equals(routeMap.get("attr_value"))) {
						return getTacheByCode(routeMap.get("tar_tache_code"));
					}
				}
			}
			// SQL方式
			if (MsgConsts.BPM_TACHE_ROUTE_TYPE_002.equals(routeMap.get("type"))) {
				List<Map> sqlData = SqlValUtil.fetch(routeMap.get("sql_str"));
				if (sqlData != null && !sqlData.isEmpty()) {
					return getTacheByCode(routeMap.get("tar_tache_code"));
				}
			}
			// class方式
			if (MsgConsts.BPM_TACHE_ROUTE_TYPE_003.equals(routeMap.get("type"))) {
				Class clazz = Class.forName(routeMap.get("class_str"));
				FetchValueClass fetchValue = (FetchValueClass) clazz.newInstance();
				fetchValue.setFlowDef(this);
				fetchValue.setFlowTache(this.getTacheByCode(src_tacheCode));
				fetchValue.setMsg(msg);
				if (MsgConsts.BPM_SUCCESS.equals(fetchValue.execute())) {
					return getTacheByCode(routeMap.get("tar_tache_code"));
				}
			}
			// 通过实例找到上一环节
			if (MsgConsts.BPM_TACHE_ROUTE_TYPE_005.equals(routeMap.get("type"))) {
				String sql1 = 
					"select c.tache_code " +
					"from bpm_wo_task a, bpm_wo_task c " + 
					"where " + 
					"  c.tache_code <> a.tache_code " + 
					"  and c.tache_code = a.dispatch_tache_code " + 
					"  and c.flow_id = a.flow_id " + 
					"  and a.dispatch_tache_code in " + 
					"   (select b.tache_code from bpm_bo_flow_tache b where " + 
					"           b.seq_no < (select d.seq_no from bpm_bo_flow_tache d where d.tache_code = ? and d.bo_type_id = ?) " + 
					"           and b.bo_type_id = ?) " + 
					"  and a.tache_code = ? " + 
					"  and a.flow_id = ? " + 
					"order by c.create_date desc";
				List<Map<String, String>> dataList = DAO.queryForMap(sql1, 
						new String[]{src_tacheCode, bo_type_id, bo_type_id, src_tacheCode, msg.getFlowInst().flow_id});
				if (!dataList.isEmpty()) {
					return getTacheByCode(dataList.get(0).get("tache_code"));
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 获取下一个环节规格
	 * 根据自然顺序获取 commented by joshui
	 * @param tacheCode
	 * @return SBpmBoFlowTache
	 * @throws Exception
	 */
	public SBpmBoFlowTache getNextTacheBySeq(String tacheCode) throws Exception {
		if (flowTaches == null) {
			loadFlowTaches();
		}

		if (isLastTache(tacheCode)) {
			return null;
		}

		int i = getTacheIndex(tacheCode);
		SBpmBoFlowTache nextTache= (SBpmBoFlowTache) flowTaches.get(i + 1);
		return nextTache;
	}
	
	/**
	 * 获取最后一个环节规格
	 * @param tacheCode
	 * @return SBpmBoFlowTache
	 * @throws Exception
	 */
	public SBpmBoFlowTache getLastTache() throws Exception {
		if (flowTaches == null) {
			loadFlowTaches();
		}
		for (int i = 0; i < flowTaches.size(); i++) {
			SBpmBoFlowTache tache = (SBpmBoFlowTache) flowTaches.get(i);
			if (i == flowTaches.size() - 1) {
				return tache;
			}
		}
		return null;
	}
	
	/**
	 * 获取环节规格
	 * @param tacheCode
	 * @return SBpmBoFlowTache
	 * @throws Exception
	 */
	public SBpmBoFlowTache getTacheByCode(String tacheCode) throws Exception {
		if (flowTaches == null) {
			loadFlowTaches();
		}
		SBpmBoFlowTache currTache = null; 
		for (int i = 0; i < flowTaches.size(); i++) {
			SBpmBoFlowTache flowTache = (SBpmBoFlowTache)flowTaches.get(i);
			if (flowTache.tache_code.equals(tacheCode)) {
				currTache = flowTache;
				break;
			}
		}
		
		return currTache;
	}
	
	/**
	 * 获取上一个环节规格
	 * Modified by joshui 看调用这个方法的地方，这里应该根据实例信息去找，而不是规格信息。
	 * @param cur_tacheCode
	 * @return SBpmBoFlowTache
	 * @throws Exception
	 */
	public SBpmBoFlowTache getPrevTacheByInst(String cur_tacheCode, boolean backToFirst) throws Exception {
		if (flowTaches == null) {
			loadFlowTaches();
		}
		
		// 直接返回第一个环节
		if (backToFirst) {
			return this.getBeginTahce();
		}
		SBpmBoFlowTache prevTache = null;
		// 先根据环节路由去找。路由优先级最高
		//SBpmBoFlowTache prevTache = getNextTacheByRoute(this.bo_type_id, cur_tacheCode);--找上一个环节干嘛非要路由表找下一个环节啊我真是不懂了。zhang.yongwei
		
		// 根据实例信息找到最近的上一环节
		if (prevTache == null) {
			String sql = "SELECT a.tache_code "
					+ "  FROM BPM_WO_TASK A "
					+ " WHERE A.WO_ID = (SELECT MAX(T.WO_ID) "
					+ "                    FROM BPM_WO_TASK T "
					+ "                   WHERE t.resp_oper_id IS NOT NULL and t.wo_state <> 'FAIL' "
					+ "						and t.tache_code <> ? AND T.FLOW_ID = ?)";
			String flow_id = this.getMsg().getFlowId();
			if(StringUtil.isEmpty(flow_id)){
				flow_id = this.getMsg().getFlowInst().flow_id;
			}
			List<Map<String, String>> tacheL = DAO.queryForMap(sql, new String[] {cur_tacheCode, flow_id});
			if (!tacheL.isEmpty()) {
				prevTache = this.getTacheByCode(tacheL.get(0).get("tache_code"));
			}
		}		
		
		return prevTache;
	}
	
	/**
	 * 任务表转成规格表
	 * Modified by zhang.yongwei
	 * @param cur_tacheCode
	 * @return SBpmBoFlowTache
	 * @throws Exception
	 */
	public SBpmBoFlowTache translateTaskToTache(MBpmWoTask task) {
		SBpmBoFlowTache tache = null;
		if(task == null){
			return null;
		}
		try {
			tache = getTacheByCode(task.tache_code);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tache;
	}
	
	/**
	 * 
	 * @param tache
	 * @return
	 * @throws Exception
	 */
	public boolean isSkip(SBpmBoFlowTache tache) throws Exception{
		Boolean isKip=false;
		String result = "false";
		if (StrUtil.isNotEmpty(tache.skip_cond_type) && StrUtil.isNotEmpty(tache.skip_cond_expr)) {
			if (BPMConsts.VALUE_METHOD.SQL.equals(tache.skip_cond_type)) { // SQL取值
				
				String sqlText = tache.skip_cond_expr;
				List<Map> resLst = SqlValUtil.fetch(sqlText);
				result = ListUtil.getSingleValue(resLst);
			} else if (BPMConsts.VALUE_METHOD.CLASS	.equals(tache.skip_cond_type)) { // JAVA类计算方式

				String className = tache.skip_cond_expr;
				Class clazz = Class.forName(className);
				FetchValueClass fetchValue = (FetchValueClass) clazz.newInstance();
				fetchValue.setFlowDef(this);
				fetchValue.setFlowTache(tache);
				fetchValue.setMsg(msg);
				result = fetchValue.execute(); // 通过BpmContext获取变量值
			}
		}

		if (result.equalsIgnoreCase("true")|| result.equalsIgnoreCase("1") ){
			 isKip = true; 
		}
		
		return isKip;
	}


	/**
	 * 判断tacheCode是否最后一个环节
	 * 
	 * @param tacheCode
	 * @return
	 */
	private boolean isLastTache(String tacheCode) {
		if (flowTaches == null) {
			loadFlowTaches();
		}
		
		for (int i = 0; i < flowTaches.size(); i++) {
			SBpmBoFlowTache tache = (SBpmBoFlowTache) flowTaches.get(i);
			if (tache.tache_code.equals(tacheCode)) {
				return (i == flowTaches.size() - 1) ? true : false;
			}
		}
		return false;
	}
	
	/**
	 * 判断tacheCode是否第一个环节
	 * 
	 * @param tacheCode
	 * @return
	 */
	public boolean isFirstTache(String tacheCode) {
		if (flowTaches == null) {
			loadFlowTaches();
		}
		SBpmBoFlowTache firstTache = (SBpmBoFlowTache)flowTaches.get(0);
		if( firstTache.tache_code.equals(tacheCode) ){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 获取当前环节在环节队列中的下标
	 * 
	 * @return int
	 */
	private int getTacheIndex(String tacheCode) {
		if (flowTaches == null) {
			loadFlowTaches();
		}
		
		int index = -1;
		for (int i = 0; i < flowTaches.size(); i++) {
			SBpmBoFlowTache flowTache = (SBpmBoFlowTache)flowTaches.get(i);
			if (flowTache.tache_code.equals(tacheCode)) {
				index = i;
				break;
			}
		}
		return index;
	}
	/**
	 * 根据BPM_TACHE_SKIP_DEF配置，按配置环节匹配，匹配成功后跳转到指定环节，如果没有配置，则不通过默认返回上一个环节
	 * 暂不实现跳转过滤器规则 SKIP_RULE_TYPE
	 * @param tacheCode 当前环节编码
	 * @param isPass    是否通过，通过匹配通过跳转环节，不通过匹配不通过后跳转环节 
	 * @return
	 
	public SBpmBoFlowTache getSkipTache(String tacheCode,boolean isPass) throws Exception{
		//加载环节跳转数据
		if(this.tacheSkipDefs == null){
			this.loadTacheSkipDef();
		}
		//当前环节通过的场景
		if(isPass){
			if(ListUtil.isEmpty(this.tacheSkipDefs)){
				//当没有配置环节跳转数据时直接跳转到下一环节
				return this.getNextTache(tacheCode);
			}
			for(IVO vo:this.tacheSkipDefs){
				SBpmTacheSkipDef skipVo = (SBpmTacheSkipDef)vo; 
				if(tacheCode.equals(skipVo.getCur_tache_code())){
					
				}
			}
		}
		//当前环节不通过的场景
		if(!isPass){
			
		}
		return null;
	}*/
	
	public MBpmWoTask getPrevWoTaskByInst(String cur_tacheCode, String tache_code) throws Exception {
		String sql = "select max(t.wo_id) wo_id" +
				" from bpm_wo_task t" +
				" where t.resp_oper_id is not null ";
		List<String> params = new ArrayList<String>();
		if(!cur_tacheCode.equals(tache_code)){
			sql += "and t.tache_code <> ?";
			params.add(cur_tacheCode);
		}
		if(this.getMsg().getFlowInst() != null){
			sql += " and t.flow_id = ?";
			params.add(this.getMsg().getFlowInst().flow_id);
		}
		if(StrUtil.isNotEmpty(this.getMsg().getFlowId())){
			sql += " and t.flow_id = ?";
			params.add(this.getMsg().getFlowId());
		}
		if(StrUtil.isNotEmpty(tache_code)){
			sql += " and t.tache_code = ?";
			params.add(tache_code);
		}
		String prev_id = DAO.querySingleValue(sql, params.toArray(new String[]{}));
		if(StrUtil.isEmpty(prev_id) && this.getMsg().getFlowInst() != null){
			throw new Exception("当前环节"+cur_tacheCode+"已经是第一个环节，找不到上一个环节！");//打回时候的动作
		}
		if(StrUtil.isEmpty(prev_id) && this.getMsg().getFlowInst() == null){
			return null;//获取打回的列表，无需抛异常
		}
		MBpmWoTask prev = (MBpmWoTask) MBpmWoTask.getDAO().findById(prev_id);
		
		return prev;
	}
	
	/**
	 * 获取上一个环节规格
	 * @param tacheCode
	 * @return SBpmBoFlowTache
	 * @throws Exception
	 */
	public SBpmBoFlowTache getPrevTache(String tacheCode) throws Exception {
		if (flowTaches == null) {
			loadFlowTaches();
		}

		int i = getTacheIndex(tacheCode);
		if( i == 0 ){
			return null;
		}
		
		SBpmBoFlowTache prevTache= (SBpmBoFlowTache) flowTaches.get(i - 1);

		if (prevTache != null && isSkip(prevTache)) {
			prevTache = getPrevTache(prevTache.tache_code);
		}
		
		return prevTache;
	}
	
	/**
	 * 当前环节是否存在在实例中
	 * @param tacheCode
	 * @return SBpmBoFlowTache
	 * @throws Exception
	 */
	public boolean isExitInInst(String tacheCode) throws Exception {
		String flow_id = msg.getFlowId();
		List exist = DAO.queryForMap("select 1 from bpm_wo_task where tache_code=? and flow_id=? ", new String[]{tacheCode,flow_id});
		if(exist.size()>0){
			return true;
		}
		return false;
	}
	
}
