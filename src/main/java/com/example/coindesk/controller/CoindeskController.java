package com.example.coindesk.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.coindesk.entity.Currency;
import com.example.coindesk.service.CoindeskService;

@RestController
@RequestMapping("/api")
public class CoindeskController {

	@Autowired
	private CoindeskService service;

	/**
	 * 讀取本地資料庫資料與更新時間
	 */
	@GetMapping("/coindesk/data")
	public ResponseEntity<?> getLocalData() {
		Map<String, Object> data = service.getLocalData();
		return ResponseEntity.ok(data);
	}

	/**
	 * 取得匯率資料
	 */
	@GetMapping("/currency")
	public ResponseEntity<List<Currency>> listCurrency() {
		return ResponseEntity.ok(service.listAll());
	}

	/**
	 * 新增匯率資料
	 * 
	 * @param currency
	 * @return
	 */
	@PostMapping("/currency")
	public ResponseEntity<?> createCurrency(@RequestBody Currency currency) {
		try {
			Currency created = service.create(currency);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("error", e.getMessage()));
		}
	}

	/**
	 * 依據幣別更新匯率
	 * 
	 * @param code 幣別
	 * @param currency
	 * @return
	 */
	@PutMapping("/currency/{code}")
	public ResponseEntity<?> updateCurrency(@PathVariable String code, @RequestBody Currency currency) {
		try {
			Currency updated = service.update(code, currency);
			return ResponseEntity.ok(updated);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
		}
	}

	/**
	 * 刪除匯率
	 * 
	 * @param code 幣別
	 * @return
	 */
	@DeleteMapping("/currency/{code}")
	public ResponseEntity<?> deleteCurrency(@PathVariable String code) {
		service.delete(code);
		return ResponseEntity.noContent().build();
	}

}
