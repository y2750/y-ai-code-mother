package com.yy.yaicodemother.service;

import cn.hutool.core.collection.CollUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.yy.yaicodemother.model.dto.app.AppAddRequest;
import com.yy.yaicodemother.model.dto.app.AppQueryRequest;
import com.yy.yaicodemother.model.entity.App;

import com.yy.yaicodemother.model.entity.User;
import com.yy.yaicodemother.model.vo.AppVO;
import com.yy.yaicodemother.model.vo.UserVO;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层。
 *
 * @author <a href="https://github.com/y2750">程序员yy</a>
 */
public interface AppService extends IService<App> {

    Flux<String>chatToGenCode(Long appId, String message, User loginUser);

    Long createApp(AppAddRequest appAddRequest, User loginUser);

    /**
     * 部署应用
     * @param appId
     * @param loginUser
     * @return
     */
    String deployApp(Long appId, User loginUser);

    void generateAppScreenshotAsync(Long appId, String appDeployUrl);

    AppVO getAppVO(App app);


    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);


    List<AppVO> getAppVOList(List<App> appList);

}
