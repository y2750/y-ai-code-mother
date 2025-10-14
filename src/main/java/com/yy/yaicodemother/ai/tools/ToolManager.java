package com.yy.yaicodemother.ai.tools;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: ToolManager
 * Package: com.yy.yaicodemother.ai.tools
 * Description:
 *
 * @Author
 * @Create 2025/10/14 10:31
 * @Version 1.0
 */
@Slf4j
@Component
public class ToolManager {

    private final Map<String, BaseTool> toolMap = new HashMap<>();

    @Resource
    private BaseTool[] tools;

    /**
     * 初始化注册工具
     */
    @PostConstruct
    public void initTools() {
        for (BaseTool tool : tools) {
            toolMap.put(tool.getToolName(), tool);
            log.info("注册工具: {} -> {}", tool.getToolName(), tool.getDisplayName());
        }
        log.info("工具注册完成，共注册 {} 个工具", toolMap.size());
    }

    /**
     * 根据工具名称获取工具
     * @param toolName
     * @return 工具示例
     */
    public BaseTool getTool(String toolName) {
        return toolMap.get(toolName);
    }

    public BaseTool[] getAllTools() {
        return tools;
    }
}
