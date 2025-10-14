package com.yy.yaicodemother.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: WebScreenshotUtilsTest
 * Package: com.yy.yaicodemother.utils
 * Description:
 *
 * @Author
 * @Create 2025/10/11 18:30
 * @Version 1.0
 */
@SpringBootTest
class WebScreenshotUtilsTest {

    @Test
    void saveWebPageScreenshot() {
        String testUrl = "https://www.baidu.com";
        String saveWebPageScreenshot = WebScreenshotUtils.saveWebPageScreenshot(testUrl);
        Assertions.assertNotNull(saveWebPageScreenshot);
    }
}