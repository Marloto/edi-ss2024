package de.thi.inf.edi.springexample;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SomeData {
	@Id
	private UUID id;
	private String msg;
	public SomeData() {
	}
	public SomeData(String msg) {
		this.id = UUID.randomUUID();
		this.msg = msg;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
