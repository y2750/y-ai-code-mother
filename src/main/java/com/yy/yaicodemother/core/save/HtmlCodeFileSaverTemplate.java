package com.yy.yaicodemother.core.save;

import cn.hutool.core.util.StrUtil;
import com.yy.yaicodemother.ai.model.HtmlCodeResult;
import com.yy.yaicodemother.exception.BusinessException;
import com.yy.yaicodemother.exception.ErrorCode;
import com.yy.yaicodemother.model.enums.CodeGenTypeEnum;

/**
 * ClassName: HtmlCodeFileSaverTemplate
 * Package: com.yy.yaicodemother.core.save
 * Description:
 *
 * @Author
 * @Create 2025/9/26 10:14
 * @Version 1.0
 */
public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult>{
    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }


    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath,"index.html",result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);

        if(StrUtil.isBlank(result.getHtmlCode())){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"html代码不能为空");
        }
    }
}
