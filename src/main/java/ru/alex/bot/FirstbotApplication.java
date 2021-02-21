package ru.alex.bot;

import javax.ws.rs.core.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@EnableScheduling
public class FirstbotApplication {

  public static void main(String[] args) {
    ApiContextInitializer.init();
    SpringApplication.run(FirstbotApplication.class, args);
  }

}
