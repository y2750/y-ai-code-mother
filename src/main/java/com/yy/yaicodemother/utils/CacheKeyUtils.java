package com.yy.yaicodemother.utils;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;


/**
 * ClassName: CacheKeyUtils
 * Package: com.yy.yaicodemother.utils
 * Description:
 *
 * @Author
 * @Create 2025/10/17 13:21
 * @Version 1.0
 */
public class CacheKeyUtils {

    public static String generateKry(Object object) {
        if(object == null) {
            return DigestUtil.md5Hex("null");
        }
        String jsonStr = JSONUtil.toJsonStr(object);
        return DigestUtil.md5Hex(jsonStr);

    }
}
