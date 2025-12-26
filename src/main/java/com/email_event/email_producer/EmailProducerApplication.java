package com.email_event.email_producer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.email_event.email_producer.services.job.ProducerJob;

@SpringBootApplication
public class EmailProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailProducerApplication.class, args);
	}

	// @Bean
	// public CommandLineRunner kickoffGmailScan(ProducerJob producerJob) {
	// return args -> {
	// producerJob.jobRunner();
	// System.exit(0);
	// };
	// }
}
