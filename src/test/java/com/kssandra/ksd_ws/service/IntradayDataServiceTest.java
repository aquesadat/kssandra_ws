/**
 * 
 */
package com.kssandra.ksd_ws.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.kssandra.ksd_common.dto.CryptoCurrencyDto;
import com.kssandra.ksd_common.dto.CryptoDataDto;
import com.kssandra.ksd_common.util.DateUtils;
import com.kssandra.ksd_persistence.dao.CryptoCurrencyDao;
import com.kssandra.ksd_persistence.dao.CryptoDataDao;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradayDataRequest;
import com.kssandra.ksd_ws.response.IntradayDataResponse;
import com.kssandra.ksd_ws.response.IntradayDataResponseItem;

/**
 * Test class for IntradayDataService
 * 
 * @author aquesada
 *
 */
@SpringBootTest
class IntradayDataServiceTest {

	@MockBean
	CryptoCurrencyDao cxCurrDao;

	@MockBean
	CryptoDataDao cryptoDataDao;

	@Autowired
	IntradayDataService indradayDataService;

	/**
	 * Test method for
	 * {@link com.kssandra.ksd_ws.service.IntradayDataService#getData(com.kssandra.ksd_ws.request.IntradayDataRequest)}.
	 * 
	 * @throws KsdServiceException
	 */
	@Test
	@DisplayName("CxData - Cx not configured")
	void testGetDataCxNotConfigured() throws KsdServiceException {
		// Crypto currency not configured in DB
		when(cxCurrDao.findByCode("XXX")).thenReturn(null);

		assertThrows(KsdServiceException.class,
				() -> indradayDataService.getData(buildIntraRq("XXX", null, false, null)));
	}

	/**
	 * Test method for
	 * {@link com.kssandra.ksd_ws.service.IntradayDataService#getData(com.kssandra.ksd_ws.request.IntradayDataRequest)}.
	 * 
	 * @throws KsdServiceException
	 */
	@Test
	@DisplayName("CxData - Price not stored")
	void testGetDataPriceNotStored() throws KsdServiceException {
		// No price data stored in DB for the crypto currency
		CryptoCurrencyDto cxCurr = new CryptoCurrencyDto("AAA");

		when(cxCurrDao.findByCode("AAA")).thenReturn(cxCurr);
		when(cryptoDataDao.findAfterDate(eq(cxCurr), any())).thenReturn(new ArrayList<CryptoDataDto>());

		IntradayDataResponse response = indradayDataService.getData(buildIntraRq("AAA", "EUR", false, "M15"));

		assertEquals("AAA", response.getCxCurr());
		assertEquals("EUR", response.getExCurr());
		assertTrue(response.getItems().isEmpty());
	}

	/**
	 * Test method for
	 * {@link com.kssandra.ksd_ws.service.IntradayDataService#getData(com.kssandra.ksd_ws.request.IntradayDataRequest)}.
	 * 
	 * @throws KsdServiceException
	 */
	@Test
	void testGetDataShortResponse() throws KsdServiceException {

		// The crypto currency has price data stored in DB
		CryptoCurrencyDto cxCurr = new CryptoCurrencyDto("BBB");
		String rqInterval = "M15";

		when(cxCurrDao.findByCode("BBB")).thenReturn(cxCurr);
		when(cryptoDataDao.findAfterDate(eq(cxCurr), any())).thenReturn(buildItemList());

		IntradayDataResponse response = indradayDataService.getData(buildIntraRq("BBB", "EUR", false, rqInterval));

		assertEquals("BBB", response.getCxCurr());
		assertEquals("EUR", response.getExCurr());
		IntervalEnum interval = IntervalEnum.valueOf(rqInterval);
		List<IntradayDataResponseItem> items = response.getItems();
		assertNotNull(items);
		assertFalse(items.isEmpty());
		for (IntradayDataResponseItem item : items) {
			// Minutes match interval values
			assertNotNull(item.getDateTime());
			LocalDateTime itemDateTime = LocalDateTime.parse(item.getDateTime(),
					DateTimeFormatter.ofPattern(DateUtils.FORMAT_DDMMYYYY_HHMMSS));
			assertTrue(interval.getValues().contains(itemDateTime.getMinute()));

			// Not extended -> Only average
			assertNull(item.getClose());
			assertNull(item.getHigh());
			assertNull(item.getLow());
			assertNull(item.getOpen());
			assertNotNull(item.getAvg());
		}

	}

	/**
	 * Test method for
	 * {@link com.kssandra.ksd_ws.service.IntradayDataService#getData(com.kssandra.ksd_ws.request.IntradayDataRequest)}.
	 * 
	 * @throws KsdServiceException
	 */
	@Test
	void testGetDataExtendedResponse() throws KsdServiceException {

		CryptoCurrencyDto cxCurr = new CryptoCurrencyDto("BBB");
		String rqInterval = "M15";

		when(cxCurrDao.findByCode("BBB")).thenReturn(cxCurr);
		when(cryptoDataDao.findAfterDate(eq(cxCurr), any())).thenReturn(buildItemList());

		IntradayDataResponse response = indradayDataService.getData(buildIntraRq("BBB", "EUR", false, rqInterval));

		// Extended response
		response = indradayDataService.getData(buildIntraRq("BBB", "EUR", true, rqInterval));
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
	}

	private List<CryptoDataDto> buildItemList() {

		List<CryptoDataDto> items = new ArrayList<>();
		Random r = new Random();
		LocalDateTime readTime = LocalDateTime.now();

		for (int i = 0; i < 100; i++) {
			CryptoDataDto dto = new CryptoDataDto();
			dto.setClose(r.nextDouble());
			dto.setHigh(r.nextDouble());
			dto.setId(i);
			dto.setLow(r.nextDouble());
			dto.setOpen(r.nextDouble());
			dto.setReadTime(readTime.plusMinutes(i).withSecond(0));
			items.add(dto);
		}

		return items;
	}

	private IntradayDataRequest buildIntraRq(String cxCurr, String exCurr, boolean extended, String interval) {
		IntradayDataRequest request = new IntradayDataRequest();

		request.setCxCurr(cxCurr);
		request.setExCurr(exCurr);
		request.setExtended(extended);
		request.setInterval(interval);

		return request;
	}

}
