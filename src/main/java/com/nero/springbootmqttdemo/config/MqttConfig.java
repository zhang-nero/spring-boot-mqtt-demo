package com.nero.springbootmqttdemo.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.UUID;

/**
 * @author Nero
 * @since 2019-07-23 17:50
 */
@Configuration
public class MqttConfig {

    private static final Logger logger = LoggerFactory.getLogger(MqttConfig.class);

    @Value("${mqtt.host}")
    private String host;
    @Value("${mqtt.username}")
    private String username;
    @Value("${mqtt.password}")
    private String password;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setServerURIs(host.split(","));
        MqttConnectOptions mqttConnectOptions = factory.getConnectionOptions();
        mqttConnectOptions.setMaxInflight(300);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setConnectionTimeout(5);
        mqttConnectOptions.setKeepAliveInterval(10);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        return factory;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel", sendTimeout = "5000")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(UUID.randomUUID().toString(), mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("SampleTopic");
        messageHandler.setDefaultQos(2);
        messageHandler.setCompletionTimeout(5000);
        return messageHandler;
    }

    @Bean
    public MessageProducer mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(UUID.randomUUID().toString(),
                mqttClientFactory(), "testTopic/#");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(mqttInputChannel());
        adapter.setRecoveryInterval(100);
        return adapter;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel", sendTimeout = "5000")
    public MessageHandler handler() {
        return (Message<?> message) -> {
            String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
            String messageString = (String) message.getPayload();
            logger.info("Mqtt received topic mesage, topic: {}, mesage: {}", topic, messageString);
        };
    }
}
