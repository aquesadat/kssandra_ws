/**
 * 
 */
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
import com.kssandra.ksd_ws.request.IntradaySimulationRequest;
import com.kssandra.ksd_ws.response.IntradaySimulationResponseItem;
import com.kssandra.ksd_ws.response.IntradaySimulationResponse;

/**
 * Integration test class for SimulationController
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
class SimulationControllerItegrationTest {

	@Autowired
	private WebTestClient testClient;

	/** The cx data test repository. */
	@Autowired
	private CryptoPredictionTestH2Repository cxPredictTestRepository;

	private static final String urlEndpoint = "/api/v1/intraday/simulate";

	IntradaySimulationRequest intraRq;

	@BeforeEach
	private void updateData() {
		List<Prediction> cxPredict = cxPredictTestRepository.findAll();

		// Update readtime to current date for all records
		cxPredict = cxPredict.stream().map(elem -> updateReadTime(elem)).collect(Collectors.toList());

		cxPredictTestRepository.saveAll(cxPredict);

		intraRq = new IntradaySimulationRequest();

		intraRq.setCxCurr(CryptoCurrEnum.BTC.getValue());
		intraRq.setExCurr(ExchangeCurrEnum.EUR.getValue());
		intraRq.setInterval(IntervalEnum.M15.getName());
		intraRq.setAmount(Double.valueOf(100));
	}

	/**
	 * Test method for getIntraDayData with bad request response (cxCurr)
	 * {@link com.kssandra.ksd_ws.controller.SimulationController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySimulationRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-Simulation. BadRQ cxCurr")
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
	 * {@link com.kssandra.ksd_ws.controller.SimulationController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySimulationRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-Simulation. BadRQ exCurr")
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
	 * {@link com.kssandra.ksd_ws.controller.SimulationController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySimulationRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-Simulation. BadRQ interval")
	void testGetIntraDayDataIntervalBadRequest() throws Exception {

		intraRq.setInterval(null);
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "interval - Missing field value");

		intraRq.setInterval("XXX");
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "interval - Invalid field value");
	}

	/**
	 * Test method for getIntraDayData with bad request response (amount)
	 * {@link com.kssandra.ksd_ws.controller.SimulationController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySimulationRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-Simulation. BadRQ amount")
	void testGetIntraDayDataAmountBadRequest() throws Exception {

		intraRq.setAmount(null);
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "amount - Missing field value");

		intraRq.setAmount(Double.valueOf(0));
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "amount - Invalid field value");

		intraRq.setAmount(-3.5);
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "amount - Invalid field value");
	}

	/**
	 * Test method for getIntraDayData with bad request response (purchaseFee)
	 * {@link com.kssandra.ksd_ws.controller.SimulationController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySimulationRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-Simulation. BadRQ purchaseFee")
	void testGetIntraDayDataPurchaseFeeBadRequest() throws Exception {

		intraRq.setPurchaseFee(-0.5);
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "purchaseFee - Invalid field value");
	}

	/**
	 * Test method for getIntraDayData with bad request response (saleFee)
	 * {@link com.kssandra.ksd_ws.controller.SimulationController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySimulationRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-Simulation. BadRQ saleFee")
	void testGetIntraDayDataSaleFeeBadRequest() throws Exception {

		intraRq.setSaleFee(-0.5);
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "saleFee - Invalid field value");

		intraRq.setSaleFee(100.5);
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("message", "saleFee - Invalid field value");
	}

	/**
	 * Test method for getIntraDayData with custom exception (cx not configured)
	 * {@link com.kssandra.ksd_ws.controller.SimulationController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySimulationRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-Simulation. Custom Exception cx not configured")
	void testGetIntraDayDataCustomExCxNotConfig() throws Exception {

		intraRq.setInterval(IntervalEnum.M15.getName());
		intraRq.setCxCurr(CryptoCurrEnum.ETH.getValue());
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isEqualTo(HttpStatus.CONFLICT);

	}

	/**
	 * Test method for getIntraDayData with custom exception (datetime not valid)
	 * {@link com.kssandra.ksd_ws.controller.SimulationController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySimulationRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-Simulation. Custom Exception datetime not valid")
	void testGetIntraDayDataCustomEx() throws Exception {

		// Request dateTime is not valid (more than 24h)
		intraRq.setDateTime(
				LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ofPattern(DateUtils.FORMAT_DDMMYYYY_HHMM)));
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isEqualTo(HttpStatus.CONFLICT);

	}

	/**
	 * Test method for getIntraDayData with no price data stored
	 * {@link com.kssandra.ksd_ws.controller.SimulationController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySimulationRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-Simulation. No price data stored")
	void testGetIntraDayDataNoPriceData() throws Exception {

		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isOk().expectBody().jsonPath("cxCurr").isEqualTo(CryptoCurrEnum.BTC.getValue())
				.jsonPath("exCurr").isEqualTo(ExchangeCurrEnum.EUR.getValue()).jsonPath("items").isEmpty();

	}

	/**
	 * Test method for getIntraDayData with OK response
	 * {@link com.kssandra.ksd_ws.controller.SimulationController#getIntraDayData(com.kssandra.ksd_ws.request.IntradaySimulationRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	@Test
	void testGetIntraDayDataOK() {

		// The crypto currency has prediction data stored in DB
		intraRq.setCxCurr(CryptoCurrEnum.ADA.getValue());

		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isOk().expectBody(IntradaySimulationResponse.class).consumeWith(result -> {
					IntradaySimulationResponse response = result.getResponseBody();
					assertEquals(CryptoCurrEnum.ADA.getValue(), response.getCxCurr());
					assertEquals(ExchangeCurrEnum.EUR.getValue(), response.getExCurr());

					List<IntradaySimulationResponseItem> items = response.getItems();
					assertNotNull(items);
					assertFalse(items.isEmpty());

					IntervalEnum interval = IntervalEnum.valueOf(IntervalEnum.M15.getName());

					Iterator<IntradaySimulationResponseItem> iter = items.iterator();
					IntradaySimulationResponseItem current, previous = iter.next();

					while (iter.hasNext()) {
						current = iter.next();

						// Minutes match interval values
						LocalDateTime prevDateTime = LocalDateTime.parse(previous.getDateTime(),
								DateTimeFormatter.ofPattern(DateUtils.FORMAT_DDMMYYYY_HHMM_2));
						assertNotNull(current.getDateTime());
						assertTrue(interval.getValues().contains(prevDateTime.getMinute()));
						assertTrue(previous.getSuccess().equals("N/A") || previous.getSuccess().contains("%"));
						assertTrue(previous.getExpectedVal() != null);
						assertTrue(previous.getProfit() != null);

						// Items are sorted by date ascending
						LocalDateTime currDateTime = LocalDateTime.parse(current.getDateTime(),
								DateTimeFormatter.ofPattern(DateUtils.FORMAT_DDMMYYYY_HHMM_2));

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
