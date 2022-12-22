package com.biit.gotify.exceptions;

public class InvalidMessageException extends Exception {
	private static final long serialVersionUID = 814742919544246580L;

	public InvalidMessageException(String message) {
		super(message);
	}

	public InvalidMessageException(Throwable e) {
		super(e);
	}

}
