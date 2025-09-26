package com.yy.yaicodemother.core.parser;

import com.yy.yaicodemother.exception.BusinessException;
import com.yy.yaicodemother.exception.ErrorCode;
import com.yy.yaicodemother.model.enums.CodeGenTypeEnum;

/**
 * ClassName: CodeParserExecutor
 * Package: com.yy.yaicodemother.core.parser
 * Description:
 *
 * @Author
 * @Create 2025/9/25 20:28
 * @Version 1.0
 */
public class CodeParserExecutor {

    private static final HtmlCodeParser htmlCodeParser = new HtmlCodeParser();

    private static final MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();
    public static Object executeParser(String codeContent, CodeGenTypeEnum codeGenType) {
        return switch (codeGenType) {
            case HTML -> htmlCodeParser.parseCode(codeContent);
            case MULTI_FILE -> multiFileCodeParser.parseCode(codeContent);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码类型");
        };
    }
}
