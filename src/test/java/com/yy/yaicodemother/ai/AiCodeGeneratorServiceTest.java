package com.yy.yaicodemother.ai;

import com.yy.yaicodemother.ai.model.HtmlCodeResult;
import com.yy.yaicodemother.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: AiCodeGeneratorServiceTest
 * Package: com.yy.yaicodemother.ai
 * Description:
 *
 * @Author
 * @Create 2025/9/25 08:49
 * @Version 1.0
 */
@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

/**
 * 测试方法：测试生成HTML代码的功能
 * 该方法验证AI代码生成服务是否能够正常生成HTML代码
 */
    @Test
    void generateHtmlCode() {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode("生成一个程序员博客网站，不超过20行");
        Assertions.assertNotNull(result);
    }

    @Test
    void generateMultiFileCode() {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode("生成一个程序员留言板，不超过20行");
        Assertions.assertNotNull(result);
    }
}