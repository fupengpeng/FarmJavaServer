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
 * @Description: 接受用户信息
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年9月27日 上午8:48:09
 *
 */
public class AcceptClientMessage implements Runnable {
    private static final String END_MARK = "quit"; // 断开连接标识  
    private static final String VIEW_USER = "viewuser"; // 查看连接客户端列表  
	
    private static List<String> userList = new CopyOnWriteArrayList<String>();  
    private static List<AcceptClientMessage> threadList = new ArrayList<AcceptClientMessage>(); // 服务器已启用线程集合  
    private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(  
            20); // 存放消息的队列  
	  
    private Socket socket;  

    private BufferedReader buff;  

    private Writer writer;  

    private String userName; // 成员名称  
    
    private JDBCService jdbcService  = new JDBCService();

    /** 
     * 构造函数<br> 
     * 处理客户端的消息，加入到在线成员列表中 
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
        pushMsg("【" + this.userName + "已连接成功】");  
        System.out.println("Form Cliect[port:" + socket.getPort() + "] "  
                + this.userName + "已进入房间可以开始游戏");  
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
        		
        		if ("login".equals(info)) {    //判断是什么请求
        			
        			
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
        			
				}else if("seedmsg".equals(info)){  //获取所有种子信息
					//查询数据库种子列表所有数据
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
					
					
				}else if("购买种子".equals(info)){
					//根据请求参数，修改用户的种子数量和金币数量    
					String userId = data.getString("用户id");
					String seedId = data.getString("种子id");
					String seedNumber = data.getString("种子数量");
					//根据id查找用户信息，更改其种子数量和金币数量
					//1.查询种子信息，计算价格
					//2.查询用户金币信息，金币是否购买种子，如不够直接返回提示用户
					//3.金币够，则修改用户的种子信息和金币信息
					//4.将新的用户数据返回给用户
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
					
					
					
					
				}else if("获取土地信息".equals(info)){
					//查询土地所有信息
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
					
					
					
				}else if("种植(含种子信息获取)".equals(info)){
					//修改种子数量，种子状态，土地状态，种子生长的过程监控。及时给客户端发送消息
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
					
				}else if("收获果实()".equals(info)){
					//修改土地状态，种子状态，生成果实修改果实数量
					
					
				}
        		
        		if (VIEW_USER.equals(msg)) { // 查看已连接客户端 
        		    sendMsg(onlineUsers());  
        		} else if (END_MARK.equals(msg)) { // 遇到退出标识时就结束让客户端退出  
        		    sendMsg(END_MARK);  
        		    break;  
        		} else {  
//                            pushMsg(String.format("%1$s说：%2$s", userName, msg)); // 用于给房间内用户发送信息  
        		}
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally { // 关闭资源，房间移除所有客户端成员  
            try {  
                writer.close();  
                buff.close();  
                socket.close();  
            } catch (Exception e) {  

            }  
            userList.remove(userName);  
            threadList.remove(this);  
            pushMsg("【" + userName + "断开连接】");  
            System.out.println("Form Cliect[port:" + socket.getPort() + "] "  
                    + userName + "断开连接");  
        }  
    }



    /** 
     * 准备发送的消息存入队列 
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
     * 发送消息 
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
     * 已连接用户列表 
     *  
     * @return 
     */  
    private String onlineUsers() {  
        StringBuffer sbf = new StringBuffer();  
        sbf.append("======== 已连接客户端列表(").append(userList.size())  
                .append(") ========\015\012");  
        for (int i = 0; i < userList.size(); i++) {  
            sbf.append("[" + userList.get(i) + "]\015\012");  
        }  
        sbf.append("===============================");  
        return sbf.toString();  
    } 
    
    

}
