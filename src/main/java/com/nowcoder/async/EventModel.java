package com.nowcoder.async;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LIU ZHAOZHI on 2017-7-7.
 */
//EventModel提供一个事件的载体
public class EventModel {
    private EventType type;  //事件类型，比如评论
    private int actorId;    //事件主题，谁评论的
    private int entityType;  //评论的哪个题目？
    private int entityId;   //评论的哪个题目的id
    private int entityOwnerId;  //这个题目是谁发布

    private Map<String,String> exts = new HashMap<String,String>();

    public EventModel(){

    }

    public EventModel setExt(String key, String value){
        exts.put(key, value);
        return this;
    }

    public String getExt(String key){
        return exts.get(key);
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        //设置成返回值为EventModel的原因，方便链状调用，xx.setType().setXX().setXX()
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public EventModel(EventType type){
        this.type = type;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
