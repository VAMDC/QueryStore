package org.rda.QueryStore.beans;

import java.util.Map;

public class NotificationDetail {

	/**
	 * The timestamp when the notification arrived to the system
	 */
	private Long timestamp;

	/**
	 * The Internet protocol access of the entity submitting the notification
	 */
	private String notifierIP;

	/**
	 * The identifier of the resource which is been acceded
	 */
	private String accededResource;

	/**
	 * The version of the acceded resource
	 */
	private String resourceVersion;

	/**
	 * The email of the final user acessing the resource
	 */
	private String userEmail;

	/**
	 * The client used by the final user for accessing to the resource
	 */
	private String usedClient;

	/**
	 * The type of the access to the resource (e.g. head or full in case of
	 * VAMDC)
	 */
	private String accessType;

	/**
	 * The version of the output format used for of data
	 */
	private String outputFormatVersion;

	/**
	 * The set of couples of (parameterName, parameterValue) used by the final
	 * user for extracting the data from the resource
	 */
	private Map<String, String> parameters;

	/**
	 * The set of couples of (parameterName, parameterValue) modified by the
	 * Query-store, starting from the parameters map for putting parameters into
	 * a canonical format for storage
	 */
	private Map<String, String> parametersCanonicalForm;

	/**
	 * The url for directly download the data
	 */
	private String dataURL;
	
	private String queryToken;
	
	private String notifierProvidedSecret;

	public String getNotifierProvidedSecret() {
		return notifierProvidedSecret;
	}

	public void setNotifierProvidedSecret(String notifierProvidedSecret) {
		this.notifierProvidedSecret = notifierProvidedSecret;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getNotifierIP() {
		return notifierIP;
	}

	public void setNotifierIP(String notifierIP) {
		this.notifierIP = notifierIP;
	}

	public String getAccededResource() {
		return accededResource;
	}

	public void setAccededResource(String accededResource) {
		this.accededResource = accededResource;
	}

	public String getResourceVersion() {
		return resourceVersion;
	}

	public void setResourceVersion(String resourceVersion) {
		this.resourceVersion = resourceVersion;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUsedClient() {
		return usedClient;
	}

	public void setUsedClient(String usedClient) {
		this.usedClient = usedClient;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String acessType) {
		this.accessType = acessType;
	}

	public String getOutputFormatVersion() {
		return outputFormatVersion;
	}

	public void setOutputFormatVersion(String outputFormatVersion) {
		this.outputFormatVersion = outputFormatVersion;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public Map<String, String> getParametersCanonicalForm() {
		return parametersCanonicalForm;
	}

	public void setParametersCanonicalForm(
			Map<String, String> parametersCanonicalForm) {
		this.parametersCanonicalForm = parametersCanonicalForm;
	}

	public String getDataURL() {
		return dataURL;
	}

	public void setDataURL(String dataURL) {
		this.dataURL = dataURL;
	}

	public String getQueryToken() {
		return queryToken;
	}

	public void setQueryToken(String queryToken) {
		this.queryToken = queryToken;
	}
}
