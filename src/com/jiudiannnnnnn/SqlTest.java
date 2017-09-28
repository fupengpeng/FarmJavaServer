package com.jiudiannnnnnn;

import java.sql.ResultSet;
import java.sql.SQLException;

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

public class SqlTest {
	  JDBCService jdbcService  = new JDBCService();
	  String info = "------";
	  String jsonObject = "";

	
	public static void main(String[] args) {
		SqlTest sqlTest = new SqlTest();
		sqlTest.test07();
		
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
		//3-1.��ѯ���ӱ��û��Ƿ���seedId���ӣ������ȡ�����������������ӣ�û������ӣ�
		String sqlIsSeed = "select * from farm_seed where seedId="+userId;
		ResultSet rsIsSeed = SqlHelper.executeQuery(sqlIsSeed);
		int priceIsSeed = 0 ;
		try {
			// ��ѯ���ݿ�,��ȡ����uid��Ӧ������
			
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
		    }
	}
	
	public  void test04(){
		//��ѯ���ݿ������б���������
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

		if (jdbcService.login()) {
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
