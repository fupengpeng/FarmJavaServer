package com.jiudianlianxian.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 
 * Title: SqlHelper Description: 数据库操作工具类 Company: 济宁九点连线信息技术有限公司 ProjectName:
 * UsersManager
 * 
 * @author fupengpeng
 * @date 2017年7月19日 上午11:34:20
 *
 */
public class SqlHelper {

	// 定义所需要的变量
	private static Connection ct = null;

	// 大多数情况下，使用的是PreparedStatement来替代Statement，目的是为了防止sql注入
	private static PreparedStatement ps = null;
	private static ResultSet rs = null;
	private static CallableStatement cs = null;

	// 连接数据库的参数
	private static String url = "";
	private static String username = "";
	private static String driver = "";
	private static String password = "";
	// 读取配置文件的属性
	private static Properties pp = null;
	private static InputStream fis = null;

	// 加载驱动，只需要一次
	static {
		try {
			// 从dbinfo。properties文件中读取配置信息
			pp = new Properties();
			// 使用web项目时，使用类加载器,类加载器读取资源时，默认读取主目录是src
			fis = SqlHelper.class.getClassLoader().getResourceAsStream(
					"dbinfo.properties");
			pp.load(fis);
			// 读取配置文件里面的参数
			url = pp.getProperty("url");
			username = pp.getProperty("username");
			driver = pp.getProperty("driver");
			password = pp.getProperty("password");
			Class.forName(driver);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			fis = null;
		}
	}

	// 得到连接
	public static Connection getConnection() {
		try {
			ct = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ct;
	}

//	// 分页问题？？？？？？？
//	public static ResultSet executeQuery2() {
//		return null;
//	}
//
//	// 调用存储过程，有返回Result参数的存储过程
//	// sql call 过程
//	public static CallableStatement callPro2(String sql, String[] inparameters,
//			Integer[] outparmeters) {
//		try {
//			ct = getConnection();
//			cs = ct.prepareCall(sql);
//			if (inparameters != null) {
//				for (int i = 0; i < inparameters.length; i++) {
//					cs.setObject(i + 1, inparameters[i]);
//				}
//			}
//			// 给out参数赋值
//			if (outparmeters != null) {
//				for (int i = 0; i < outparmeters.length; i++) {
//					// TODO setObject参数待修改
//					cs.setObject(inparameters.length + i + 1, inparameters[i]);
//				}
//			}
//
//			cs.execute();
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e.getMessage());
//		} finally {
//			// 不需要关闭
//		}
//		return cs;
//	}

	// 调用存储过程
	// sql 象 {call 过程(?,?,?)}
	public static void callPro1(String sql, String[] parameters) {

		try {
			ct = getConnection();
			cs = ct.prepareCall(sql);

			// ？ 赋值
			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					cs.setObject(i + 1, parameters[i]);
				}
			}
			cs.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} finally {
			close(rs, cs, ct);
		}
	}

	// 统一的SELECT----oracle
	// ResultSet->Array
	public static ResultSet executeQueryOracle(String sql, String[] parameters) {

		try {
			ct = getConnection();
			ps = ct.prepareStatement(sql);
			if (parameters != null && parameters.equals("")) {
				for (int i = 0; i < parameters.length; i++) {
					ps.setString(i + 1, parameters[i]);
				}
			}
			rs = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} finally {
			// close(rs,cs,ct);
		}

		return rs;

	}

	// 统一的SELECT
	// ResultSet->Array
	public static ResultSet executeQuery(String sql) {

		try {
			ct = getConnection();
			ps = ct.prepareStatement(sql);

			rs = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} finally {
//			 close(rs, cs, ct);
		}

		return rs;

	}

	// 如果有多个sql语句 update/delete /insert [需要考虑下事物]
	public static void executeUpdate2(String[] sql, String[][] parameters) {
		try {
			// 核心
			// 1.获得连接
			ct = getConnection();

			// 传入的sql（可能是多个）
			ct.setAutoCommit(false);  //设置sql语句不是自动提交
			for (int i = 0; i < sql.length; i++) {
				if (parameters[i] != null) {
					ps = ct.prepareStatement(sql[i]);
					for (int j = 0; j < parameters[i].length; j++) {
						ps.setString(j + 1, parameters[i][j]);
					}
					ps.executeUpdate();
				}
			}
			ct.commit();  //手动提交
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				// 回滚
				ct.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e.getMessage());

		} finally {
			close(rs, ps, ct);
		}
	}

	// 只有一个语句 update/delete /insert
	// sql 格式：update 表名 set 字段名=？ where 字段=？
	// parameters
	public static void executeUpdateOracle(String sql, String[] parameters) {
		try {
			// 创建一个ps
			ct = getConnection();
			ps = ct.prepareStatement(sql);
			// 给？赋值
			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					ps.setString(i + 1, parameters[i]);
				}
			}
			// 执行
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());

		} finally {
			close(rs, ps, ct);
		}

	}

	// 只有一个语句 update/delete /insert
	// sql 格式：update 表名 set 字段名=？ where 字段=？
	// parameters
	public static void executeUpdate(String sql) {
		try {
			// 创建一个ps
			ct = getConnection();
			ps = ct.prepareStatement(sql);
			System.out.println("sql=="+sql);
			// 执行
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());

		} finally {
//			close(rs, ps, ct);
		}

	}
	
	// 只有一个语句 update/delete /insert
	// sql 格式：update 表名 set 字段名=？ where 字段=？
	// parameters
	public static long executeUpdateGetId(String sql , String sql1) {
		long id = 0  ; 
		try {
			// 创建一个ps
			ct = getConnection();
			ps = ct.prepareStatement(sql);
			System.out.println("sql=="+sql);
			// 执行
			ps.executeUpdate();
			
			ps = ct.prepareStatement(sql1);

			rs = ps.executeQuery();
			while (rs.next()) {
				
				id = rs.getLong(1);
				System.out.println("刚刚插入数据的id  = " + id);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());

		} finally {
//			close(rs, ps, ct);
		}
		return id;

	}

	// 关闭资源的函数
	public static void close(ResultSet rs, Statement ps, Connection ct) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		if (ct != null) {
			try {
				ct.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static Connection getCt() {
		return ct;
	}

	public static PreparedStatement getPs() {
		return ps;
	}

	public static ResultSet getRs() {
		return rs;
	}

	public static CallableStatement getCs() {
		return cs;
	}

}
