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
import com.kssandra.ksd_persistence.dao.CryptoCurrencyDao;
import com.kssandra.ksd_persistence.dao.CryptoDataDao;
import com.kssandra.ksd_ws.enums.IntervalEnum;
import com.kssandra.ksd_ws.exception.KsdServiceException;
import com.kssandra.ksd_ws.request.IntradayDataRequest;
import com.kssandra.ksd_ws.response.IntradayDataResponse;
import com.kssandra.ksd_ws.response.IntradayDataResponseItem;

@Service
public class IntraDayDataService {

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
				.map(dto -> items.add(getIntraRsItem(dto, extended))).collect(Collectors.toList());

		return items;
	}

	private IntradayDataResponseItem getIntraRsItem(CryptoDataDto dto, boolean extended) {
		IntradayDataResponseItem item = new IntradayDataResponseItem();

		if (extended) {
			item.setClose(dto.getClose());
			item.setHigh(dto.getHigh());
			item.setLow(dto.getLow());
			item.setOpen(dto.getOpen());
		} else {
			item.setAvg((dto.getHigh() + dto.getLow()) / 2);
		}

		item.setDateTime(dto.getReadTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

		return item;
	}

}
