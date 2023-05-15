/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package mq;

import DAOS.SensorDAO;
import Entidades.Registro;
import Entidades.Sensor;
import java.util.concurrent.TimeUnit;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import conexiones.ConexionBD;
import datos.DataGenerator;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tonyd
 */
public class Sender {
    private final static String QUEUE_NAME = "cola_datos";
    private final static int INTERVALO = 10000; // intervalo entre cada envío en milisegundos
    private static DataGenerator dataGenerator;
    private static ConexionBD conn = new ConexionBD();
    private static SensorDAO sen;
    private static List<Sensor> sensores;

    public static void main(String[] args) throws Exception {
        dataGenerator = new DataGenerator();
        Sender.sen = new SensorDAO(conn);
        sensores = new ArrayList<>();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        while (true) {
            sensores = sen.consultarTodos();
            for(Sensor sensor: sensores){
                Registro registro = dataGenerator.generarRegistro(sensor);
                
                // Serializar los campos del objeto Registro como una cadena de caracteres
                String data = registro.getHumedad() + "," + registro.getTemperatura() + "," + registro.getFecha().getTime() + "," + sensor.getId();

                // Enviar la cadena de caracteres a la cola
                channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_BASIC, data.getBytes());
                System.out.println("Enviado: " + registro);
            }
            // Esperar el intervalo antes de enviar el siguiente objeto Registro
            TimeUnit.MILLISECONDS.sleep(INTERVALO);
        }
    }
}
