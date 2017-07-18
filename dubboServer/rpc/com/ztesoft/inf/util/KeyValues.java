package com.ztesoft.inf.util;

/**
 * 
 * 定义常量键值对
 * 
 */
public class KeyValues {

	/** ip攻击，单位时间内允许请求的最大数 */
	public static final String IPATTACK_MAXCONCURRENTREQUESTS = "IPATTACK_MAXCONCURRENTREQUESTS";
	/** ip攻击，最小时间秒数 */
	public static final String IPATTACK_PERIOD_MIN = "IPATTACK_PERIOD_MIN";
	/** ip攻击，最大时间秒数 */
	public static final String IPATTACK_PERIOD_MAX = "IPATTACK_PERIOD_MAX";
	/** ip攻击启用  1-启用  0-关闭   */
	public static final String IPATTACK_ENABLE = "IPATTACK_ENABLE";

	public static final String LOGIN = "1";
	public static final String LOGOUT = "0";
	// 状态内容
	public static final String LOG_SUCCESS = "1";
	public static final String LOG_FAIL = "0";
	
	// 返回状态标识
	public static final String STATUSSIGN = "logsign";
	// 返回状态内容
	public static final String SUCCESS = "true";
	public static final String FAIL = "false";
	// 结果返回信息标识
	public static final String MSGSIGN = "msg";
	// 登录返回信息内容
	public static final String ERRORSTR0 = "登录失败：用户名不存在";
	public static final String ERRORSTR1 = "登录失败：验证码错误";
	public static final String ERRORSTR2 = "登录失败：密码错误";

	// 菜单类型
	/** 菜单 **/
	public static final String MENU_TYPE_MENU = "1"; // 菜单
	/** 按钮 **/
	public static final String MENU_TYPE_BUTTON = "2"; // 按钮

	// 权限类型
	/** 菜单权限 **/
	public static final String PRIVILEGE_TYPE_MENU = "1"; // 菜单权限
	/** 按钮权限 **/
	public static final String PRIVILEGE_TYPE_BUTTON = "2"; // 按钮权限
	/** 目录权限 **/
	public static final String PRIVILEGE_TYPE_DIR = "3"; // 目录权限
	
	/** 团队负责人 **/
	public static final String IS_TEAM_DIRECTOR_T = "T"; // 团队负责人
	/** 非团队负责人 **/
	public static final String IS_TEAM_DIRECTOR_F = "F"; // 非团队负责人
	
	/** 有效状态 **/
	public static final String STATE_00A = "00A"; // 有效状态   通用
	
	/** 新建状态 **/
	public static final String STATE_00B = "00B";	//新建状态

	/** 下架状态 **/
	public static final String STATE_00S = "00S";	//下架状态
	
	/** 删除状态 **/
	public static final String STATE_00X = "00X";	//删除状态
	
	/** 返回给前台响应成功的状态字段 **/
	public static final String RESPONSE_SUCCESS = "success";	//返回给前台响应成功的状态字段
	
	/** 返回给前台响应失败的状态字段 **/
	public static final String RESPONSE_FAILED = "failed";	//返回给前台响应失败的状态字段
	
	/**数据源名称**/
	public static final String DATASOURCE_BDP = "bdp_datasource";	//bdp的数据源
	
	/**服务类型:数据服务**/
	public static final String SERVICE_TYPE_DATA = "DATA";//DATA:数据服务 TASK任务服务
	
	/**服务类型:任务服务**/
	public static final String SERVICE_TYPE_TASK = "TASK";//DATA:数据服务 TASK任务服务
	
	/**服务类型:安全数据使用服务**/
	public static final String SERVICE_TYPE_SECURITY = "SECURITY";//DATA:数据服务 TASK任务服务 SECURITY安全数据使用服务
	
	public static final String SELECT_TABLE_BYFILL = "2";	//手动 来源表输入方式
	
	public static final String SELECT_TABLE_SELECTED = "2";	//选择来源表输入方式
	
	public static final String USE_DST_ALGORITHM = "1";	//使用脱敏算法
	
	public static final String NOT_USE_DST_ALGORITHM = "0";	//不使用脱敏算法
	
