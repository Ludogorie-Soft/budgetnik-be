package com.ludogorieSoft.budgetnik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BudgetnikApplication {

  public static void main(String[] args) {
    SpringApplication.run(BudgetnikApplication.class, args);
  }
}
