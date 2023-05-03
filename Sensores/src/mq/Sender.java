/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package mq;

import java.util.concurrent.TimeUnit;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import datos.DataGenerator;

/**
 *
 * @author tonyd
 */
public class Sender {
    private final static String QUEUE_NAME = "cola_entidad";
    private final static int INTERVALO = 1000; // intervalo entre cada envío en milisegundos
    private static DataGenerator dataGenerator;

    public static void main(String[] args) throws Exception {
        dataGenerator = new DataGenerator();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        while (true) {
            String data = dataGenerator.generateData();

            // Enviar la entidad a la cola
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_BASIC, data.getBytes());
            System.out.println("Enviado: " + data);

            // Esperar el intervalo antes de enviar la siguiente entidad
            TimeUnit.MILLISECONDS.sleep(INTERVALO);
        }
    }
}
