package com.auth.route.vo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by fangyitao on 2019/8/6.
 * 过滤器定义模型
 */
public class GatewayFilterDefinition {
    /**
     * FilterConfiguration Name
     */
    private String name;

    /**
     * 对应的路由规则
     */
    private Map<String, String> args = new LinkedHashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }
}
