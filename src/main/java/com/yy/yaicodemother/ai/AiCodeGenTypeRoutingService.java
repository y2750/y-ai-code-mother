package com.yy.yaicodemother.ai;

import com.yy.yaicodemother.model.enums.CodeGenTypeEnum;
import dev.langchain4j.service.SystemMessage;

/**
 * ClassName: AiCodeGenTypeRoutingService
 * Package: com.yy.yaicodemother.ai
 * Description:
 *
 * @Author
 * @Create 2025/10/13 13:46
 * @Version 1.0
 */
public interface AiCodeGenTypeRoutingService {

    /**
     * 路由代码生成类型
     */
    @SystemMessage(fromResource = "prompt/codegen-routing-system-prompt.txt")
    CodeGenTypeEnum routeCodeGenType(String userPrompt);
}
