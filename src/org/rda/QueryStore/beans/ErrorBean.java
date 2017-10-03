package org.rda.QueryStore.beans;

public class ErrorBean {
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	public String getQueryToken() {
		return queryToken;
	}
	public void setQueryToken(String queryToken) {
		this.queryToken = queryToken;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
	private String UUID;
	private String queryToken;
	private String errorMessage;
	private String parameters;
	private String timestamp;
	private String phase;
}
