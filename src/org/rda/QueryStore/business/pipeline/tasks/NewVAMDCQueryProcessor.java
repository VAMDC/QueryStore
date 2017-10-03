package org.rda.QueryStore.business.pipeline.tasks;

import java.sql.SQLException;
import java.util.UUID;

import org.rda.QueryStore.beans.NotificationDetail;
import org.rda.QueryStore.dao.QueryDao;

public class NewVAMDCQueryProcessor extends PipelineTask {
	
	private static final String NOTIFICATION_TYPE_HEAD = "head";
	
	public NewVAMDCQueryProcessor(NotificationDetail notification) {
		super();
		this.notification = notification;
	}

	private NotificationDetail notification;
	private UUID queryId;

	@Override
	public void runCurrentTask() throws PipelineNodeException {
		queryId = UUID.randomUUID();
		try {
			QueryDao.getInstance().insertQueryDetail(queryId, notification);
			System.out.println("### "+getTime()+ " query details persisted for query "+ this.queryId);
			
			QueryDao.getInstance().insertQueryUserLink(queryId, notification);
			System.out.println("### "+getTime()+" query user link persisted for query "+ this.queryId);
		} catch (Exception e) {
			new ErrorHandler(notification, queryId, e.getMessage(), this.getClass().getName());
			throw new PipelineNodeException(e.getMessage());
		}

	}

	@Override
	protected void instanciateFollowingTask() {
		if(notification.getAccessType().equalsIgnoreCase(NOTIFICATION_TYPE_HEAD)){
			//if the query is head type, we do not download the data and we do not
			//fetch bibliographic references.
			this.setFollowingTask(null);
		}else{
			// if the query is get type, we continue the pipeline
			this.setFollowingTask(new VamdcDataFileDownloader(queryId, notification));
		}
	}

}
