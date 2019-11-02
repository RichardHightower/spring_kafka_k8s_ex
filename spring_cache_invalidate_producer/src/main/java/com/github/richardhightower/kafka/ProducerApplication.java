package com.github.richardhightower.kafka;

import java.util.HashMap;
import java.util.Map;

import com.github.richardhightower.model.CacheInvalidateMessageSerializer;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;


@SpringBootApplication
public class ProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("context loader");
        };
    }

    @Value("${bootstrap.servers}")
    private String bootStrapServers;


    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public DefaultKafkaProducerFactory kafkaProducerFactory(KafkaProperties properties) {
       return new DefaultKafkaProducerFactory(properties.buildProducerProperties(), new StringSerializer(), new CacheInvalidateMessageSerializer());
    }
}
