package com.kssandra.ksd_ws.exception;

public class KsdServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7109136485136044210L;

	public KsdServiceException(String message, Exception e) {
		super(message, e);
	}

	public KsdServiceException(String message) {
		super(message);
	}
}
