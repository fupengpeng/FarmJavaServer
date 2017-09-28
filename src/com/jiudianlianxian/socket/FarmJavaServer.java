package com.jiudianlianxian.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import com.alibaba.fastjson.JSON;


public class FarmJavaServer extends ServerSocket {  
	  
    private static final int SERVER_PORT = 8945; // 服务端端口  

    private static List<AcceptClientMessage> threadList = new ArrayList<AcceptClientMessage>(); // 服务器已启用线程集合  
    private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(  
            20); // 存放消息的队列  
  
    public FarmJavaServer() throws Exception {  
        super(SERVER_PORT);  
    }  
  
    /** 
     * 启动向客户端发送消息的线程，使用线程处理每个客户端发来的消息 
     *  
     * @throws Exception 
     */  
    public void load() throws Exception {  
    	//向用户发送消息
        new Thread(new SendClientMessage(threadList, msgQueue)).start(); // 开启向客户端发送消息的线程  
  
        while (true) {  
            // server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的  
            Socket socket = this.accept();  
            /** 
             * 我们的服务端处理客户端的连接请求是同步进行的， 每次接收到来自客户端的连接请求后， 
             * 都要先跟当前的客户端通信完之后才能再处理下一个连接请求。 这在并发比较多的情况下会严重影响程序的性能， 
             * 为此，我们可以把它改为如下这种异步处理与客户端通信的方式 
             */  
            // 每接收到一个Socket就建立一个新的线程来处理它  接收用户发送的消息，并进行处理
            new Thread(new AcceptClientMessage(socket,threadList,msgQueue)).start();  
        }  
    }  
 
  
    /** 
     * 入口 
     *  
     * @param args 
     */  
    public static void main(String[] args) {  
        try {  
        	FarmJavaServer server = new FarmJavaServer(); // 启动服务端  
            server.load();  
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}
