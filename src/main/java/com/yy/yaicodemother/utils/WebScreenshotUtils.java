package com.yy.yaicodemother.utils;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.yy.yaicodemother.exception.BusinessException;
import com.yy.yaicodemother.exception.ErrorCode;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

/**
 * ClassName: WebScreenshotUtils
 * Package: com.yy.yaicodemother.utils
 * Description:
 *
 * @Author
 * @Create 2025/10/11 14:18
 * @Version 1.0
 */
@Slf4j
public class WebScreenshotUtils {

    private static final WebDriver webDriver;

    static {
        final int DEFAULT_WIDTH = 1600;
        final int DEFAULT_HEIGHT = 900;
        webDriver = initChromeDriver(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @PreDestroy
    public void destroy() {
        webDriver.quit();
    }

    public static String saveWebPageScreenshot(String webUrl) {
        if(StrUtil.isBlank(webUrl)){
            log.error("网页截图失败，url为空");
            return null;
        }

        try {
            String rootPath = System.getProperty("user.dir") + "/tmp/screenshots/" + UUID.randomUUID().toString().substring(0, 8);
            FileUtil.mkdir(rootPath);

            final String IMAGE_SUFFIX = ".png";

            String imageSavePath = rootPath + File.separator + RandomUtil.randomNumbers(5)+IMAGE_SUFFIX;

            webDriver.get(webUrl);

            waitForPageLoad(webDriver);

            byte[] screenshotsBytes = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);

            saveImage(screenshotsBytes, imageSavePath);

            log.info("网页截图成功，截图路径:{}", imageSavePath);

            final String COMPRESS_SUFFIX = "_compressed.jpg";
            String compressImagePath = rootPath + File.separator + RandomUtil.randomNumbers(5)+COMPRESS_SUFFIX;
            compressImage(imageSavePath, compressImagePath);
            log.info("压缩图片成功，压缩路径:{}", compressImagePath);

            FileUtil.del(imageSavePath);
            return compressImagePath;
        }catch (Exception e){
            log.error("网页截图失败:{}",webUrl, e);
            return null;
        }

    }

    /**
     * 初始化 Chrome 浏览器驱动
     */
    private static WebDriver initChromeDriver(int width, int height) {
        try {
            // 自动管理 ChromeDriver
            WebDriverManager.chromedriver().setup();
            // 配置 Chrome 选项
            ChromeOptions options = new ChromeOptions();
            // 无头模式
            options.addArguments("--headless");
            // 禁用GPU（在某些环境下避免问题）
            options.addArguments("--disable-gpu");
            // 禁用沙盒模式（Docker环境需要）
            options.addArguments("--no-sandbox");
            // 禁用开发者shm使用
            options.addArguments("--disable-dev-shm-usage");
            // 设置窗口大小
            options.addArguments(String.format("--window-size=%d,%d", width, height));
            // 禁用扩展
            options.addArguments("--disable-extensions");
            // 设置用户代理
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            // 创建驱动
            WebDriver driver = new ChromeDriver(options);
            // 设置页面加载超时
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            // 设置隐式等待
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            return driver;
        } catch (Exception e) {
            log.error("初始化 Chrome 浏览器失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化 Chrome 浏览器失败");
        }
    }

    private static void saveImage(byte[] imageBates, String imagePath) {
      try {
          FileUtil.writeBytes(imageBates, imagePath);
      }catch (Exception e){
          log.error("保存图片失败:{}",imagePath, e);
          throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存图片失败");
      }
    }

    /**
     * 压缩图片
     * @param originImagePath
     * @param compressImagePath
     */
    private static void compressImage(String originImagePath, String compressImagePath) {
        final float COMPRESSION_QUALITY = 0.5f;
        try {
            ImgUtil.compress(
                    FileUtil.file(originImagePath),
                    FileUtil.file(compressImagePath),
                    COMPRESSION_QUALITY
            );
        }catch (Exception e){
            log.error("压缩图片失败:{}",originImagePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "压缩图片失败");
        }
    }

    private static void waitForPageLoad(WebDriver webDriver) {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
            wait.until(driver -> ((JavascriptExecutor) driver)
                    .executeScript("return document.readyState")
                    .equals("complete"));

            Thread.sleep(2000);
            log.info("页面加载完成");
        }catch (Exception e){
            log.error("等待页面加载失败", e);
        }
    }
}

