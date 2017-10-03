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
@WebServlet("/InfoQuery")
public class InfoQuery extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InfoQuery() {
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

		String jsonFromValidQuery = getValidQueryInfo(request, response);
		if (null != jsonFromValidQuery) {
			page.println(jsonFromValidQuery);
		} else {
			String jsonErrorQuery = getErrorQueryInfo(request, response);
			if (null != jsonErrorQuery) {
				page.println(jsonErrorQuery);
			} else {
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			}
		}
		page.close();
	}

	private String getErrorQueryInfo(HttpServletRequest request,
			HttpServletResponse response) throws ClassNotFoundException,
			SQLException {
		String queryUUID = request.getParameter("uuid");

		ErrorBean queryError = ErrorDAO.getInstance().getErrorByUUID(queryUUID);
		if (null != queryError) {
			JSONObject error = new JSONObject();
			JSONObject errorDetail = new JSONObject();
			errorDetail.put("UUID", queryError.getUUID());
			errorDetail.put("queryToken", queryError.getQueryToken());
			errorDetail.put("errorMessage", queryError.getErrorMessage());
			errorDetail.put("parameters", queryError.getParameters());
			errorDetail.put("timestamp", queryError.getTimestamp());
			errorDetail.put("phase", queryError.getPhase());
			error.put("queryError", errorDetail);
			String toReturn = error.toJSONString();
			return toReturn;
		} else {
			return null;
		}
	}

	private String getValidQueryInfo(HttpServletRequest request,
			HttpServletResponse response) throws ClassNotFoundException,
			SQLException {

		String queryUUID = request.getParameter("uuid");
		QueryDetails detailedQuery = QueryDao.getInstance().getQueryInfo(
				queryUUID);
		if (null != detailedQuery) {
			JSONObject query = new JSONObject();
			JSONObject queryDetails = new JSONObject();
			queryDetails.put("UUID", detailedQuery.getUUID());
			queryDetails.put("accededResource",
					detailedQuery.getAccededResource());

			queryDetails.put("resourceVersion",
					detailedQuery.getResourceVersion());
			queryDetails.put("outputFormatVersion",
					detailedQuery.getOutputFormatVersion());
			queryDetails.put("dataURL", detailedQuery.getDataURL());
			queryDetails.put("queryRexecutionLink",
					detailedQuery.getQueryRexecutionLink());
			queryDetails.put("biblioGraphicReferences",
					detailedQuery.getBibliographicReferences());

			JSONArray parameters = new JSONArray();
			List<String> paramersList = Helper.getInstance().decodeParam(
					detailedQuery.getCanonicalParameters());
			for (String currentString : paramersList) {
				parameters.add(currentString);
			}

			queryDetails.put("parameters", parameters);
			
			JSONArray queryInvocations = new JSONArray();
			for (int i = 0; i < detailedQuery.getQueryInvocationDetails()
					.size(); i++) {
				JSONObject queryDetail = new JSONObject();
				queryDetail.put("timestamp", detailedQuery
						.getQueryInvocationDetails().get(i).getTimestamp());
				queryDetail.put("queryToken", detailedQuery
						.getQueryInvocationDetails().get(i).getQueryToken());
				queryInvocations.add(queryDetail);
			}

			queryDetails.put("queryInvocationDetails", queryInvocations);
			query.put("queryInformation", queryDetails);
			String toReturn = query.toJSONString();
			return toReturn;
		}
		return null;
	}

}
