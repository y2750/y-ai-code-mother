package com.yy.yaicodemother.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yy.yaicodemother.ai.model.HtmlCodeResult;
import com.yy.yaicodemother.ai.model.MultiFileCodeResult;
import com.yy.yaicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * ClassName: codeFileSave
 * Package: com.yy.yaicodemother.core
 * Description:
 *
 * @Author
 * @Create 2025/9/25 10:18
 * @Version 1.0
 */
@Deprecated
public class CodeFileSaver {

    //文件保存的根目录
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir")+"/tmp/code_output";

    //保存 HTML 网页代码
    public static File saveHtmlCodeResult(HtmlCodeResult htmlCodeResult){
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        writeToFile(baseDirPath,"index.html",htmlCodeResult.getHtmlCode());
        return new File(baseDirPath);
    }

    //保存多文件代码
    public static File saveMultiFileCodeResult(MultiFileCodeResult multiFileCodeResult){
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        writeToFile(baseDirPath,"index.html",multiFileCodeResult.getHtmlCode());
        writeToFile(baseDirPath,"style.css",multiFileCodeResult.getCssCode());
        writeToFile(baseDirPath,"script.js",multiFileCodeResult.getJsCode());
        return new File(baseDirPath);
    }

    //构建文件的唯一路径：tmp/code_output/bizType_雪花 ID
    private static String buildUniqueDir(String bizType){
        String uniqueDirName = StrUtil.format("{}_{}" , bizType , IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    //保存单个文件
    private static void writeToFile(String dirPath,String filename, String content){
        String filePath = dirPath+ File.separator + filename;
        FileUtil.writeString(content,filePath, StandardCharsets.UTF_8);
    }
}
