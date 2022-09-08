package com.kssandra.ksd_ws.logger;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LoggingServiceImpl implements LoggingService {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingServiceImpl.class);

	private static final String RQ_BEGIN = "===========================request begin============================================";

	private static final String RS_BEGIN = "===========================response begin============================================";

	private static final String RQ_END = "==========================request end===============================================";

	private static final String RS_END = "==========================response end===============================================";

	private static final String TRACE_METHOD = "Method      : {}";

	private static final String TRACE_HEADERS = "Headers     : {}";

	private static final String TRACE_PARAMS = "Parameters  : {}";

	private static final String TRACE_URI = "URI         : {}";

	private static final String TRACE_STATUS = "Status      : {}";

	private static final String TRACE_RQ_BODY = "Request body: {}";

	private static final String TRACE_RS_BODY = "Response body: {}";

	@Override
	public void displayReq(HttpServletRequest request, Object requestBody) {
		StringBuilder reqMessage = new StringBuilder();
		Map<String, String> parameters = getParameters(request);

		LOG.debug(RQ_BEGIN);
		LOG.debug(TRACE_METHOD, request.getMethod());
		LOG.debug(TRACE_HEADERS, getHeaders(request));
		LOG.debug(TRACE_URI, request.getRequestURI());
		if (!parameters.isEmpty()) {
			reqMessage.append(" parameters = [").append(parameters).append("] ");
			LOG.debug(TRACE_PARAMS, parameters);
		}

		if (null != requestBody) {
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonObject = "";
			try {
				jsonObject = objectMapper.writeValueAsString(requestBody);
			} catch (JsonProcessingException e) {
				LOG.error("Error logging request body: {} ", e.getMessage());
			}
			LOG.debug(TRACE_RQ_BODY, jsonObject);
		} else {
			LOG.debug(TRACE_RQ_BODY, "");
		}

		LOG.debug(RQ_END);
	}

	@Override
	public void displayResp(HttpServletRequest request, HttpServletResponse response, Object responseBody) {
		Map<String, String> headers = getHeaders(response);

		LOG.debug(RS_BEGIN);
		LOG.debug(TRACE_URI, request.getRequestURI());
		LOG.debug(TRACE_STATUS, response.getStatus());
		if (!headers.isEmpty()) {
			LOG.debug(TRACE_HEADERS, headers);
		}

		if (null != responseBody) {
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonObject = "";
			try {
				jsonObject = objectMapper.writeValueAsString(responseBody);
			} catch (JsonProcessingException e) {
				LOG.error("Error logging response body: {} ", e.getMessage());
			}
			LOG.debug(TRACE_RS_BODY, jsonObject);
		} else {
			LOG.debug(TRACE_RS_BODY, "");
		}

		LOG.debug(RS_END);
	}

	private Map<String, String> getHeaders(HttpServletResponse response) {
		Map<String, String> headers = new HashMap<>();
		Collection<String> headerMap = response.getHeaderNames();
		for (String str : headerMap) {
			headers.put(str, response.getHeader(str));
		}
		return headers;
	}

	private Map<String, String> getParameters(HttpServletRequest request) {
		Map<String, String> parameters = new HashMap<>();
		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String paramName = params.nextElement();
			String paramValue = request.getParameter(paramName);
			parameters.put(paramName, paramValue);
		}
		return parameters;
	}

	private static Map<String, String> getHeaders(HttpServletRequest request) {

		Map<String, String> map = new HashMap<>();

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = headerNames.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}

		return map;
	}

}
