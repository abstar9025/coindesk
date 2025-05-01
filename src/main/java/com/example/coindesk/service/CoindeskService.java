package com.example.coindesk.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.coindesk.entity.Currency;
import com.example.coindesk.repository.CurrencyRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CoindeskService {

	@Autowired
	private CurrencyRepository currencyRepository;

	private static final String COINDESK_API = "https://kengp3.github.io/blog/coindesk.json";

	public List<Currency> getAllCurrencies() {
		return currencyRepository.findAll();
	}

	public Currency getCurrency(String code) {
		return currencyRepository.findById(code).orElse(null);
	}

	public Currency addCurrency(Currency currency) {
		System.out.println("收到新增請求: code=" + currency.getCode() + ", chineseName=" + currency.getChineseName());
		return currencyRepository.save(currency);
	}

	public Currency updateCurrency(String code, Currency updated) {
		Currency currency = currencyRepository.findById(code).orElse(null);
		if (currency != null) {
			currency.setChineseName(updated.getChineseName());
			return currencyRepository.save(currency);
		}
		return null;
	}

	public boolean deleteCurrency(String code) {
		if (currencyRepository.existsById(code)) {
			currencyRepository.deleteById(code);
			return true;
		}
		return false;
	}

	public Map<String, Object> getTransformedData() throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		String json = restTemplate.getForObject(COINDESK_API, String.class);
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode root = objectMapper.readTree(json);

		String updatedTime = root.path("time").path("updatedISO").asText();
		LocalDateTime dateTime = LocalDateTime.parse(updatedTime, DateTimeFormatter.ISO_DATE_TIME);
		String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

		JsonNode bpi = root.path("bpi");
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("更新時間", formattedTime);

		List<Map<String, String>> currencyInfo = new ArrayList<>();
		bpi.fields().forEachRemaining(entry -> {
			String code = entry.getKey();
			String rate = entry.getValue().path("rate").asText();
			String chineseName = currencyRepository.findById(code).map(Currency::getChineseName).orElse("未定義");

			Map<String, String> data = new LinkedHashMap<>();
			data.put("幣別", code);
			data.put("幣別中文名稱", chineseName);
			data.put("匯率", rate);
			currencyInfo.add(data);
		});

		result.put("幣別資訊", currencyInfo);
		return result;
	}
}
