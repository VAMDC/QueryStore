package org.rda.QueryStore.business;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import jdk.nashorn.internal.ir.RuntimeNode.Request;

import org.rda.QueryStore.beans.NotificationDetail;
import org.rda.QueryStore.dao.NotificationDAO;

public class NotificationVerifier {
	private static final NotificationVerifier instance = new NotificationVerifier();

	public static NotificationVerifier getInstance() {
		return instance;
	}

	private NotificationVerifier() {
	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException {
		NotificationVerifier.getInstance().checkSingleNotificationField(
				"accessType", "head");
	}

	public Boolean isNotificationCompliant(NotificationDetail notification)
			throws ClassNotFoundException, SQLException {
	
		Boolean isNotificationCompliant = true;

		isNotificationCompliant = isNotificationCompliant
				&& checkSingleNotificationField("notifierIP",
						notification.getNotifierIP());
		isNotificationCompliant = isNotificationCompliant
				&& checkSingleNotificationField("accededResource",
						notification.getAccededResource());
		isNotificationCompliant = isNotificationCompliant
				&& checkSingleNotificationField("resourceVersion",
						notification.getResourceVersion());
		isNotificationCompliant = isNotificationCompliant
				&& checkSingleNotificationField("userEmail",
						notification.getUserEmail());
		isNotificationCompliant = isNotificationCompliant
				&& checkSingleNotificationField("usedClient",
						notification.getUsedClient());
		isNotificationCompliant = isNotificationCompliant
				&& checkSingleNotificationField("accessType",
						notification.getAccessType());
		isNotificationCompliant = isNotificationCompliant
				&& checkSingleNotificationField("outputFormatVersion",
						notification.getOutputFormatVersion());
		isNotificationCompliant = isNotificationCompliant
				&& checkSingleNotificationField("dataURL",
						notification.getDataURL());
		
		isNotificationCompliant = isNotificationCompliant
				&& checkSingleNotificationField("queryToken",
						notification.getDataURL());
		
		isNotificationCompliant = isNotificationCompliant
				&& areParametersAuthorized(notification);
		
		return isNotificationCompliant;
	}

	private Boolean checkSingleNotificationField(String fieldName,
			String fieldValue) throws ClassNotFoundException, SQLException {
		// we check if the fieldName received as argument is registered into the
		// DB.
		// If this field is not present into the DB, the following string is
		// null, this meaning that there is no constraints on the values that
		// may be taken by this field.
		String dbReturnedFieldName = NotificationDAO.getInstance()
				.getFieldNameFromDB(fieldName);

		if (null == dbReturnedFieldName || dbReturnedFieldName.length() < 1) {
			return true;

		} else {
			// if the fieldName is not null into the DB, we get the associated
			// auothorizedFields
			List<String> authorizedFields = NotificationDAO.getInstance()
					.getAuthorizedValuesForField(fieldName);
			return authorizedFields.contains(fieldValue);
		}
	}

	private Boolean areParametersAuthorized(NotificationDetail notification)
			throws ClassNotFoundException, SQLException {
		Boolean toReturn = true;
		// get from the database the list of the names corresponding to the
		// authorized parameters
		List<String> authorizedParameterNames = NotificationDAO.getInstance()
				.getAuthorizedParameters();
		// if the list of authorized parameter names is empty, then there is no
		// constraints and everything is accepted
		if (null == authorizedParameterNames
				|| authorizedParameterNames.size() < 1) {
			return true;
		} else {
			Map<String, String> parametersMap = notification.getParameters();
			for (String paramName : parametersMap.keySet()) {
				toReturn = toReturn
						&& authorizedParameterNames.contains(paramName);
			}
			return toReturn;
		}
	}

}
