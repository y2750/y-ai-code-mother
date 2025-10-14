package com.yy.yaicodemother.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ClassName: ProjectDownloadService
 * Package: com.yy.yaicodemother.service
 * Description:
 *
 * @Author
 * @Create 2025/10/13 10:09
 * @Version 1.0
 */
public interface ProjectDownloadService {

    void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response);
}
