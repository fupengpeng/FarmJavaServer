package com.jiudianlianxian.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;













import org.json.JSONException;
import org.json.JSONObject;

import com.jiudianlianxian.bean.BuySeedResult;
import com.jiudianlianxian.bean.GetLandMsgResult;
import com.jiudianlianxian.bean.GetSeedMsgResult;
import com.jiudianlianxian.bean.LoginResult;
import com.jiudianlianxian.bean.SeedMsgAllResult;
import com.jiudianlianxian.data.BuySeedResultData;
import com.jiudianlianxian.data.GetLandMsgResultData;
import com.jiudianlianxian.data.GetSeedMsgResultData;
import com.jiudianlianxian.data.LoginResultData;
import com.jiudianlianxian.data.SeedMsgAllResultData;
import com.jiudianlianxian.service.JDBCService;


/**
 * 
 * @Title: AcceptClientMessage
 * @Description: �����û���Ϣ
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��9��27�� ����8:48:09
 *
 */
public class AcceptClientMessage implements Runnable {
    private static final String END_MARK = "quit"; // �Ͽ����ӱ�ʶ  
    private static final String VIEW_USER = "viewuser"; // �鿴���ӿͻ����б�  
	
    private static List<String> userList = new CopyOnWriteArrayList<String>();  
    private static List<AcceptClientMessage> threadList = new ArrayList<AcceptClientMessage>(); // �������������̼߳���  
    private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(  
            20); // �����Ϣ�Ķ���  
	  
    private Socket socket;  

    private BufferedReader buff;  

    private Writer writer;  

    private String userName; // ��Ա����  
    
    private JDBCService jdbcService  = new JDBCService();

