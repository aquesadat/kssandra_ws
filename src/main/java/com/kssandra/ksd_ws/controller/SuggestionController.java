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

import com.kssandra.ksd_ws.request.IntradaySuggestionRequest;
import com.kssandra.ksd_ws.response.IntradaySuggestionResponse;
import com.kssandra.ksd_ws.service.IntradaySuggestionService;

/**
 * Controller to make investment suggestions for different crypto currencies.
 * 
 * @author aquesada
 *
 */
@RestController
@CrossOrigin(origins = { "http://localhost:8080", "http://ksd-prediction.sytes.net" })
@RequestMapping("/api/v1")
public class SuggestionController {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SuggestionController.class);

	/** The intraday suggest service. */
	@Autowired
	IntradaySuggestionService intradaySuggestService;

	/**
	 * This post method returns the best cryptocurrencies to invest in
	 *
	 * @param intraRq  the endpoint request
	 * @param request  the http request
	 * @param response the hhtp response
	 * @return the endpoint response
	 */
	@PostMapping(value = "/intraday/suggest", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public IntradaySuggestionResponse getIntraDayData(@Valid @RequestBody IntradaySuggestionRequest intraRq,
			HttpServletRequest request, HttpServletResponse response) {

		long beginTime = System.currentTimeMillis();
		LOG.info("Begin intraday suggestion");

		IntradaySuggestionResponse intraRs = intradaySuggestService.getSuggestion(intraRq);

		// Sets status as OK (200). Any other case will be captured in
		// CustomRestExceptionHandler
		response.setStatus(HttpServletResponse.SC_OK);

		LOG.info("End intraday simulation. Elapsed time: {} ms", System.currentTimeMillis() - beginTime);

		return intraRs;

	}

}
