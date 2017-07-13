package com.nowcoder.async;

/**
 * Created by LIU ZHAOZHI on 2017-7-7.
 */
public enum EventType {
    LIKE(0),
        COMMENT(1),
            LOGIN(2),
                MAIL(3);

    private int value;
    EventType(int value){ this.value = value; }  //默认private修饰符，不允许外部修改
    public int getValue(){
        return value;
    }
}
