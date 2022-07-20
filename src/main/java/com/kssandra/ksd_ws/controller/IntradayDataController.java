package com.kssandra.ksd_ws.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kssandra.ksd_common.util.LogUtil;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradayDataRequest;
import com.kssandra.ksd_ws.response.IntradayDataResponse;
import com.kssandra.ksd_ws.response.error.BadRequest;
import com.kssandra.ksd_ws.service.IntraDayDataService;
import com.kssandra.ksd_ws.validation.IntradayDataValidator;

@RestController
@RequestMapping("/api/v1")
public class IntradayDataController {

	@Autowired
	IntraDayDataService intraDayService;

	private static final Logger LOG = LoggerFactory.getLogger(IntradayDataController.class);

	@PostMapping(value = "/intraday/data", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Object getIntraDayData(@RequestBody IntradayDataRequest intraRq, HttpServletRequest request,
			HttpServletResponse response) {

		Object finalResponse = null;

		// Trace request
		if (LOG.isInfoEnabled()) {
			LOG.info("Request: {}", LogUtil.toString(intraRq));
		}

		// Validate request
		BadRequest badRequest = IntradayDataValidator.validate(intraRq);

		try {
			if (badRequest.getErrors() != null && !badRequest.getErrors().isEmpty()) {
				finalResponse = badRequest;
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			} else {
				IntradayDataResponse intraRs = intraDayService.getData(intraRq);
				finalResponse = intraRs;
				response.setStatus(HttpServletResponse.SC_OK);
			}
		} catch (KsdServiceException ksdEx) {
			LOG.error(ksdEx.getMessage());
			finalResponse = "Error proccessing request. Please contact our support team.";
			response.setStatus(HttpServletResponse.SC_CONFLICT);
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			finalResponse = "Unexpected error proccessing request. Please contact our support team.";
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		return finalResponse;

	}

}
