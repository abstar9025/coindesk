package com.example.coindesk.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "currency")
public class Currency {

	/**
	 * 幣別
	 */
	@Id
	private String code;

	/**
	 * 幣別中文名稱
	 */
	private String name;

	/**
	 * 原始匯率字串（含千分位）
	 */
	private String rate;

	/**
	 * 更新時間
	 */
	private LocalDateTime updated;

	public Currency() {
	}

	public Currency(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public LocalDateTime getUpdated() {
		return updated;
	}

	public void setUpdated(LocalDateTime updated) {
		this.updated = updated;
	}

}
