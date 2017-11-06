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

	private static final int SERVER_PORT = 8944; // ����˶˿�
	Long seedState1 = 1L; // ����״̬----�̵�
	Long seedState2 = 2L; // ����״̬----����
	Long seedState3 = 3L; // ����״̬----��ֲ
	Long landState1 = 1L; // ����״̬----δ����
	Long landState2 = 2L; // ����״̬----δ��ֲ���ѿ���
	Long landState3 = 3L; // ����״̬----����ֲ����ʱ���س���һ�����Ӷ���
	Long landState4 = 4L; // ����״̬----��ʵ����

	private static List<String> userList = new CopyOnWriteArrayList<String>();
	private static List<DisposeAffair> threadList = new ArrayList<DisposeAffair>(); // �������������̼߳���
	private static BlockingQueue<String> sendMsgQueue = new ArrayBlockingQueue<String>(
			20); // �����Ϣ�Ķ���
	private static BlockingQueue<String> requestMsgQueue = new ArrayBlockingQueue<String>(
			20); // �����Ϣ�Ķ���

	static SocketServer server;


	/**
	 * ���
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
					// System.out.println("----ִ������ ");
				}
			};
			// long timestamp = 60000;
			long timestamp = 1000;
			timer.schedule(timerTask, date1, timestamp);

			server = new SocketServer(); // ���������
			server.load();

			// new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			// try {
			// server = new SocketServer(); // ���������
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
		// 1.��ѯfarm_seed����ȡseedStateΪ3�����Ӽ���

		/**
		 * 1.��ѯfarm_land,��ȡ״̬Ϊ3������id 2.����landId���ϲ�ѯland_seed����ȡseedId
		 * 3.����seedId���ϣ���ѯfarm_land����ȡ���ӵ���ֲʱ�������ʱ�� 4.������ȡ����������Ϣ���ж������Ƿ񳤳ɹ�ʵ��
		 * 5.�ǵĻ������ݴ�����id����ѯland_seed����ȡlandid 6.���h����ֱ�ӷ��ؿն��� 7.�ı��ȡ��������id��
		 */
		Long landState4 = 4L;
		// 1.��ѯfarm_land,��ȡ״̬Ϊ3������id
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
		// 2.����landId���ϲ�ѯland_seed����ȡseedId
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

			// 3.����seedId���ϣ���ѯfarm_seed����ȡ���ӵ���ֲʱ�������ʱ��
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

			// 4.������ȡ����������Ϣ���ж������Ƿ񳤳ɹ�ʵ��
			for (RipeMessage ripeMessage : ripeMessages) {

				Long plantTime = ripeMessage.getSeedPlantTime(); // ��ֲʱ��
				Long currentTime = new Date().getTime(); // ��ǰʱ��
				Long growthTime = ripeMessage.getSeedGrowthTime(); // ����ʱ��
				Long harvestTime = plantTime + growthTime; // �ջ�ʱ��

				if (currentTime >= harvestTime) {
					// 1.����seedId��ȡlandId
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
					// 2.����landId�ı�����״̬
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
	 * ����ͻ��˷�������Ϣ�߳���
	 */
	// class DisposeAffair implements Runnable {
	class DisposeAffair extends Thread {

		private Socket socket;

		private BufferedReader buff;

		private InputStream is;

		private Writer writer;

		private String userName; // ��Ա����

		/**
		 * ���캯��<br>
		 * ����ͻ��˵���Ϣ�����뵽���߳�Ա�б���
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
					// �۲���
					Observer disposeMsg = new DisposeMsg(sendMsgQueue);
					// ������������Ϣ���б��۲���
					RequestMsg requestMsg = new RequestMsg();
					// 
					requestMsg.addObserver(disposeMsg);
					// �۲���Ϣ���б仯
					requestMsg.addMsg(is,requestMsgQueue);
					
					Date date = new Date();
					System.out.println("����ʱ�䣺" + date.getHours()
							+ " ʱ   " + date.getMinutes() + " ��    "
							+ date.getSeconds() + " ��       "
							+ date.getTime());
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
				System.out.println("Form Cliect[port:" + socket.getPort()
						+ "] " + userName + "�Ͽ�����");
			}
		}

	
		/**
		 * ׼�����͵���Ϣ�������
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
		 * ������Ϣ
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
	 * ������ͻ��˷�����Ϣ���̣߳�ʹ���̴߳���ÿ���ͻ��˷�������Ϣ
	 * 
	 * @throws Exception
	 */
	public void load() throws Exception {
		new Thread(new PushMsgTask()).start(); // ������ͻ��˷�����Ϣ���߳�

		while (true) {
			// server���Խ�������Socket����������server��accept����������ʽ��
			Socket socket = this.accept();
			System.out.println("�ͻ������ӿ����߳�");
			/**
			 * ���ǵķ���˴���ͻ��˵�����������ͬ�����еģ� ÿ�ν��յ����Կͻ��˵����������
			 * ��Ҫ�ȸ���ǰ�Ŀͻ���ͨ����֮������ٴ�����һ���������� ���ڲ����Ƚ϶������»�����Ӱ���������ܣ�
			 * Ϊ�ˣ����ǿ��԰�����Ϊ���������첽������ͻ���ͨ�ŵķ�ʽ
			 */
			// ÿ���յ�һ��Socket�ͽ���һ���µ��߳���������
			// new Thread(new DisposeAffair(socket)).start();
			new DisposeAffair(socket);
		}
	}

	/**
	 * ����Ϣ������ȡ��Ϣ���ٷ��͸������ӵ����пͻ��˳�Ա
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
