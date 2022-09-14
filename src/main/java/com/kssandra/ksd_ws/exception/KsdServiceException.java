package com.kssandra.ksd_ws.exception;

/**
 * Custom exception for the WS.
 *
 * @author aquesada
 */
public class KsdServiceException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7109136485136044210L;

	/**
	 * Instantiates a new KsdServiceException.
	 *
	 * @param message the message
	 * @param e       the e
	 */
	public KsdServiceException(String message, Exception e) {
		super(message, e);
	}

	/**
	 * Instantiates a new KsdServiceException.
	 *
	 * @param message the message
	 */
	public KsdServiceException(String message) {
		super(message);
	}
}
