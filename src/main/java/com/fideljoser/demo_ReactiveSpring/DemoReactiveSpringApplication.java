package com.fideljoser.demo_ReactiveSpring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoReactiveSpringApplication {

	public static void main(String[] args) {
//		ReactorDebugAgent.init();
		SpringApplication.run(DemoReactiveSpringApplication.class, args);
	}

}
