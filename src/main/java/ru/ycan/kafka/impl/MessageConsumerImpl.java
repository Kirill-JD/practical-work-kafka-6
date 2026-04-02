package ru.ycan.kafka.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import ru.ycan.kafka.MessageConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static ru.ycan.kafka.impl.MessageProducerImpl.getFilePath;

@Slf4j
@RequiredArgsConstructor
public class MessageConsumerImpl implements MessageConsumer {
    private final String topic;
    private final String groupId;

    @Override
    public void startReadMessages() {
        var props = getConsumerProperties();
        try (var consumer = new KafkaConsumer<String, String>(props)) {
            // Подписка на топик
            consumer.subscribe(Collections.singletonList(topic));
            // Чтение сообщений в бесконечном цикле
            while (true) {
                try {
                    // Получение сообщений (время увеличено до 1 секунды, т.к. логи улетают мгновенно)
                    var records = consumer.poll(Duration.ofMillis(1000));
                    for (var record : records) {
                        log.info("Получено сообщение из топика '{}': partition = {}, offset = {}, key = '{}', value = '{}'",
                                 record.topic(), record.partition(), record.offset(), record.key(), record.value());
                    }
                } catch (Exception e) {
                    log.error("Не удалось вычитать сообщение, возможно нет прав для топика '{}'", topic, e);
                }
            }
        }
    }

    private Properties getConsumerProperties() {
        Properties props = new Properties();
        // Адреса брокеров Kafka
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        // Уникальный идентификатор группы
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // Десериализация ключа
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // Десериализация значения
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // Начало чтения с самого начала
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Автоматический коммит смещений
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        // Время ожидания активности от консьюмера
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "6000");

        // SSL Configuration with JKS
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, getFilePath("consumer/creds/consumer.truststore.jks")); // Truststore
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password-consumer"); // Truststore password
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, getFilePath("consumer/creds/consumer.keystore.jks")); // Keystore
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "password-consumer"); // Keystore password
        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "password-consumer"); // Key password
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, ""); // Отключение проверки hostname
        return props;
    }
}
