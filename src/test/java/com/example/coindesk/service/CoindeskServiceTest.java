package com.example.coindesk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class CoindeskServiceTest {

	private final ObjectMapper mapper = new ObjectMapper();
	private final CoindeskService svc = new CoindeskService();

	@Test
	public void testTransform() throws Exception {
		// 1. 手動拼接 JSON 字串（Java8 不支援 Text Block）
		String json = "{" + "\"time\": { \"updatedISO\": \"2024-09-02T07:07:20+00:00\" }," + "\"bpi\": {" + "\"USD\": {"
				+ "\"description\": \"United States Dollar\"," + "\"rate\": \"57,756.298\"" + "}," + "\"EUR\": {"
				+ "\"description\": \"Euro\"," + "\"rate\": \"52,243.287\"" + "}" + "}" + "}";

		// 2. 轉成 Map
		@SuppressWarnings("unchecked")
		Map<String, Object> raw = mapper.readValue(json, Map.class);

		// 3. 固定一個 timestamp
		LocalDateTime ts = LocalDateTime.of(2024, 9, 2, 15, 7, 20);

		// 4. 呼叫你剛抽好的 transform 方法
		Map<String, Object> out = svc.transform(raw, ts);

		// 5. 斷言「更新時間」
		assertEquals("2024/09/02 15:07:20", out.get("更新時間"));

		// 6. 斷言「資料」列表
		@SuppressWarnings("unchecked")
		List<Map<String, String>> list = (List<Map<String, String>>) out.get("資料");
		assertEquals(2, list.size());

		// 分別取 USD 跟 EUR 檢查內容
		Map<String, String> usd = null;
		Map<String, String> eur = null;
		for (Map<String, String> m : list) {
			if ("USD".equals(m.get("幣別")))
				usd = m;
			if ("EUR".equals(m.get("幣別")))
				eur = m;
		}
		assertNotNull(usd);
		assertEquals("United States Dollar", usd.get("幣別中文名稱"));
		assertEquals("57,756.298", usd.get("匯率"));

		assertNotNull(eur);
		assertEquals("Euro", eur.get("幣別中文名稱"));
		assertEquals("52,243.287", eur.get("匯率"));
	}
}