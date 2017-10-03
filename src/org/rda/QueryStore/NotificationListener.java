package org.rda.QueryStore;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rda.QueryStore.beans.NotificationDetail;
import org.rda.QueryStore.business.NotificationVerifier;
import org.rda.QueryStore.business.pipeline.Pipeline;
import org.rda.QueryStore.business.pipeline.tasks.PipelineNodeException;
import org.rda.QueryStore.business.pipeline.tasks.StateVAMDCQueryUniqueness;
import org.rda.QueryStore.dao.NotificationDAO;
import org.rda.QueryStore.dao.TechConfigDao;

/**
 * Servlet implementation class NotificationListener
 */
@WebServlet("/NotificationListener")
public class NotificationListener extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String ACCEDED_RESOURCE = "accededResource";
	private static final String RESOURCE_VERSION = "resourceVersion";
	private static final String USER_EMAIL = "userEmail";
	private static final String USED_CLIENT = "usedClient";
	private static final String ACESS_TYPE = "accessType";
	private static final String OUTPUT_FORMAT_VERSION = "outputFormatVersion";
	private static final String DATA_URL = "dataURL";
	private static final String QUERY_TOKEN = "queryToken";
	private static final String SECRET = "secret";

	/**
	 * Default constructor.
	 */
	public NotificationListener() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		getNotificationDetails(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		getNotificationDetails(request, response);
	}

	private void getNotificationDetails(HttpServletRequest request,
			HttpServletResponse response) {
		NotificationDetail notification = buildModelFromRequest(request);
		try {
			// check the security
			Boolean securityCheckerOk = this.isSecurityCheckerGood(notification);

			// if the notification is compliant with what is expected
			if (NotificationVerifier.getInstance().isNotificationCompliant(
					notification)
					&& securityCheckerOk) {

				writerServerResponse(response);

				// we persist the details about the notification
				NotificationDAO.getInstance().persistObject(notification);
				// we create and run the pipeline for processing this new
				// notification
				Pipeline pipeline = new Pipeline(new StateVAMDCQueryUniqueness(
						notification));
				pipeline.executePipeline();
			} else {
				// if the notification is not compliant, we just tell it and do
				// nothing
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}

		} catch (ClassNotFoundException | SQLException | PipelineNodeException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writerServerResponse(HttpServletResponse response)
			throws IOException {
		response.setContentType("application/text");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setCharacterEncoding("UTF-8");
		PrintWriter page;
		page = response.getWriter();
		page.println();
		page.close();
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
	}

	private NotificationDetail buildModelFromRequest(HttpServletRequest request) {
		// get the actual timestamp
		long nowTimeStamp = Instant.now().toEpochMilli();
		String notifierIp = this.getUserIP(request);

		Map<String, String[]> originalRequestParameters = request
				.getParameterMap();

		Map<String, String[]> requestParameters = new HashMap<String, String[]>();
		for (Entry<String, String[]> entry : originalRequestParameters
				.entrySet()) {
			requestParameters.put(entry.getKey(), entry.getValue());
		}

		// building the notfication bean object
		NotificationDetail notification = new NotificationDetail();

		notification.setTimestamp(nowTimeStamp);
		notification.setNotifierIP(notifierIp);

		String accededResource = request.getParameter(ACCEDED_RESOURCE);
		requestParameters.remove(ACCEDED_RESOURCE);
		notification.setAccededResource(accededResource);

		String resourceVersion = request.getParameter(RESOURCE_VERSION);
		requestParameters.remove(RESOURCE_VERSION);
		notification.setResourceVersion(resourceVersion);

		String userEmail = request.getParameter(USER_EMAIL);
		requestParameters.remove(USER_EMAIL);
		notification.setUserEmail(userEmail);

		String usedClient = request.getParameter(USED_CLIENT);
		requestParameters.remove(USED_CLIENT);
		notification.setUsedClient(usedClient);

		String acessType = request.getParameter(ACESS_TYPE);
		requestParameters.remove(ACESS_TYPE);
		notification.setAccessType(acessType);

		String outputFormatVersion = request
				.getParameter(OUTPUT_FORMAT_VERSION);
		requestParameters.remove(OUTPUT_FORMAT_VERSION);
		notification.setOutputFormatVersion(outputFormatVersion);

		String dataURL = request.getParameter(DATA_URL);
		requestParameters.remove(DATA_URL);
		notification.setDataURL(dataURL);

		String queryToken = request.getParameter(QUERY_TOKEN);
		requestParameters.remove(QUERY_TOKEN);
		notification.setQueryToken(queryToken);
			
		String notifierProvidedSecret = request.getParameter(SECRET);
		requestParameters.remove(SECRET);
		notification.setNotifierProvidedSecret(notifierProvidedSecret);
		
		Map<String, String> userProvidedParameters = new HashMap<String, String>();
		for (Entry<String, String[]> entry : requestParameters.entrySet()) {
			userProvidedParameters.put(entry.getKey(), entry.getValue()[0]);
		}
		notification.setParameters(userProvidedParameters);

		return notification;
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

	private Boolean isSecurityCheckerGood(NotificationDetail notification)
			throws ClassNotFoundException, SQLException {
		String userSentSecret = notification.getNotifierProvidedSecret();
		String storedSecret = TechConfigDao.getInstance().getSecuritySecret();
		return userSentSecret.equals(storedSecret);
	}

}
