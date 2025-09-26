package com.yy.yaicodemother.config;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: RedisChatMemoryStoreConfig
 * Package: com.yy.yaicodemother.config
 * Description:
 *
 * @Author
 * @Create 2025/9/30 10:09
 * @Version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedisChatMemoryStoreConfig {

    private String host;

    private int port;

    private String password;

    private int database;

    private long ttl;

    @Bean
    public RedisChatMemoryStore redisChatMemoryStore() {
        return   RedisChatMemoryStore.builder()
                    .host(host)
                    .port(port)
                    .password(password)
                    .ttl(ttl)
                    .build();
    }
}
