package com.email_event.email_producer.configs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.email_event.email_producer.models.EmailEvent;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
public class RedisConfig {

    // ... inside your @Bean method
    @Bean
    public RedisTemplate<String, List<EmailEvent>> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, List<EmailEvent>> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());

        // 1. Define the Validator (Security check for storing class names)
        BasicPolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();

        // 2. Build the Jackson 3 Mapper
        // Note: findAndRegisterModules() is NOT needed; Java 25 dates work out of the
        // box.
        ObjectMapper mapper = JsonMapper.builder()
                .activateDefaultTyping(
                        validator,
                        DefaultTyping.NON_FINAL,
                        JsonTypeInfo.As.PROPERTY)
                .build();

        // 3. Use the mapper
        var serializer = new GenericJacksonJsonRedisSerializer(mapper);

        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}
