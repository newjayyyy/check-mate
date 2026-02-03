package Capstone.checkmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class CheckmateApplication {

	public static void main(String[] args) {
		ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
		SpringApplication.run(CheckmateApplication.class, args);
	}

}
