package com.jiudianlianxian.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jiudianlianxian.data.BuySeedResultData;
import com.jiudianlianxian.data.GetLandMsgResultData;
import com.jiudianlianxian.data.GetSeedMsgResultData;
import com.jiudianlianxian.data.LoginResultData;
import com.jiudianlianxian.data.SeedMsgAllResultData;
import com.jiudianlianxian.domain.Land;
import com.jiudianlianxian.domain.Seed;
import com.jiudianlianxian.domain.User;
import com.jiudianlianxian.util.SqlHelper;

/**
 * 
 * Title: UsersService Description: User�����Ĕ�����̎��� Company: �����ŵ�������Ϣ�������޹�˾
 * ProjectName: UsersManager
 * 
 * @author fupengpeng
 * @date 2017��7��19�� ����9:17:44
 *
 */
public class JDBCService {

	public void plant(Long userId, Long seedId, Long landId) {
		// 1.�жϴ���������Ƿ���
		// 1-1.����seedId��ѯ���ݿ⣬��ȡ��id����������
		int seedNumber = 0; // ����seedId����������
		String sql1 = "select * from farm_seed where userId=" + userId;
		System.out.println("sql1 = " + sql1);
		ResultSet rs1 = SqlHelper.executeQuery(sql1);
		try {
			// ��ѯ���ݿ�,��ȡ����uid��Ӧ������

			while (rs1.next()) {
				seedNumber = rs1.getInt(12);

				System.out.println("����seedId����������    seedNumber = "
						+ seedNumber);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs1, SqlHelper.getPs(), SqlHelper.getConnection());
		}

		// 1-2.��ȡ����id���������������жϣ���֤�Ƿ��д�����
		if (seedNumber > 0) { // ���д����ӣ�������ֲ
			// 2.�жϴ��������״̬
			// 2-1.����landId��ѯ���ݿ⣬��ȡ��id������״̬
			String landState = null; // ����landId������״̬
			String sql2 = "select * from farm_land where landId=" + landId;
			System.out.println("sql2 = " + sql2);
			ResultSet rs2 = SqlHelper.executeQuery(sql2);
			try {
				// ��ѯ���ݿ�,��ȡ����uid��Ӧ������

				while (rs2.next()) {
					landState = rs2.getString(3);

					System.out.println("����landId������״̬    landState = "
							+ landState);
				}
			} catch (SQLException e) {
				//
				e.printStackTrace();
			} finally {
				SqlHelper.close(rs2, SqlHelper.getPs(),
						SqlHelper.getConnection());
			}
			// 2-2.�ж�����״̬�Ƿ������ֲ
			if ("2".equals(landState)) {
				// ����״̬Ϊ2������ֲ
				// 3.�ı�����״̬
				// 4.�ı䴫��seedId��������
				// 5.��ȡseedId���������ƣ�����seedId���������ơ�seedState��userId��ѯfarm_seed���Ƿ�����ֲ�������ϵĸ�����
				// 6.����ı�������������Ӹ����ӣ�����������ӵ�landId����,ע�⣬��ʱ����һ�����͵����ӿ��������ڶ�����أ������ӳ���һ�����ؼ���
				// 5.���������Ƿ񳤳ɹ�ʵ����ʵʱ���ͻ��˷��������������
			} else {
				// ����״̬����2��������ֲ��������ʾ��Ϣ

			}

		} else { // û�д������ˣ���Ҫ����

		}

	}

	/**
	 * 
	 * @Description: �򿪱���
	 * @return
	 */
	public List<Seed> openKnapsack() {
		Long userId = (long) 1;
		// 1.
		List<Seed> seeds = new ArrayList<Seed>();
		String sql1 = "select * from farm_seed where userId=" + userId
				+ " and seedState='" + 2 + "' ";
		System.out.println("sql1 = " + sql1);
		ResultSet rs1 = SqlHelper.executeQuery(sql1);
		try {
			// ��ѯ���ݿ�,��ȡ����uid��Ӧ������

			while (rs1.next()) {
				Seed seed = new Seed();
				seed.setSeedId(rs1.getLong(1));
				seed.setSeedName(rs1.getString(2));
				seed.setSeedState(rs1.getString(3));
				seed.setSeedGrowthTime(rs1.getLong(4));
				seed.setSeedBuyPrice(rs1.getLong(5));
				seed.setSeedSellingPrice(rs1.getLong(6));
				seed.setSeedExperience(rs1.getLong(7));
				seed.setSeedYield(rs1.getLong(8));
				seed.setSeedFruitSellingPrice(rs1.getLong(9));
				seed.setSeedType(rs1.getLong(10));
				seed.setSeedImage(rs1.getString(11));
				seed.setSeedNumber(rs1.getInt(12));

				seeds.add(seed);
				System.out.println("��������������    lands = " + seeds);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs1, SqlHelper.getPs(), SqlHelper.getConnection());
		}
		return seeds;
	}

