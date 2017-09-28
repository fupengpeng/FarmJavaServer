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
	  
    private static final int SERVER_PORT = 8945; // ����˶˿�  

    private static List<AcceptClientMessage> threadList = new ArrayList<AcceptClientMessage>(); // �������������̼߳���  
    private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(  
            20); // �����Ϣ�Ķ���  
  
    public FarmJavaServer() throws Exception {  
        super(SERVER_PORT);  
    }  
  
    /** 
     * ������ͻ��˷�����Ϣ���̣߳�ʹ���̴߳���ÿ���ͻ��˷�������Ϣ 
     *  
     * @throws Exception 
     */  
    public void load() throws Exception {  
    	//���û�������Ϣ
        new Thread(new SendClientMessage(threadList, msgQueue)).start(); // ������ͻ��˷�����Ϣ���߳�  
  
        while (true) {  
            // server���Խ�������Socket����������server��accept����������ʽ��  
            Socket socket = this.accept();  
            /** 
             * ���ǵķ���˴���ͻ��˵�����������ͬ�����еģ� ÿ�ν��յ����Կͻ��˵���������� 
             * ��Ҫ�ȸ���ǰ�Ŀͻ���ͨ����֮������ٴ�����һ���������� ���ڲ����Ƚ϶������»�����Ӱ���������ܣ� 
             * Ϊ�ˣ����ǿ��԰�����Ϊ���������첽������ͻ���ͨ�ŵķ�ʽ 
             */  
            // ÿ���յ�һ��Socket�ͽ���һ���µ��߳���������  �����û����͵���Ϣ�������д���
            new Thread(new AcceptClientMessage(socket,threadList,msgQueue)).start();  
        }  
    }  
 
  
    /** 
     * ��� 
     *  
     * @param args 
     */  
    public static void main(String[] args) {  
        try {  
        	FarmJavaServer server = new FarmJavaServer(); // ���������  
            server.load();  
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}
