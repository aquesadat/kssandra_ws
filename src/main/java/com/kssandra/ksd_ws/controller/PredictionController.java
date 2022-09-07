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
import com.kssandra.ksd_ws.request.IntradayPredictionRequest;
import com.kssandra.ksd_ws.response.IntradayPredictionResponse;
import com.kssandra.ksd_ws.service.IntradayPredictionService;

@RestController
@RequestMapping("/api/v1")
public class PredictionController {

	@Autowired
	IntradayPredictionService intradayPredictionService;

	private static final Logger LOG = LoggerFactory.getLogger(PredictionController.class);

	@PostMapping(value = "/intraday/prediction", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public IntradayPredictionResponse getIntraDayData(@Valid @RequestBody IntradayPredictionRequest intraRq,
			HttpServletRequest request, HttpServletResponse response) throws KsdServiceException {

		LOG.info("Begin intraday prediction");

		IntradayPredictionResponse intraRs = intradayPredictionService.getPrediction(intraRq);
		response.setStatus(HttpServletResponse.SC_OK);

		return intraRs;

	}

}
