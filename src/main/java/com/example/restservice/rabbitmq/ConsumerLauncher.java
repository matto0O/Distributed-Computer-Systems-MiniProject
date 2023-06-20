package com.example.restservice.rabbitmq;

public class ConsumerLauncher {

    public static void start(Runnable consumer){
        System.out.println(consumer.toString() + ": start");
        Thread t = new Thread(consumer, consumer.toString());
        t.start();
    }

    public static void main(String[] args) {
        ConsumerStatus cs = new ConsumerStatus();
        ConsumerAll ca = new ConsumerAll();
        ConsumerRPC cr = new ConsumerRPC();

        start(cs);
        start(ca);
        start(cr);
    }
}
