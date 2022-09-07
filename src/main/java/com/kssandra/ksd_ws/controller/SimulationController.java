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
import com.kssandra.ksd_ws.request.IntradaySimulationRequest;
import com.kssandra.ksd_ws.response.IntradaySimulationResponse;
import com.kssandra.ksd_ws.service.IntradaySimulationService;

@RestController
@RequestMapping("/api/v1")
public class SimulationController {

	private static final Logger LOG = LoggerFactory.getLogger(SimulationController.class);

	@Autowired
	IntradaySimulationService intradaySimulService;

	@PostMapping(value = "/intraday/simulate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public IntradaySimulationResponse getIntraDayData(@Valid @RequestBody IntradaySimulationRequest intraRq,
			HttpServletRequest request, HttpServletResponse response) throws KsdServiceException {

		LOG.info("Begin intraday simulate");

		IntradaySimulationResponse intraRs = intradaySimulService.getSimulation(intraRq);
		response.setStatus(HttpServletResponse.SC_OK);

		return intraRs;

	}
}
