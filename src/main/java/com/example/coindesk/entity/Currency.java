package com.example.coindesk.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "currency")
public class Currency {

	@Id
	@Column(name = "code")
	private String code;

	@Column(name = "chinese_name")
	private String chineseName;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

}
