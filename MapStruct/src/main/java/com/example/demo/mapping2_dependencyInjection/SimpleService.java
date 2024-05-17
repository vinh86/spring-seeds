package com.example.demo.mapping2_dependencyInjection;

import org.springframework.stereotype.Component;

@Component
public class SimpleService {
	public String getName(String name) {
		return "name";
	}
}
