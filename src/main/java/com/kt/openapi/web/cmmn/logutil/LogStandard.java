package com.kt.openapi.web.cmmn.logutil;

public class LogStandard {
	private String timestamp;
	private String service;
	private String operation;
	private String bizTransactionId;
	private String transactionId;
	private String logType;
	private String payload;
	private String serviceDomain;
	private String url;
	private String seq;

	private LOGSTANDARDCaller caller;
	private LOGSTANDARDHost host;
	private LOGSTANDARDResponse response;
	private LOGSTANDARDUser user;
	//private LOGSTANDARDDevice device;
	//private LOGSTANDARDDestination destination;
	private LOGSTANDARDSecurity security;

	public LogStandard() {
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getBizTransactionId() {
		return bizTransactionId;
	}

	public void setBizTransactionId(String bizTransactionId) {
		this.bizTransactionId = bizTransactionId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public LOGSTANDARDCaller getCaller() {
		return caller;
	}

	public void setCaller(LOGSTANDARDCaller caller) {
		this.caller = caller;
	}

	public LOGSTANDARDHost getHost() {
		return host;
	}

	public void setHost(LOGSTANDARDHost host) {
		this.host = host;
	}

	public LOGSTANDARDResponse getResponse() {
		return response;
	}

	public void setResponse(LOGSTANDARDResponse response) {
		this.response = response;
	}

	public LOGSTANDARDUser getUser() {
		return user;
	}

	public void setUser(LOGSTANDARDUser user) {
		this.user = user;
	}

	public LOGSTANDARDSecurity getSecurity() {
		return security;
	}

	public void setSecurity(LOGSTANDARDSecurity security) {
		this.security = security;
	}

	public String getServiceDomain() {
		return serviceDomain;
	}

	public void setServiceDomain(String serviceDomain) {
		this.serviceDomain = serviceDomain;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	/*
	public LOGSTANDARDDevice getDevice() {
		return device;
	}

	public void setDevice(LOGSTANDARDDevice device) {
		this.device = device;
	}

	public LOGSTANDARDDestination getDestination() {
		return destination;
	}

	public void setDestination(LOGSTANDARDDestination destination) {
		this.destination = destination;
	}
*/
	
}
