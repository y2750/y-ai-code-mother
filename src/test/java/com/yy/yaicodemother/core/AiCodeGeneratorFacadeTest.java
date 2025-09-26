package com.yy.yaicodemother.core;

import com.yy.yaicodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: AiCodeGeneratorFacadeTest
 * Package: com.yy.yaicodemother.core
 * Description:
 *
 * @Author
 * @Create 2025/9/25 12:10
 * @Version 1.0
 */
@SpringBootTest
class AiCodeGeneratorFacadeTest {

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateCodeAndSaveCode() {
        File file = aiCodeGeneratorFacade.generateAndSaveCode("生成一个HTML页面", CodeGenTypeEnum.HTML,1L);
        assertNotNull(file);
    }

    @Test
    void generateCodeAndSaveCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream("生成一个登录页面，不超过20行代码", CodeGenTypeEnum.MULTI_FILE,1L);
        List<String> result = codeStream.collectList().block();
        Assertions.assertNotNull(result);
        String join = String.join("", result);
        Assertions.assertNotNull(join);
    }
}