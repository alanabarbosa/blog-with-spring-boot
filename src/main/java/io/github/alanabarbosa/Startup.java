package io.github.alanabarbosa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Startup {

    public static void main(String[] args) {
    	//System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(Startup.class, args);
    }
}