	/** 公告状态：未发布 */
	public static final String NOTICE_STATE_0 = "0";
	/** 公告状态：已发布 */
	public static final String NOTICE_STATE_1 = "1";
	
	/** 公告范围：全体 */
	public static final String NOTICE_RANGE_1 = "1";
	/** 公告范围：团队 */
	public static final String NOTICE_RANGE_2 = "2";
	
	/** 算法类型：MD5对照表 **/
	public static final String ALG_TYPE_0 = "0";
	
	/** 算法类型：RSA/ASE密钥 **/
	public static final String ALG_TYPE_1 = "1";	
	
	/** 算法类型：截断 **/
	public static final String ALG_TYPE_2 = "2";	
	
	/** 算法类型：混淆 **/
	public static final String ALG_TYPE_3 = "3";	
	
	/** 分发模式：ftp **/
	public static final String DISPATCH_TYPE_FTP = "ftp"; 
	
	/** 分发模式：db_import **/
	public static final String DISPATCH_TYPE_DB_IMPORT = "db_import"; 
	
	//一级账期分区
	public static final String FIRST_DIVISION_CODE = "FIRST_DIVISION";
	
	//二级账期分区
	public static final String SECOND_DIVISION_CODE = "SECOND_DIVISION";
	
	//抽取频率静态编码
	public static final String EXTRACT_FREQ_CODE = "EXTRACT_FREQ";
	
	//是否静态编码
	public static final String YES_OR_NOT_CODE = "YES_OR_NOT";
	
	//抽取频率（月）
	public static final String EXTRACT_FREQ_MONTH = "M";
	
	//抽取频率（日）
	public static final String EXTRACT_FREQ_DAY = "D";
	
	//分发日期的前缀
	public static final String DISPATH_TIME_CODE_PRE = "SUPPLY_TIME_";
	
	/**服务申请的流程规格ID**/
	public static final String BO_TYPE_ID_SERVICE_APPLY = "SERVICE_APPLY";
	
	/**服务修改的流程规格ID**/
	public static final String BO_TYPE_ID_SERVICE_MOD = "SERVICE_MOD";
	
	/**服务取消的流程规格ID**/
	public static final String BO_TYPE_ID_SERVICE_CANCEL = "SERVICE_CANCEL";
	
	//申请单号前缀
	public static final String APPLY_CODE_PREFIX = "APPLY_";

	/**服务申请的状态**/
	public static final String APPLY_STATE_AUDIT = "00B"; //审批中
	public static final String APPLY_STATE_WITHDRAW = "00C"; //已撤销
	public static final String APPLY_STATE_SUCCESS = "00A"; //通过
	public static final String APPLY_STATE_FAIL = "00X"; //未通过
	
	/**服务申请的流程状态**/
	public static final String BO_STATE_ACTIVE = "ACTIVE";//审批中
	public static final String BO_STATE_INVALID = "INVALID";//已撤销
	public static final String BO_STATE_END = "END";//通过
	public static final String BO_STATE_FAIL = "FAIL";//未通过
	
	/**数据库类型**/
	public static final String DB_TYPE_ORACLE = "0";	//ORACLE类型
	
	/**工单的状态**/
	public static final String ORDER_STATE_TODO = "001"; //待处理
	public static final String ORDER_STATE_DEALING = "002"; //正在处理 --预占的状态
	public static final String ORDER_STATE_INF = "003"; //已送接口
	public static final String ORDER_STATE_DISPATCH = "004"; //分发中 --预占的状态
	public static final String ORDER_STATE_SUCCESS = "005"; //已完成
	public static final String ORDER_STATE_FAIL = "999"; //失败
	
	public static final int ASYNC_TASK_QUERY_FAILED = 2;//2是失败
	public static final int ASYNC_TASK_QUERY_ERROR = -1;
	public static final int ASYNC_TASK_QUERY_SUCCESS = 1;//1是成功
	public static final int ASYNC_TASK_QUERY_RUNNING = 0;//0是进行中
	
	public static final String ALERT_TYPE_IMPORT = "IMPORT";
	public static final String ALERT_TYPE_GENERATE = "GENERATE";
	public static final String ALERT_TYPE_DISPATH = "DISPATH";
	public static final String ALERT_TYPE_RERUN = "RERUN";
	
