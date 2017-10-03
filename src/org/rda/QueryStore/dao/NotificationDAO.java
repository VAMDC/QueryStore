package org.rda.QueryStore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.rda.QueryStore.beans.NotificationDetail;
import org.rda.QueryStore.helper.Helper;

public class NotificationDAO {

	private static final String INSERTION_QUERY = "insert into Notifications (timestamp, notifierIP, accededResource, resourceVersion, userEmail, usedClient, accessType, outputFormatVersion, dataURL, parameters, queryToken) values (?,?,?,?,?,?,?,?,?,?,?)";

	private static final NotificationDAO instance = new NotificationDAO();

	public static NotificationDAO getInstance() {
		return instance;
	}

	private NotificationDAO() {
	}

	public void persistObject(NotificationDetail notification)
			throws ClassNotFoundException, SQLException {
		Connection conn = DBConnectionBuilder.getInstance().getConnection();

		PreparedStatement ps = conn.prepareStatement(INSERTION_QUERY);
		ps.setString(1, notification.getTimestamp().toString());
		ps.setString(2, notification.getNotifierIP());
		ps.setString(3, notification.getAccededResource());
		ps.setString(4, notification.getResourceVersion());
		ps.setString(5, notification.getUserEmail());
		ps.setString(6, notification.getUsedClient());
		ps.setString(7, notification.getAccessType());
		ps.setString(8, notification.getOutputFormatVersion());
		ps.setString(9, notification.getDataURL());
		ps.setString(
				10,
				Helper.getInstance().encodeParametersMap(
						notification.getParameters()));
		ps.setString(11, notification.getQueryToken());

		ps.execute();
		ps.close();
		conn.commit();
		conn.close();
	}

	
	public List<String> getAuthorizedValuesForField(String fieldName)
			throws SQLException, ClassNotFoundException {
		Connection conn = DBConnectionBuilder.getInstance().getConnection();

		String Query = "select authorizedValues from AuthorizedFields where fieldNames = ?";
		PreparedStatement ps = conn.prepareStatement(Query);
		ps.setString(1, fieldName);

		List<String> dbReturnedAuthorizedValues = new ArrayList<String>();
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			dbReturnedAuthorizedValues.add(rs.getString("authorizedValues"));
		}

		ps.close();
		conn.close();
		return dbReturnedAuthorizedValues;
	}
	
	public String getFieldNameFromDB(String fieldName)
			throws ClassNotFoundException, SQLException {
		String dbReturnedFieldName = null;
		Connection conn = DBConnectionBuilder.getInstance().getConnection();

		String Query = "select distinct fieldNames from AuthorizedFields where fieldNames = ?";
		PreparedStatement ps = conn.prepareStatement(Query);
		ps.setString(1, fieldName);

		
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			dbReturnedFieldName = rs.getString(1);
		}

		ps.close();
		conn.close();
		return dbReturnedFieldName;
	}
	
	public List<String> getAuthorizedParameters() throws SQLException,
			ClassNotFoundException {
		Connection conn = DBConnectionBuilder.getInstance().getConnection();

		String Query = "select distinct paramName from AuthorizedParameters";
		PreparedStatement ps = conn.prepareStatement(Query);
		
		List<String> dbReturnedAuthorizedParameterNames = new ArrayList<String>();
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			dbReturnedAuthorizedParameterNames.add(rs.getString(1));
		}

		ps.close();
		conn.close();
		return dbReturnedAuthorizedParameterNames;
	}

}
