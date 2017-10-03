package org.rda.QueryStore.business.pipeline.tasks;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.rda.QueryStore.beans.NotificationDetail;
import org.rda.QueryStore.dao.QueryDao;

import vamdcsqlcomparator.VamdcSqlRequestComparator;

public class StateVAMDCQueryUniqueness extends PipelineTask {

	public StateVAMDCQueryUniqueness(NotificationDetail notification) {
		super();
		this.notification = notification;
	}

	private NotificationDetail notification;
	private boolean isNewQuery;
	private UUID queryId;

	@Override
	public void runCurrentTask() throws PipelineNodeException {
		isNewQuery = isQueryNew();
	}

	@Override
	protected void instanciateFollowingTask() {
		if (isNewQuery) {
			System.out.println("### " + getTime()
					+ " processing a new query. Query Token= "
					+ notification.getQueryToken());
			this.setFollowingTask(new NewVAMDCQueryProcessor(this.notification));
		} else {
			System.out
					.println("### "
							+ getTime()
							+ " processing an already existing query. Existing UUID= "
							+ queryId + " Query Token= "
							+ notification.getQueryToken());
			this.setFollowingTask(new ExistingVAMDCQueryProcessor(
					this.notification, this.queryId));
		}
	}

	private void getCanonicalFormForParameters() {
		// We put the VAMDC query into its canonical form.

		// We initialize the map containing the canonical form for the
		// parameters
		notification.setParametersCanonicalForm(new HashMap<String, String>());

		// We loop over the parameters map contained into the notification
		// object
		for (Map.Entry<String, String> entry : notification.getParameters()
				.entrySet()) {
			// if the name of the parameter is "query", we put the query into
			// its canonical form
			if (entry.getKey().equalsIgnoreCase("query")) {
				String queryCanonicalForm = "";
				try {
					queryCanonicalForm = VamdcSqlRequestComparator
							.canonicalForm(notification.getParameters().get(
									"query"));
				} catch (Exception e) {
					// Cannot convert query to canonical form. We keep the
					// original one
					queryCanonicalForm = notification.getParameters().get(
							"query");
				}
				notification.getParametersCanonicalForm().put(entry.getKey(),
						queryCanonicalForm);
			} else {
				// the parameters other than query are kept unchanged
				notification.getParametersCanonicalForm().put(entry.getKey(),
						entry.getValue());
			}
		}
	}

	private boolean isQueryNew() throws PipelineNodeException {
		// This function state if the Vamc-query contained into the notification
		// object is new or not.

		// We get the parameters into the canonical form
		this.getCanonicalFormForParameters();

		// get the Id of an evenual existing query identical to the one which is
		// submitted
		try {

			this.queryId = QueryDao.getInstance().getIdentifierExistingQuery(
					notification);
		} catch (Exception e) {
			new ErrorHandler(notification, queryId, e.getMessage(), this
					.getClass().getName());
			throw new PipelineNodeException(e.getMessage());
		}
		return (null == queryId);
	}

}
