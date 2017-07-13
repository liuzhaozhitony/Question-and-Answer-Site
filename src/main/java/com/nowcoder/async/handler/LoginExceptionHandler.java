package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by LIU ZHAOZHI on 2017-7-13.
 */
@Component
public class LoginExceptionHandler implements EventHandler{

    @Autowired
    MailSender mailSender;


    @Override
    public void doHandler(EventModel model) {
        //判断某个用户登录异常
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("username",model.getExt("username"));
        mailSender.sendWithHTMLTemplate(model.getExt("email"),"登录IP异常","mails/login_exception.html",map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