    /** 
     * ���캯��<br> 
     * ����ͻ��˵���Ϣ�����뵽���߳�Ա�б��� 
     *  
     * @throws Exception 
     */  
    public AcceptClientMessage(Socket socket,
    		List<AcceptClientMessage> threadList,
    		BlockingQueue<String> msgQueue) {  
        this.socket = socket;  
        this.threadList = threadList;
        this.msgQueue = msgQueue;
        this.userName = String.valueOf(socket.getPort());  
        try {  
            this.buff = new BufferedReader(new InputStreamReader(  
                    socket.getInputStream(), "UTF-8"));  
            this.writer = new OutputStreamWriter(socket.getOutputStream(),  
                    "UTF-8");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        userList.add(this.userName);  
        threadList.add(this);  
        pushMsg("��" + this.userName + "�����ӳɹ���");  
        System.out.println("Form Cliect[port:" + socket.getPort() + "] "  
                + this.userName + "�ѽ��뷿����Կ�ʼ��Ϸ");  
    }  

    @Override  
    public void run() {  
        try {  
            while (true) {  
                String msg = buff.readLine(); 
                String aaa = "{'info':'login','data':{'username':'zhangsan','sex':'nan','age':'55'}}";
                String jsonObject = "";
                
                JSONObject jsonObject1;
                String info = null ;
                JSONObject data = null;
        		try {
        			jsonObject1 = new JSONObject(msg);
        			info = jsonObject1.getString("info");
        			data = jsonObject1.getJSONObject("data");
        			System.out.println("data  = " + data.toString());
        			System.out.println("username = " + data.getString("username"));
        			System.out.println("sex = " + data.getString("sex"));
        			System.out.println("age = " + data.getString("age"));
        			
        		} catch (JSONException e) {
        			// 
        			e.printStackTrace();
        		}
        		
        		if ("login".equals(info)) {    //�ж���ʲô����
        			
        			
        			if (jdbcService.login()) {
        				Long userId = (long) 0;
						LoginResultData loginResponseData = jdbcService.loginResult(userId);
						LoginResult loginResponse = new LoginResult();
						loginResponse.setInfo(info);
						loginResponse.setLoginResponseData(loginResponseData);
					    jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(loginResponse);
					    System.out.println("jsonObject  = " + jsonObject);
    					pushMsg(jsonObject);
					}else {
						LoginResult loginResponse = new LoginResult();
						loginResponse.setInfo(info);
						jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(loginResponse);
					    System.out.println("jsonObject  = " + jsonObject);
    					pushMsg(jsonObject);
					}
        			
				}else if("seedmsg".equals(info)){  //��ȡ����������Ϣ
					//��ѯ���ݿ������б���������
					SeedMsgAllResultData seedMsgAllResultData = jdbcService.getSeedMsgAll();
					SeedMsgAllResult seedMsgAllResult = new SeedMsgAllResult();
					if (seedMsgAllResultData != null) {
						seedMsgAllResult.setInfo(info);
						seedMsgAllResult.setSeedMsgAllResultData(seedMsgAllResultData);
						jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(seedMsgAllResult);
					    System.out.println("jsonObject  = " + jsonObject);
    					pushMsg(jsonObject);
						
					}else {
						jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(seedMsgAllResult);
					    System.out.println("jsonObject  = " + jsonObject);
    					pushMsg(jsonObject);
					}
					
					
				}else if("��������".equals(info)){
					//��������������޸��û������������ͽ������    
					String userId = data.getString("�û�id");
					String seedId = data.getString("����id");
					String seedNumber = data.getString("��������");
					//����id�����û���Ϣ�����������������ͽ������
					//1.��ѯ������Ϣ������۸�
					//2.��ѯ�û������Ϣ������Ƿ������ӣ��粻��ֱ�ӷ�����ʾ�û�
					//3.��ҹ������޸��û���������Ϣ�ͽ����Ϣ
					//4.���µ��û����ݷ��ظ��û�
					BuySeedResultData buySeedResultData = jdbcService.getBuySeedResultData(userId, seedId, seedNumber);
					BuySeedResult buySeedResult = new BuySeedResult();
					if (buySeedResultData != null) {
						buySeedResult.setInfo(info);
						buySeedResult.setBuySeedResultData(buySeedResultData);
						jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(buySeedResultData);
					    System.out.println("jsonObject  = " + jsonObject);
    					pushMsg(jsonObject);
					}else {
						jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(buySeedResultData);
					    System.out.println("jsonObject  = " + jsonObject);
    					pushMsg(jsonObject);
					}
					
					
					
					
				}else if("��ȡ������Ϣ".equals(info)){
					//��ѯ����������Ϣ
					Long userId = (long) 1;
					GetLandMsgResultData getLandMsgResultData = jdbcService.getLandMsg(userId);
					GetLandMsgResult getLandMsgResult = new GetLandMsgResult();
					if (getLandMsgResultData != null) {
						getLandMsgResult.setInfo(info);
						getLandMsgResult.setGetLandMsgResultData(getLandMsgResultData);
						jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(getLandMsgResult);
					    System.out.println("jsonObject  = " + jsonObject);
    					pushMsg(jsonObject);
					}else {
						jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(getLandMsgResult);
					    System.out.println("jsonObject  = " + jsonObject);
    					pushMsg(jsonObject);
					}
					
					
					
				}else if("��ֲ(��������Ϣ��ȡ)".equals(info)){
					//�޸���������������״̬������״̬�����������Ĺ��̼�ء���ʱ���ͻ��˷�����Ϣ
					Long userId = (long) 1;
					GetSeedMsgResultData getSeedMsgResultData = jdbcService.getSeedMsg(userId);
					GetSeedMsgResult getSeedMsgResult = new GetSeedMsgResult();
					if (getSeedMsgResultData != null) {
						getSeedMsgResult.setInfo(info);
						getSeedMsgResult.setGetSeedMsgResultData(getSeedMsgResultData);
						jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(getSeedMsgResult);
					    System.out.println("jsonObject  = " + jsonObject);
    					pushMsg(jsonObject);
					}else {
						jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(getSeedMsgResult);
					    System.out.println("jsonObject  = " + jsonObject);
    					pushMsg(jsonObject);
					}
					
				}else if("�ջ��ʵ()".equals(info)){
					//�޸�����״̬������״̬�����ɹ�ʵ�޸Ĺ�ʵ����
					
					
				}
        		
        		if (VIEW_USER.equals(msg)) { // �鿴�����ӿͻ��� 
        		    sendMsg(onlineUsers());  
        		} else if (END_MARK.equals(msg)) { // �����˳���ʶʱ�ͽ����ÿͻ����˳�  
        		    sendMsg(END_MARK);  
        		    break;  
        		} else {  
//                            pushMsg(String.format("%1$s˵��%2$s", userName, msg)); // ���ڸ��������û�������Ϣ  
        		}
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally { // �ر���Դ�������Ƴ����пͻ��˳�Ա  
            try {  
                writer.close();  
                buff.close();  
                socket.close();  
            } catch (Exception e) {  

            }  
            userList.remove(userName);  
            threadList.remove(this);  
            pushMsg("��" + userName + "�Ͽ����ӡ�");  
            System.out.println("Form Cliect[port:" + socket.getPort() + "] "  
                    + userName + "�Ͽ�����");  
        }  
    }



    /** 
     * ׼�����͵���Ϣ������� 
     *  
     * @param msg 
     */  
    private void pushMsg(String msg) {  
        try {  
            msgQueue.put(msg);  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
    }  

    /** 
     * ������Ϣ 
     *  
     * @param msg 
     */  
    public void sendMsg(String msg) {  
        try {  
            writer.write(msg);  
            writer.write("\015\012");  
            writer.flush();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  

    /** 
     * �������û��б� 
     *  
     * @return 
     */  
    private String onlineUsers() {  
        StringBuffer sbf = new StringBuffer();  
        sbf.append("======== �����ӿͻ����б�(").append(userList.size())  
                .append(") ========\015\012");  
        for (int i = 0; i < userList.size(); i++) {  
            sbf.append("[" + userList.get(i) + "]\015\012");  
        }  
        sbf.append("===============================");  
        return sbf.toString();  
    } 
    
    

}
