package org.rda.QueryStore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.rda.QueryStore.beans.NotificationDetail;
import org.rda.QueryStore.beans.QueryDetails;
import org.rda.QueryStore.beans.QueryInvocationDetails;
import org.rda.QueryStore.helper.Helper;

import com.sun.jmx.snmp.Timestamp;

public class QueryDao {
	private static final QueryDao instance = new QueryDao();

	public static QueryDao getInstance() {
		return instance;
	}

	private QueryDao() {
	}

	/**
	 * @param notification
	 * @return this function return a string encoding the set of canonical
	 *         parameters contained into the notification object. If this object
	 *         does not contain a canonical form, the returned string encodes
	 *         the set of raw parameters.
	 */
	private String tryEncodeParametersCanonicalForm(
			NotificationDetail notification) {
		String toReturn;
		// if the canonical form for parameters is not defined we use the raw
		// version for encoding the string
		if (null == notification.getParametersCanonicalForm()
				|| notification.getParametersCanonicalForm().size() <= 0) {
			toReturn = Helper.getInstance().encodeParametersMap(
					notification.getParameters());
		} else {
			// if the canonical form for parameter is defined, we use it for
			// encoding the string
			toReturn = Helper.getInstance().encodeParametersMap(
					notification.getParametersCanonicalForm());
		}
		return toReturn;
	}

	public UUID getIdentifierExistingQuery(NotificationDetail notification)
			throws ClassNotFoundException, SQLException {
		Connection conn = DBConnectionBuilder.getInstance().getConnection();

		String query = "select UUID from Queries where accededResource=? and resourceVersion=? and outputFormatVersion=? and canonicalParameters=?";
		String identifier = null;
		UUID toReturn = null;

		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, notification.getAccededResource());
		ps.setString(2, notification.getResourceVersion());
		ps.setString(3, notification.getOutputFormatVersion());

