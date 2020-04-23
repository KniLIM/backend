package com.knilim.account.util;

import com.alibaba.fastjson.JSONObject;
import com.knilim.data.utils.Tuple;

public class Response extends JSONObject {

    public Response(boolean success, Tuple<String,Object>... objects) {
        put("success", success);
        for(Tuple<String,Object> obj:objects)
            put(obj.getFirst(),obj.getSecond());
    }
}
