package com.mejustmocha.payment_wallet.messaging;

import com.mejustmocha.payment_wallet.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveNotification(String message){
        // Placeholder to add the email or SMS gateway here
        // for the POC purpose, simulating a slow external process using Thread.sleep()
        try{
            System.out.println("Processing notification: "+ message);
            Thread.sleep(3000); //simulating a 3 seconds delay to send an email
            System.out.println("Notification sent successfully");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
