package de.thi.inf.edi.springexample;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class Example {
	
	private SomeDataRepository repo;

	public Example(SomeDataRepository repo) {
		this.repo = repo;
		
	}
	
	public void addSomething(SomeData data) {
		this.repo.save(data);
	}
	
	public List<SomeData> doSomething() {
		Iterable<SomeData> all = this.repo.findAll();
		List<SomeData> list = new ArrayList<>();
		for(SomeData el : all) {
			list.add(el);
		}
		return list;
	}
}
