package com.knilim.account.util;

import com.alibaba.fastjson.JSONObject;

public class Response extends JSONObject {

    public Response(boolean success, String name, Object object) {
        put("success", success);
        put(name, object);
    }
}
