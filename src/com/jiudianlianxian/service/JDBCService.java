package com.jiudianlianxian.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
 * Title: UsersService Description: User對應的數據庫處理類 Company: 济宁九点连线信息技术有限公司
 * ProjectName: UsersManager
 * 
 * @author fupengpeng
 * @date 2017年7月19日 上午9:17:44
 *
 */
public class JDBCService {

	public GetSeedMsgResultData getSeedMsg(Long userId) {
		GetSeedMsgResultData getSeedMsgResultData = new GetSeedMsgResultData();
		// 1
		String sql = "select * from user_seed where userId=" + userId;
		List<Long> seedIds = new ArrayList<Long>();
		System.out.println("sql = " + sql);
		ResultSet rs = SqlHelper.executeQuery(sql);
		try {
			// 查询数据库,获取上述uid对应的数据

			while (rs.next()) {
				seedIds.add(rs.getLong(3));

//				System.out.println("购买种子所需金币    lands = " + seedIds.get(1));
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
				// 查询数据库,获取上述uid对应的数据

				while (rs1.next()) {
					Seed seed = new Seed();
					seed.setSeedId(long1);
					seed.setSeedName(rs1.getString(2));
					seed.setSeedState(rs1.getString(3));
					seed.setSeedGrowthTime(rs1.getLong(4));
					seed.setSeedSellingPrice(rs1.getInt(5));
					seed.setSeedImage(rs1.getString(6));
					seed.setSeedNumber(rs1.getInt(7));
					
					seeds.add(seed);
					System.out.println("购买种子所需金币    lands = " + seeds);
				}
			} catch (SQLException e) {
				//
				e.printStackTrace();
			} finally {
				SqlHelper.close(rs1, SqlHelper.getPs(), SqlHelper.getConnection());
			}

		}
		String seedname = "";
		for (Seed seed : seeds) {    //遍历查询到的用户种子列表种子
			if (seed.getSeedName().equals(seedname) && seed.getSeedState().equals("3")) {  //判断用户种子列表中种到土地中的种子是否含有现在要种植的种子
				//有  则改变数量
				// 修改用户的种子数量
				String sql2 = "UPDATE farm_seed SET " + "seedNumber='"
						+ (seed.getSeedNumber()+1) + "' " + "WHERE seedId=" + seed.getSeedId()
						+ ";";
				System.out.println("设置在当前种子的数量 = "
						+ (seed.getSeedNumber()+1));
				System.out.println("sql2 = " + sql2);
				try {
					SqlHelper.executeUpdate(sql2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}else {
				//无  则增加状态为3，种子名为传入的seedName的种子，数量为1
				// 给用户添加新种子
				String sql3 = "INSERT INTO farm_seed("
						+ "seedName,seedState,seedGrowthTime,seedSellingPrice,seedImage,"
						+ "seedNumber)" + "VALUES('" + seed.getSeedName() + "','" + 3
						+ "'," + seed.getSeedGrowthTime() + "," + seed.getSeedSellingPrice() + ",'"
						+ seed.getSeedImage() + "'," + 1 + ") ";
				System.out.println("sql3====" + sql3);
				String sql4 = "select last_insert_id()";
				System.out.println("sql11 = " + sql4);

				long lastid = 0;
				try {
					lastid = SqlHelper.executeUpdateGetId(sql3, sql4);
					System.out.println("刚刚插入数据的id ---  11 = " + lastid);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (seed.getSeedName().equals(seedname) && seed.getSeedState().equals("2")) {  //判断用户种子状态是2，名称时传入的seedname的种子时改变其数量
				//有  则改变数量
				// 修改用户的种子数量
				String sql2 = "UPDATE farm_seed SET " + "seedNumber='"
						+ (seed.getSeedNumber()-1) + "' " + "WHERE seedId=" + seed.getSeedId()
						+ ";";
				System.out.println("设置在当前种子的数量 = "
						+ (seed.getSeedNumber()+1));
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
	 * @Description: 根据用户id获取用户土地信息
	 * @param userId
	 * @return
	 */
	public GetLandMsgResultData getLandMsg(Long userId) {
		GetLandMsgResultData getLandMsgResultData = new GetLandMsgResultData();
		boolean b = true;
		// 1.查询种子信息，计算价格
		String sql = "select * from farm_land where userId=" + userId;
		List<Land> lands = new ArrayList<Land>();
		System.out.println("sql1 = " + sql);
		ResultSet rs = SqlHelper.executeQuery(sql);
		try {
			// 查询数据库,获取上述uid对应的数据

			while (rs.next()) {
				Land land = new Land();
				land.setLandId(rs.getLong(1));
				land.setLandName(rs.getString(2));
				land.setLandState(rs.getString(3));
				lands.add(land);
				System.out.println("购买种子所需金币    lands = " + lands);
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
	 * @Description: 购买种子，并返回购买后用户数据
	 * @param userId
	 * @param seedId
	 * @param seedNumber
	 * @return
	 */
	public BuySeedResultData getBuySeedResultData(String userId, String seedId,
			String seedNumber) {
		BuySeedResultData buySeedResultData = new BuySeedResultData();

		boolean b = true;
		// 1.查询种子信息，计算购买所需金币
		String sql1 = "select * from farm_seed where seedId=" + seedId;
		System.out.println("sql1 = " + sql1);
		ResultSet rs1 = SqlHelper.executeQuery(sql1);
		int price1 = 0;
		String seedName = "";
		Long seedGrowthTime = null;
		int seedSellingPrice = 0;
		String seedImage = "";
		try {
			while (rs1.next()) {
				price1 = (int) (Integer.parseInt(seedNumber) * (rs1.getInt(5)));
				seedName = rs1.getString(2);
				seedGrowthTime = rs1.getLong(4);
				seedSellingPrice = rs1.getInt(5);
				seedImage = rs1.getString(6);
				System.out.println("购买种子所需金币    price1 = " + price1);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs1, SqlHelper.getPs(), SqlHelper.getConnection());
		}

		// 2.查询用户金币信息，
		String sql2 = "select * from farm_user where userId=" + userId;
		System.out.println("sql2 = " + sql2);
		ResultSet rs2 = SqlHelper.executeQuery(sql2);
		int price2 = 0;
		try {
			// 查询数据库,获取上述uid对应的数据

			while (rs2.next()) {
				price2 = rs2.getInt(4);
				System.out.println("用户金币数    price2 = " + price2);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs2, SqlHelper.getPs(), SqlHelper.getConnection());
		}
		// 3. 判断用户剩余金币是否够买种子，如不够直接返回提示用户
		if (price2 > price1) {
			// 3.金币够，则修改用户的种子信息和金币信息
			// 3-1.查询种子表，用户是否有seedId种子
			String sql3 = "select * from user_seed where userId=" + userId
					+ " and seedId=" + seedId;
			System.out.println("sql3 = " + sql3);
			b = false;
			ResultSet rs3 = SqlHelper.executeQuery(sql3);
			try {
				while (rs3.next()) {
					b = true;
					rs3.getInt(3);
					System.out.println("查询该用户是否有该种子，rs3.getInt(3) = "
							+ rs3.getInt(3));
				}
			} catch (SQLException e) {
				//
				b = false;
				e.printStackTrace();
			} finally {
				SqlHelper.close(rs3, SqlHelper.getPs(),
						SqlHelper.getConnection());
			}

			// 3-2.判断用户是否已有seedId的种子
			if (b) {   // 用户有seedId的种子，
				// 修改用户的金币数量
				String sql4 = "UPDATE farm_user SET " + "userGold='"
						+ (price2 - price1) + "' " + "WHERE userId='" + userId
						+ "';";
				System.out.println("设置用户金币剩余额  price2-price1 = "
						+ (price2 - price1));
				System.out.println("sql4 = " + sql4);
				try {
					SqlHelper.executeUpdate(sql4);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 改变用户的种子数量
				//     查询种子表，用户是否有seedId种子，有则获取其数量，并进行增加，没有则添加，
				String sql10 = "select * from farm_seed where userId=" + userId
						+ " and seedId=" + seedId;
				System.out.println("sql10 = " + sql10);
				Long seedid = (long) 0;
				ResultSet rs10 = SqlHelper.executeQuery(sql10);

				try {
					while (rs10.next()) {
						seedid = rs10.getLong(3);
						System.out.println("查询该用户是否有该种子，rs3.getInt(2)   10 = "
								+ rs10.getInt(3));
					}
				} catch (SQLException e) {
					//
					e.printStackTrace();
				} finally {
					SqlHelper.close(rs10, SqlHelper.getPs(),
							SqlHelper.getConnection());
				}

				String sql5 = "UPDATE user_seed SET " + "seedNumber='"
						+ seedNumber + "' " + "WHERE seedId='" + seedid + "';";
				System.out.println("sql5 = " + sql5);
				try {
					SqlHelper.executeUpdate(sql5);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 给用户返回购买成功的信息及数据
				// 给用户返回数据
				resultBuySeedResult(userId, buySeedResultData);
				return buySeedResultData;

			} else {

				String sql6 = "UPDATE farm_user SET " + "userGold='"
						+ (price2 - price1) + "' " + "WHERE userId='" + userId
						+ "';";
				System.out.println("设置用户金币剩余额  price2-price1 = "
						+ (price2 - price1));
				System.out.println("sql6 = " + sql6);
				try {
					SqlHelper.executeUpdate(sql6);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 给用户添加新种子
				String sql7 = "INSERT INTO farm_seed("
						+ "seedName,seedState,seedGrowthTime,seedSellingPrice,seedImage,"
						+ "seedNumber)" + "VALUES('" + seedName + "','" + 2
						+ "'," + seedGrowthTime + "," + seedSellingPrice + ",'"
						+ seedImage + "'," + seedNumber + ") ";
				System.out.println("sql7====" + sql7);
				String sql11 = "select last_insert_id()";
				System.out.println("sql11 = " + sql11);

				long lastid = 0;
				try {
					lastid = SqlHelper.executeUpdateGetId(sql7, sql11);
					System.out.println("刚刚插入数据的id ---  11 = " + lastid);

				} catch (Exception e) {
					e.printStackTrace();
				}

				String sql8 = "INSERT INTO user_seed(" + "userId,seedId)"
						+ "VALUES(" + Long.parseLong(userId) + "," + lastid
						+ ") ";
				System.out.println("sql8====" + sql8);
				try {
					SqlHelper.executeUpdate(sql8);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 给用户返回数据
				resultBuySeedResult(userId, buySeedResultData);
				return buySeedResultData;

			}
		} else {
			// 3.金币不够，给用户提示
			// 给用户返回数据
			return buySeedResultData;

		}

	}

	/**
	 * 
	 * @Description: 购买种子成功后的返回信息
	 * @param userId
	 * @param buySeedResultData
	 */
	private void resultBuySeedResult(String userId,
			BuySeedResultData buySeedResultData) {
		// 给用户返回购买成功的信息及数据
		// 2.查询用户金币信息，金币是否购买种子，如不够直接返回提示用户
		String sql12 = "select * from farm_user where userId=" + userId;
		System.out.println("sql12 = " + sql12);
		ResultSet rs12 = SqlHelper.executeQuery(sql12);
		long price12 = 0;
		try {
			// 查询数据库,获取上述uid对应的数据

			while (rs12.next()) {
				price12 = rs12.getLong(4);
				System.out.println("用户金币数    price12 = " + price12);
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
			// 查询数据库,获取上述uid对应的数据

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
				// 查询数据库,获取上述uid对应的数据

				while (rs14.next()) {
					seed.setSeedId(rs14.getLong(1));
					seed.setSeedName(rs14.getString(2));
					seed.setSeedState(rs14.getString(3));
					seed.setSeedGrowthTime(rs14.getLong(4));
					seed.setSeedSellingPrice(rs14.getInt(5));
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
	 * @Description: 获取所有种子信息
	 * @return
	 */
	public SeedMsgAllResultData getSeedMsgAll() {
		SeedMsgAllResultData seedMsgAllResultData = new SeedMsgAllResultData();
		List<Seed> seeds = new ArrayList<Seed>();
		for (int i = 0; i < 10000; i++) {
			Seed seed = new Seed();
			String sql = "select * from farm_seed";

			// TODO SELECT DISTINCT * FROM farm_seed
			ResultSet rs = SqlHelper.executeQuery(sql);
			try {
				// 查询数据库,获取上述uid对应的数据

				while (rs.next()) {

					seed.setSeedId(rs.getLong(1));
					seed.setSeedName(rs.getString(2));
					seed.setSeedState(rs.getString(3));
					seed.setSeedGrowthTime(rs.getLong(4));
					seed.setSeedSellingPrice(rs.getInt(5));
					seed.setSeedImage(rs.getString(6));
					seed.setSeedNumber(rs.getInt(7));
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

	/**
	 * 
	 * @Description: 登录判断
	 * @return
	 */
	public boolean login() {
		boolean b = true;
		// String sql = "select * from farm_user where ";
		// try {
		// SqlHelper.executeUpdate(sql);
		// } catch (Exception e) {
		// b = false;
		// e.printStackTrace();
		// }

		return b;
	}

	/**
	 * 
	 * @Description: 登录返回数据处理
	 * @param uid
	 * @return
	 */
	public LoginResultData loginResult(Long userId) {

		User user = new User();
		String sql = "select * from farm_user where userId=" + userId;
		ResultSet rs = SqlHelper.executeQuery(sql);
		try {
			// 查询数据库,获取上述uid对应的数据
			while (rs.next()) {
				user.setUserId(rs.getLong(1));
				;
				user.setUserNickName(rs.getString(2));
				user.setUserImage(rs.getString(3));
				user.setUserGold(rs.getLong(4));

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
				// 查询数据库,获取上述uid对应的数据
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
	// * Description: 添加用户
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
	// * Description: 修改用户
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
	// * Description: 根据给定的uid查询数据库数据
	// * @param uid
	// * @return
	// */
	// public User getUserByUid (String uid){
	//
	// User user = new User();
	// String sql = "select * from user where uid='"+uid+"'";
	// ResultSet rs = SqlHelper.executeQuery(sql);
	// try {
	// // 查询数据库,获取上述uid对应的数据
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
	// * Description: 根据给定的uid刪除用戶
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
	// * Description: 获取pageCount
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
	// // 查询数据库,计算数据共有多少页
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
	// * Description: 按照分页来获取用户
	// * @param pageNow 当前页
	// * @param pageSize 当前页显示的数据数
	// * @return 用户数据对象集合
	// */
	// public ArrayList<User> getUsersByPage(int pageNow, int pageSize) {
	// ArrayList<User> al = new ArrayList<User>();
	//
	// String sql = "SELECT * from user WHERE id<=" + pageSize * pageNow
	// + " and id>=" + (pageSize * (pageNow - 1) + 1) + "; ";
	//
	// ResultSet rs = SqlHelper.executeQuery(sql);
	// // 二次封装，将ResultSet---->User对象---->ArrayList集合
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
	// * Description: 登录验证
	// * @param user
	// * @return 登录是否成功
	// */
	// public boolean checkUser(User user) {
	//
	// boolean b = false;
	// // //1.连接数据库
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
	// // //给？赋值
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
	// // 使用SqlHelper来实现数据库操作
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
