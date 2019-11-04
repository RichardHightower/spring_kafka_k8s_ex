package com.github.richardhightower.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.github.richardhightower.model.CacheInvalidateMessageDeserializer;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.util.backoff.BackOffExecution;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ConsumerApplication {

    private final Logger logger = LoggerFactory.getLogger(ConsumerApplication.class);

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
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        return new KafkaAdmin(configs);
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer config,
            ConsumerFactory<Object, Object> kafkaConsumerFactory,
            KafkaTemplate<Object, Object> template) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();



        config.configure(factory, kafkaConsumerFactory);

        final var backOff = new ExponentialBackOff(200L, 2);



        var action = new BiConsumer<ConsumerRecord<?, ?>, Exception>(){
            BackOffExecution backOffExecution =null;
            @Override
            public void accept(ConsumerRecord<?, ?> consumerRecord, Exception e) {
                var consumerMessage = String.format("offset = %s, key = %s, topic = %s, partition = %s, timestamp %s",
                        consumerRecord.offset(), consumerRecord.key(),
                        consumerRecord.topic(), consumerRecord.partition(), consumerRecord.timestamp());
                if (backOffExecution ==  null) {
                    backOffExecution = backOff.start();
                }

                try {
                    logger.warn("About to back off");
                    var time = backOffExecution.nextBackOff();
                    logger.warn(String.format("Backing off %d, %s ",time, consumerMessage));
                    Thread.currentThread().wait(time);
                } catch (InterruptedException ex) {
                    logger.warn(String.format("Backing off failed %s", consumerMessage));
                }

            }
        };

        var errorHandler = new SeekToCurrentErrorHandler(action, 10000);
        factory.setConcurrency(1);
        factory.setErrorHandler(errorHandler);
        return factory;
    }

}
