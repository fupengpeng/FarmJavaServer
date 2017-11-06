package com.jiudianlianxian.socket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jiudianlianxian.data.RipeMessage;
import com.jiudianlianxian.util.JDBCUtil;

public class SocketServer extends ServerSocket {

	private static final int SERVER_PORT = 8944; // 服务端端口
	Long seedState1 = 1L; // 种子状态----商店
	Long seedState2 = 2L; // 种子状态----背包
	Long seedState3 = 3L; // 种子状态----种植
	Long landState1 = 1L; // 土地状态----未开垦
	Long landState2 = 2L; // 土地状态----未种植，已开垦
	Long landState3 = 3L; // 土地状态----已种植，此时土地持有一个种子对象
	Long landState4 = 4L; // 土地状态----果实成熟

	private static List<String> userList = new CopyOnWriteArrayList<String>();
	private static List<DisposeAffair> threadList = new ArrayList<DisposeAffair>(); // 服务器已启用线程集合
	private static BlockingQueue<String> sendMsgQueue = new ArrayBlockingQueue<String>(
			20); // 存放消息的队列
	private static BlockingQueue<String> requestMsgQueue = new ArrayBlockingQueue<String>(
			20); // 存放消息的队列

	static SocketServer server;


	/**
	 * 入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Date date1 = new Date();
			Timer timer = new Timer();
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					furitRipe();
					// System.out.println("----执行任务 ");
				}
			};
			// long timestamp = 60000;
			long timestamp = 1000;
			timer.schedule(timerTask, date1, timestamp);

			server = new SocketServer(); // 启动服务端
			server.load();

			// new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			// try {
			// server = new SocketServer(); // 启动服务端
			// server.load();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
			// }).start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void furitRipe() {
		// 1.查询farm_seed表，获取seedState为3的种子集合

		/**
		 * 1.查询farm_land,获取状态为3的种子id 2.遍历landId集合查询land_seed表，获取seedId
		 * 3.遍历seedId集合，查询farm_land表，获取种子的种植时间和生长时间 4.遍历获取到的种子信息，判断种子是否长成果实，
		 * 5.是的话，根据此种子id，查询land_seed表，获取landid 6.否的h话，直接返回空对象 7.改变获取到的土地id的
		 */
		Long landState4 = 4L;
		// 1.查询farm_land,获取状态为3的种子id
		List<Long> landIds = new ArrayList<Long>();
		String sqlLandIds = "select * from farm_land where landState=3";
		ResultSet rsLandIds = JDBCUtil.executeQuery(sqlLandIds);
		try {
			while (rsLandIds.next()) {
				landIds.add(rsLandIds.getLong(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rsLandIds, JDBCUtil.getPs(),
					JDBCUtil.getConnection());
		}
		// 2.遍历landId集合查询land_seed表，获取seedId
		List<Long> seedIds = new ArrayList<Long>();
		for (Long landId : landIds) {
			String sqlSeedIds = "select * from land_seed where landId="
					+ landId;
			ResultSet rsSeedIds = JDBCUtil.executeQuery(sqlSeedIds);
			try {
				while (rsSeedIds.next()) {
					seedIds.add(rsSeedIds.getLong(2));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rsSeedIds, JDBCUtil.getPs(),
						JDBCUtil.getConnection());
			}

			// 3.遍历seedId集合，查询farm_seed表，获取种子的种植时间和生长时间
			List<RipeMessage> ripeMessages = new ArrayList<RipeMessage>();
			for (Long seedId : seedIds) {
				String sql = "select * from farm_seed where seedId=" + seedId;
				ResultSet rs = JDBCUtil.executeQuery(sql);
				try {
					while (rs.next()) {
						RipeMessage ripeMessage = new RipeMessage();
						ripeMessage.setSeedId(rs.getLong(1));
						ripeMessage.setSeedGrowthTime(rs.getLong(4));
						ripeMessage.setSeedPlantTime(rs.getLong(14));
						ripeMessages.add(ripeMessage);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					JDBCUtil.close(rs, JDBCUtil.getPs(),
							JDBCUtil.getConnection());
				}
			}

			// 4.遍历获取到的种子信息，判断种子是否长成果实，
			for (RipeMessage ripeMessage : ripeMessages) {

				Long plantTime = ripeMessage.getSeedPlantTime(); // 种植时间
				Long currentTime = new Date().getTime(); // 当前时间
				Long growthTime = ripeMessage.getSeedGrowthTime(); // 生长时间
				Long harvestTime = plantTime + growthTime; // 收获时间

				if (currentTime >= harvestTime) {
					// 1.根据seedId获取landId
					Long landIdd = null;
					String sql1 = "select * from land_seed where seedId="
							+ ripeMessage.getSeedId();
					ResultSet rs1 = JDBCUtil.executeQuery(sql1);
					try {
						while (rs1.next()) {
							landIdd = rs1.getLong(1);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						JDBCUtil.close(rs1, JDBCUtil.getPs(),
								JDBCUtil.getConnection());
					}
					// 2.根据landId改变土地状态
					String sql3 = "UPDATE farm_land SET " + "landState="
							+ landState4 + " WHERE landId=" + landIdd + ";";
					System.out.println("----sql-------------3 = " + sql3);
					try {
						JDBCUtil.executeUpdate(sql3);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					// System.out.println("-=--------------------------");
				}
			}

		}

	}

	public SocketServer() throws Exception {
		super(SERVER_PORT);
	}


	/**
	 * 处理客户端发来的消息线程类
	 */
	// class DisposeAffair implements Runnable {
	class DisposeAffair extends Thread {

		private Socket socket;

		private BufferedReader buff;

		private InputStream is;

		private Writer writer;

		private String userName; // 成员名称

		/**
		 * 构造函数<br>
		 * 处理客户端的消息，加入到在线成员列表中
		 * 
		 * @throws Exception
		 */
		public DisposeAffair(Socket socket) {
			this.start();
			this.socket = socket;
			this.userName = String.valueOf(socket.getPort());
			try {
				this.is = socket.getInputStream();
				this.writer = new OutputStreamWriter(socket.getOutputStream(),
						"UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}

			userList.add(this.userName);
			threadList.add(this);
		}

	
		
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			try {
				boolean issocket = socket.isConnected();
				while (issocket) {
					socket.sendUrgentData(0);
					// 观察者
					Observer disposeMsg = new DisposeMsg(sendMsgQueue);
					// 定义出请求的消息队列被观察者
					RequestMsg requestMsg = new RequestMsg();
					// 
					requestMsg.addObserver(disposeMsg);
					// 观察消息队列变化
					requestMsg.addMsg(is,requestMsgQueue);
					
					Date date = new Date();
					System.out.println("请求时间：" + date.getHours()
							+ " 时   " + date.getMinutes() + " 分    "
							+ date.getSeconds() + " 秒       "
							+ date.getTime());
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
				System.out.println("Form Cliect[port:" + socket.getPort()
						+ "] " + userName + "断开连接");
			}
		}

	
		/**
		 * 准备发送的消息存入队列
		 * 
		 * @param msg
		 */
		private void pushMsg(String msg) {
			try {
				sendMsgQueue.put(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 发送消息
		 * 
		 * @param msg
		 */
		private void sendMsg(String msg) {
			try {
				writer.write(msg);
				writer.write("\015\012");
				writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}




	
	}

	/**
	 * 启动向客户端发送消息的线程，使用线程处理每个客户端发来的消息
	 * 
	 * @throws Exception
	 */
	public void load() throws Exception {
		new Thread(new PushMsgTask()).start(); // 开启向客户端发送消息的线程

		while (true) {
			// server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
			Socket socket = this.accept();
			System.out.println("客户端连接开启线程");
			/**
			 * 我们的服务端处理客户端的连接请求是同步进行的， 每次接收到来自客户端的连接请求后，
			 * 都要先跟当前的客户端通信完之后才能再处理下一个连接请求。 这在并发比较多的情况下会严重影响程序的性能，
			 * 为此，我们可以把它改为如下这种异步处理与客户端通信的方式
			 */
			// 每接收到一个Socket就建立一个新的线程来处理它
			// new Thread(new DisposeAffair(socket)).start();
			new DisposeAffair(socket);
		}
	}

	/**
	 * 从消息队列中取消息，再发送给已连接的所有客户端成员
	 */
	class PushMsgTask implements Runnable {

		@Override
		public void run() {
			while (true) {
				String msg = null;
				try {
					msg = sendMsgQueue.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (msg != null) {
					for (DisposeAffair thread : threadList) {
						System.out.println("sssssssssssss");
						thread.sendMsg(msg);
						
					}
				}
			}
		}

	}

}
