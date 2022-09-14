package com.kssandra.ksd_ws.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradayDataRequest;
import com.kssandra.ksd_ws.response.IntradayDataResponse;
import com.kssandra.ksd_ws.service.IntradayDataService;

/**
 * Controller to obtain historic intra-day data from different
 * crypto-currencies.
 * 
 * @author aquesada
 *
 */
@RestController
@RequestMapping("/api/v1")
public class CryptoDataController {

	/** The intraday data service. */
	@Autowired
	IntradayDataService intradayDataService;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(CryptoDataController.class);

	/**
	 * This POST method returns the last 24h price data for the specified
	 * crypto-currency.
	 *
	 * @param intraRq  the endpoint request
	 * @param request  the http servlet request
	 * @param response the http servlet response
	 * @return the endpoint response
	 * @throws KsdServiceException the custom exception
	 */
	@PostMapping(value = "/intraday/data", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public IntradayDataResponse getIntraDayData(@Valid @RequestBody IntradayDataRequest intraRq,
			HttpServletRequest request, HttpServletResponse response) throws KsdServiceException {

		long beginTime = System.currentTimeMillis();
		LOG.info("Begin intraday data");

		IntradayDataResponse intraRs = intradayDataService.getData(intraRq);

		// Sets status as OK (200). Any other case will be captured in
		// CustomRestExceptionHandler
		response.setStatus(HttpServletResponse.SC_OK);

		LOG.info("End intraday data. Elapsed time: {} ms", System.currentTimeMillis() - beginTime);

		return intraRs;

	}

}
