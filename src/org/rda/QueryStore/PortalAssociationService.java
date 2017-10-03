package org.rda.QueryStore;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.rda.QueryStore.beans.QueryDetails;
import org.rda.QueryStore.dao.ErrorDAO;
import org.rda.QueryStore.dao.QueryDao;

/**
 * Servlet implementation class AssociationService
 */
@WebServlet("/PortalAssociationService")
public class PortalAssociationService extends HttpServlet {

	private static final String QUERY_TOKEN = "queryToken";
	private static final String EMAIL = "email";
	private static final String USED_CLIENT = "usedClient";
	private static final String CLIENT_IP = "userIp";

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PortalAssociationService() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * "
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			writeServerResponse(request, response);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			writeServerResponse(request, response);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void writeServerResponse(HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			ClassNotFoundException, SQLException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setCharacterEncoding("UTF-8");

		String headQueryToken = request.getParameter(QUERY_TOKEN);
		String userEmail = request.getParameter(EMAIL);
		String userIP = request.getParameter(CLIENT_IP);
		String usedClient = request.getParameter(USED_CLIENT);

		System.out.println("### " + LocalDateTime.now().toString()
				+ " try to associate query with token " + headQueryToken);

		String correctlyAssociatedUUIDtoReturn = this.tryAssociationAndGetUUID(
				headQueryToken, userEmail, userIP, usedClient);

		String errorUUIDAssociatedWithHeadToken = this
				.getUUIDInErrorDirectlyOnHead(headQueryToken);

		String errorUUIDAssociatedWithGetToken = this
				.tryAssociationAndGetUUIDOnError(headQueryToken);

		if (null != correctlyAssociatedUUIDtoReturn
				|| null != errorUUIDAssociatedWithHeadToken
				|| null != errorUUIDAssociatedWithGetToken) {

			JSONObject returnedObject = new JSONObject();

			if (null != correctlyAssociatedUUIDtoReturn) {
				System.out.println("### " + LocalDateTime.now().toString()
						+ " " + headQueryToken
						+ " successfully associated with the UUID "
						+ correctlyAssociatedUUIDtoReturn);

				returnedObject.put("UUIDCorrectlyAssociated",
						correctlyAssociatedUUIDtoReturn);
			}

			if (null != errorUUIDAssociatedWithHeadToken) {
				System.out.println("### " + LocalDateTime.now().toString()
						+ " " + headQueryToken
						+ " has errors. Corresponding UUID "
						+ errorUUIDAssociatedWithHeadToken);
			
				returnedObject.put("UUIDInErrorOnHeadQuery",
						errorUUIDAssociatedWithHeadToken);
			}

			if (null != errorUUIDAssociatedWithGetToken) {
				System.out.println("### " + LocalDateTime.now().toString()
						+ " " + headQueryToken
						+ " has errors. Corresponding UUID "
						+ errorUUIDAssociatedWithHeadToken);
				
				returnedObject.put("UUIDInErrorOnGetQuery",
						errorUUIDAssociatedWithGetToken);
			}

			PrintWriter page;
			page = response.getWriter();

			page.println(returnedObject.toJSONString());
			page.close();

		} else {

			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			System.out.println("### " + LocalDateTime.now().toString()
					+ " cannot associate the query with token " + headQueryToken);
		}
	}

	private String getUUIDInErrorDirectlyOnHead(String headQueryToken)
			throws ClassNotFoundException, SQLException {
		return ErrorDAO.getInstance().UUIDInErrorByToken(headQueryToken);
	}

	private String tryAssociationAndGetUUIDOnError(String headQueryToken)
			throws ClassNotFoundException, SQLException {
		String associatedUUIDInErrortoReturn = null;
		String queryUUID = null;
		if (headQueryToken.endsWith("head")) {

			// we get the UUID corresponding to the HEAD query
			queryUUID = QueryDao.getInstance().getUUIDByQueryToken(
					headQueryToken);

			// we get the timestamp of the head query
			Long headQueryTimestamp = QueryDao.getInstance()
					.getTimestampByQueryToken(headQueryToken);

			// we get the nearest (in time) GET query associated with the
			// HEAD
			String nearestCorrespondingGetToken = null;
			if (null != headQueryTimestamp) {
				nearestCorrespondingGetToken = QueryDao.getInstance()
						.getGetTokenNearestToHeadToken(headQueryTimestamp,
								queryUUID);
			}

			if (null != nearestCorrespondingGetToken) {
				associatedUUIDInErrortoReturn = ErrorDAO.getInstance()
						.UUIDInErrorByToken(nearestCorrespondingGetToken);
			}
		}
		return associatedUUIDInErrortoReturn;
	}

	private String tryAssociationAndGetUUID(String headQueryToken,
			String userEmail, String userIP, String usedClient)
			throws ClassNotFoundException, SQLException {
		String associatedUUIDtoReturn = null;
		String queryUUID = null;

		// we associate only head type queries from the portal

		if (headQueryToken.endsWith("head")) {
			if (null != headQueryToken && null != userEmail
					&& isMailGood(userEmail)) {
				// we get the UUID corresponding to the HEAD query
				queryUUID = QueryDao.getInstance().getUUIDByQueryToken(
						headQueryToken);

				// we get the timestamp of the head query
				Long headQueryTimestamp = QueryDao.getInstance()
						.getTimestampByQueryToken(headQueryToken);

				// we get the nearest (in time) GET query associated with the
				// HEAD
				String nearestCorrespondingGetToken = null;
				if (null != headQueryTimestamp) {
					nearestCorrespondingGetToken = QueryDao.getInstance()
							.getGetTokenNearestToHeadToken(headQueryTimestamp,
									queryUUID);
				}

				if (null != nearestCorrespondingGetToken) {
					// we associate the get with the user
					QueryDao.getInstance().associateQueryToUser(
							nearestCorrespondingGetToken, userEmail,
							usedClient, userIP);
					associatedUUIDtoReturn = queryUUID;
					// we delete the HEAD entry
					QueryDao.getInstance().deleteQueryLinkByToken(
							headQueryToken);
				}
			}
		}
		return associatedUUIDtoReturn;
	}

	private boolean isMailGood(String userEmail) {
		Boolean toReturn = false;
		if (userEmail.contains("@") && userEmail.contains(".")) {
			toReturn = true;
		}
		return toReturn;
	}

}
