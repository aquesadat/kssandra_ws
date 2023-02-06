package com.kssandra.ksd_ws.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.kssandra.ksd_common.util.DateUtils;
import com.kssandra.ksd_persistence.domain.Prediction;
import com.kssandra.ksd_ws.enums.CryptoCurrEnum;
import com.kssandra.ksd_ws.enums.ExchangeCurrEnum;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.repository.CryptoPredictionTestH2Repository;
import com.kssandra.ksd_ws.request.IntradayPredictionRequest;

import com.kssandra.ksd_ws.response.IntradayPredictionResponse;
import com.kssandra.ksd_ws.response.IntradayPredictionResponseItem;

/**
 * Integration test class for PredictionController
 * 
 * @author aquesada
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc(addFilters = false)
@ComponentScan({ "com.kssandra" })
@EntityScan("com.kssandra.ksd_persistence.domain")
@EnableJpaRepositories("com.kssandra.ksd_ws.repository")
class PredictionControllerIntegrationTest {

	@Autowired
	private WebTestClient testClient;

	/** The cx data test repository. */
	@Autowired
	private CryptoPredictionTestH2Repository cxPredictTestRepository;

	private static final String urlEndpoint = "/api/v1/intraday/prediction";

	IntradayPredictionRequest intraRq;

	@BeforeEach
	private void updateData() {
		List<Prediction> cxPredict = cxPredictTestRepository.findAll();

		// Update readtime to current date for all records
		cxPredict = cxPredict.stream().map(elem -> updateReadTime(elem)).collect(Collectors.toList());

		cxPredictTestRepository.saveAll(cxPredict);

		intraRq = new IntradayPredictionRequest();
		intraRq.setCxCurr(CryptoCurrEnum.BTC.getValue());
		intraRq.setExCurr(ExchangeCurrEnum.EUR.getValue());
		intraRq.setInterval(IntervalEnum.M15.getName());
	}

	/**
	 * Test method for getIntraDayData with bad request response (cxCurr)
	 * {@link com.kssandra.ksd_ws.controller.PredictionController#getIntraDayData(com.kssandra.ksd_ws.request.IntradayPredictionRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-Predict. BadRQ cxCurr")
	void testGetIntraDayDataCxCurrBadRequest() throws Exception {

		intraRq.setCxCurr(null);
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "cxCurr - Missing field value");

		intraRq.setCxCurr("XXX");
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "cxCurr - Invalid field value");
	}

	/**
	 * Test method for getIntraDayData with bad request response (exCurr)
	 * {@link com.kssandra.ksd_ws.controller.PredictionController#getIntraDayData(com.kssandra.ksd_ws.request.IntradayPredictionRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-Predict. BadRQ exCurr")
	void testGetIntraDayDataExCurrBadRequest() throws Exception {

		intraRq.setExCurr(null);
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "exCurr - Missing field value");

		intraRq.setExCurr("XXX");
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "exCurr - Invalid field value");
	}

	/**
	 * Test method for getIntraDayData with bad request response (interval)
	 * {@link com.kssandra.ksd_ws.controller.PredictionController#getIntraDayData(com.kssandra.ksd_ws.request.IntradayPredictionRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-Predict. BadRQ interval")
	void testGetIntraDayDataIntervalBadRequest() throws Exception {

		intraRq.setInterval(null);
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "interval - Missing field value");

		intraRq.setInterval("XXX");
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "interval - Invalid field value");
	}

	/**
	 * Test method for getIntraDayData with custom exception
	 * {@link com.kssandra.ksd_ws.controller.PredictionController#getIntraDayData(com.kssandra.ksd_ws.request.IntradayPredictionRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-Predict. Custom Exception")
	void testGetIntraDayDataCustomEx() throws Exception {

		intraRq.setCxCurr(CryptoCurrEnum.ETH.getValue());
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isEqualTo(HttpStatus.CONFLICT);

	}

	/**
	 * Test method for getIntraDayData with no price data stored
	 * {@link com.kssandra.ksd_ws.controller.PredictionController#getIntraDayData(com.kssandra.ksd_ws.request.IntradayPredictionRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-Prediction. No price data stored")
	void testGetIntraDayDataNoPriceData() throws Exception {

		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isOk().expectBody().jsonPath("cxCurr").isEqualTo(CryptoCurrEnum.BTC.getValue())
				.jsonPath("exCurr").isEqualTo(ExchangeCurrEnum.EUR.getValue()).jsonPath("items").isEmpty();

	}

	/**
	 * Test method for getIntraDayData with OK response
	 * {@link com.kssandra.ksd_ws.controller.PredictionController#getIntraDayData(com.kssandra.ksd_ws.request.IntradayPredictionRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	void testGetIntraDayDataOK() throws Exception {

		// The crypto currency has prediction data stored in DB
		intraRq.setCxCurr(CryptoCurrEnum.ADA.getValue());

		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isOk().expectBody(IntradayPredictionResponse.class).consumeWith(result -> {
					IntradayPredictionResponse response = result.getResponseBody();
					assertEquals(CryptoCurrEnum.ADA.getValue(), response.getCxCurr());
					assertEquals(ExchangeCurrEnum.EUR.getValue(), response.getExCurr());

					List<IntradayPredictionResponseItem> items = response.getItems();
					assertNotNull(items);
					assertFalse(items.isEmpty());

					IntervalEnum interval = IntervalEnum.valueOf(IntervalEnum.M15.getName());

					Iterator<IntradayPredictionResponseItem> iter = items.iterator();
					IntradayPredictionResponseItem current, previous = iter.next();

					while (iter.hasNext()) {
						current = iter.next();

						// Minutes match interval values
						LocalDateTime prevDateTime = LocalDateTime.parse(previous.getDateTime(),
								DateTimeFormatter.ofPattern(DateUtils.FORMAT_DDMMYYYY_HHMMSS));
						assertNotNull(current.getDateTime());
						assertTrue(interval.getValues().contains(prevDateTime.getMinute()));
						assertTrue(previous.getSuccess().equals("N/A") || previous.getSuccess().contains("%"));
						assertTrue(previous.getExpectedVal() != null);

						// Items are sorted by date ascending
						LocalDateTime currDateTime = LocalDateTime.parse(current.getDateTime(),
								DateTimeFormatter.ofPattern(DateUtils.FORMAT_DDMMYYYY_HHMMSS));

						assertTrue(currDateTime.isAfter(prevDateTime));
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
