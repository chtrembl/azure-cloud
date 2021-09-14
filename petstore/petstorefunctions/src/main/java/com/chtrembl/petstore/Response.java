package com.chtrembl.petstore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class Response implements Serializable {
	Map<String, Integer> sessions = new HashMap<>();
	int userCount = 0;

	public Map<String, Integer> getSessions() {
		return sessions;
	}

	public void addSession(String session, int count) {
		this.sessions.put(session, count);
	}

	public int getUserCount() {
		return userCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}
}
