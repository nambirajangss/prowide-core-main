package com.prowidesoftware.controller;

public class ParsedResult {
	private String json;
	private String xml;
	private String mtId;
	private String mtVariant;
	private String errorMsg;

	public ParsedResult(String json, String xml, String mtId, String mtVariant) {
		this.json = json;
		this.xml = xml;
		this.mtId = mtId;
		this.mtVariant = mtVariant;
	}

	public ParsedResult(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean hasError() {
		return errorMsg != null && !errorMsg.isEmpty();
	}

	public String getJson() {
		return json;
	}
	
	public String getXml() {
		return xml;
	}

	public String getMtId() {
		return mtId;
	}
	
	public String getMtVariant() {
		return mtVariant;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}
