package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class DemoApplicationTests {

	@Test
	void printHash() {
        System.out.println("THE_HASH=" + new BCryptPasswordEncoder().encode("password"));
	}

}
