package com.yy.yaicodemother.ai.model;


import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * ClassName: MultiFileCodeResult
 * Package: com.yy.yaicodemother.ai.model
 * Description:
 *
 * @Author
 * @Create 2025/9/25 09:12
 * @Version 1.0
 */
@Data
@Description("生成多文件代码的结果")
public class MultiFileCodeResult {

    /**
     * html代码
     */
    @Description("html代码")
    private String htmlCode;

    /**
     * css代码
     */
    @Description("css代码")
    private String cssCode;

    /**
     * js代码
     */
    @Description("js代码")
    private String jsCode;

    @Description("生成代码的描述")
    private String description;
}
