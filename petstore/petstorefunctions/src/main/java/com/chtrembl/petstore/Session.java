package com.chtrembl.petstore;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Session implements Serializable {
	public String sessionId;
	public String sessionBrowser;
	public String sessionState;
	public int sessionPageHits;

	public Session(String sessionId, String sessionBrowser, String sessionState, int sessionPageHits) {
		this.sessionId = sessionId;
		this.sessionBrowser = sessionBrowser;
		this.sessionState = sessionState;
		this.sessionPageHits = sessionPageHits;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getSessionBrowser() {
		return sessionBrowser;
	}

	public String getSessionState() {
		return sessionState;
	}

	public int getSessionPageHits() {
		return sessionPageHits;
	}
}
