package com.chtrembl.petstoreapp.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SignalRMessage implements Serializable {
	public String target;
	public Object[] arguments;

	public SignalRMessage(String target, Object[] arguments) {
		this.target = target;
		this.arguments = arguments;
	}
}
