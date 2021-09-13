package com.chtrembl.petstore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class Response implements Serializable {
	Map<String, Integer> sessions = new HashMap<>();

	public Map<String, Integer> getSessions() {
		return sessions;
	}

	public void addSession(String session, int count) {
		this.sessions.put(session, count);
	}

}
