package com.kssandra.ksd_ws.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradaySimulationRequest;
import com.kssandra.ksd_ws.response.IntradaySimulationResponse;
import com.kssandra.ksd_ws.service.IntradaySimulationService;

/**
 * Controller to make price simulations for a specified cryptocurrency.
 *
 * @author aquesada
 */
@RestController
@CrossOrigin(origins = { "http://localhost:8081", "http://ksd-prediction.sytes.net" })
@RequestMapping("/api/v1")
public class SimulationController {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SimulationController.class);

	/** The intraday simulation service. */
	@Autowired
	IntradaySimulationService intradaySimulService;

	/**
	 * Given a cryptocurrency, this POST method returns its price simulation for the
	 * next 24h
	 *
	 * @param intraRq  the endpoint request
	 * @param request  the http request
	 * @param response the hhtp response
	 * @return the endpoint response
	 * @throws KsdServiceException the custom exception
	 */
	@PostMapping(value = "/intraday/simulate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public IntradaySimulationResponse getIntraDayData(@Valid @RequestBody IntradaySimulationRequest intraRq,
			HttpServletRequest request, HttpServletResponse response) throws KsdServiceException {

		long beginTime = System.currentTimeMillis();
		LOG.info("Begin intraday simulation");

		IntradaySimulationResponse intraRs = intradaySimulService.getSimulation(intraRq);

		// Sets status as OK (200). Any other case will be captured in
		// CustomRestExceptionHandler
		response.setStatus(HttpServletResponse.SC_OK);

		LOG.info("End intraday simulation. Elapsed time: {} ms", System.currentTimeMillis() - beginTime);

		return intraRs;

	}
}
