package com.nero.springbootmqttdemo.config;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * @author Nero
 * @since 2019-08-02 11:10
 */
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel", defaultReplyTimeout = "5000", defaultRequestTimeout = "5000")
public interface MqttGateWay {
    /**
     * 发布内容到指定主题
     * @param data 要发布的内容
     * @param topic 发布主题
     */
    void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);

}
