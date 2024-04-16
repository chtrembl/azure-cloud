package com.dazhar.petcloudstore.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ChatMessage implements Serializable {
	public String sender;
	public String text;
}