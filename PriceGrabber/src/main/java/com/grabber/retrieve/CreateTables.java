 package com.grabber.retrieve;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;

import com.grabber.models.Item;

public class CreateTables {
	
	
	public static void Create(ArrayList<Item> items) throws Exception{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
			
		}
		String url = "jdbc:mysql://localhost:3306/item_prices";
		String USER = "root";
		String PASS = "admin";
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, USER, PASS);
			Statement stmt = conn.createStatement();
			for(Item i: items){
				String sql = "INSERT INTO `item_names` (item_id,item_name) values (?,?)";
				PreparedStatement preparedStmt = conn.prepareStatement(sql);
				preparedStmt.setInt(1, i.id);
				preparedStmt.setString(2, i.name);
				preparedStmt.execute();
				System.out.println(i.name);
			}
			
		

		} catch (Exception e) {
			
			throw e;

		} finally {
			conn.close();
		}
	
	}
}
