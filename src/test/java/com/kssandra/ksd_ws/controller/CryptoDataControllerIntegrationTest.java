package com.kssandra.ksd_ws.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.kssandra.ksd_persistence.domain.CryptoData;
import com.kssandra.ksd_ws.enums.CryptoCurrEnum;
import com.kssandra.ksd_ws.enums.ExchangeCurrEnum;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.repository.CryptoDataTestH2Repository;
import com.kssandra.ksd_ws.request.IntradayDataRequest;
import com.kssandra.ksd_ws.response.IntradayDataResponse;
import com.kssandra.ksd_ws.response.IntradayDataResponseItem;

/**
 * Integration test class for CryptoDataController.
 *
 * @author aquesada
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc(addFilters = false)
@ComponentScan({ "com.kssandra" })
@EntityScan("com.kssandra.ksd_persistence.domain")
@EnableJpaRepositories("com.kssandra.ksd_ws.repository")
class CryptoDataControllerIntegrationTest {

	/** The test client. */
	@Autowired
	private WebTestClient testClient;

	/** The cx data test repository. */
	@Autowired
	private CryptoDataTestH2Repository cxDataTestRepository;

	/** The Constant urlEndpoint. */
	private static final String urlEndpoint = "/api/v1/intraday/data";

	IntradayDataRequest intraRq;

	/**
	 * Update data.
	 */
	@BeforeEach
	private void updateData() {
		List<CryptoData> cxData = cxDataTestRepository.findAll();

		// Update readtime to current date for all records
		cxData = cxData.stream().map(elem -> updateReadTime(elem)).collect(Collectors.toList());

		cxDataTestRepository.saveAll(cxData);

		intraRq = new IntradayDataRequest();
		intraRq.setCxCurr(CryptoCurrEnum.BTC.getValue());
		intraRq.setExCurr(ExchangeCurrEnum.EUR.getValue());
		intraRq.setExtended(false);
		intraRq.setInterval(IntervalEnum.M15.getName());
	}

	/**
	 * Test method for getIntraDayData with bad request response (cxCurr)
	 * {@link com.kssandra.ksd_ws.controller.CryptoDataController#getIntraDayData(com.kssandra.ksd_ws.request.IntradayDataRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-CxData. BadRQ cxCurr")
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
	 * {@link com.kssandra.ksd_ws.controller.CryptoDataController#getIntraDayData(com.kssandra.ksd_ws.request.IntradayDataRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-CxData. BadRQ exCurr")
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
	 * {@link com.kssandra.ksd_ws.controller.CryptoDataController#getIntraDayData(com.kssandra.ksd_ws.request.IntradayDataRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-CxData. BadRQ interval")
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
	 * {@link com.kssandra.ksd_ws.controller.CryptoDataController#getIntraDayData(com.kssandra.ksd_ws.request.IntradayDataRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-CxData. Custom Exception")
	void testGetIntraDayDataCustomEx() throws Exception {

		intraRq.setCxCurr(CryptoCurrEnum.ETH.getValue());
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isEqualTo(HttpStatus.CONFLICT);

	}

	/**
	 * Test method for getIntraDayData with no price data stored
	 * {@link com.kssandra.ksd_ws.controller.CryptoDataController#getIntraDayData(com.kssandra.ksd_ws.request.IntradayDataRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-CxData. No price data stored")
	void testGetIntraDayDataNoPriceData() throws Exception {

		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isOk().expectBody().jsonPath("cxCurr").isEqualTo(CryptoCurrEnum.BTC.getValue())
				.jsonPath("exCurr").isEqualTo(ExchangeCurrEnum.EUR.getValue()).jsonPath("items").isEmpty();

	}

	/**
	 * Test method for getIntraDayData with OK short response
	 * {@link com.kssandra.ksd_ws.controller.CryptoDataController#getIntraDayData(com.kssandra.ksd_ws.request.IntradayDataRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@DisplayName("Integration-CxData. Short OK response")
	void testGetIntraDayDataOKShortResponse() throws Exception {

		// The crypto currency has price data stored in DB
		intraRq.setCxCurr(CryptoCurrEnum.ADA.getValue());

		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isOk().expectBody(IntradayDataResponse.class).consumeWith(result -> {
					IntradayDataResponse response = result.getResponseBody();
					assertEquals(CryptoCurrEnum.ADA.getValue(), response.getCxCurr());
					assertEquals(ExchangeCurrEnum.EUR.getValue(), response.getExCurr());

					List<IntradayDataResponseItem> items = response.getItems();
					assertNotNull(items);
					assertFalse(items.isEmpty());
					for (IntradayDataResponseItem item : items) {
						// Minutes match interval values
						assertNotNull(item.getDateTime());
						LocalDateTime itemDateTime = LocalDateTime.parse(item.getDateTime(),
								DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
						assertTrue(IntervalEnum.M15.getValues().contains(itemDateTime.getMinute()));

						// Not extended -> Only average
						assertNull(item.getClose());
						assertNull(item.getHigh());
						assertNull(item.getLow());
						assertNull(item.getOpen());
						assertNotNull(item.getAvg());
					}
				});

	}

	/**
	 * Test method for getIntraDayData with OK extended response
	 * {@link com.kssandra.ksd_ws.controller.CryptoDataController#getIntraDayData(com.kssandra.ksd_ws.request.IntradayDataRequest, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	void testGetIntraDayDataOKExtendResponse() throws Exception {

		// The crypto currency has price data stored in DB
		intraRq.setCxCurr(CryptoCurrEnum.ADA.getValue());

		// Extended response
		intraRq.setExtended(true);
		testClient.post().uri(urlEndpoint).contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq).exchange()
				.expectStatus().isOk().expectBody(IntradayDataResponse.class).consumeWith(result -> {
					IntradayDataResponse response = result.getResponseBody();
					List<IntradayDataResponseItem> items = response.getItems();
					assertNotNull(items);
					assertFalse(items.isEmpty());
					for (IntradayDataResponseItem item : items) {
						// Extended -> open, close, high and low
						assertNotNull(item.getClose());
						assertNotNull(item.getHigh());
						assertNotNull(item.getLow());
						assertNotNull(item.getOpen());

						assertNull(item.getAvg());
						assertNotNull(item.getDateTime());
					}
				});

	}

	/**
	 * Updates the day, month and year of the readTime to the current date.
	 *
	 * @param cxData the cx data
	 * @return cryptoData with updated readTime date
	 */
	private CryptoData updateReadTime(CryptoData cxData) {

		LocalDateTime dateTime = cxData.getReadTime();
		LocalDateTime currTime = LocalDateTime.now();

		dateTime = dateTime.withMonth(currTime.getMonth().getValue());
		dateTime = dateTime.withDayOfMonth(currTime.getDayOfMonth());
		dateTime = dateTime.withYear(currTime.getYear());

		cxData.setReadTime(dateTime);

		return cxData;
	}

}
