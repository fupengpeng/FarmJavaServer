package com.jiudianlianxian.socket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * 
 * @Title: SendClientMessage
 * @Description: ���û�������Ϣ
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��9��27�� ����8:48:20
 *
 */
public class SendClientMessage implements Runnable{
	 private static List<AcceptClientMessage> threadList = new ArrayList<AcceptClientMessage>(); // �������������̼߳���  
	 private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(  
	            20); // �����Ϣ�Ķ���  
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
