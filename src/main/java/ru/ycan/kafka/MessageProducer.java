package ru.ycan.kafka;

public interface MessageProducer {
    /**
     * Старт отправки сообщений в топик kafka (указывается при инициализации объекта)
     */
    void startSendMessages();
}
