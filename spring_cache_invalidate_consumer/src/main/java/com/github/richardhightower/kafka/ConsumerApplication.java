package com.github.richardhightower.kafka;

import java.util.HashMap;
import java.util.Map;

import com.github.richardhightower.model.CacheInvalidateMessageDeserializer;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }


    @Value("${bootstrap.servers}")
    private String bootstrapServers;


    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("context loaded " + bootstrapServers);
        };
    }


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }


    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class.toString());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CacheInvalidateMessageDeserializer.class.toString());
        return new KafkaAdmin(configs);
    }

}
