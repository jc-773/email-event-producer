package com.email_event.email_producer;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.email_event.email_producer.models.EmailEvent;
import com.email_event.email_producer.services.job.ProducerJob;

@SpringBootApplication
@Configuration
public class EmailProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailProducerApplication.class, args);
	}

	@Bean
	public CommandLineRunner kickoffGmailScan(ProducerJob producerJob) {
		return args -> {
			producerJob.jobRunner();
			System.exit(0);
		};
	}

}
