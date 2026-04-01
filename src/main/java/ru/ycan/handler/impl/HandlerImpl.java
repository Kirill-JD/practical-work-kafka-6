package ru.ycan.handler.impl;

import lombok.extern.slf4j.Slf4j;
import ru.ycan.handler.Handler;
import ru.ycan.kafka.MessageConsumer;
import ru.ycan.kafka.MessageProducer;
import ru.ycan.kafka.impl.MessageConsumerImpl;
import ru.ycan.kafka.impl.MessageProducerImpl;

@Slf4j
public class HandlerImpl implements Handler {
    private static final String TOPIC_1 = "topic-1";
    private static final String TOPIC_2 = "topic-2";
    private static final String CONSUMER_GROUP_ID_1 = "group_id_1";
    private static final String CONSUMER_GROUP_ID_2 = "group_id_2";
    private static final MessageProducer MESSAGE_PRODUCER_1 = new MessageProducerImpl(TOPIC_1);
    private static final MessageProducer MESSAGE_PRODUCER_2 = new MessageProducerImpl(TOPIC_2);
    private static final MessageConsumer MESSAGE_CONSUMER_1 = new MessageConsumerImpl(TOPIC_1, CONSUMER_GROUP_ID_1);
    private static final MessageConsumer MESSAGE_CONSUMER_2 = new MessageConsumerImpl(TOPIC_2, CONSUMER_GROUP_ID_2);

    @Override
    public void startProcess() {
        readMessagesProcess();
    }

    /**
     * Запуск консьюмеров и продюсеров в отдельных потоках
     */
    private void readMessagesProcess() {
        new Thread(MESSAGE_PRODUCER_1::startSendMessages).start();
        new Thread(MESSAGE_PRODUCER_2::startSendMessages).start();
        new Thread(MESSAGE_CONSUMER_1::startReadMessages).start();
        new Thread(MESSAGE_CONSUMER_2::startReadMessages).start();
    }
}
