package com.jiudianlianxian.socket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * 
 * @Title: SendClientMessage
 * @Description: 给用户发送消息
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年9月27日 上午8:48:20
 *
 */
public class SendClientMessage implements Runnable{
	 private static List<AcceptClientMessage> threadList = new ArrayList<AcceptClientMessage>(); // 服务器已启用线程集合  
	 private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(  
	            20); // 存放消息的队列  
	public SendClientMessage( List<AcceptClientMessage> threadList,BlockingQueue<String> msgQueue){
		this.threadList = threadList;
		this.msgQueue = msgQueue;
		
	}
    @Override  
    public void run() {  
        while (true) {  
            String msg = null;  
            try {  
                msg = msgQueue.take();  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
            if (msg != null) {  
                for (AcceptClientMessage thread : threadList) {  
                    thread.sendMsg(msg);  
                }  
            }  
        }  
    }  

}
