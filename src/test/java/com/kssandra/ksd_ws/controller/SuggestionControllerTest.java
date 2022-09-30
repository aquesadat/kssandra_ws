/**
 * 
 */
package com.kssandra.ksd_ws.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.kssandra.ksd_ws.request.IntradaySuggestionRequest;
import com.kssandra.ksd_ws.response.IntradaySuggestionResponse;
import com.kssandra.ksd_ws.response.IntradaySuggestionResponseItem;
import com.kssandra.ksd_ws.service.IntradaySuggestionService;

/**
 * Unit test class for SuggestionController
 * 
 * @author aquesada
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
class SuggestionControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	IntradaySuggestionService intradaySuggestionService;

	private static final String urlEndpoint = "/api/v1/intraday/suggest";

	/**
	 * Test method for getIntraDayData with any kind of KO response
	 * {@link com.kssandra.ksd_ws.controller.SuggestionController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySuggestionRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception
	 */
	@Test
	void testGetIntraDayDataKO() throws Exception {

		String request = null;

		// Bad Request - Malformed request
		request = "";
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		// Bad Request - exCurr
		request = getMockRequest(null, null);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("exCurr - Missing field value"));

		request = getMockRequest("XXX", null);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("exCurr - Invalid field value"));

		// Bad Request - numResult
		request = getMockRequest(ExchangeCurrEnum.EUR.getValue(), 0);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("numResult - Invalid field value"));

		request = getMockRequest(ExchangeCurrEnum.EUR.getValue(), IntradaySuggestionService.MAX_RESULTS + 1);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("numResult - Invalid field value"));

	}

	/**
	 * Test method for getIntraDayData with OK response
	 * {@link com.kssandra.ksd_ws.controller.SuggestionController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySuggestionRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception
	 */
	@Test
	void testGetIntraDayDataOK() throws Exception {
		String request = getMockRequest(ExchangeCurrEnum.EUR.getValue(), 1);

		IntradaySuggestionResponse response = getMockResponse();
		when(intradaySuggestionService.getSuggestion(any())).thenReturn(response);

		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.exCurr").value("EUR")).andExpect(jsonPath("$.items").exists())
				.andExpect(jsonPath("$.items").isArray()).andExpect(jsonPath("$.items[0].cxCurr").value("ADA"))
				.andExpect(jsonPath("$.items[0].expectedRaise").value("10%"))
				.andExpect(jsonPath("$.items[0].expectedVal").value(5.35))
				.andExpect(jsonPath("$.items[0].rank").value(1)).andExpect(jsonPath("$.items[0].success").value("60%"));
	}

	private String getMockRequest(String exCurr, Integer numResult) throws JsonProcessingException {
		IntradaySuggestionRequest request = new IntradaySuggestionRequest();

		request.setExCurr(exCurr);
		request.setNumResult(numResult);

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonObject = objectMapper.writeValueAsString(request);

		return jsonObject;
	}

	private IntradaySuggestionResponse getMockResponse() {
		IntradaySuggestionResponse response = new IntradaySuggestionResponse();
		response.setExCurr(ExchangeCurrEnum.EUR.getValue());
		List<IntradaySuggestionResponseItem> items = new ArrayList<>();
		IntradaySuggestionResponseItem item = new IntradaySuggestionResponseItem();
		item.setCxCurr(CryptoCurrEnum.ADA.getValue());
		item.setExpectedRaise("10%");
		item.setExpectedVal(5.35);
		item.setRank(1);
		item.setSuccess("60%");
		items.add(item);
		response.setItems(items);

		return response;
	}

}
