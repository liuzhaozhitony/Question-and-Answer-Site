package com.nowcoder.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LIU ZHAOZHI on 17/6/12.
 * 为了传递一个综合的对象，比如前端页面要显示两个对象（user,question）的属性，可以先set到viewObject
 * 里，再从前端取出来
 */
public class ViewObject {
    private Map<String, Object> objs = new HashMap<String, Object>();
    public void set(String key, Object value) {
        objs.put(key, value);
    }

    public Object get(String key) {
        return objs.get(key);
    }
}
