package com.yy.yaicodemother.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.yy.yaicodemother.exception.BusinessException;
import com.yy.yaicodemother.exception.ErrorCode;
import com.yy.yaicodemother.exception.ThrowUtils;
import com.yy.yaicodemother.service.ProjectDownloadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;

/**
 * ClassName: ProjectDownloadServiceImpl
 * Package: com.yy.yaicodemother.service.impl
 * Description:
 *
 * @Author
 * @Create 2025/10/13 10:10
 * @Version 1.0
 */
@Service
@Slf4j
public class ProjectDownloadServiceImpl implements ProjectDownloadService {

    /**
     * 需要过滤的文件和目录名称
     */
    private static final Set<String> IGNORED_NAMES = Set.of(
            "node_modules",
            ".git",
            "dist",
            "build",
            ".DS_Store",
            ".env",
            "target",
            ".mvn",
            ".idea",
            ".vscode"
    );

    /**
     * 需要过滤的文件扩展名
     */
    private static final Set<String> IGNORED_EXTENSIONS = Set.of(
            ".log",
            ".tmp",
            ".cache"
    );

    /**
     * 检查路径是否允许包含在压缩包中
     *
     * @param projectRoot 项目根目录
     * @param fullPath    完整路径
     * @return 是否允许
     */
    private boolean isPathAllowed(Path projectRoot, Path fullPath) {
        // 获取相对路径
        Path relativizePath = projectRoot.relativize(fullPath);

        // 检查是否为忽略的文件或目录
        for (Path part : relativizePath) {
            String partName = part.toString();
            if (IGNORED_NAMES.contains(partName)) {
                return false;
            }

            if(IGNORED_EXTENSIONS.stream().anyMatch(ext -> partName.toLowerCase().endsWith(ext))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response) {
        ThrowUtils.throwIf(StrUtil.isBlank(projectPath), ErrorCode.PARAMS_ERROR, "项目路径不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(downloadFileName), ErrorCode.PARAMS_ERROR, "下载文件名不能为空");
        File projectDir = new File(projectPath);
        ThrowUtils.throwIf(!projectDir.exists(), ErrorCode.NOT_FOUND_ERROR, "项目路径不存在");
        ThrowUtils.throwIf(!projectDir.isDirectory(), ErrorCode.PARAMS_ERROR, "项目路径不是一个目录");
        log.info("开始打包下载项目：{} -> {}.zip",projectPath, downloadFileName);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/zip");
        response.addHeader("Content-Disposition", String.format("attachment; filename=\"%s.zip\"", downloadFileName));

        FileFilter filter = file -> isPathAllowed(projectDir.toPath(), file.toPath());
        try {
            ZipUtil.zip(response.getOutputStream(), StandardCharsets.UTF_8, false, filter, projectDir);
            log.info("打包下载项目成功：{} -> {}.zip",projectPath, downloadFileName);
        }catch (Exception e){
            log.error("打包下载项目失败：{} -> {}.zip",projectPath, downloadFileName, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "打包下载项目失败");
        }
    }
}

