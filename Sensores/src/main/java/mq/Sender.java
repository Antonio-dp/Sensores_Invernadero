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
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.crypto.Cipher;

/**
 *
 * @author tonyd
 */
public class Sender {

    private static PublicKey publicKey;
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

        String publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApYdjjK3oH2AN2SbIc9CryqxVO6bra8btMyR8MjEDIGAe8f/tmurkq38BW0m2r4L+v7uidrQ75srGi6nBujt+sAQ0uZGiU+RTCEJZiEjSBrphlLKhtPiAEgV+OwvpW3gYIcfIxmLMYvIbJ+qVupj6aY1i1nzmJEzlIXhmQUabslrRlwGV0Wc0jeYYnTQsrx+EJvYuCHvX8UffpXFjEjvoZY1AQj89zHhEtMmwfuAcM+Mj2Zb96EVg2SGnbj9G19zefsrD2KtxbsVFHaristI89L+VHkQyqQsGo76c8wmkUQ/tkbVV1uL8JVV+QuC2ktAuvczb8FV6dS9wCc0+vZkkRwIDAQAB"; // Reemplazar con tu clave pública generada
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        while (true) {
            sensores = sen.consultarTodos();
            for (Sensor sensor : sensores) {
                Registro registro = dataGenerator.generarRegistro(sensor);

                // Serializar los campos del objeto Registro como una cadena de caracteres
                String data = registro.getHumedad() + "," + registro.getTemperatura() + "," + registro.getFecha().getTime() + "," + sensor.getId();

                byte[] encryptedData = encryptData(data);

                // Enviar la cadena de caracteres a la cola
                channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_BASIC, encryptedData);
                System.out.println("Enviado: " + registro);
            }
            // Esperar el intervalo antes de enviar el siguiente objeto Registro
            TimeUnit.MILLISECONDS.sleep(INTERVALO);
        }
    }

    private static byte[] encryptData(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes());
    }

}
