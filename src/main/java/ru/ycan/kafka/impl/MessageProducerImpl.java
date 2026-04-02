package ru.ycan.kafka.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import ru.ycan.kafka.MessageProducer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class MessageProducerImpl implements MessageProducer {

    private final String topic;

    @Override
    public void startSendMessages() {
        // Создание продюсера
        try (var producer = new KafkaProducer<String, String>(getProducerProperties())) {
            for (var message : generateMessage()) {
                var record = new ProducerRecord<>(topic, UUID.randomUUID().toString(), message);
                log.info("ProducerRecord до отправки в kafka: {}", record);
                // Отправка сообщения
                producer.send(record);
                log.info("Сообщение отправлено в топик '{}'", topic);
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage(), e);
        }
    }

    /**
     * Отдельный метод получения свойств для подключения к {@link KafkaProducer}
     * @return свойства {@link Properties} для подключения к {@link KafkaProducer}
     */
    private static Properties getProducerProperties() {
        // Конфигурация продюсера – адрес сервера, сериализаторы для ключа и значения.
        Properties props = new Properties();
        // Адреса брокеров Kafka для локального прогона
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        // Сериализация ключа
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // Сериализация значения
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // Подтверждение от брокеров. all - ждёт от всех (не только от лидера)
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        // Кол-во повторных попыток отправки сообщения
        props.put(ProducerConfig.RETRIES_CONFIG, "3");

        // SSL Configuration with JKS
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, getFilePath("producer/creds/producer.truststore.jks")); // Truststore
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password-producer"); // Truststore password
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, getFilePath("producer/creds/producer.keystore.jks")); // Keystore
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "password-producer"); // Keystore password
        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "password-producer"); // Key password
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, ""); // Отключение проверки hostname
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        return props;
    }

    private List<String> generateMessage() {
        List<String> messages = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            messages.add("message-" + i);
        }
        return messages;
    }

    public static String getFilePath(String relativeFilePath) {
        String projectRoot = System.getProperty("user.dir");
        Path filePath = Paths.get(projectRoot, "certs", relativeFilePath);
        return filePath.toAbsolutePath().toString();
    }
}
