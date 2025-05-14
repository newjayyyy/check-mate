package Capstone.checkmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CheckmateApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheckmateApplication.class, args);
	}

}
