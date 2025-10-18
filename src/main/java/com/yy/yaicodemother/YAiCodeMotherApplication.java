package com.yy.yaicodemother;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.yy.yaicodemother.mapper")
@EnableCaching
public class YAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(YAiCodeMotherApplication.class, args);
    }

}
