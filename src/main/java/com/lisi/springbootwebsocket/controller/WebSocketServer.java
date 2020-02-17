package com.lisi.springbootwebsocket.controller;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/webSocket/{name}")
public class WebSocketServer {
    //存储客户端的连接对象,每个客户端连接都会产生一个连接对象
    private static ConcurrentHashMap<String,WebSocketServer> map=new ConcurrentHashMap();
    //每个连接都会有自己的会话
    private Session session;
    //用户
    private String name;

    /**
     * 打开聊天
     * @param name
     * @param session
     */
    @OnOpen
    public void open(@PathParam("name") String name, Session session){
        map.put(name,this);
        System.out.println(name+"连接服务器成功");
        System.out.println("客户端连接个数:"+getConnetNum());
        this.session=session;
        this.name=name;
    }

    /**
     * 退出聊天
     */
    @OnClose
    public void close(){
        map.remove(name);
        System.out.println(name+"断开了服务器连接");
    }

    /**
     * 遇到异常时怎么处理
     * @param error
     */
    @OnError
    public void error(Throwable error){
        error.printStackTrace();
        System.out.println(name+"出现了异常");
    }

    /**
     * 接收消息
     * @param message
     * @throws IOException
     */
    @OnMessage
    public void getMessage(String message) throws IOException {
        System.out.println("收到"+name+":"+message);
        System.out.println("客户端连接个数:"+getConnetNum());
        Set<Map.Entry<String, WebSocketServer>> entries = map.entrySet();
        for (Map.Entry<String, WebSocketServer> entry : entries) {
            if(!entry.getKey().equals(name)){//将消息转发到其他非自身客户端
                entry.getValue().send(message);
            }
        }

    }

    /**
     * 发送文本消息
     * @param message
     * @throws IOException
     */
    public void send(String message) throws IOException {
        if(session.isOpen()){
            session.getBasicRemote().sendText(message);
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
