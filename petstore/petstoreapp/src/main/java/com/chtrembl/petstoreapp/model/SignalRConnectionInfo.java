package com.chtrembl.petstoreapp.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SignalRConnectionInfo implements Serializable {
	public SignalRConnectionInfo() {
	}

	public SignalRConnectionInfo(String url, String accessToken) {
		this.url = url;
		this.accessToken = accessToken;
	}

	/**
	 * SignalR Sevice endpoint
	 */
	public String url;

	/**
	 * Access token to use to connect to SignalR Service endpoint
	 */
	public String accessToken;
}
