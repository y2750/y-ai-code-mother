package com.yy.yaicodemother.controller;

import com.yy.yaicodemother.common.BaseResponse;
import com.yy.yaicodemother.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: TestController
 * Package: com.yy.yaicodemother.controller
 * Description:
 *
 * @Author
 * @Create 2025/9/22 11:52
 * @Version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {
    @GetMapping("/")
    public BaseResponse<String> test() {
        return ResultUtils.success("success");
    }
}
