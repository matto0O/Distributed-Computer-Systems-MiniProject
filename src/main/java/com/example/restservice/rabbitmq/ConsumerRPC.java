package com.example.restservice.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConsumerRPC implements Runnable{
    private static final String QUEUE_NAME = "rpcq";
    private final ConnectionFactory factory;

    public String toString(){return "ConsumerRPC";}

    public ConsumerRPC(){
        log("create");
        factory = new ConnectionFactory();
        factory.setHost("localhost");
    }

    private void log(String message){
        if (!message.isEmpty()) System.out.println("ConsumerRPC: "+message);
    }

    public void run(){
        log("run");
        try (Connection connection = factory.newConnection()) {
            Channel channel;
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            while(true) {
                DefaultConsumer consumer = new DefaultConsumer(channel) {
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body) throws IOException {
                        String msgFileName = new String(body, "UTF-8");
                        channel.basicPublish("", properties.getReplyTo(), properties,
                                getFileContent(msgFileName));
                        log("processed photo");
                    }
                };
                channel.basicConsume(QUEUE_NAME, true, consumer);
            }
        } catch (Exception e){e.printStackTrace();}
    }

    private byte[] getFileContent(String fileName) throws IOException {
        String filePath = fileName;
        byte[] file =  Files.readAllBytes(Paths.get(filePath));
        System.out.println(file.length);
        return file;
    }
}
