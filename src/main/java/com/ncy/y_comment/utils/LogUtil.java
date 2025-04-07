package com.ncy.y_comment.utils;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LogUtil {
    public static void logAction(String module, String action, Long userId, Map<String, Object> params) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("module", module);
        logMap.put("action", action);
        logMap.put("userId", userId);
        logMap.put("timestamp", System.currentTimeMillis());
        logMap.put("params", params);
        log.info(JSONUtil.toJsonStr(logMap));
    }
}