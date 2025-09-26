package com.yy.yaicodemother.core.saver;

import cn.hutool.core.util.StrUtil;
import com.yy.yaicodemother.ai.model.MultiFileCodeResult;
import com.yy.yaicodemother.model.enums.CodeGenTypeEnum;

/**
 * ClassName: MultiFileCodeFileSaverTemplate
 * Package: com.yy.yaicodemother.core.save
 * Description:
 *
 * @Author
 * @Create 2025/9/26 10:32
 * @Version 1.0
 */
public class MultiFileCodeFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult>{
    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFiles(MultiFileCodeResult result, String baseDirPath) {

        writeToFile(baseDirPath,"index.html",result.getHtmlCode());

        writeToFile(baseDirPath,"style.css",result.getCssCode());

        writeToFile(baseDirPath,"script.js",result.getJsCode());
    }

    @Override
    protected void validateInput(MultiFileCodeResult result) {
        super.validateInput(result);
        if(StrUtil.isBlank(result.getHtmlCode())){
            throw new RuntimeException("html代码不能为空");
        }
    }

}