	public GetSeedMsgResultData getSeedMsg(Long userId) {
		GetSeedMsgResultData getSeedMsgResultData = new GetSeedMsgResultData();
		// 1
		String sql = "select * from user_seed where userId=" + userId;
		List<Long> seedIds = new ArrayList<Long>();
		System.out.println("sql = " + sql);
		ResultSet rs = SqlHelper.executeQuery(sql);
		try {
			// ��ѯ���ݿ�,��ȡ����uid��Ӧ������

			while (rs.next()) {
				seedIds.add(rs.getLong(3));

				// System.out.println("��������������    lands = " + seedIds.get(1));
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getConnection());
		}
		System.out.println("seedIds = " + seedIds);
		List<Seed> seeds = new ArrayList<Seed>();
		for (Long long1 : seedIds) {
			// 1.
			String sql1 = "select * from farm_seed where seedId=" + long1;
			System.out.println("sql1 = " + sql1);
			ResultSet rs1 = SqlHelper.executeQuery(sql1);
			try {
				// ��ѯ���ݿ�,��ȡ����uid��Ӧ������

				while (rs1.next()) {
					Seed seed = new Seed();
					seed.setSeedId(long1);
					seed.setSeedName(rs1.getString(2));
					seed.setSeedState(rs1.getString(3));
					seed.setSeedGrowthTime(rs1.getLong(4));
					seed.setSeedSellingPrice(rs1.getLong(5));
					seed.setSeedImage(rs1.getString(6));
					seed.setSeedNumber(rs1.getInt(7));

					seeds.add(seed);
					System.out.println("��������������    lands = " + seeds);
				}
			} catch (SQLException e) {
				//
				e.printStackTrace();
			} finally {
				SqlHelper.close(rs1, SqlHelper.getPs(),
						SqlHelper.getConnection());
			}

		}
		String seedname = "";
		for (Seed seed : seeds) { // ������ѯ�����û������б�����
			if (seed.getSeedName().equals(seedname)
					&& seed.getSeedState().equals("3")) { // �ж��û������б����ֵ������е������Ƿ�������Ҫ��ֲ������
				// �� ��ı�����
				// �޸��û�����������
				String sql2 = "UPDATE farm_seed SET " + "seedNumber='"
						+ (seed.getSeedNumber() + 1) + "' " + "WHERE seedId="
						+ seed.getSeedId() + ";";
				System.out
						.println("�����ڵ�ǰ���ӵ����� = " + (seed.getSeedNumber() + 1));
				System.out.println("sql2 = " + sql2);
				try {
					SqlHelper.executeUpdate(sql2);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				// �� ������״̬Ϊ3��������Ϊ�����seedName�����ӣ�����Ϊ1
				// ���û����������
				String sql3 = "INSERT INTO farm_seed("
						+ "seedName,seedState,seedGrowthTime,seedSellingPrice,seedImage,"
						+ "seedNumber)" + "VALUES('" + seed.getSeedName()
						+ "','" + 3 + "'," + seed.getSeedGrowthTime() + ","
						+ seed.getSeedSellingPrice() + ",'"
						+ seed.getSeedImage() + "'," + 1 + ") ";
				System.out.println("sql3====" + sql3);
				String sql4 = "select last_insert_id()";
				System.out.println("sql11 = " + sql4);

				long lastid = 0;
				try {
					lastid = SqlHelper.executeUpdateGetId(sql3, sql4);
					System.out.println("�ող������ݵ�id ---  11 = " + lastid);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (seed.getSeedName().equals(seedname)
					&& seed.getSeedState().equals("2")) { // �ж��û�����״̬��2������ʱ�����seedname������ʱ�ı�������
				// �� ��ı�����
				// �޸��û�����������
				String sql2 = "UPDATE farm_seed SET " + "seedNumber='"
						+ (seed.getSeedNumber() - 1) + "' " + "WHERE seedId="
						+ seed.getSeedId() + ";";
				System.out
						.println("�����ڵ�ǰ���ӵ����� = " + (seed.getSeedNumber() + 1));
				System.out.println("sql2 = " + sql2);
				try {
					SqlHelper.executeUpdate(sql2);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}
		getSeedMsgResultData.setSeeds(seeds);
		return getSeedMsgResultData;
	}

	/**
	 * 
	 * @Description: �����û�id��ȡ�û�������Ϣ
	 * @param userId
	 * @return
	 */
	public GetLandMsgResultData getLandMsg(Long userId) {
		GetLandMsgResultData getLandMsgResultData = new GetLandMsgResultData();
		boolean b = true;
		// 1.��ѯ������Ϣ������۸�
		String sql = "select * from farm_land where userId=" + userId;
		List<Land> lands = new ArrayList<Land>();
		System.out.println("sql1 = " + sql);
		ResultSet rs = SqlHelper.executeQuery(sql);
		try {
			// ��ѯ���ݿ�,��ȡ����uid��Ӧ������

			while (rs.next()) {
				Land land = new Land();
				land.setLandId(rs.getLong(1));
				land.setLandName(rs.getString(2));
				land.setLandState(rs.getString(3));
				lands.add(land);
				System.out.println("��������������    lands = " + lands);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getConnection());
		}

		getLandMsgResultData.setLands(lands);
		return getLandMsgResultData;
	}

	/**
	 * 
	 * @Description: �������ӣ������ع�����û�����
	 * @param userId
	 * @param seedId
	 * @param seedNumber
	 * @return
	 */
	public BuySeedResultData getBuySeedResultData(String userId, String seedId,
			String seedNumber) {
		BuySeedResultData buySeedResultData = new BuySeedResultData();

		boolean b = true;
		// 1.��ѯ������Ϣ�����㹺��������

		int seedTotalPrices = 0; // �����ܼ�
		Seed seed = new Seed(); // ��Ҫ�����������Ϣ

		// 1-1.��ѯ���ӵ��ۣ����㹺������������������
		String sql1 = "select * from farm_seed where seedId=" + seedId;
		System.out.println("sql1 = " + sql1);
		ResultSet rs1 = SqlHelper.executeQuery(sql1);
		try {
			while (rs1.next()) {
				seedTotalPrices = (int) (Integer.parseInt(seedNumber) * (rs1
						.getInt(5)));
				seed.setSeedName(rs1.getString(2));
				seed.setSeedGrowthTime(rs1.getLong(4));
				seed.setSeedBuyPrice(rs1.getLong(5));
				seed.setSeedSellingPrice(rs1.getLong(6));
				seed.setSeedExperience(rs1.getLong(7));
				seed.setSeedYield(rs1.getLong(8));
				seed.setSeedFruitSellingPrice(rs1.getLong(9));
				seed.setSeedType(rs1.getLong(10));
				seed.setSeedImage(rs1.getString(11));
				System.out.println("��������������    seedTotalPrices = "
						+ seedTotalPrices);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs1, SqlHelper.getPs(), SqlHelper.getConnection());
		}

		// 2.��ѯ�û������Ϣ��
		int userGold = 0; // �û����еĽ������
		String sql2 = "select * from farm_user where userId=" + userId;
		System.out.println("sql2 = " + sql2);
		ResultSet rs2 = SqlHelper.executeQuery(sql2);
		try {
			while (rs2.next()) {
				userGold = rs2.getInt(4);
				System.out.println("�û������    price2 = " + userGold);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs2, SqlHelper.getPs(), SqlHelper.getConnection());
		}

		// 3. �ж��û�ʣ�����Ƿ������ӣ��粻��ֱ�ӷ�����ʾ�û�
		if (userGold > seedTotalPrices) {
			// 3.��ҹ������޸��û���������Ϣ�ͽ����Ϣ
			// 3-1.�޸��û��Ľ������
			String sql4 = "UPDATE farm_user SET " + "userGold='"
					+ (userGold - seedTotalPrices) + "' " + "WHERE userId='"
					+ userId + "';";
			System.out.println("�����û����ʣ���  price2-price1 = "
					+ (userGold - seedTotalPrices));
			System.out.println("sql4 = " + sql4);
			try {
				SqlHelper.executeUpdate(sql4);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 3-2.��ѯ���ӱ��û��Ƿ��������ΪseedName������
			Long seedIdStateTwo = null; // �ѹ��������id
			Long seedNumberStateTwo = null; // �ѹ������������
			String sql3 = "select * from farm_seed where userId=" + userId
					+ " and seedState='" + 2 + "' and seedName='"
					+ seed.getSeedName() + "'";
			System.out.println("sql3 = " + sql3);
			b = false;
			ResultSet rs3 = SqlHelper.executeQuery(sql3);
			try {
				while (rs3.next()) {
					b = true;
					rs3.getInt(3);
					seedIdStateTwo = rs3.getLong(1);
					seedNumberStateTwo = rs3.getLong(12);
					System.out.println("��ѯ���û��Ƿ��и����ӣ�rs3.getInt(3) = "
							+ rs3.getInt(3) + "seedIdStateTwo = "
							+ seedIdStateTwo + "seedNumberStateTwo = "
							+ seedNumberStateTwo);
				}
			} catch (SQLException e) {
				//
				b = false;
				e.printStackTrace();
			} finally {
				SqlHelper.close(rs3, SqlHelper.getPs(),
						SqlHelper.getConnection());
			}

			if (b) { // 3-2-1.�û����������ΪseedName������
				// �ı��û����еĸ���������

				String sql5 = "UPDATE farm_seed SET " + "seedNumber="
						+ (seedNumberStateTwo + seedNumber) + " WHERE seedId="
						+ seedIdStateTwo + ";";
				System.out.println("sql5 = " + sql5);
				try {
					SqlHelper.executeUpdate(sql5);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// ���û����ع���ɹ�����Ϣ������
				// ���û���������
				resultBuySeedResult(userId, buySeedResultData);
				return buySeedResultData;

			} else { // 3-2-2.�û�û�й��������ΪseedName�����ӣ�����Ӵ�����
				// 01.���û����������
				String sql7 = "insert into farm_seed ("
						+ "seedName,seedState,seedGrowthTime,"
						+ "seedBuyPrice,seedSellingPrice,seedExperience,"
						+ "seedYield,seedFruitSellingPrice,seedType,"
						+ "seedImage,seedNumber,userId" + ") values('"
						+ seed.getSeedName()
						+ "','"
						+ 2
						+ "',"
						+ seed.getSeedGrowthTime()
						+ ","
						+ seed.getSeedBuyPrice()
						+ ","
						+ seed.getSeedSellingPrice()
						+ ","
						+ seed.getSeedExperience()
						+ ","
						+ seed.getSeedYield()
						+ ","
						+ seed.getSeedFruitSellingPrice()
						+ ","
						+ seed.getSeedType()
						+ ",'"
						+ seed.getSeedImage()
						+ "'," + seedNumber + "," + userId + ")";
				System.out.println("sql7====" + sql7);

				// 02.��ӵ�������id
				String sql11 = "select last_insert_id()";
				System.out.println("sql11 = " + sql11);
				long lastid = 0;
				try {
					lastid = SqlHelper.executeUpdateGetId(sql7, sql11);
					System.out.println("�ող������ݵ�id ---  11 = " + lastid);

				} catch (Exception e) {
					e.printStackTrace();
				}

				// ���û���������
				resultBuySeedResult(userId, buySeedResultData);
				return buySeedResultData;

			}
		} else {
			// 3.��Ҳ��������û���ʾ
			// ���û���������
			return buySeedResultData;

		}

	}

	/**
	 * 
	 * @Description: �������ӳɹ���ķ�����Ϣ
	 * @param userId
	 * @param buySeedResultData
	 */
	private void resultBuySeedResult(String userId,
			BuySeedResultData buySeedResultData) {
		// ���û����ع���ɹ�����Ϣ������
		// 2.��ѯ�û������Ϣ������Ƿ������ӣ��粻��ֱ�ӷ�����ʾ�û�
		String sql12 = "select * from farm_user where userId=" + userId;
		System.out.println("sql12 = " + sql12);
		ResultSet rs12 = SqlHelper.executeQuery(sql12);
		long price12 = 0;
		try {
			// ��ѯ���ݿ�,��ȡ����uid��Ӧ������

			while (rs12.next()) {
				price12 = rs12.getLong(4);
				System.out.println("�û������    price12 = " + price12);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs12, SqlHelper.getPs(), SqlHelper.getConnection());
		}

		String sql13 = "select * from user_seed where userId=" + userId;
		System.out.println("sql13 = " + sql13);
		ResultSet rs13 = SqlHelper.executeQuery(sql13);
		List<Long> seedIds = new ArrayList<Long>();
		try {
			// ��ѯ���ݿ�,��ȡ����uid��Ӧ������

			while (rs13.next()) {
				seedIds.add(rs13.getLong(3));
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs13, SqlHelper.getPs(), SqlHelper.getConnection());
		}
		List<Seed> seeds = new ArrayList<Seed>();
		for (Long seedid : seedIds) {
			String sql14 = "select * from farm_seed where seedId=" + seedid;
			System.out.println("sql14 = " + sql14);
			ResultSet rs14 = SqlHelper.executeQuery(sql14);
			Seed seed = new Seed();
			try {
				// ��ѯ���ݿ�,��ȡ����uid��Ӧ������

				while (rs14.next()) {
					seed.setSeedId(rs14.getLong(1));
					seed.setSeedName(rs14.getString(2));
					seed.setSeedState(rs14.getString(3));
					seed.setSeedGrowthTime(rs14.getLong(4));
					seed.setSeedSellingPrice(rs14.getLong(5));
					seed.setSeedImage(rs14.getString(6));
					seed.setSeedNumber(rs14.getInt(7));

				}
			} catch (SQLException e) {
				//
				e.printStackTrace();
			} finally {
				SqlHelper.close(rs14, SqlHelper.getPs(),
						SqlHelper.getConnection());
			}
			seeds.add(seed);
		}
		buySeedResultData.setUserGold(price12);
		buySeedResultData.setUserSeeds(seeds);

	}

	/**
	 * 
	 * @Description: ��ȡ����������Ϣ
	 * @return
	 */
	public SeedMsgAllResultData getSeedMsgAll() {
		SeedMsgAllResultData seedMsgAllResultData = new SeedMsgAllResultData();
		List<Seed> seeds = new ArrayList<Seed>();
		for (int i = 0; i < 10000; i++) {
			Seed seed = new Seed();
			String sql = "select * from farm_seed where seedState='1'";

			// TODO SELECT DISTINCT * FROM farm_seed
			ResultSet rs = SqlHelper.executeQuery(sql);
			try {
				// ��ѯ���ݿ�,��ȡ����uid��Ӧ������

				while (rs.next()) {

					seed.setSeedId(rs.getLong(1));
					seed.setSeedName(rs.getString(2));
					seed.setSeedState(rs.getString(3));
					seed.setSeedGrowthTime(rs.getLong(4));
					seed.setSeedBuyPrice(rs.getLong(5));
					seed.setSeedSellingPrice(rs.getLong(6));
					seed.setSeedExperience(rs.getLong(7));
					seed.setSeedYield(rs.getLong(8));
					seed.setSeedFruitSellingPrice(rs.getLong(9));
					seed.setSeedType(rs.getLong(10));
					seed.setSeedImage(rs.getString(11));

				}
			} catch (SQLException e) {
				//
				e.printStackTrace();
			} finally {
				SqlHelper.close(rs, SqlHelper.getPs(),
						SqlHelper.getConnection());
			}
			seeds.add(seed);
		}
		seedMsgAllResultData.setSeeds(seeds);

		return seedMsgAllResultData;
	}

	class ReadByGet extends Thread {
		String uri = null;
		String json = null;

		public ReadByGet(String uri, String json) {
			this.uri = uri;
			this.json = json;
		}

		StringBuilder builder = null;

		@Override
		public void run() {
			try {
				URL url = new URL(uri);// ����в���������ַ��Я������
				URLConnection conn = url.openConnection();
				InputStream is = conn.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);

				String line;
				builder = new StringBuilder();
				while ((line = br.readLine()) != null) {
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
			json = builder.toString();
			System.out.println(builder.toString());
		}

	}
	JSONObject jsonObject;
	String access_token = null;
	int expires_in = 0;
	String refresh_token = null;
	String openid = null;
	String scope = null;
	
	int errcode = 0;
	String errmsg = null;
	
	String nickname = null;
	int sex = 0;
	String province = null;
	String city = null;
	String country = null;
	String headimgurl = null;
	JSONArray jsonArray = null;
	String[] privilege = null;
	String unionid = null;
	
	public void analysisGetAndRefreshAccess_tokenSuccess(String json) {
		try {
			jsonObject = new JSONObject(json);
			access_token = jsonObject.getString("access_token");
			expires_in = jsonObject.getInt("expires_in");
			refresh_token = jsonObject.getString("refresh_token");
			openid = jsonObject.getString("openid");
			scope = jsonObject.getString("scope");
		} catch (JSONException e) {
			//
			e.printStackTrace();
		}
	}
	
	public void analysisDefeated(String json) {
		try {
			jsonObject = new JSONObject(json);
			errcode = jsonObject.getInt("errcode");
			errmsg = jsonObject.getString("errmsg");
		} catch (JSONException e) {
			//
			e.printStackTrace();
		}
	}
	
	public void analysisGetUnionID(String json) {
		try {
			jsonObject = new JSONObject(json);
			openid = jsonObject.getString("openid");
			nickname = jsonObject.getString("nickname");
			sex = jsonObject.getInt("sex");
			province = jsonObject.getString("province");

			city = jsonObject.getString("city");
			country = jsonObject.getString("country");
			headimgurl = jsonObject.getString("headimgurl");
			jsonArray = jsonObject.getJSONArray("privilege");
			for (int i = 0; i < jsonArray.length(); i++) {
				privilege[i] = jsonArray.getString(i);
			}
			unionid = jsonObject.getString("unionid");

		} catch (JSONException e) {
			//
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Description: ��¼�ж�
	 * @return
	 */
	public boolean login(String code) {
		boolean b = true;
//		String json = null;
//		new ReadByGet(
//				"https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code="+code+"&grant_type=authorization_code",
//				json).start();
//		if (json != null) {
//			analysisGetAndRefreshAccess_tokenSuccess(json);
//		}
//		new ReadByGet(
//				"https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN",
//				json).start();
//		if (json != null) {
//			analysisGetAndRefreshAccess_tokenSuccess(json);
//		}
//		new ReadByGet(
//				"https://api.weixin.qq.com/sns/auth?access_token=ACCESS_TOKEN&openid=OPENID",
//				json).start();
//		if (json != null) {
//			analysisDefeated(json);
//		}
//		new ReadByGet(
//				"https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid,
//				json).start();
//		if (json != null) {
//			analysisGetUnionID(json);
//		}
//		
//		// �� ������״̬Ϊ3��������Ϊ�����seedName�����ӣ�����Ϊ1
//		// ���û����������
//		String sql3 = "INSERT INTO farm_user("
//				+ "userNickName,userImage,userGold,userEcperience)" 
//				+ "VALUES('" 
//				+ nickname + "','" + headimgurl + "'," + 1000 + "," + 500 + ") ";
//		System.out.println("sql3====" + sql3);
//		String sql4 = "select last_insert_id()";
//		System.out.println("sql11 = " + sql4);
//
//		long lastid = 0;
//		try {
//			lastid = SqlHelper.executeUpdateGetId(sql3, sql4);
//			System.out.println("�ող������ݵ�id ---  11 = " + lastid);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return b;
	}

	/**
	 * 
	 * @Description: ��¼�ж�
	 * @return
	 */
	public Long WeiXinlogin(String code) {
		
		String json = null;
		new ReadByGet(
				"https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code="+code+"&grant_type=authorization_code",
				json).start();
		if (json != null) {
			analysisGetAndRefreshAccess_tokenSuccess(json);
			System.out.println("access_token = " + access_token);
		}
		new ReadByGet(
				"https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN",
				json).start();
		if (json != null) {
			analysisGetAndRefreshAccess_tokenSuccess(json);
		}
		new ReadByGet(
				"https://api.weixin.qq.com/sns/auth?access_token=ACCESS_TOKEN&openid=OPENID",
				json).start();
		if (json != null) {
			analysisDefeated(json);
		}
		new ReadByGet(
				"https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid,
				json).start();
		if (json != null) {
			analysisGetUnionID(json);
			System.out.println("nickname = " + nickname);
		}
		
		// �� ������״̬Ϊ3��������Ϊ�����seedName�����ӣ�����Ϊ1
		// ���û����������
		String sql3 = "INSERT INTO farm_user("
				+ "userNickName,userImage,userGold,userEcperience)" 
				+ "VALUES('" 
				+ nickname + "','" + headimgurl + "'," + 1000 + "," + 500 + ") ";
		System.out.println("sql3====" + sql3);
		String sql4 = "select last_insert_id()";
		System.out.println("sql11 = " + sql4);

		long lastid = 0;
		try {
			lastid = SqlHelper.executeUpdateGetId(sql3, sql4);
			System.out.println("�ող������ݵ�id ---  11 = " + lastid);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return lastid;
	}

	/**
	 * 
	 * @Description: ��¼�������ݴ���
	 * @param uid
	 * @return
	 */
	public LoginResultData loginResult(Long userId) {

		User user = new User();
		String sql = "select * from farm_user where userId=" + userId;
		ResultSet rs = SqlHelper.executeQuery(sql);
		try {
			// ��ѯ���ݿ�,��ȡ����uid��Ӧ������
			while (rs.next()) {
				user.setUserId(rs.getLong(1));

				user.setUserNickName(rs.getString(2));
				user.setUserImage(rs.getString(3));
				user.setUserGold(rs.getLong(4));
				user.setUserExperience(rs.getLong(5));

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getConnection());
		}

		List<Land> lands = new ArrayList<Land>();
		for (int i = 0; i < 100; i++) {
			Land land = new Land();
			String sql2 = "select * from farm_land where userId="
					+ user.getUserId();
			ResultSet rs2 = SqlHelper.executeQuery(sql2);
			try {
				// ��ѯ���ݿ�,��ȡ����uid��Ӧ������
				while (rs2.next()) {
					land.setLandId(rs2.getLong(1));
					land.setLandName(rs2.getString(2));
					land.setLandState(rs2.getString(3));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				SqlHelper.close(rs, SqlHelper.getPs(),
						SqlHelper.getConnection());
			}
			lands.add(land);
		}
		LoginResultData loginResponseData = new LoginResultData();
		loginResponseData.setUser(user);
		loginResponseData.setLands(lands);

		return loginResponseData;

	}

	// /**
	// *
	// * Description: ����û�
	// * @param user
	// * @return
	// */
	// public boolean addUser(User user){
	// boolean b = true;
	// String sql = "INSERT INTO user("
	// + "id,uid,username,sex,phonenumber,"
	// + "location,detailedaddress,postcode,birthday,wechat,"
	// + "growthvalue,account,password,integral,isdefaultaddress) "
	// + "VALUES("
	// +
	// user.getId()+","+user.getUid()+","+user.getUsername()+","+user.getSex()+","+user.getPhonenumber()+","
	// +
	// user.getLocation()+","+user.getDetailedaddress()+","+user.getPostcode()+","+user.getBirthday()+","+user.getWechat()+","
	// + user.getGrowthvalue()+","+
	// user.getAccount()+","+user.getPassword()+","+user.getIntegral()+","+user.getIsdefaultaddress()+") ;";
	// System.out.println("sql====" + sql);
	// try {
	// SqlHelper.executeUpdate(sql);
	// } catch (Exception e) {
	// b = false;
	// e.printStackTrace();
	// }
	// return b;
	// }
	//
	// /**
	// *
	// * Description: �޸��û�
	// * @param user
	// * @return
	// */
	// public boolean updateUser(User user){
	// boolean b = true;
	// String sql = "UPDATE user SET "
	// + "username='"+user.getUsername()+"' , "
	// + "sex='"+user.getSex()+"' ,  "
	// + "phonenumber='"+user.getPhonenumber()+"' ,  "
	// + "location='"+user.getLocation()+"' ,  "
	// + "detailedaddress='"+user.getDetailedaddress()+"' ,  "
	// + "postcode='"+user.getPostcode()+"' ,  "
	// + "birthday='"+user.getBirthday()+"' ,  "
	// + "wechat='"+user.getWechat()+"' ,  "
	// + "growthvalue='"+user.getGrowthvalue()+"' ,  "
	// + "account='"+user.getAccount()+"' ,  "
	// + "password='"+user.getPassword()+"' ,  "
	// + "integral='"+user.getIntegral()+"' ,  "
	// + "isdefaultaddress='"+user.getIsdefaultaddress()+"' "
	// + "WHERE uid='"+user.getUid()+"';";
	// try {
	// SqlHelper.executeUpdate(sql);
	// } catch (Exception e) {
	// b = false;
	// e.printStackTrace();
	// }
	//
	//
	// return b;
	// }
	//
	// /**
	// *
	// * Description: ���ݸ�����uid��ѯ���ݿ�����
	// * @param uid
	// * @return
	// */
	// public User getUserByUid (String uid){
	//
	// User user = new User();
	// String sql = "select * from user where uid='"+uid+"'";
	// ResultSet rs = SqlHelper.executeQuery(sql);
	// try {
	// // ��ѯ���ݿ�,��ȡ����uid��Ӧ������
	// while (rs.next()) {
	//
	// user.setId(rs.getInt(1));
	// user.setUid(rs.getString(2));
	// user.setUsername(rs.getString(3));
	// user.setSex(rs.getString(4));
	// user.setPhonenumber(rs.getString(5));
	// user.setLocation(rs.getString(6));
	// user.setDetailedaddress(rs.getString(7));
	// user.setPostcode(rs.getString(8));
	// user.setBirthday(rs.getString(9));
	// user.setWechat(rs.getString(10));
	// user.setGrowthvalue(rs.getString(11));
	// user.setAccount(rs.getString(12));
	// user.setPassword(rs.getString(13));
	// user.setIntegral(rs.getString(14));
	// user.setIsdefaultaddress(rs.getString(15));
	//
	// }
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }finally{
	// SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getConnection());
	// }
	//
	// return user;
	//
	// }
	//
	// /**
	// *
	// * Description: ���ݸ�����uid�h���Ñ�
	// * @param uid
	// * @return
	// */
	// public boolean delUser(String uid){
	// boolean b = true;
	// String sql = "delete from user where uid = '"+uid+"'";
	//
	// try {
	// System.out.println("uid---"+uid);
	// SqlHelper.executeUpdate(sql);
	// } catch (Exception e) {
	// b = false;
	// e.printStackTrace();
	// }
	//
	// return b;
	// }
	// /**
	// *
	// * Description: ��ȡpageCount
	// * @param pageSize
	// * @return
	// */
	// public int getPageCount(int pageSize) {
	// int rowCount = 0;
	// ResultSet rs = null;
	// String sql = "SELECT * from user";
	//
	// try {
	// rs = SqlHelper.executeQuery(sql);
	//
	// // ��ѯ���ݿ�,�������ݹ��ж���ҳ
	// while (rs.next()) {
	// rowCount = rs.getInt(1);
	// }
	//
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getConnection());
	// }
	// return (rowCount - 1) / pageSize + 1;
	//
	// }
	//
	// /**
	// * @return
	// * Description: ���շ�ҳ����ȡ�û�
	// * @param pageNow ��ǰҳ
	// * @param pageSize ��ǰҳ��ʾ��������
	// * @return �û����ݶ��󼯺�
	// */
	// public ArrayList<User> getUsersByPage(int pageNow, int pageSize) {
	// ArrayList<User> al = new ArrayList<User>();
	//
	// String sql = "SELECT * from user WHERE id<=" + pageSize * pageNow
	// + " and id>=" + (pageSize * (pageNow - 1) + 1) + "; ";
	//
	// ResultSet rs = SqlHelper.executeQuery(sql);
	// // ���η�װ����ResultSet---->User����---->ArrayList����
	// try {
	// while (rs.next()) {
	// User user = new User();
	// user.setUid(rs.getString(2));
	// user.setUsername(rs.getString(3));
	// user.setPhonenumber(rs.getString(5));
	// user.setLocation(rs.getString(6));
	// user.setDetailedaddress(rs.getString(7));
	// al.add(user);
	// }
	// } catch (SQLException e) {
	// e.printStackTrace();
	// } finally {
	// SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getConnection());
	// }
	//
	// return al;
	// }
	//
	// /**
	// * Description: ��¼��֤
	// * @param user
	// * @return ��¼�Ƿ�ɹ�
	// */
	// public boolean checkUser(User user) {
	//
	// boolean b = false;
	// // //1.�������ݿ�
	// // Connection ct = null;
	// // PreparedStatement ps = null;
	// // ResultSet rs = null;
	// //
	// // try {
	// //
	// // Class.forName("com.mysql.jdbc.Driver");
	// // ct =
	// //
	// DriverManager.getConnection("jdbc:mysql://localhost:3306/shoppingmall",
	// // "root", "root");
	// // //SELECT * FROM user WHERE account='15858585959' and
	// // password='100101'
	// // // ps =
	// //
	// ct.prepareStatement("SELECT * from user WHERE 	account='"+user.getAccount()+"' and password='"+user.getPassword()+"'");
	// // ps = ct.prepareStatement("SELECT * FROM user ");
	// //
	// // //������ֵ
	// // rs = ps.executeQuery();
	// //
	// // if(rs.next()){
	// // b = true;
	// // }
	// //
	// // } catch (Exception e) {
	// // e.printStackTrace();
	// // }finally{
	// // if(rs != null){
	// // try {
	// // rs.close();
	// // } catch (SQLException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// // rs = null;
	// // }
	// // if(ps != null){
	// // try {
	// // ps.close();
	// // } catch (SQLException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// // ps = null;
	// // }
	// // if(ct != null){
	// // try {
	// // ct.close();
	// // } catch (SQLException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// // ct = null;
	// // }
	// // }
	//
	// // ʹ��SqlHelper��ʵ�����ݿ����
	// String sql = "SELECT * FROM user";
	// ResultSet rs = SqlHelper.executeQuery(sql);
	// try {
	// if (rs.next()) {
	// System.out.println("userservi==" + rs.toString());
	// b = true;
	// }
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getCt());
	// }
	//
	// return b;
	// }
	//

}
