package com.flx.porto.roastflow;

import org.springframework.boot.SpringApplication;

public class TestRoastFlowApplication {

	public static void main(String[] args) {
		SpringApplication.from(RoastFlowApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
