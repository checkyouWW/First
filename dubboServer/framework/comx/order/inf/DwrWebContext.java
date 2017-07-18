package comx.order.inf;


/**
 * DWR作为AJAX调度器的上下文
 * AppFrmDwrWebContext.java Class Name : AppFrmDwrWebContext.java<br>
 * Description :<br>
 * Copyright 2010 ztesoft<br>
 * Author : zhou.jundi<br>
 * Date : 2012-7-17<br>
 *
 * Last Modified :<br>
 * Modified by :<br>
 * Version : 1.0<br>
 */
public class DwrWebContext extends WebContext {

	@Override
	public void initSession() throws Exception {
//		String sessionIdName = this.getClass().getSimpleName() + "_session_id";
//		String sessionId = (String) RequestContextDwr.getContext().getHttpSession()
//				.getAttribute(sessionIdName);
//		if (!StringUtils.hasText(sessionId))
//			sessionId = "";
//		this.session = ISession.getInstance(sessionId);	
//		RequestContextDwr.getContext().getHttpSession()
//				.setAttribute(sessionIdName, this.getSession().getSessionId());
//
//		ObjDeltaMng.getInstance(this.session).pushLayer();
	}
}
