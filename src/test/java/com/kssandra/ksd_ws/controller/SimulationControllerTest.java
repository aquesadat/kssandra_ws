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
import com.kssandra.ksd_common.util.DateUtils;
import com.kssandra.ksd_ws.enums.CryptoCurrEnum;
import com.kssandra.ksd_ws.enums.ExchangeCurrEnum;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradaySimulationRequest;
import com.kssandra.ksd_ws.response.IntradaySimulationResponse;
import com.kssandra.ksd_ws.response.IntradaySimulationResponseItem;
import com.kssandra.ksd_ws.service.IntradaySimulationService;

/**
 * Unit test class for SimulationController
 * 
 * @author aquesada
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
class SimulationControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	IntradaySimulationService intradaySimulationService;

	private static final String urlEndpoint = "/api/v1/intraday/simulate";

	/**
	 * Test method for getIntraDayData with any kind of KO response
	 * {@link com.kssandra.ksd_ws.controller.SimulationController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySimulationRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
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

		// Bad Request - CxCurr
		request = getMockRequest(null, ExchangeCurrEnum.EUR.getValue(), false, IntervalEnum.M15.getName(), 100.3, null,
				null);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("cxCurr - Missing field value"));

		request = getMockRequest("XXX", ExchangeCurrEnum.EUR.getValue(), false, IntervalEnum.M15.getName(), 100.3, null,
				null);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("cxCurr - Invalid field value"));

		// Bad Request - exCurr
		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), null, false, IntervalEnum.M15.getName(), 100.3, null,
				null);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("exCurr - Missing field value"));

		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), "XXX", false, IntervalEnum.M15.getName(), 100.3, null,
				null);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("exCurr - Invalid field value"));

		// Bad Request - Interval
		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), ExchangeCurrEnum.EUR.getValue(), false, null, 100.3,
				null, null);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("interval - Missing field value"));

		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), ExchangeCurrEnum.EUR.getValue(), false, "XXX", 100.3,
				null, null);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("interval - Invalid field value"));

		// Bad Request - Amount
		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), ExchangeCurrEnum.EUR.getValue(), false,
				IntervalEnum.M15.getName(), null, null, null);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("amount - Missing field value"));

		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), ExchangeCurrEnum.EUR.getValue(), false,
				IntervalEnum.M15.getName(), Double.valueOf(0), null, null);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("amount - Invalid field value"));

		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), ExchangeCurrEnum.EUR.getValue(), false,
				IntervalEnum.M15.getName(), -3.5, null, null);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("amount - Invalid field value"));

		// Bad Request - PurchaseFee
		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), ExchangeCurrEnum.EUR.getValue(), false,
				IntervalEnum.M15.getName(), 40.5, -0.5, null);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("purchaseFee - Invalid field value"));

		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), ExchangeCurrEnum.EUR.getValue(), false,
				IntervalEnum.M15.getName(), 40.5, 100.5, null);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("purchaseFee - Invalid field value"));

		// Bad Request - SaleFee
		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), ExchangeCurrEnum.EUR.getValue(), false,
				IntervalEnum.M15.getName(), 40.5, null, -0.5);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("saleFee - Invalid field value"));

		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), ExchangeCurrEnum.EUR.getValue(), false,
				IntervalEnum.M15.getName(), 40.5, null, 100.5);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("saleFee - Invalid field value"));

		// Conflict - Custom Exception
		request = getMockRequest(CryptoCurrEnum.ADA.getValue(), ExchangeCurrEnum.EUR.getValue(), false,
				IntervalEnum.M15.getName(), 100.5, null, null);
		when(intradaySimulationService.getSimulation(any())).thenThrow(KsdServiceException.class);
		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
	}

	/**
	 * Test method for getIntraDayData with OK response
	 * {@link com.kssandra.ksd_ws.controller.SimulationController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySimulationRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception
	 */
	@Test
	void testGetIntraDayDataOK() throws Exception {
		String request = getMockRequest(CryptoCurrEnum.ADA.getValue(), ExchangeCurrEnum.EUR.getValue(), false,
				IntervalEnum.M15.getName(), 100.5, null, null);
		IntradaySimulationResponse response = getMockResponse();
		when(intradaySimulationService.getSimulation(any())).thenReturn(response);

		mvc.perform(post(urlEndpoint).content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.cxCurr").value("ADA")).andExpect(jsonPath("$.exCurr").value("EUR"))
				.andExpect(jsonPath("$.items").exists()).andExpect(jsonPath("$.items").isArray())
				.andExpect(jsonPath("$.items[0].profit").value(1.2))
				.andExpect(jsonPath("$.items[0].expectedVal").value(4.58))
				.andExpect(jsonPath("$.items[0].success").value("40%"))
				.andExpect(jsonPath("$.items[0].dateTime").isNotEmpty());
	}

	private String getMockRequest(String cxCurr, String exCurr, boolean extended, String interval, Double amount,
			Double purchaseFee, Double saleFee) throws JsonProcessingException {
		IntradaySimulationRequest request = new IntradaySimulationRequest();

		request.setCxCurr(cxCurr);
		request.setExCurr(exCurr);
		request.setInterval(interval);
		request.setAmount(amount);
		request.setPurchaseFee(purchaseFee);
		request.setSaleFee(saleFee);

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonObject = objectMapper.writeValueAsString(request);

		return jsonObject;
	}

	private IntradaySimulationResponse getMockResponse() {
		IntradaySimulationResponse response = new IntradaySimulationResponse();
		response.setCxCurr(CryptoCurrEnum.ADA.getValue());
		response.setExCurr(ExchangeCurrEnum.EUR.getValue());
		List<IntradaySimulationResponseItem> items = new ArrayList<>();
		IntradaySimulationResponseItem item = new IntradaySimulationResponseItem();
		item.setProfit(1.2);
		item.setExpectedVal(4.58);
		item.setSuccess("40%");
		item.setDateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtils.FORMAT_DDMMYYYY_HHMMSS)));
		items.add(item);
		response.setItems(items);

		return response;
	}
}
