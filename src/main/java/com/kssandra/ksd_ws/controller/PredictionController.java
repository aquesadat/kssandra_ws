package com.kssandra.ksd_ws.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kssandra.ksd_common.logger.KSDLoggerFactory;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradayPredictionRequest;
import com.kssandra.ksd_ws.response.IntradayPredictionResponse;
import com.kssandra.ksd_ws.service.IntradayPredictionService;

/**
 * Controller to make price predictions for a specified cryptocurrency
 *
 * @author aquesada
 */
@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/api/v1")
public class PredictionController {

	/** The intraday prediction service. */
	@Autowired
	IntradayPredictionService intradayPredictionService;

	/** The Constant LOG. */
	private static final Logger LOG = KSDLoggerFactory.getLogger();

	/**
	 * Given a cryptocurrency, this POST method returns its price prediction for the
	 * next 24h
	 *
	 * @param intraRq  the endpoint request
	 * @param request  the http request
	 * @param response the hhtp response
	 * @return the endpoint response
	 * @throws KsdServiceException the custom exception
	 */
	@PostMapping(value = "/intraday/prediction", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public IntradayPredictionResponse getIntraDayData(@Valid @RequestBody IntradayPredictionRequest intraRq,
			HttpServletRequest request, HttpServletResponse response) throws KsdServiceException {

		long beginTime = System.currentTimeMillis();
		LOG.info("Begin intraday prediction");

		IntradayPredictionResponse intraRs = intradayPredictionService.getPrediction(intraRq);

		// Sets status as OK (200). Any other case will be captured in
		// CustomRestExceptionHandler
		response.setStatus(HttpServletResponse.SC_OK);

		LOG.info("End intraday prediction. Elapsed time: {} ms", System.currentTimeMillis() - beginTime);

		return intraRs;

	}

}
