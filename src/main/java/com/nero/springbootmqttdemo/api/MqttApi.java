package com.nero.springbootmqttdemo.api;


import com.nero.springbootmqttdemo.config.MqttGateWay;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Nero
 * @since 2019-08-02 11:21
 */
@RestController
@RequestMapping(value = "mqttApi")
public class MqttApi {

    @Autowired
    private MqttGateWay mqttGateWay;

    @ApiOperation(value = "发送Mqtt消息", httpMethod = "GET")
    @RequestMapping(value = "sendMessage", method = RequestMethod.GET)
    public void sendMessage(@RequestParam String topic, @RequestParam String message){
        mqttGateWay.sendToMqtt(message, "testSendTopic/" + topic);
    }
}
