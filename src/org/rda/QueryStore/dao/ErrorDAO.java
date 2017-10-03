package org.rda.QueryStore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.rda.QueryStore.beans.ErrorBean;


public class ErrorDAO {
	
	private static final String INSERTION_QUERY = "insert into Errors (UUID, Token, ErrorMessage, Parameters, Timestamp, Phase) values (?,?,?,?,?,?)";
	
	private static final ErrorDAO instance = new ErrorDAO();

	public static ErrorDAO getInstance() {
		return instance;
	}

	private ErrorDAO() {
	}
	
	
	public String UUIDInErrorByToken(String queryToken) throws SQLException, ClassNotFoundException{
		String UUIDtoReturn = null;
		String sqlQuery = "select UUID from Errors where Token=?";
			
		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		PreparedStatement ps = conn.prepareStatement(sqlQuery);
		ps.setString(1, queryToken);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			UUIDtoReturn = rs.getString("UUID");
		}
		conn.close();
		
		return UUIDtoReturn;
	}
	
	
	public ErrorBean getErrorByUUID(String UUID) throws SQLException, ClassNotFoundException{
		ErrorBean toReturn = null;
		String sqlQuery = "select UUID, Token, ErrorMessage, Parameters, Timestamp, Phase from Errors where UUID=?";
		
		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		PreparedStatement ps = conn.prepareStatement(sqlQuery);
		ps.setString(1, UUID);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			toReturn = new ErrorBean();
			toReturn.setUUID(rs.getString("UUID"));
			toReturn.setQueryToken(rs.getString("Token"));
			toReturn.setErrorMessage(rs.getString("ErrorMessage"));
			toReturn.setParameters(rs.getString("Parameters"));
			toReturn.setTimestamp(rs.getString("Timestamp"));
			toReturn.setPhase(rs.getString("Phase"));
		}
		conn.close();
		
		return toReturn;
	}
	
	
	public void logError(String phase, String UUID, String queryToken,
			String errorMessage, String parameters, String timestamp)
			throws SQLException, ClassNotFoundException {
		Connection conn = DBConnectionBuilder.getInstance().getConnection();

		PreparedStatement ps = conn.prepareStatement(INSERTION_QUERY);
		ps.setString(1, UUID);
		ps.setString(2, queryToken);
		ps.setString(3, errorMessage);
		ps.setString(4, parameters);
		ps.setString(5, timestamp);
		ps.setString(6, phase);
		
		ps.execute();
		ps.close();
		conn.commit();
		conn.close();
	}
}
