package com.knilim.relationship.utils;

import com.alibaba.fastjson.JSONObject;

public class Response extends JSONObject {
    public Response(boolean success, String name, Object object) {
        put("success", success);
        put(name, object);
    }
}
