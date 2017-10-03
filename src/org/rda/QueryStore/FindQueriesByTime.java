package org.rda.QueryStore;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.rda.QueryStore.beans.QueryDetails;
import org.rda.QueryStore.dao.QueryDao;
import org.rda.QueryStore.helper.Helper;

/**
 * Servlet implementation class findQueries
 */
@WebServlet("/FindQueries")
public class FindQueriesByTime extends HttpServlet {
	
	private static final String INF_TIME = "from";
	private static final String SUP_TIME = "to";
	private static final String QUERY_TYPE = "queryType";
	
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FindQueriesByTime() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			writeServerResponse(request, response);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			writeServerResponse(request, response);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writeServerResponse(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ClassNotFoundException, SQLException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setCharacterEncoding("UTF-8");
		PrintWriter page;
		page = response.getWriter();
		page.println(convertOutputToJson(getQueriesIds(request)));
		page.close();
	}
	
	private List<String> getQueriesIds(HttpServletRequest request) throws ClassNotFoundException, SQLException{
		String timeInf = request.getParameter(INF_TIME);
		String timeSup = request.getParameter(SUP_TIME);
		String queryType= request.getParameter(QUERY_TYPE);
		return QueryDao.getInstance().getQueriesIdsByTime(timeInf, timeSup, queryType);
	}
	
	private String convertOutputToJson(List<String> idsList) throws ClassNotFoundException, SQLException{
		JSONObject returnedObject = new JSONObject();
		JSONArray queriesResume = new JSONArray();
		for(String currentId : idsList){
			queriesResume.add(getQueryResume(currentId));
		}
		returnedObject.put("Queries", queriesResume);
		return returnedObject.toJSONString();
	}
	
	private JSONObject getQueryResume(String queryId) throws ClassNotFoundException, SQLException{
		JSONObject returnedObject=new JSONObject();
		QueryDetails detailedQuery = QueryDao.getInstance().getQueryInfo(queryId);
		
		returnedObject.put("UUID", detailedQuery.getUUID());
		returnedObject.put("accededResource",
				detailedQuery.getAccededResource());

		returnedObject.put("resourceVersion",
				detailedQuery.getResourceVersion());
		returnedObject.put("outputFormatVersion",
				detailedQuery.getOutputFormatVersion());

		JSONArray parameters = new JSONArray();
		List<String> paramersList = Helper.getInstance().decodeParam(
				detailedQuery.getCanonicalParameters());
		for (String currentString : paramersList) {
			parameters.add(currentString);
		}
		returnedObject.put("parameters", parameters);

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

		returnedObject.put("queryInvocationDetails", queryInvocations);
		
		
		return returnedObject;
	}

}
