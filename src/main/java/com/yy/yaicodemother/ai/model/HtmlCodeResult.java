package com.yy.yaicodemother.ai.model;


import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * ClassName: HtmlCodeResult
 * Package: com.yy.yaicodemother.ai.model
 * Description:
 *
 * @Author
 * @Create 2025/9/25 09:11
 * @Version 1.0
 */
@Data
@Description("生成的HTML代码文件的结果")
public class HtmlCodeResult {
    /**
     * 生成的HTML代码
     */
    @Description("HTML代码")
    private String htmlCode;
    /**
     * 代码描述
     */
    @Description("生成代码的描述")
    private String description;
}
