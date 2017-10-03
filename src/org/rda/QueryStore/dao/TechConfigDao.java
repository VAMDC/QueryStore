package org.rda.QueryStore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TechConfigDao {
	private static final TechConfigDao instance = new TechConfigDao();

	public static TechConfigDao getInstance() {
		return instance;
	}

	private TechConfigDao() {
		super();
	}
	
	public String getServletContainerAddress() throws ClassNotFoundException, SQLException{
		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		String toReturn=null;
		String query = "select ServletContainerAddress from TechConfig";
		
		PreparedStatement ps = conn.prepareStatement(query);
				ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			toReturn = rs.getString(1);
		}
		conn.close();
		return toReturn;
	}
	
	public String getAbsoluteDataPath() throws ClassNotFoundException, SQLException{
		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		String toReturn=null;
		String query = "select AbsoluteDataPath from TechConfig";
		
		PreparedStatement ps = conn.prepareStatement(query);
				ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			toReturn = rs.getString(1);
		}
		conn.close();
		return toReturn;
	}
	
	public String getAbsoluteConfigPath() throws ClassNotFoundException, SQLException{
		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		String toReturn=null;
		String query = "select AbsoluteConfigPath from TechConfig";
		
		PreparedStatement ps = conn.prepareStatement(query);
				ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			toReturn = rs.getString(1);
		}
		conn.close();
		return toReturn;
	}
	
	public String getSecuritySecret()throws ClassNotFoundException, SQLException{
		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		String toReturn=null;
		String query = "select secret from TechConfig";
		
		PreparedStatement ps = conn.prepareStatement(query);
				ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			toReturn = rs.getString(1);
		}
		conn.close();
		return toReturn;
	}
	
	
}
