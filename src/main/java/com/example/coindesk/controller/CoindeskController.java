package com.example.coindesk.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/coindesk")
public class CoindeskController {

	@Autowired
	private CoindeskService coindeskService;

	// 取得所有幣別
	@GetMapping
	public List<Currency> getAllCurrencies() {
		return coindeskService.getAllCurrencies();
	}

	// 新增幣別
	@PostMapping
	public Currency createCurrency(@RequestBody Currency currency) {
		return coindeskService.addCurrency(currency);
	}

	// 修改幣別
	@PutMapping("/{code}")
	public ResponseEntity<Currency> updateCurrency(@PathVariable String code, @RequestBody Currency updatedCurrency) {
		Currency result = coindeskService.updateCurrency(code, updatedCurrency);
		return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
	}

	// 刪除幣別
	@DeleteMapping("/{code}")
	public ResponseEntity<Void> deleteCurrency(@PathVariable String code) {
		boolean deleted = coindeskService.deleteCurrency(code);
		return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

	// 查詢單一幣別
	@GetMapping("/{code}")
	public ResponseEntity<Currency> getCurrencyByCode(@PathVariable String code) {
		Currency currency = coindeskService.getCurrency(code);
		return currency != null ? ResponseEntity.ok(currency) : ResponseEntity.notFound().build();
	}
}
