package org.rda.QueryStore;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rda.QueryStore.beans.NotificationDetail;
import org.rda.QueryStore.dao.QueryDao;
import org.rda.QueryStore.dao.TechConfigDao;

/**
 * Servlet implementation class pushDoi
 */
@WebServlet("/pushDoi")
public class pushDoi extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SECRET = "secret";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public pushDoi() {
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
			e.printStackTrace();
		}
	}

	private void writeServerResponse(HttpServletRequest request,
			HttpServletResponse response) throws ClassNotFoundException,
			SQLException {
		String userSentSecret = request.getParameter(SECRET);
		if (isSecurityCheckerGood(userSentSecret)) {
			String doi = request.getParameter("Doi");
			String doiSubmitId = request.getParameter("DoiSubmitId");
			String uuid = request.getParameter("uuid");
			try {
				QueryDao.getInstance().putDOI(doi, doiSubmitId, uuid);
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				e.printStackTrace();
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private Boolean isSecurityCheckerGood(String userSentSecret)
			throws ClassNotFoundException, SQLException {
		String storedSecret = TechConfigDao.getInstance().getSecuritySecret();
		return userSentSecret.equals(storedSecret);
	}

}
