package org.rda.QueryStore.beans;

import java.util.List;

public class QueryDetails {
	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
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

	public String getOutputFormatVersion() {
		return outputFormatVersion;
	}

	public void setOutputFormatVersion(String outputFormatVersion) {
		this.outputFormatVersion = outputFormatVersion;
	}

	public String getDataURL() {
		return dataURL;
	}

	public void setDataURL(String dataURL) {
		this.dataURL = dataURL;
	}

	public String getCanonicalParameters() {
		return canonicalParameters;
	}

	public void setCanonicalParameters(String canonicalParameters) {
		this.canonicalParameters = canonicalParameters;
	}

	public String getQueryRexecutionLink() {
		return queryRexecutionLink;
	}

	public void setQueryRexecutionLink(String queryRexecutionLink) {
		this.queryRexecutionLink = queryRexecutionLink;
	}

	public String getBibliographicReferences() {
		return bibliographicReferences;
	}

	public void setBibliographicReferences(String bibliographicReferences) {
		this.bibliographicReferences = bibliographicReferences;
	}

	public List<QueryInvocationDetails> getQueryInvocationDetails() {
		return queryInvocationDetails;
	}

	public void setQueryInvocationDetails(
			List<QueryInvocationDetails> queryInvocationDetails) {
		this.queryInvocationDetails = queryInvocationDetails;
	}
	
	public String getQueryToken() {
		return queryToken;
	}

	public void setQueryToken(String queryToken) {
		this.queryToken = queryToken;
	}
	
	private String UUID;
	private String accededResource;
	private String resourceVersion;
	private String outputFormatVersion;
	private String dataURL;
	private String canonicalParameters;
	private String queryRexecutionLink;
	private String bibliographicReferences;
	private String queryToken;
	

	private List<QueryInvocationDetails> queryInvocationDetails;

}
