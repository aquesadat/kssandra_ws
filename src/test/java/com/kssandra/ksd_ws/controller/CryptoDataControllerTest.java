package com.kssandra.ksd_ws.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradayDataRequest;
import com.kssandra.ksd_ws.response.IntradayDataResponse;
import com.kssandra.ksd_ws.response.IntradayDataResponseItem;
import com.kssandra.ksd_ws.service.IntradayDataService;

/**
 * Unit test class for CryptoDataController
 * 
 * @author aquesada
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
class CryptoDataControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	IntradayDataService intradayDataService;

	/**
	 * Different type of errors validating the request
	 * 
	 * @throws Exception
	 */
	@Test
	void testGetIntraDayDataKO() throws Exception {

		String request = null;

		// Bad Request - Malformed request
		request = "";
		mvc.perform(post("/api/v1/intraday/data").content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		// Bad Request - CxCurr
		request = getMockRequest(null, "EUR", false, "M15");
		mvc.perform(post("/api/v1/intraday/data").content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("cxCurr - Missing field value"));

		request = getMockRequest("XXX", "EUR", false, "M15");
		mvc.perform(post("/api/v1/intraday/data").content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("cxCurr - Invalid field value"));

		// Bad Request - exCurr
		request = getMockRequest("ADA", null, false, "M15");
		mvc.perform(post("/api/v1/intraday/data").content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("exCurr - Missing field value"));

		request = getMockRequest("ADA", "XXX", false, "M15");
		mvc.perform(post("/api/v1/intraday/data").content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("exCurr - Invalid field value"));

		// Bad Request - Interval
		request = getMockRequest("ADA", "EUR", false, null);
		mvc.perform(post("/api/v1/intraday/data").content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("interval - Missing field value"));

		request = getMockRequest("ADA", "EUR", false, "XXX");
		mvc.perform(post("/api/v1/intraday/data").content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("interval - Invalid field value"));

		// Conflict - Custom Exception
		request = getMockRequest("ADA", "EUR", false, "M15");
		when(intradayDataService.getData(any())).thenThrow(KsdServiceException.class);
		mvc.perform(post("/api/v1/intraday/data").content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
	}

	/**
	 * Status OK (200) in case of validate the request
	 * 
	 * @throws Exception
	 */
	@Test
	void testGetIntraDayDataOK() throws Exception {

		String request = null;

		// Custom Exception
		request = getMockRequest("ADA", "EUR", false, "M15");
		IntradayDataResponse response = getMockResponse();
		when(intradayDataService.getData(any())).thenReturn(response);

		mvc.perform(post("/api/v1/intraday/data").content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.cxCurr").value("ADA")).andExpect(jsonPath("$.exCurr").value("EUR"))
				.andExpect(jsonPath("$.items").exists()).andExpect(jsonPath("$.items").isArray());
	}

	private IntradayDataResponse getMockResponse() {
		IntradayDataResponse response = new IntradayDataResponse();
		response.setCxCurr("ADA");
		response.setExCurr("EUR");
		List<IntradayDataResponseItem> items = new ArrayList<>();
		IntradayDataResponseItem item = new IntradayDataResponseItem();
		item.setHigh(4.58);
		item.setLow(3.27);
		item.setDateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
		items.add(item);
		response.setItems(items);

		return response;
	}

	private String getMockRequest(String cxCurr, String exCurr, boolean extended, String interval)
			throws JsonProcessingException {
		IntradayDataRequest request = new IntradayDataRequest();

		request.setCxCurr(cxCurr);
		request.setExCurr(exCurr);
		request.setExtended(extended);
		request.setInterval(interval);

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonObject = objectMapper.writeValueAsString(request);

		return jsonObject;
	}

}