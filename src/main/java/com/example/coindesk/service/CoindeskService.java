package com.example.coindesk.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.coindesk.entity.Currency;
import com.example.coindesk.repository.CurrencyRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CoindeskService {
	private static final String COINDESK_URL = "https://kengp3.github.io/blog/coindesk.json";
	private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	private static final DateTimeFormatter OUT_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	@Autowired
	private CurrencyRepository repo;

	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * 首次啟動時載入遠端api，初始化本地資料庫
	 */
	@PostConstruct
	public void initialLoad() {
		try {
			fetchAndSave();
		} catch (IOException e) {
			throw new RuntimeException("Initial load failed: " + e.getMessage(), e);
		}
	}

	/**
	 * 從遠端取得所有匯率並寫入本地
	 */
	private void fetchAndSave() throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(COINDESK_URL).openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);
		Map<String, Object> rawJson;
		try (InputStream in = conn.getInputStream();
				InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
			rawJson = mapper.readValue(reader, new TypeReference<Map<String, Object>>() {
			});
		} finally {
			conn.disconnect();
		}
		@SuppressWarnings("unchecked")
		Map<String, Map<String, Object>> bpi = (Map<String, Map<String, Object>>) rawJson.get("bpi");
		// 取得api的更新時間並轉換
		@SuppressWarnings("unchecked")
		Map<String, String> timeNode = (Map<String, String>) rawJson.get("time");
		String updatedISO = timeNode.get("updatedISO");
		LocalDateTime remoteTime = OffsetDateTime.parse(updatedISO, ISO_FMT).atZoneSameInstant(ZoneId.of("Asia/Taipei"))
				.toLocalDateTime();
		// 確認table中有無此幣別，沒有的話新增
		bpi.forEach((code, detail) -> {
			if (!repo.existsById(code)) {
				Currency currency = new Currency();
				currency.setCode(code);
				currency.setName((String) detail.get("description"));
				currency.setRate((String) detail.get("rate"));
				currency.setUpdated(remoteTime);
				repo.save(currency);
			} else {
				Currency currency = repo.findById(code).get();
				currency.setRate((String) detail.get("rate"));
				currency.setUpdated(remoteTime);
				repo.save(currency);
			}
		});
	}

	/**
	 * 只讀本地資料庫，並回傳更新時間與資料
	 */
	public Map<String, Object> getLocalData() {
		List<Currency> list = repo.findAll();
		// 最近更新時間
		Optional<LocalDateTime> max = list.stream().map(Currency::getUpdated).filter(t -> t != null)
				.max(LocalDateTime::compareTo);
		String updated = max.map(t -> t.format(OUT_FMT)).orElse("");
		// 整理出前端List需要的資料
		List<Map<String, String>> data = list.stream().map(c -> {
			Map<String, String> map = new LinkedHashMap<>();
			map.put("幣別", c.getCode());
			map.put("幣別中文名稱", c.getName());
			map.put("匯率", c.getRate());
			return map;
		}).collect(Collectors.toList());
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("更新時間", updated);
		result.put("資料", data);
		return result;
	}

	/**
	 * 查詢所有資料
	 */
	public List<Currency> listAll() {
		return repo.findAll();
	}

	/**
	 * 新增幣別
	 * 
	 * @param currency
	 * @return
	 */
	public Currency create(Currency currency) {
		// 檢查幣別是否重複
		if (repo.existsById(currency.getCode())) {
			throw new IllegalArgumentException("幣別已存在: " + currency.getCode());
		}
		currency.setUpdated(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
		return repo.save(currency);
	}

	/**
	 * 更新幣別
	 * 
	 * @param code
	 * @param currency
	 * @return
	 */
	public Currency update(String code, Currency currency) {
		// 查詢是否為已有的幣別再更新
		Currency exist = repo.findById(code).orElseThrow(() -> new IllegalArgumentException("找不到幣別: " + code));
		// 若傳回來有值才更新
		if (StringUtils.hasText(currency.getName())) {
			exist.setName(currency.getName());
		}
		if (StringUtils.hasText(currency.getRate())) {
			exist.setRate(currency.getRate());
		}
		exist.setUpdated(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
		return repo.save(exist);
	}

	/**
	 * 刪除幣別
	 * 
	 * @param code
	 */
	public void delete(String code) {
		repo.deleteById(code);
	}

	/**
	 * 單元測試用
	 * 
	 * @param json
	 * @param time
	 * @return
	 */
	public Map<String, Object> transform(Map<String, Object> json, LocalDateTime time) {
		@SuppressWarnings("unchecked")
		Map<String, Map<String, Object>> bpi = (Map<String, Map<String, Object>>) json.get("bpi");

		List<Map<String, String>> list = new ArrayList<>();
		for (Map.Entry<String, Map<String, Object>> e : bpi.entrySet()) {
			Map<String, Object> d = e.getValue();
			Map<String, String> item = new LinkedHashMap<>();
			item.put("幣別", e.getKey());
			item.put("幣別中文名稱", (String) d.get("description"));
			item.put("匯率", (String) d.get("rate"));
			list.add(item);
		}

		String formatted = time.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
		Map<String, Object> out = new LinkedHashMap<>();
		out.put("更新時間", formatted);
		out.put("資料", list);
		return out;
	}
}