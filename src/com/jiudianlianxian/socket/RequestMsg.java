package com.jiudianlianxian.socket;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;


/**
 * 
 * @Title: RequestMsg
 * @Description: 消息队列被观察者
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年11月6日 下午4:08:34
 *
 */
public class RequestMsg extends Observable {
	
	//添加消息
	public void addMsg(InputStream is,BlockingQueue<String> requestMsgQueue) {
		
		try {
			byte[] byteBuff = new byte[1024 * 8];
			int len;
			// 读取完流中所有的消息且消息中含有“@jdlx”时保存至消息队列，
			while ((len = is.read(byteBuff)) != -1) {
				
				Date date = new Date();
				System.out.println(new String(Arrays.copyOf(byteBuff, len)) + "----请求时间：" + date.getHours()
						+ " 时   " + date.getMinutes() + " 分    "
						+ date.getSeconds() + " 秒       "
						+ date.getTime());
				requestMsgQueue
						.put(new String(Arrays.copyOf(byteBuff, len)));
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// 通知所有的观察者
		super.setChanged();
		super.notifyObservers(requestMsgQueue);
	}
	

}
