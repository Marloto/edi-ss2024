package de.thi.informatik.edi.reactive;

import java.util.List;

public class StreamExample {
	public static void main(String[] args) {
		List<String> of = List.of("1", "21", "2", "4", "3", "42");
		of.stream()
			.map(Integer::valueOf)
			.map(el -> el + 1)
			.filter(el -> el > 20)
			.forEach(System.out::println);
	}
}
