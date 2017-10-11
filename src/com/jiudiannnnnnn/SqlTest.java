package com.jiudiannnnnnn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.Test;

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
import com.jiudianlianxian.util.SqlHelper;
import com.jiudianlianxian.utils.HttpCallBackListener;
import com.jiudianlianxian.utils.HttpUtil;

public class SqlTest {
	  JDBCService jdbcService  = new JDBCService();
	  String info = "------";
	  String jsonObject = "";

	
	public static void main(String[] args) {
		SqlTest sqlTest = new SqlTest();
		sqlTest.test12();
//		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
//		String aaa = sqlTest.sendGet(url);
//		System.out.println("aaa = " + aaa);
		
	}
	
	
	public void test12(){
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
		HttpUtil.requestData(url, new HttpCallBackListener() {
            @Override
            public void onFinish(String respose) {
                //处理请求
            	System.out.println("qiuchengong ");
            	System.out.println("ssssssssss" + HttpUtil.getJson());
            	
            }

            @Override
            public void onError(Exception e) {
                //处理异常
            	System.out.println("siqnsa;djfal;fskdjlaksdfj;alfskjd ");
            	System.out.println("sadfasdfa"+ HttpUtil.getJson());
            }
        });
		
	}
	
    /**

     * 发送get请求

     * @param url    路径

     * @return

     */

    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
//            // 设置通用的请求属性
//            connection.setRequestProperty("accept", "*/*");
//            connection.setRequestProperty("connection", "Keep-Alive");
//            connection.setRequestProperty("user-agent",
//                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
//            connection.connect();
            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
                
            }
            
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
	public void test11(){
		
		
		
		String code = "071GzoZb1Npuls0hWkXb11hIZb1GzoZ5";
		jdbcService.WeiXinlogin(code);
	}
	public void test10(){
		Date date = new Date();
		Timer timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				System.out.println("ganshenmene????");
			}
		};
		timer.schedule(timerTask, date);
		date.getTime();
		Timestamp timestamp = new Timestamp(date.getTime());
		
		// timestamp = 2017-10-11 14:30:03.809
		//  date = 1507703403809
//		timestamp = 2017-10-11 14:31:24.111
//		date = 1507703484111

		System.out.println( " timestamp = " + timestamp );
		System.out.println("date = " + date.getTime());
	}
	public void test09(){
		String uri = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
		StringBuilder builder = null;
		String json_access_token = null;
		try {
			URL url = new URL(uri);//如果有参数，在网址中携带参数
			URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			String line;
			builder = new StringBuilder();
			while((line=br.readLine())!=null){
			      builder.append(line);
			}
			br.close();
			isr.close();
			is.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        json_access_token = builder.toString();
        System.out.println(builder.toString());
        
        

        String aaa = "{'info':'login','data':{'username':'zhangsan','sex':'nan','age':'55'}}";
        String jsonObject = "";
        
        JSONObject jsonObject1;
        String info = null ;
        JSONObject data = null;
		try {
			jsonObject1 = new JSONObject(json_access_token);
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
        
        
        
	}
	public void test08(){
		String code = "";
		boolean b = jdbcService.login(code);
		System.out.println("bh = " + b);
	}
	public void test07(){
		Long userId = (long) 1;
		GetSeedMsgResultData getSeedMsgResultData = jdbcService.getSeedMsg(userId);
		GetSeedMsgResult getSeedMsgResult = new GetSeedMsgResult();
		if (getSeedMsgResultData != null) {
			getSeedMsgResult.setInfo(info);
			getSeedMsgResult.setGetSeedMsgResultData(getSeedMsgResultData);
			jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(getSeedMsgResult);
		    System.out.println("jsonObject  = " + jsonObject);
			
		}else {
			jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(getSeedMsgResult);
		    System.out.println("jsonObject  = " + jsonObject);
		}
	}
	public void test06(){
		Long userId = (long) 1;
		GetLandMsgResultData getLandMsgResultData = jdbcService.getLandMsg(userId);
		GetLandMsgResult getLandMsgResult = new GetLandMsgResult();
		if (getLandMsgResultData != null) {
			getLandMsgResult.setInfo(info);
			getLandMsgResult.setGetLandMsgResultData(getLandMsgResultData);
			jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(getLandMsgResultData);
		    System.out.println("jsonObject  = " + jsonObject);
			
		}else {
			jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(getLandMsgResultData);
		    System.out.println("jsonObject  = " + jsonObject);
			
		}
	}
	
	
	public void test01(){
		String userId = "1";
		String seedId = "5";
		String seedNumber = "5";
		//3-1.查询种子表，用户是否有seedId种子，有则获取其数量，并进行增加，没有则添加，
		String sqlIsSeed = "select * from farm_seed where seedId="+userId;
		ResultSet rsIsSeed = SqlHelper.executeQuery(sqlIsSeed);
		int priceIsSeed = 0 ;
		try {
			// 查询数据库,获取上述uid对应的数据
			
			while (rsIsSeed.next()) {
				priceIsSeed = (int) (Integer.parseInt(seedNumber) * (rsIsSeed.getDouble(5)));
			}
		} catch (SQLException e) {
			// 
			e.printStackTrace();
		}finally{
			SqlHelper.close(rsIsSeed, SqlHelper.getPs(), SqlHelper.getConnection());
		}
	}
	public static void test02(){
		JDBCService  jdbcService = new JDBCService();
		jdbcService.getBuySeedResultData("1", "55", "2");
		
	}
	public  void test03(){
		String userId = "1";
		String seedId = "5";
		String seedNumber = "5";
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
		    }
	}
	
	public  void test04(){
		//查询数据库种子列表所有数据
		SeedMsgAllResultData seedMsgAllResultData = jdbcService.getSeedMsgAll();
		SeedMsgAllResult seedMsgAllResult = new SeedMsgAllResult();
		if (seedMsgAllResultData != null) {
			seedMsgAllResult.setInfo(info);
			seedMsgAllResult.setSeedMsgAllResultData(seedMsgAllResultData);
			jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(seedMsgAllResult);
		    System.out.println("jsonObject  = " + jsonObject);
			
			
		}else {
			jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(seedMsgAllResult);
		    System.out.println("jsonObject  = " + jsonObject);
			
		}
	}
	public  void test05(){

		String code = "" ;
		if (jdbcService.login(code)) {
			Long userId = (long) 1;
			LoginResultData loginResponseData = jdbcService.loginResult(userId);
			LoginResult loginResponse = new LoginResult();
			loginResponse.setInfo(info);
			loginResponse.setLoginResponseData(loginResponseData);
		    jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(loginResponse);
		    System.out.println("jsonObject  = " + jsonObject);
			
		}else {
			LoginResult loginResponse = new LoginResult();
			loginResponse.setInfo(info);
			jsonObject = com.alibaba.fastjson.JSONObject.toJSONString(loginResponse);
		    System.out.println("jsonObject  = " + jsonObject);
			
		}
	}

}
