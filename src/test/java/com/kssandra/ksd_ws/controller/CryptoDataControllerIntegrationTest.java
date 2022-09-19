package com.kssandra.ksd_ws.controller;

import java.time.LocalDateTime;
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

import com.kssandra.ksd_persistence.domain.CryptoData;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.repository.CryptoDataTestH2Repository;
import com.kssandra.ksd_ws.request.IntradayDataRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc(addFilters = false)
class CryptoDataControllerIntegrationTest {

	@Autowired
	private WebTestClient testClient;

	@Autowired
	private CryptoDataTestH2Repository cxDataTestRepository;

	@BeforeEach
	private void updateData() {
		List<CryptoData> cxData = cxDataTestRepository.findAll();

		// Update readtime to current date for all records
		cxData = cxData.stream().map(elem -> updateReadTime(elem)).collect(Collectors.toList());

		cxDataTestRepository.saveAll(cxData);
	}

	private CryptoData updateReadTime(CryptoData elem) {

		LocalDateTime dateTime = elem.getReadTime();
		LocalDateTime currTime = LocalDateTime.now();

		dateTime.withMonth(currTime.getMonth().getValue());
		dateTime.withDayOfMonth(currTime.getMonth().getValue());
		dateTime.withYear(currTime.getYear());

		elem.setReadTime(dateTime);

		return elem;
	}

//	@AfterEach
//	private void deleteData() {
//		cxDataTestRepository.deleteAll();
//	}

	@Test
	void testGetIntraDayDataKO() throws Exception {

		IntradayDataRequest intraRq = new IntradayDataRequest();
		intraRq.setCxCurr("BTC");
		intraRq.setExCurr("EUR");
		intraRq.setExtended(false);
		intraRq.setInterval(IntervalEnum.M15.getName());

		testClient.post().uri("/api/v1/intraday/data").contentType(MediaType.APPLICATION_JSON).bodyValue(intraRq)
				.exchange().expectStatus().isOk().expectBody().jsonPath("cxCurr", "BTC");

	}

}
