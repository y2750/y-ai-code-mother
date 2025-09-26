package com.yy.yaicodemother.ai;

import com.yy.yaicodemother.ai.model.HtmlCodeResult;
import com.yy.yaicodemother.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.SystemMessage;
import reactor.core.publisher.Flux;

/**
 * ClassName: AiCodeGeneratorService
 * Package: com.yy.yaicodemother.ai
 * Description:
 *
 * @Author
 * @Create 2025/9/24 22:04
 * @Version 1.0
 */
public interface AiCodeGeneratorService {
/**
 * 生成代码的方法
 * 根据用户输入的消息生成相应的代码
 *
 * @param userMessage 用户输入的消息，用于生成代码的依据
 * @return 返回生成的代码字符串
 */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(String userMessage);

    @SystemMessage(fromResource = "prompt/codegen-multi-file-prompt.txt")
    MultiFileCodeResult generateMultiFileCode(String userMessage);

    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    Flux<String> generateHtmlCodeStream(String userMessage);

    @SystemMessage(fromResource = "prompt/codegen-multi-file-prompt.txt")
    Flux<String> generateMultiFileCodeStream(String userMessage);
}