	public static final String SCHEDULE_LOG_RESULT_NORMAL = "1";	//调度正常进行日志
	public static final String SCHEDULE_LOG_RESULT_ERROR = "0";		//调度发生异常日志
	public static final String SCHEDULE_LOG_RESULT_UNKNOW = "2";	//调度发生未知情况日志

	
	/** 系统管理员：是 */
	public static final String IS_MANAGER_T = "T";
	/** 系统管理员：否 */
	public static final String IS_MANAGER_F = "F";
	
	/** 大数据权限类型：队列 */
	public static final String BD_PRIV_21 = "21";
	/** 大数据权限类型：HIVE表 */
	public static final String BD_PRIV_23 = "23";
	/** 大数据权限类型：HIVE视图 */
	public static final String BD_PRIV_29 = "29";
	
	/** 大数据默认租户角色类型 */
	public static final String BDP_DEF_ROLE_TYPE = "001";//普通角色 001，hdfs角色 002
	
	public static final String EXTRACT_TYPE_ONCE = "once";	//一次性提取 
	
	public static final String FTP_DISPATH_PUSH	=	"push";		//FTP push 推送模式
	
	public static final String XLS_IMPORT_TYPE1 = "1";	//导入基本数据能力
	
	public static final String XLS_IMPORT_TYPE2 = "2";	//导入字段信息
	
	/** INF:与BDP公共接口机 */
	public static final String FTP_TYPE_INF = "INF";
	/** TEAM:团队接口机 */
	public static final String FTP_TYPE_TEAM = "TEAM";
	/** TASK:任务接口机 */
	public static final String FTP_TYPE_TASK = "TASK";
	
	public static final String DISPATCH_LOG_SUC = "1";
	
	public static final String DISPATCH_LOG_FAILED = "0";
	
	public static final String DISPATCH_LOG_FILE_DELETEING = "3";
	
	public static final String DISPATCH_LOG_FILE_HASDEAL = "2";
	
	public static final String CLEAN_FTP_FILE_INTERVAL = "CLEAN_FTP_FILE_INTERVAL";
	
	/**数据同步工单状态：未同步*/
	public static final String SYN_ORDER_STATE_001 = "001";
	/**数据同步工单状态：同步中*/
	public static final String SYN_ORDER_STATE_002 = "002";
	/**数据同步工单状态：已同步*/
	public static final String SYN_ORDER_STATE_100 = "100";
	/**数据同步工单状态：同步失败*/
	public static final String SYN_ORDER_STATE_999 = "999";
	
	/**数据同步工单状态：未同步*/
	public static final String SYN_ORDER_ITEM_STATE_001 = "001";
	/**数据同步工单状态：同步中*/
	public static final String SYN_ORDER_ITEM_STATE_002 = "002"; // --预占的状态
	/**数据同步工单状态：已送接口*/
	public static final String SYN_ORDER_ITEM_STATE_003 = "003";
	/**数据同步工单状态：校验接口处理中*/
	public static final String SYN_ORDER_ITEM_STATE_004 = "004"; // --预占的状态
	/**数据同步工单状态：已同步*/
	public static final String SYN_ORDER_ITEM_STATE_100 = "100";
	/**数据同步工单状态：同步失败*/
	public static final String SYN_ORDER_ITEM_STATE_999 = "999";
	
	public static final String SRC_SYS_HD = "HD";
	
	/**同步或者分发的where 条件*/
	public static final String WHERE_TYPE_SYN = "syn";//数据从db同步到hive时候的where 条件,包括create hive table 和 同步
	public static final String WHERE_TYPE_DISPATCH = "dispatch";//数据从hive同步到db ftp时候的where 条件,包括create hive view 和 分发
	
	/**这个字段是否在hive公共库新建分区*/
	public static final String CREATE_NEW_PARTITION_YES = "1";//是
	public static final String CREATE_NEW_PARTITION_NO = "0";//否
	
	/**重跑标识*/
	public static final String RE_RUN_FLAG_YES = "1";//是
	public static final String RE_RUN_FLAG_NO = "0";//否
}
