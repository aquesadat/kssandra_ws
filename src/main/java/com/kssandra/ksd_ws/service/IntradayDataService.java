package com.kssandra.ksd_ws.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

@Service
public class IntradayDataService {

	@Autowired
	CryptoDataDao cryptoDataDao;

	@Autowired
	CryptoCurrencyDao cxCurrDao;

	public IntradayDataResponse getData(IntradayDataRequest intraRq) throws KsdServiceException {

		IntradayDataResponse response = null;

		CryptoCurrencyDto cxCurrDto = cxCurrDao.findByCode(intraRq.getCxCurr().getValue());

		if (cxCurrDto != null) {
			List<CryptoDataDto> data = cryptoDataDao.findAfterDate(cxCurrDto, LocalDateTime.now().minusDays(1));
			IntervalEnum interval = IntervalEnum.fromName(intraRq.getInterval());

			response = new IntradayDataResponse();
			response.setCxCurr(intraRq.getCxCurr().getValue());
			response.setExCurr(intraRq.getExCurr().getValue());
			response.setItems(getItems(data, intraRq.isExtended(), interval));

		} else {
			throw new KsdServiceException(
					"Any cxcurrency found in DB for code: ".concat(intraRq.getCxCurr().getValue()));
		}

		return response;
	}

	private List<IntradayDataResponseItem> getItems(List<CryptoDataDto> data, boolean extended, IntervalEnum interval) {
		List<IntradayDataResponseItem> items = new ArrayList<>();

		data.stream().filter(dto -> interval.getValues().contains(dto.getReadTime().getMinute()))
				.sorted((e1, e2) -> e2.getReadTime().compareTo(e1.getReadTime()))
				.forEach(dto -> items.add(getIntraRsItem(dto, extended)));

		return items;
	}

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
