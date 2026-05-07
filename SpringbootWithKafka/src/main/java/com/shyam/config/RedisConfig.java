package com.shyam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

	@Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
      //String serializers make human readable keys and values in redis
//    	template.setKeySerializer(new StringRedisSerializer());
//    	template.setValueSerializer(new StringRedisSerializer());
//    	template.setHashKeySerializer(new StringRedisSerializer());
//    	template.setHashValueSerializer(new StringRedisSerializer());
        
        // ✅ Key as String
        template.setKeySerializer(new StringRedisSerializer());

        // ✅ Value as JSON
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    	
    	
        return template;
    }
}
