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
import org.rda.QueryStore.dao.ErrorDAO;
import org.rda.QueryStore.dao.QueryDao;

/**
 * Servlet implementation class AssociationService
 */
@WebServlet("/AssociationService")
public class AssociationService extends HttpServlet {

	private static final String QUERY_TOKEN = "queryToken";
	private static final String EMAIL = "email";
	private static final String USED_CLIENT = "usedClient";

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AssociationService() {
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

		String userEmail = request.getParameter(EMAIL);
		String userIP = getUserIP(request);
		String usedClient = request.getParameter(USED_CLIENT);
		String queryToken = request.getParameter(QUERY_TOKEN);
		
		System.out.println("### " + LocalDateTime.now().toString()
				+ " try to associate query with token " + queryToken);
		
		String correctlyAssociatedUUID = this.performAssociationAndGetUUID(
				userEmail, userIP, usedClient, queryToken);

		String errorUUIDAssociated = this.getUUIDInError(queryToken);
		
		if (null != correctlyAssociatedUUID || null!=errorUUIDAssociated) {
			JSONObject returnedObject = new JSONObject();
			
			returnedObject.put("queryToken", queryToken);
			
			if (null != correctlyAssociatedUUID) {
				System.out.println("### " + LocalDateTime.now().toString()
						+ " " + queryToken
						+ " successfully associated with the UUID "
						+ correctlyAssociatedUUID);

				returnedObject.put("UUIDCorrectlyAssociated",
						correctlyAssociatedUUID);
			}
			
			if (null != errorUUIDAssociated) {
				System.out.println("### " + LocalDateTime.now().toString()
						+ " " + queryToken
						+ " has errors. Corresponding UUID "
						+ errorUUIDAssociated);
			
				returnedObject.put("UUIDInError",
						errorUUIDAssociated);
			}
			
			PrintWriter page;
			page = response.getWriter();
			page.println(returnedObject.toJSONString());
			page.close();
		} else {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
	}
	
	
	
	private String getUUIDInError(String queryToken)
			throws ClassNotFoundException, SQLException {
		return ErrorDAO.getInstance().UUIDInErrorByToken(queryToken);
	}

	private String performAssociationAndGetUUID(String userEmail,
			String userIP, String usedClient, String queryToken)
			throws ClassNotFoundException, SQLException {
		String associatedUUIDtoReturn = null;

		// we associate only get request type
		if (queryToken.endsWith("get")) {

			if (null != queryToken && null != userEmail
					&& isMailGood(userEmail)) {
				associatedUUIDtoReturn = QueryDao.getInstance()
						.getUUIDByQueryToken(queryToken);
				if (null != associatedUUIDtoReturn) {
					QueryDao.getInstance().associateQueryToUser(queryToken,
							userEmail, usedClient, userIP);
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
	
	private String getUserIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

}
