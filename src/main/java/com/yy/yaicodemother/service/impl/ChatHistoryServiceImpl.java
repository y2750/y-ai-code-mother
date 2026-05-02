package com.yy.yaicodemother.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.yy.yaicodemother.constant.UserConstant;
import com.yy.yaicodemother.exception.ErrorCode;
import com.yy.yaicodemother.exception.ThrowUtils;
import com.yy.yaicodemother.model.dto.chathistory.ChatHistoryQueryRequest;
import com.yy.yaicodemother.model.entity.App;
import com.yy.yaicodemother.model.entity.ChatHistory;
import com.yy.yaicodemother.mapper.ChatHistoryMapper;
import com.yy.yaicodemother.model.entity.User;
import com.yy.yaicodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.yy.yaicodemother.service.AppService;
import com.yy.yaicodemother.service.ChatHistoryService;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 服务层实现。
 *
 * @author <a href="https://github.com/y2750">程序员yy</a>
 */
@Service
@Slf4j
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>  implements ChatHistoryService{

    @Resource
    @Lazy
    private AppService appService;

    @Override
    /**
     * 分页查询应用的聊天历史记录
     * @param appId 应用ID，必须为正数
     * @param pageSize 每页记录数，必须在1-50之间
     * @param lastCreateTime 上一次查询的创建时间，用于分页
     * @param loginUser 当前登录用户，用于权限验证
     * @return 返回分页结果，包含聊天历史记录列表
     */
    public Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                                      LocalDateTime lastCreateTime,
                                                      User loginUser) {
        // 验证应用ID参数
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        // 验证分页大小参数
        ThrowUtils.throwIf(pageSize <= 0 || pageSize > 50, ErrorCode.PARAMS_ERROR, "页面大小必须在1-50之间");
        // 验证用户是否登录
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 验证权限：只有应用创建者和管理员可以查看
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        boolean isAdmin = UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole());
        boolean isCreator = app.getUserId().equals(loginUser.getId());
        ThrowUtils.throwIf(!isAdmin && !isCreator, ErrorCode.NO_AUTH_ERROR, "无权查看该应用的对话历史");
        // 构建查询条件
        ChatHistoryQueryRequest queryRequest = new ChatHistoryQueryRequest();
        queryRequest.setAppId(appId);
        queryRequest.setLastCreateTime(lastCreateTime);
        QueryWrapper queryWrapper = this.getQueryWrapper(queryRequest);
        // 查询数据
        return this.page(Page.of(1, pageSize), queryWrapper);
    }


/**
 * 加载聊天历史到内存中的方法
 * @param appId 应用ID，用于标识特定的应用
 * @param chatMemory 聊天内存窗口，用于存储加载的聊天历史
 * @param maxCount 最大加载的聊天历史条数
 * @return 实际加载的聊天历史条数，如果加载失败则返回0
 */
    @Override
    public int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount) {
        try {
        // 创建查询条件，按应用ID查询，按创建时间倒序排列，并限制查询结果数量
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .eq(ChatHistory::getAppId, appId)  // 筛选指定应用ID的历史记录
                    .orderBy(ChatHistory::getCreateTime, false)  // 按创建时间降序排列
                    .limit(1, maxCount);  // 设置查询范围，从第1条开始，最多返回maxCount条记录
            List<ChatHistory> historyList = this.list(queryWrapper);
        // 如果查询结果为空，直接返回0
            if(CollUtil.isEmpty(historyList)){
                return 0;
            }
        // 将查询结果反转，使最早的记录在前
            historyList = historyList.reversed();

            int loadedCount = 0;  // 记录已加载的聊天历史数量
            chatMemory.clear();;

            for (ChatHistory history : historyList) {
                if(ChatHistoryMessageTypeEnum.USER.getValue().equals(history.getMessageType())){
                    chatMemory.add(UserMessage.from(history.getMessage()));
                }else {
                    chatMemory.add(UserMessage.from(history.getMessage()));
                }
                loadedCount++;
            }
            log.info("成功为appId：{}，加载{}条对话历史到内存中", appId, loadedCount);
            return loadedCount;
        }catch (Exception e){
            log.error("为appId：{}，加载对话历史到内存中失败", appId, e);
            return 0;
        }
    }


    @Override
    public boolean addChatMessage(Long appId, String message, String messageTye, Long userId) {
        ThrowUtils.throwIf(appId == null || appId <=0, ErrorCode.PARAMS_ERROR, "应用Id不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "消息不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(messageTye), ErrorCode.PARAMS_ERROR, "消息类型不能为空");
        ThrowUtils.throwIf(userId == null || userId <=0, ErrorCode.PARAMS_ERROR, "用户Id不能为空");

        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .message(message)
                .messageType(messageTye)
                .userId(userId)
                .build();

        return this.save(chatHistory);
    }

    @Override
    public boolean deleteByAppId(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <=0, ErrorCode.PARAMS_ERROR, "应用Id不能为空");
        QueryWrapper queryWrapper = QueryWrapper.create().eq("appId", appId);
        return this.remove(queryWrapper);
    }

    /**
     * 获取查询包装类
     *
     * @param chatHistoryQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (chatHistoryQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chatHistoryQueryRequest.getId();
        String message = chatHistoryQueryRequest.getMessage();
        String messageType = chatHistoryQueryRequest.getMessageType();
        Long appId = chatHistoryQueryRequest.getAppId();
        Long userId = chatHistoryQueryRequest.getUserId();
        LocalDateTime lastCreateTime = chatHistoryQueryRequest.getLastCreateTime();
        String sortField = chatHistoryQueryRequest.getSortField();
        String sortOrder = chatHistoryQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq("id", id)
                .like("message", message)
                .eq("messageType", messageType)
                .eq("appId", appId)
                .eq("userId", userId);
        // 游标查询逻辑 - 只使用 createTime 作为游标
        if (lastCreateTime != null) {
            queryWrapper.lt("createTime", lastCreateTime);
        }
        // 排序
        if (StrUtil.isNotBlank(sortField)) {
            queryWrapper.orderBy(sortField, "ascend".equals(sortOrder));
        } else {
            // 默认按创建时间降序排列
            queryWrapper.orderBy("createTime", false);
        }
        return queryWrapper;
    }

}
