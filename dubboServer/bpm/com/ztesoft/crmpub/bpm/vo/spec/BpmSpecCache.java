package com.ztesoft.crmpub.bpm.vo.spec;

import java.util.concurrent.ConcurrentHashMap;


/**
 * 霜最寞跡杅擂遣湔
 * 
 * @author lirx & major
 * 
 */
public class BpmSpecCache {

	private static ConcurrentHashMap flowDefCacheMap = new ConcurrentHashMap(); // 霜最寞跡

	private static ConcurrentHashMap flowTacheCacheMap = new ConcurrentHashMap(); // 霜最寞跡

	public static SBpmBoFlowDef getFlowDef(String boTypeId) {
		SBpmBoFlowDef flowDef = (SBpmBoFlowDef) flowDefCacheMap.get(boTypeId);
		if (flowDef == null) {
			flowDef = (SBpmBoFlowDef)SBpmBoFlowDef.getDAO().findById(boTypeId);
			flowDefCacheMap.put(boTypeId, flowDef);
		}
		return flowDef;
	}

	@Deprecated
	public static SBpmBoFlowTache getFlowTache(String woTypeId) {
		SBpmBoFlowTache flowTache = (SBpmBoFlowTache) flowTacheCacheMap.get(woTypeId);
		if (flowTache == null) {
			flowTache = (SBpmBoFlowTache)SBpmBoFlowTache.getDAO().findById(woTypeId);
			flowTacheCacheMap.put(woTypeId, flowTache);
		}
		return flowTache;
	}
}
