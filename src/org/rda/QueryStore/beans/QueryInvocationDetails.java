package org.rda.QueryStore.beans;

public class QueryInvocationDetails {
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public String getOriginalParameters() {
		return originalParameters;
	}
	public void setOriginalParameters(String originalParameters) {
		this.originalParameters = originalParameters;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserClient() {
		return userClient;
	}
	public void setUserClient(String userClient) {
		this.userClient = userClient;
	}
	public String getNotifierIP() {
		return notifierIP;
	}
	public void setNotifierIP(String notifierIP) {
		this.notifierIP = notifierIP;
	}
	public String getQueryToken() {
		return queryToken;
	}
	public void setQueryToken(String queryToken) {
		this.queryToken = queryToken;
	}
	private Long timestamp;
	private String originalParameters;
	private String userEmail;
	private String userClient;
	private String notifierIP;
	private String queryToken;
}
