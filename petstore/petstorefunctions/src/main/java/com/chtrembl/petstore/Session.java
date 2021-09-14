package com.chtrembl.petstore;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Session implements Serializable {
	public String sessionId;
	public int sessionPageHits;

	public Session(String sessionId, int sessionPageHits) {
		this.sessionId = sessionId;
		this.sessionPageHits = sessionPageHits;
	}

	public String getSessionId() {
		return sessionId;
	}

	public int getSessionPageHits() {
		return sessionPageHits;
	}
}
