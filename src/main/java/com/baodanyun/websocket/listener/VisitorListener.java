package com.baodanyun.websocket.listener;

import com.baodanyun.websocket.bean.user.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2017/3/3.
 */

@Service
public class VisitorListener {
    private static final Logger logger = LoggerFactory.getLogger(VisitorListener.class);

    public void login(Visitor visitor){

    }

    public void logOut(Visitor visitor){
    }

    public void leaveMessage(String myid,String m ){

    }

    public void chat(String mark,Visitor visitor){
    }

}
