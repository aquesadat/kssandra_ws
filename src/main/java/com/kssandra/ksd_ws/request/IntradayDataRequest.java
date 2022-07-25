package com.kssandra.ksd_ws.request;

public class IntradayDataRequest extends IntraDayRequest {

	private static final long serialVersionUID = 6613866438516988260L;

	private boolean extended;

	public boolean isExtended() {
		return extended;
	}

	public void setExtended(boolean extended) {
		this.extended = extended;
	}

}
