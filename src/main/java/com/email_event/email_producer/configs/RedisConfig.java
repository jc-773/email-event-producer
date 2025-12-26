package com.email_event.email_producer.configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.email_event.email_producer.models.EmailEvent;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfig {
    // @Bean
    // public RedisTemplate<String, List<EmailEvent>> redisTemplate(
    // RedisConnectionFactory factory,
    // ObjectMapper objectMapper) {

    // RedisTemplate<String, List<EmailEvent>> template = new RedisTemplate<>();
    // template.setConnectionFactory(factory);

    // JacksonJsonRedisSerializer<List<EmailEvent>> serializer = new
    // JacksonJsonRedisSerializer<>(
    // objectMapper,
    // new TypeReference<List<EmailEvent>>() {
    // });

    // template.setKeySerializer(new StringRedisSerializer());
    // template.setValueSerializer(serializer);

    // template.afterPropertiesSet();
    // return template;
    // }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}
