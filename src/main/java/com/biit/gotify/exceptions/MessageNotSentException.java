package com.biit.gotify.exceptions;

public class MessageNotSentException extends Exception {
	private static final long serialVersionUID = 814742969524246580L;

	public MessageNotSentException(String message) {
		super(message);
	}

	public MessageNotSentException(Throwable e) {
		super(e);
	}

}
