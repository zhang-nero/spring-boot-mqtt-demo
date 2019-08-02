# spring-boot-mqtt-demo
    Spring boot 集成 Mqtt示例  
    
    Mqtt收发消息的基本配置，Mqtt服务[部署](http://docs.emqtt.cn/zh_CN/latest/)

## 发送消息
```java
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
```

## 接收处理消息
```java

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel", sendTimeout = "5000")
    public MessageHandler handler() {
        return (Message<?> message) -> {
            String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
            String messageString = (String) message.getPayload();
            logger.info("Mqtt received topic mesage, topic: {}, mesage: {}", topic, messageString);
        };
    }
```