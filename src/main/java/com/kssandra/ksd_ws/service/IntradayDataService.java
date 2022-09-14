package com.kssandra.ksd_ws.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kssandra.ksd_common.dto.CryptoCurrencyDto;
import com.kssandra.ksd_common.dto.CryptoDataDto;

import com.kssandra.ksd_common.util.PriceUtils;
import com.kssandra.ksd_persistence.dao.CryptoCurrencyDao;
import com.kssandra.ksd_persistence.dao.CryptoDataDao;

import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradayDataRequest;
import com.kssandra.ksd_ws.response.IntradayDataResponse;
import com.kssandra.ksd_ws.response.IntradayDataResponseItem;

/**
 * Service class for /intraday/data endpoint
 *
 * @author aquesada
 */
@Service
public class IntradayDataService {

	/** The crypto data DAO. */
	@Autowired
	CryptoDataDao cryptoDataDao;

	/** The crypto currency DAO. */
	@Autowired
	CryptoCurrencyDao cxCurrDao;

	/**
	 * Gets the last 24h price data of the specified crypto currency.
	 *
	 * @param intraRq the request
	 * @return the price data
	 * @throws KsdServiceException the custom exception
	 */
	public IntradayDataResponse getData(IntradayDataRequest intraRq) throws KsdServiceException {

		CryptoCurrencyDto cxCurrDto = cxCurrDao.findByCode(intraRq.getCxCurr());

		if (cxCurrDto != null) {
			List<CryptoDataDto> data = cryptoDataDao.findAfterDate(cxCurrDto, LocalDateTime.now().minusDays(1));
			IntervalEnum interval = IntervalEnum.fromName(intraRq.getInterval());

			IntradayDataResponse response = new IntradayDataResponse();
			response.setCxCurr(intraRq.getCxCurr());
			response.setExCurr(intraRq.getExCurr());
			response.setItems(getItems(data, intraRq.isExtended(), interval));

			return response;

		} else {
			throw new KsdServiceException("Any cxcurrency found in DB for code: ".concat(intraRq.getCxCurr()));
		}

	}

	/**
	 * Builds the response list of items.
	 *
	 * @param data     las 24h price data
	 * @param extended extended price info
	 * @param interval time interval
	 * @return the list of response items
	 */
	private List<IntradayDataResponseItem> getItems(List<CryptoDataDto> data, boolean extended, IntervalEnum interval) {
		List<IntradayDataResponseItem> items = new ArrayList<>();

		// For every dto read from DB:
		// 1.- Check if dto time matches interval times. E.g. For 15min interval, dto
		// times should be HH:00, HH:15, HH:30 or HH:45
		// 2.- Sort elements descending according to the price time
		// 3.- Build response item and add it to list
		data.stream().filter(dto -> interval.getValues().contains(dto.getReadTime().getMinute()))
				.sorted((e1, e2) -> e2.getReadTime().compareTo(e1.getReadTime()))
				.forEach(dto -> items.add(getIntraRsItem(dto, extended)));

		return items;
	}

	/**
	 * Build the respose item.
	 *
	 * @param dto      dto price data
	 * @param extended extended price info
	 * @return the response item
	 */
	private IntradayDataResponseItem getIntraRsItem(CryptoDataDto dto, boolean extended) {
		IntradayDataResponseItem item = new IntradayDataResponseItem();

		if (extended) {
			item.setClose(PriceUtils.roundPrice(dto.getClose()));
			item.setHigh(PriceUtils.roundPrice(dto.getHigh()));
			item.setLow(PriceUtils.roundPrice(dto.getLow()));
			item.setOpen(PriceUtils.roundPrice(dto.getOpen()));
			item.setAvg(null);
		} else {
			item.setAvg(PriceUtils.roundPrice((dto.getHigh() + dto.getLow()) / 2));
			item.setClose(null);
			item.setHigh(null);
			item.setLow(null);
			item.setOpen(null);
		}

		item.setDateTime(dto.getReadTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

		return item;
	}

}
