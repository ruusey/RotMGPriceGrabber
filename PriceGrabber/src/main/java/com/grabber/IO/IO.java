package com.grabber.IO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.grabber.models.Item;
import com.grabber.models.TimeOffer;

public class IO {
	static HashMap<String, Integer> nameToId = new HashMap<String, Integer>();

	public boolean updatePrices(ArrayList<Item> items) throws Exception {

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {

		}
		String url = "jdbc:mysql://localhost:3306/item_prices";
		String USER = "root";
		String PASS = "";
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, USER, PASS);
			for (Item i : items) {
				if (!Double.isNaN(i.valueDef)) {
					String sql = "INSERT INTO `" + i.name.toLowerCase()
							+ "` (item_name, item_value)" + " values (?, ?)";
					PreparedStatement preparedStmt = conn.prepareStatement(sql);
					preparedStmt.setString(1, i.name);
					preparedStmt.setDouble(2, i.valueDef);
					preparedStmt.execute();
				} else {
					System.out
							.println("could not update price because the value was NaN");
				}

			}

		} catch (Exception e) {

			throw e;

		} finally {
			try {
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return true;
	}

	public ArrayList<TimeOffer> getItemPriceHistory(Item i) throws Exception {
		ArrayList<TimeOffer> res = new ArrayList<TimeOffer>();
		String url = "jdbc:mysql://localhost:3306/item_prices";
		String USER = "root";
		String PASS = "";
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, USER, PASS);
			Statement st = conn.createStatement();
			String SQL = "SELECT item_name,item_value,date FROM `"
					+ i.name.toLowerCase() + "` ORDER BY id ASC";
			ResultSet rs = st.executeQuery(SQL);
			while (rs.next()) {
				int columnId = 1;

				TimeOffer t = new TimeOffer();
				t.setName(rs.getString(columnId++));
				t.setValueDef(rs.getDouble(columnId++));
				t.setTimestamp(rs.getTimestamp(columnId++));

				res.add(t);
			}

		} catch (Exception e) {
			throw e;

		} finally {
			try {
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return res;
	}

	public ArrayList<TimeOffer> getItemPriceHistory(String itemName)
			throws Exception {
		ArrayList<TimeOffer> res = new ArrayList<TimeOffer>();
		String url = "jdbc:mysql://localhost:3306/item_prices";
		String USER = "root";
		String PASS = "";
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, USER, PASS);
			Statement st = conn.createStatement();
			String SQL = "SELECT inames.item_name,iprices.item_value,iprices.date FROM item_names as inames "+ 
						 "INNER JOIN item_prices iprices WHERE inames.id=iprices.item_id AND inames.item_name='"+itemName+"' "+
						 "ORDER BY inames.item_name ASC";
			ResultSet rs = st.executeQuery(SQL);
			while (rs.next()) {
				int columnId = 1;

				TimeOffer t = new TimeOffer();
				t.setName(rs.getString(columnId++));
				t.setValueDef(rs.getDouble(columnId++));
				t.setTimestamp(rs.getTimestamp(columnId++));

				res.add(t);
			}

		} catch (Exception e) {
			throw e;

		} finally {
			try {
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return res;
	}

	public boolean updatePricesNew(ArrayList<Item> items) throws Exception {
		loadHash();
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {

		}
		String url = "jdbc:mysql://localhost:3306/item_prices";
		String USER = "root";
		String PASS = "";
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, USER, PASS);
			for (Item i : items) {
				if (!Double.isNaN(i.valueDef)) {
					String sql = "INSERT INTO `item_prices` (item_id, item_value)"
							+ " values (?, ?)";
					PreparedStatement preparedStmt = conn.prepareStatement(sql);
					preparedStmt.setInt(1, nameToId.get(i.name));
					preparedStmt.setDouble(2, i.valueDef);
					preparedStmt.execute();
				} else {
					System.out
							.println("could not update price because the value was NaN");
				}

			}

		} catch (Exception e) {

			throw e;

		} finally {
			try {
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return true;
	}

	public static void loadHash() throws Exception {
		nameToId.clear();
		String url = "jdbc:mysql://localhost:3306/item_prices";
		String USER = "root";
		String PASS = "";
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, USER, PASS);
			Statement st = conn.createStatement();
			String SQL = "SELECT item_name, id FROM `item_names` ORDER BY id ASC";
			ResultSet rs = st.executeQuery(SQL);
			while (rs.next()) {
				int columnId = 1;

				nameToId.put(rs.getString(columnId++), rs.getInt(columnId++));
			}

		} catch (Exception e) {
			throw e;

		} finally {
			try {
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

	}
}
