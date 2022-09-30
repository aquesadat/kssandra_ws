/**
 * 
 */
package com.kssandra.ksd_ws.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.kssandra.ksd_persistence.domain.Prediction;
import com.kssandra.ksd_ws.enums.ExchangeCurrEnum;
import com.kssandra.ksd_ws.repository.CryptoPredictionTestH2Repository;
import com.kssandra.ksd_ws.request.IntradaySuggestionRequest;
import com.kssandra.ksd_ws.response.IntradaySuggestionResponse;
import com.kssandra.ksd_ws.response.IntradaySuggestionResponseItem;
import com.kssandra.ksd_ws.service.IntradaySuggestionService;

/**
 * Integration test class for SuggestionController
 * 
 * @author aquesada
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc(addFilters = false)
class SuggestionControllerIntegrationTest {

	@Autowired
	private WebTestClient testClient;

	/** The cx data test repository. */
	@Autowired
	private CryptoPredictionTestH2Repository cxPredictTestRepository;

	private static final String urlEndpoint = "/api/v1/intraday/suggest";

	@BeforeEach
	private void updateData() {
		List<Prediction> cxPredict = cxPredictTestRepository.findAll();

		// Update readtime to current date for all records
		cxPredict = cxPredict.stream().map(elem -> updateReadTime(elem)).collect(Collectors.toList());

		cxPredictTestRepository.saveAll(cxPredict);
	}

	/**
	 * Test method for getIntraDayData with any kind of KO response
	 * {@link com.kssandra.ksd_ws.controller.SuggestionController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySuggestionRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	@Test
	void testGetIntraDayDataKO() {
		IntradaySuggestionRequest intraRq = new IntradaySuggestionRequest();

		// Bad Request - exCurr
		intraRq.setExCurr(null);
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "exCurr - Missing field value");

		intraRq.setExCurr("XXX");
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "exCurr - Invalid field value");

		// Bad Request - numResult
		intraRq.setExCurr(ExchangeCurrEnum.EUR.getValue());
		intraRq.setNumResult(0);
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "numResult - Invalid field value");

		intraRq.setNumResult(IntradaySuggestionService.MAX_RESULTS + 1);
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "numResult - Invalid field value");
	}

	/**
	 * Test method for getIntraDayData with OK response
	 * {@link com.kssandra.ksd_ws.controller.SuggestionController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySuggestionRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	@Test
	void testGetIntraDayDataOK() {
		IntradaySuggestionRequest intraRq = new IntradaySuggestionRequest();

		intraRq.setExCurr(ExchangeCurrEnum.EUR.getValue());
		intraRq.setNumResult(2);

		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isOk().expectBody(IntradaySuggestionResponse.class).consumeWith(result -> {
					IntradaySuggestionResponse response = result.getResponseBody();
					assertEquals(ExchangeCurrEnum.EUR.getValue(), response.getExCurr());

					List<IntradaySuggestionResponseItem> items = response.getItems();
					assertNotNull(items);
					assertFalse(items.isEmpty());
					assertEquals(2, items.size());
					Iterator<IntradaySuggestionResponseItem> iter = items.iterator();
					IntradaySuggestionResponseItem current, previous = iter.next();

					while (iter.hasNext()) {
						current = iter.next();

						assertTrue(current.getRank() > previous.getRank());
						previous = current;
					}
				});

	}

	private Prediction updateReadTime(Prediction pred) {
		LocalDateTime currTime = pred.getCurrTime();
		long diffHours = ChronoUnit.HOURS.between(currTime, LocalDateTime.now());

		pred.setCurrTime(LocalDateTime.now());
		pred.setPredictTime(pred.getPredictTime().plusHours(diffHours));

		return pred;
	}

}
