/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import DAOS.SensorDAO;
import Entidades.Registro;
import Entidades.Sensor;
import conexiones.ConexionBD;
import java.util.Calendar;
import java.util.Random;

/**
 *
 * @author tonyd
 */
public class DataGenerator {

    private static final int MIN_TEMPERATURE = 20; // Temperatura mínima
    private static final int MAX_TEMPERATURE = 40; // Temperatura máxima
    private static final int MIN_HUMIDITY = 40; // Humedad mínima
    private static final int MAX_HUMIDITY = 60; // Humedad máxima

    private Random random = new Random();

    public String generateData() {
        int temperature = random.nextInt(MAX_TEMPERATURE - MIN_TEMPERATURE) + MIN_TEMPERATURE;
        int humidity = random.nextInt(MAX_HUMIDITY - MIN_HUMIDITY) + MIN_HUMIDITY;
        return String.format("%d,%d", temperature, humidity);
    }

    public Registro generarRegistro(Sensor sensor) {
        Registro registro = new Registro();
        
        registro.setHumedad(random.nextFloat() * (MAX_HUMIDITY - MIN_HUMIDITY) + MIN_HUMIDITY);
        registro.setTemperatura(random.nextFloat() * (MAX_TEMPERATURE - MIN_TEMPERATURE) + MIN_TEMPERATURE);
        registro.setFecha(Calendar.getInstance().getTime());
        registro.setSensor(sensor); // aquí puedes agregar la lógica para consultar de la base de datos
        return registro;
    }
}
