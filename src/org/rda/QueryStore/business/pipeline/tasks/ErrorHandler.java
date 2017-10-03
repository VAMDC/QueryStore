package org.rda.QueryStore.business.pipeline.tasks;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.management.Query;

import org.rda.QueryStore.beans.NotificationDetail;
import org.rda.QueryStore.dao.ErrorDAO;
import org.rda.QueryStore.dao.QueryDao;
import org.rda.QueryStore.helper.Helper;

public class ErrorHandler {

	public ErrorHandler(NotificationDetail notification, UUID queryId,
			String errorMessage, String pipelinePhase) {
		super();
		this.notification = notification;
		this.queryId = queryId;
		this.errorMessage = errorMessage;
		this.pipelinePhase = pipelinePhase;
		this.logErrors();
	}

	private NotificationDetail notification;
	private UUID queryId;
	private String errorMessage;
	private String pipelinePhase;

	
	private void logErrors() {
		try {
			// Insert the error into the database
			ErrorDAO.getInstance().logError(
					pipelinePhase,
					queryId.toString(),
					notification.getQueryToken(),
					errorMessage,
					Helper.getInstance().encodeParametersMap(
							notification.getParameters()),
					notification.getTimestamp().toString());

			// get the UUID of the query from its token
			String queryUUID = QueryDao.getInstance().getUUIDByQueryToken(
					notification.getQueryToken());

			// get the number of queries associated with this UUID
			Integer nbAssociatedQueries = QueryDao.getInstance()
					.getQueriesAssociatedWithUUID(queryUUID).size();

			// creating a list containing only the UUID
			List<String> uuidList = new ArrayList<String>();
			uuidList.add(queryUUID);

			// if the failing query is associated only with one token
			if (null != nbAssociatedQueries && nbAssociatedQueries < 2) {
				QueryDao.getInstance().purgeQuery(uuidList, true, true);
			} else {
				// otherwise, we erase only the link keeping the query
				QueryDao.getInstance().deleteQueryLinkByToken(
						notification.getQueryToken());
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
