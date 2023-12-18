package com.scheduler;

import com.scheduler.service.ScheduledTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling // Enable Spring's scheduled task execution
public class SchedulerApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(SchedulerApplication.class, args);

	}

}
