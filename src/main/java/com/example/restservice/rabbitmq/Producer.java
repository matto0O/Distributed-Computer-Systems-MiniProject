package com.example.restservice.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Producer {
    private final Channel channel, channelRPC;
    private final String EXCHANGE_NAME = "exchangeName";
    private final String RPC_QUEUE = "rpcq";

    public Producer() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();

        channel = connection.createChannel();
        channelRPC = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
    }

    public void send(String msg, String funcName) throws IOException {
        String routingKey;
        if (Objects.equals(funcName, "changeStatus")) routingKey = funcName;
        else routingKey = "";
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, msg.getBytes());
    }

    public byte[] sendImage(String filePath) throws IOException, ExecutionException, InterruptedException {
        String correlationID = UUID.randomUUID().toString();
        String replyQName = channelRPC.queueDeclare().getQueue();
        System.out.println(replyQName);

        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .correlationId(correlationID)
                .replyTo(replyQName)
                .build();

        channelRPC.queueDeclare(RPC_QUEUE, false, false, false, null);

        channelRPC.basicPublish("", RPC_QUEUE, props, filePath.getBytes());

        CompletableFuture<byte[]> response = new CompletableFuture<>();
        String tag = channelRPC.basicConsume(replyQName, false,
                (consumerTag, returnMsg) -> {
                    if (returnMsg.getProperties().getCorrelationId().equals(correlationID))
                        response.complete(returnMsg.getBody());
                },consumerTag -> { }
        );

        byte[] result = response.get();
        channelRPC.basicCancel(tag);
        return result;
    }
}
