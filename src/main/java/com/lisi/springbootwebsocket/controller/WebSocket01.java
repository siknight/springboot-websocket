package com.lisi.springbootwebsocket.controller;

import org.springframework.stereotype.Controller;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@ServerEndpoint("/server01/{name}")
public class WebSocket01 {

    private Session session;
    private String name;
   public static ConcurrentHashMap<String,WebSocket01> map=new ConcurrentHashMap<String, WebSocket01>();

    /**
     * 开启的时候自动打开
     * 相当于初始化
     * @param name
     * @param session
     */
    @OnOpen
    public void open(@PathParam("name") String name , Session session){
        map.put(name,this); //存一个用户
        System.out.println(name +" server01 open 打开了");
        this.session = session;
        this.name = name;
    }
    @OnClose
    public void close() throws IOException {
//        session.close();
        map.remove(name);
        System.out.println("");
    }
    @OnMessage
    public void getAndSendMessage(String message) throws IOException {
        System.out.println("得到客户端的消息="+message);
        System.out.println("当前在线数"+getConnetNum());
        if(session.isOpen()){
            Set<String> sets = map.keySet();
            for(String set:sets){
                if (!set.equals(name)){
                    System.out.println(name);
                    //要换个会话
                    map.get(set).session.getBasicRemote().sendText(message);

                }

           }

        }
    }

    /**
     * 获取当前连接数
     * @return
     */
    public int  getConnetNum(){
        return map.size();
    }
}
