package com.kssandra.ksd_ws.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kssandra.ksd_ws.enums.CryptoCurrEnum;
import com.kssandra.ksd_ws.enums.ExchangeCurrEnum;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradayPredictionRequest;
import com.kssandra.ksd_ws.response.IntradayPredictionResponse;
import com.kssandra.ksd_ws.response.IntradayPredictionResponseItem;
import com.kssandra.ksd_ws.service.IntradayPredictionService;

/**
 * Unit test class for PredictionController
 * 
 * @author aquesada
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
class PredictionControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	IntradayPredictionService intradayPredictionService;

	private static final String urlEndpoint = "/api/v1/intraday/prediction";

	/**
	 * Test method for getIntraDayData with any kind of KO response
	 * {@link com.kssandra.ksd_ws.controller.PredictionController#getIntraDayData(com.kssandra.ksd_ws.request.IntradayPredictionRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	@Test
	void testGetIntraDayDataKO() throws Exception {
		String request = null;

		// Bad Request - Malformed request
		request = "";
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		// Bad Request - CxCurr
		request = getMockRequest(null, ExchangeCurrEnum.EUR.getValue(), false, IntervalEnum.M15.getName());
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("cxCurr - Missing field value"));

		request = getMockRequest("XXX", ExchangeCurrEnum.EUR.getValue(), false, IntervalEnum.M15.getName());
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("cxCurr - Invalid field value"));

		// Bad Request - exCurr
		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), null, false, IntervalEnum.M15.getName());
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("exCurr - Missing field value"));

		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), "XXX", false, IntervalEnum.M15.getName());
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("exCurr - Invalid field value"));

		// Bad Request - Interval
		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), ExchangeCurrEnum.EUR.getValue(), false, null);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("interval - Missing field value"));

		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), ExchangeCurrEnum.EUR.getValue(), false, "XXX");
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("interval - Invalid field value"));

		// Conflict - Custom Exception
		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), ExchangeCurrEnum.EUR.getValue(), false,
				IntervalEnum.M15.getName());
		when(intradayPredictionService.getPrediction(any())).thenThrow(KsdServiceException.class);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
	}

	/**
	 * Test method for getIntraDayData with OK response
	 * {@link com.kssandra.ksd_ws.controller.PredictionController#getIntraDayData(com.kssandra.ksd_ws.request.IntradayPredictionRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	@Test
	void testGetIntraDayDataOK() throws Exception {

		String request = getMockRequest(CryptoCurrEnum.ADA.getValue(), ExchangeCurrEnum.EUR.getValue(), false,
				IntervalEnum.M15.getName());
		IntradayPredictionResponse response = getMockResponse();
		when(intradayPredictionService.getPrediction(any())).thenReturn(response);

		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.cxCurr").value("ADA")).andExpect(jsonPath("$.exCurr").value("EUR"))
				.andExpect(jsonPath("$.items").exists()).andExpect(jsonPath("$.items").isArray())
				.andExpect(jsonPath("$.items[0].expectedVal").value(4.58))
				.andExpect(jsonPath("$.items[0].success").value("40%"))
				.andExpect(jsonPath("$.items[0].dateTime").isNotEmpty());
	}

	private IntradayPredictionResponse getMockResponse() {
		IntradayPredictionResponse response = new IntradayPredictionResponse();
		response.setCxCurr(CryptoCurrEnum.ADA.getValue());
		response.setExCurr(ExchangeCurrEnum.EUR.getValue());
		List<IntradayPredictionResponseItem> items = new ArrayList<>();
		IntradayPredictionResponseItem item = new IntradayPredictionResponseItem();
		item.setExpectedVal(4.58);
		item.setSuccess("40%");
		item.setDateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
		items.add(item);
		response.setItems(items);

		return response;
	}

	private String getMockRequest(String cxCurr, String exCurr, boolean extended, String interval)
			throws JsonProcessingException {
		IntradayPredictionRequest request = new IntradayPredictionRequest();

		request.setCxCurr(cxCurr);
		request.setExCurr(exCurr);
		request.setInterval(interval);

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonObject = objectMapper.writeValueAsString(request);

		return jsonObject;
	}

}
