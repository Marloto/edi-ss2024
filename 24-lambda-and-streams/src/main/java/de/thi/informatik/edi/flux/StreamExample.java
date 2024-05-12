package de.thi.informatik.edi.flux;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamExample {
	public static void main(String[] args) {
//		List.of("1", "21", "2", "4", "3", "42").forEach(System.out::println);
		List<String> list = List.of("1", "21", "2", "4", "3", "42");
		
		Stream<String> stream = list.stream();
		
		// Operator: map
		// -> wendet eine Operation auf den eingangswert an, und erzeugt etwas neues 
		Stream<Integer> afterMap = stream.map(element -> Integer.valueOf(element));
		// Operation: filter
		// -> wendet eine Operation auf einen eingangswert an, wenn diese true liefert,
		//    wird das Element weiter verwendet
		Stream<Integer> afterAfterMap = afterMap.filter(element -> element > 20);
		
		// Weiterer map-Operator
		Stream<String> afterFilter = afterAfterMap.map(el -> "Wert: " + el);
		
		// Terminal-Operator
		afterFilter.forEach(System.out::println);
		
		System.out.println("---");
		
		list.stream()
			.map(element -> Integer.valueOf(element))
			.filter(element -> element > 20)
			.map(el -> "Wert: " + el)
			.forEach(System.out::println);
		
		System.out.println("---");
		
		List<String> collect = list.stream()
			.map(element -> Integer.valueOf(element))
			.filter(element -> element > 20)
			.map(el -> "Wert: " + el)
			.collect(Collectors.toList());
		System.out.println(collect);
		
		System.out.println("---");
		
		Integer reduce = list.stream()
			.map(element -> Integer.valueOf(element))
			.filter(element -> element > 20)
			.reduce(0, (a, b) -> a + b);
		
		System.out.println(reduce);
		
		System.out.println("---");
		
		// Verwenden Sie die Stream-API, um die Abteilung aller 
		// Mitarbeiter die älter als 30 und weniger als 50.000 verdienen zu finden.
		List<Employee> employees = Arrays.asList(
		    new Employee("Alice", 25, "Marketing", 55000.0),
		    new Employee("Bob", 30, "Sales", 60000.0),
		    new Employee("Charlie", 35, "Engineering", 75000.0),
		    new Employee("Dave", 40, "Engineering", 80000.0),
		    new Employee("Emily", 28, "Marketing", 45000.0),
		    new Employee("Frank", 33, "Sales", 65000.0)
		);
		// Ideen:
		// -> get-Funktionen notwendig
		// -> stream umwandeln
		// -> filter employees zzgl. Attribute die gefiltert werden müssen
		
		
		StreamExample ref = new StreamExample();
		Set<String> set = employees.stream()
			.filter(ref::checking)
//			.filter(Employee::checking)
//			.filter(employee -> employee.getAge() > 30)
//			.filter(employee -> employee.getIncome() > 50000)
			.map(employee -> employee.getDepartment())
			.collect(Collectors.toSet());
		System.out.println(set);
		
		List<String> input = List.of("1", "21", "2", "4", "3", "42");
		// Summe und Anzahl
		// -> input.size()
		
		Integer result = input.stream()
				.map(Integer::valueOf)
				.reduce(0, (x, y) -> x + y);
		
		System.out.println(result / input.size());
		
		// Parallel Streams, async?
		
		input.stream()
			.map(Integer::valueOf).count();
		
	}
	public boolean checking(Employee employee) {
		return employee.getAge() > 30 && employee.getIncome() > 50000;
	}
}

class Employee {

	private double income;
	private String department;
	private int age;
	private String name;

	public boolean checking(Employee employee) {
		return employee.getAge() > 30 && employee.getIncome() > 50000;
	}
	
	public Employee(String name, int age, String department, double income) {
		this.name = name;
		this.age = age;
		this.department = department;
		this.income = income;
	}
	

	public double getIncome() {
		return income;
	}

	public void setIncome(double income) {
		this.income = income;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
