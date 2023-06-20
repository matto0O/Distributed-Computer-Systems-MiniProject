package com.example.restservice.rabbitmq;

import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;

public class ConsumerStatus implements Runnable{
    private static final String EXCHANGE_NAME = "exchangeName";
    private final ConnectionFactory factory;

    public String toString(){return "consumerStatus";}

    public ConsumerStatus(){
        log("create");
        factory = new ConnectionFactory();
        factory.setHost("localhost");
    }

    private void log(String message){
        if (!message.isEmpty()) System.out.println("ConsumerStatus: "+message);
    }

    public void run(){
        log("run");
        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, "changeStatus");
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body) {
                    String msg = new String(body, StandardCharsets.UTF_8);
                    log(msg);
                }
            };
            while(true)
                channel.basicConsume(queueName, true, consumer);
        } catch (Exception e){e.printStackTrace();}
    }
}