		ps.setString(4, tryEncodeParametersCanonicalForm(notification));

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			identifier = rs.getString(1);
		}

		conn.close();

		if (null == identifier) {
			// do nothing
		} else {
			toReturn = UUID.fromString(identifier);
		}
		return toReturn;
	}

	public void insertQueryDetail(UUID queryId, NotificationDetail notification)
			throws ClassNotFoundException, SQLException {
		String query = "insert into Queries (UUID, accededResource, resourceVersion, outputFormatVersion, canonicalParameters) values (?,?,?,?,?)";
		Connection conn = DBConnectionBuilder.getInstance().getConnection();

		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, queryId.toString());
		ps.setString(2, notification.getAccededResource());
		ps.setString(3, notification.getResourceVersion());
		ps.setString(4, notification.getOutputFormatVersion());
		ps.setString(5, tryEncodeParametersCanonicalForm(notification));

		ps.execute();
		ps.close();
		conn.commit();
		conn.close();
	}

	public void insertQueryUserLink(UUID queryId,
			NotificationDetail notification) throws ClassNotFoundException,
			SQLException {
		String query = "insert into QueryUserLink (UUID, timestamp, OriginalParameters, userEmail, userClient, queryToken) values (?,?,?,?,?,?)";
		Connection conn = DBConnectionBuilder.getInstance().getConnection();

		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, queryId.toString());
		ps.setLong(2, notification.getTimestamp());
		ps.setString(
				3,
				Helper.getInstance().encodeParametersMap(
						notification.getParameters()));
		ps.setString(4, notification.getUserEmail());
		ps.setString(5, notification.getUsedClient());
		ps.setString(6, notification.getQueryToken());

		ps.execute();
		ps.close();
		conn.commit();
		conn.close();
	}

	public Boolean isDataFilePresent(UUID queryId) throws SQLException,
			ClassNotFoundException {
		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		String query = "select dataURL from Queries where UUID=?";

		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, queryId.toString());
		ResultSet rs = ps.executeQuery();
		System.out.println();
		String dataURL = null;
		while (rs.next()) {
			dataURL = rs.getString(1);
		}
		conn.close();
		if (null == dataURL) {
			return false;
		} else {
			if (dataURL.length() < 0) {
				return false;
			} else {
				return true;
			}
		}
	}

	public void addDataURL(String dataURL, UUID queryId) throws SQLException,
			ClassNotFoundException {
		String query = "update Queries set dataURL=? where UUID=?";
		Connection conn = DBConnectionBuilder.getInstance().getConnection();

		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, dataURL);
		ps.setString(2, queryId.toString());

		ps.execute();
		ps.close();
		conn.commit();
		conn.close();
	}

	public void addReferences(String references, UUID queryId)
			throws SQLException, ClassNotFoundException {
		String query = "update Queries set biblioGraphicReferences=? where UUID=?";
		Connection conn = DBConnectionBuilder.getInstance().getConnection();

		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, references);
		ps.setString(2, queryId.toString());

		ps.execute();
		ps.close();
		conn.commit();
		conn.close();
	}

	public String getReferences(UUID queryId) throws ClassNotFoundException,
			SQLException {
		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		String query = "select biblioGraphicReferences from Queries where UUID=?";

		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, queryId.toString());
		ResultSet rs = ps.executeQuery();
		String toReturn = null;

		while (rs.next()) {
			toReturn = rs.getString(1);
		}
		conn.close();
		return toReturn;
	}

	public QueryDetails getQueryInfo(String queryId) throws SQLException,
			ClassNotFoundException {
		QueryDetails toReturn = null;
		String query = "select UUID, accededResource, resourceVersion, outputFormatVersion, dataURL, canonicalParameters, queryRexecutionLink, biblioGraphicReferences, Doi, DoiSubmitId from Queries where UUID=?";

		Connection conn = DBConnectionBuilder.getInstance().getConnection();

		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, queryId);

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			toReturn = new QueryDetails();
			toReturn.setUUID(rs.getString("UUID"));
			toReturn.setAccededResource(rs.getString("accededResource"));
			toReturn.setResourceVersion(rs.getString("resourceVersion"));
			toReturn.setOutputFormatVersion(rs.getString("outputFormatVersion"));
			toReturn.setDataURL(rs.getString("dataURL"));
			toReturn.setCanonicalParameters(rs.getString("canonicalParameters"));
			toReturn.setQueryRexecutionLink(rs.getString("queryRexecutionLink"));
			toReturn.setBibliographicReferences(rs
					.getString("biblioGraphicReferences"));
			toReturn.setDOI("Doi");
			toReturn.setDoiSubmitId("DoiSubmitId");
		}
		if (null != toReturn) {
			QueryDao.getInstance().getQueryInvocationDetails(toReturn, conn);
		}

		conn.close();
		return toReturn;
	}

	private QueryDetails getQueryInvocationDetails(QueryDetails queryBean,
			Connection conn) throws SQLException {

		String query = "select timestamp, queryToken from QueryUserLink where UUID=? order by timestamp DESC";

		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, queryBean.getUUID());
		ResultSet rs = ps.executeQuery();

		List<QueryInvocationDetails> queryInvocationsDetails = new ArrayList<QueryInvocationDetails>();
		while (rs.next()) {
			QueryInvocationDetails tempDetail = new QueryInvocationDetails();
			tempDetail.setTimestamp(Long.parseLong(rs.getString("timestamp")));
			tempDetail.setQueryToken(rs.getString("queryToken"));
			queryInvocationsDetails.add(tempDetail);
		}
		queryBean.setQueryInvocationDetails(queryInvocationsDetails);
		return queryBean;
	}

	public List<String> getQueriesIdsByTime(String timeInf, String timeSup,
			String queryType) throws SQLException, ClassNotFoundException {
		List<String> toReturn;

		String query = "select distinct UUID from QueryUserLink";
		String queryInf = null;
		String querySup = null;
		if (null != timeInf) {
			queryInf = "timestamp >=" + timeInf;
		}
		if (null != timeSup) {
			querySup = "timestamp <=" + timeSup;
		}

		if (null != queryInf && null != querySup) {
			query = query + " where " + queryInf + " and " + querySup;
		} else {
			if (null != queryInf) {
				query = query + " where " + queryInf;
			}
			if (null != querySup) {
				query = query + " where " + querySup;
			}
		}

		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		PreparedStatement ps = conn.prepareStatement(query);
		ResultSet rs = ps.executeQuery();
		toReturn = new ArrayList<String>();
		while (rs.next()) {
			toReturn.add(rs.getString("UUID"));
		}
		conn.close();
		return toReturn;
	}
	
	
	public void putDOI(String Doi, String DoiSubmitID, String queryUUID) throws ClassNotFoundException, SQLException{
		String sqlQuery = "update Queries set Doi=?, DoiSubmitId=? where UUID=?";
		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		try {
			conn = DBConnectionBuilder.getInstance().getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlQuery);
			ps.setString(1, Doi);
			ps.setString(2, DoiSubmitID);
			ps.setString(3, queryUUID);
			ps.execute();
			
			conn.commit();

		} catch (Exception e) {
			conn.rollback();
			e.printStackTrace();
		} finally {
			conn.close();
		}
		
		
	}

	public void associateQueryToUser(String queryToken, String userEmail,
			String usedClient, String userIP) throws SQLException,
			ClassNotFoundException {
		String sqlQuery = "update QueryUserLink set userEmail=?, notifierIP=? where queryToken=?";

		Connection conn;
		conn = DBConnectionBuilder.getInstance().getConnection();
		try {
			conn = DBConnectionBuilder.getInstance().getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlQuery);
			ps.setString(1, userEmail);
			ps.setString(2, userIP);
			ps.setString(3, queryToken);
			ps.execute();

			if (null != usedClient) {
				sqlQuery = "update QueryUserLink set userClient=? where queryToken=?";
				PreparedStatement ps1 = conn.prepareStatement(sqlQuery);
				ps1.setString(1, usedClient);
				ps1.setString(2, queryToken);
				ps1.execute();
			}
			conn.commit();

		} catch (Exception e) {
			conn.rollback();
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	public List<String> getQueriesAssociatedWithUUID(String UUID)
			throws SQLException, ClassNotFoundException {
		List<String> toReturn = new ArrayList<String>();
		String sqlQuery = "SELECT queryToken FROM QueryUserLink where UUID =?";
		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		PreparedStatement ps = conn.prepareStatement(sqlQuery);
		ps.setString(1, UUID);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			toReturn.add(rs.getString("queryToken"));
		}
		conn.close();

		return toReturn;
	}

	public String getUUIDByQueryToken(String queryToken)
			throws ClassNotFoundException, SQLException {
		String query = "select UUID from QueryUserLink where queryToken=?";
		String toReturn = null;
		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, queryToken);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			toReturn = rs.getString("UUID");
		}
		conn.close();
		return toReturn;
	}

	public List<String> getHeadQueriesIdsByTime(String timeInf, String timeSup)
			throws SQLException, ClassNotFoundException {
		List<String> toReturn;

		String query = "select distinct UUID from QueryUserLink where queryToken like '%head'";
		String queryInf = null;
		String querySup = null;
		if (null != timeInf) {
			queryInf = "timestamp >=" + timeInf;
		}
		if (null != timeSup) {
			querySup = "timestamp <=" + timeSup;
		}

		if (null != queryInf && null != querySup) {
			query = query + " and " + queryInf + " and " + querySup;
		} else {
			if (null != queryInf) {
				query = query + " and " + queryInf;
			}
			if (null != querySup) {
				query = query + " and " + querySup;
			}
		}

		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		PreparedStatement ps = conn.prepareStatement(query);
		ResultSet rs = ps.executeQuery();
		toReturn = new ArrayList<String>();
		while (rs.next()) {
			toReturn.add(rs.getString("UUID"));
		}
		conn.close();
		return toReturn;
	}

	public List<String> getUUIDHavingGETQueryToken(String uuid)
			throws SQLException, ClassNotFoundException {
		List<String> toReturn = new ArrayList<String>();
		String query = "select UUID from QueryUserLink where queryToken like '%get' and UUID=?";

		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, uuid);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			toReturn.add(rs.getString("UUID"));
		}
		conn.close();

		return toReturn;
	}

	public void purgeQuery(List<String> uuids, boolean eraseLink,
			boolean eraseQuery) throws SQLException, ClassNotFoundException {
		List<String> queries = new ArrayList<String>();
		for (String currentId : uuids) {
			if (eraseLink) {
				queries.add("delete from QueryUserLink where UUID like '"
						+ currentId + "'");
			}
			if (eraseQuery) {
				queries.add("delete from Queries where UUID like '" + currentId
						+ "'");
			}
		}
		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		Statement st = conn.createStatement();
		for (String query : queries) {
			st.addBatch(query);
		}
		@SuppressWarnings("unused")
		int[] updatedCol = st.executeBatch();
		conn.close();

	}

	public String getGetTokenNearestToHeadToken(Long headTimestamp,
			String queryUUID) throws SQLException, ClassNotFoundException {
		String query = "select queryToken from QueryUserLink where UUID=? and timestamp > ? and  queryToken like '%get' order by timestamp limit 1";

		String nearestGetToken = null;

		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, queryUUID);
		ps.setLong(2, headTimestamp);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			nearestGetToken = rs.getString("queryToken");
		}
		conn.close();

		return nearestGetToken;
	}

	public Long getTimestampByQueryToken(String queryToken)
			throws ClassNotFoundException, SQLException {
		String query = "select timestamp from QueryUserLink where queryToken=?";

		Long toReturn = null;

		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, queryToken);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			toReturn = rs.getLong("timestamp");
		}
		conn.close();

		return toReturn;
	}

	public void deleteQueryLinkByToken(String queryToken) throws SQLException,
			ClassNotFoundException {
		String sqlQuery = "delete from QueryUserLink where queryToken=?";
		Connection conn = DBConnectionBuilder.getInstance().getConnection();
		;
		try {
			PreparedStatement ps = conn.prepareStatement(sqlQuery);
			ps.setString(1, queryToken);
			ps.execute();
			conn.commit();

		} catch (Exception e) {
			conn.rollback();
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

}
