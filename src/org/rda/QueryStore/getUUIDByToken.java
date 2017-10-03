package org.rda.QueryStore;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.rda.QueryStore.beans.ErrorBean;
import org.rda.QueryStore.beans.QueryDetails;
import org.rda.QueryStore.dao.ErrorDAO;
import org.rda.QueryStore.dao.QueryDao;
import org.rda.QueryStore.helper.Helper;

/**
 * Servlet implementation class InfoQuery
 */
@WebServlet("/GetUUIDByToken")
public class getUUIDByToken extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public getUUIDByToken() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			this.writeServerResponse(request, response);
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
			this.writeServerResponse(request, response);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeServerResponse(HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			ClassNotFoundException, SQLException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setCharacterEncoding("UTF-8");

		PrintWriter page;
		page = response.getWriter();

		String jsonFromValidQuery = getUUIDCorrespondingToToken(request);
		if (null != jsonFromValidQuery) {
			page.println(jsonFromValidQuery);
		} else {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
		page.close();
	}

	private String getUUIDCorrespondingToToken(HttpServletRequest request)
			throws ClassNotFoundException, SQLException {
		String toReturn;

		// get the query Token from user request
		String queryToken = request.getParameter("queryToken");

		if (null == queryToken || queryToken.length() < 1) {
			toReturn = null;
		} else {
			// get, if available, the corresponding UUID
			String associatedUUID = QueryDao.getInstance().getUUIDByQueryToken(
					queryToken);
			JSONObject queriesIdentifiers = new JSONObject();
			queriesIdentifiers.put("queryToken", queryToken);
			queriesIdentifiers.put("UUID", associatedUUID);
			toReturn = queriesIdentifiers.toJSONString();
		}

		return toReturn;

	}

}
