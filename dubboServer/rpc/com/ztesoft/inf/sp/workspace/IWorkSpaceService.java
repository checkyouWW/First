package com.ztesoft.inf.sp.workspace;

import com.ztesoft.inf.util.RpcPageModel;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by kam on 2016/9/8.
 */
public interface IWorkSpaceService {
    @Transactional
    RpcPageModel myDataServiceList(Map params);

    @Transactional
    RpcPageModel myTaskServiceList(Map params);

    @Transactional
    RpcPageModel myTeamMenbers(Map params);

    @Transactional
    List myTeamMenbersId(Map params);

    @Transactional
    Map deleteMenbers(Map params) throws Exception;

    @Transactional
    Map setAdmin(Map params) throws Exception;

    @Transactional
    Map addMenber(Map params) throws Exception;

    @Transactional
    RpcPageModel myApply(Map params);

    @Transactional
    Map recoverApply(Map params) throws Exception;

    @Transactional
    Map cancelApply(Map params) throws Exception;

    @Transactional
	Map revokeApply(Map params) throws Exception;
}
