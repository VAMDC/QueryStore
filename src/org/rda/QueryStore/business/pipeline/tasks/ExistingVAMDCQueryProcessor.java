package org.rda.QueryStore.business.pipeline.tasks;

import java.sql.SQLException;
import java.util.UUID;

import org.rda.QueryStore.beans.NotificationDetail;
import org.rda.QueryStore.dao.QueryDao;

public class ExistingVAMDCQueryProcessor extends PipelineTask {

	private static final String NOTIFICATION_TYPE_HEAD = "head";

	public ExistingVAMDCQueryProcessor(NotificationDetail notification,
			UUID queryId) {
		super();
		this.notification = notification;
		this.queryId = queryId;
	}

	private NotificationDetail notification;
	private UUID queryId;

	@Override
	public void runCurrentTask() throws PipelineNodeException {
		try {
			QueryDao.getInstance().insertQueryUserLink(queryId, notification);
		} catch (Exception e) {
			new ErrorHandler(notification, queryId, e.getMessage(), this.getClass().getName());
			throw new PipelineNodeException(e.getMessage());
		}
	}

	@Override
	protected void instanciateFollowingTask() {
		if (notification.getAccessType().equalsIgnoreCase(
				NOTIFICATION_TYPE_HEAD)) {
			// if the query is head type, we do not download the data and we do
			// not
			// fetch bibliographic references.
			this.setFollowingTask(null);
		} else {
			// if the query is get type, we continue the pipeline
			this.setFollowingTask(new VamdcDataFileDownloader(queryId,
					notification));
		}
	}

}
