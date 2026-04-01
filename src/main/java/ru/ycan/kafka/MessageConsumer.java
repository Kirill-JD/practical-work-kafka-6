package ru.ycan.kafka;

public interface MessageConsumer {
    /**
     * Старт вычитки из топика kafka (указывается при инициализации объекта)
     */
    void startReadMessages();
}
