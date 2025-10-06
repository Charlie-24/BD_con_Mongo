package Sensores;

import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class GuardarSensoresMongo {
    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");  
        MongoDatabase database = mongoClient.getDatabase("Sensores");
        MongoCollection<Document> sensor1Collection = database.getCollection("Sensor1");
        MongoCollection<Document> sensor2Collection = database.getCollection("Sensor2");

        SerialPort puertoSerie = SerialPort.getCommPort("COM5");
        puertoSerie.setBaudRate(9600);
        puertoSerie.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

        if (!puertoSerie.openPort()) {
            System.out.println("❌ Error al abrir el puerto serie");
            return;
        }

        System.out.println("✅ Escuchando datos del puerto serie...");
        Scanner scanner = new Scanner(puertoSerie.getInputStream());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        while (scanner.hasNextLine()) {
            try {
                String linea = scanner.nextLine().trim();

                if (linea.isEmpty() || !linea.startsWith("{") || !linea.endsWith("}")) {
                    System.out.println("⚠️ JSON mal formado: " + linea);
                    continue;
                }

                JsonObject jsonData = JsonParser.parseString(linea).getAsJsonObject();

                // Obtener la fecha y hora actual
                String fechaHora = LocalDateTime.now().format(formatter);

                if (jsonData.has("sensor_1")) {
                    JsonObject sensor1 = jsonData.getAsJsonObject("sensor_1");
                    Document doc1 = new Document("temperatura", sensor1.get("temperatura").getAsDouble())
                            .append("humedad", sensor1.get("humedad").getAsDouble())
                            .append("fecha_hora", fechaHora);  // Añadir la fecha y hora
                    sensor1Collection.insertOne(doc1);
                    System.out.println("✅ Datos insertados en Sensor1: " + doc1.toJson());
                }
                
                if (jsonData.has("sensor_2")) {
                    JsonObject sensor2 = jsonData.getAsJsonObject("sensor_2");
                    Document doc2 = new Document("temperatura", sensor2.get("temperatura").getAsDouble())
                            .append("humedad", sensor2.get("humedad").getAsDouble())
                            .append("fecha_hora", fechaHora);  // Añadir la fecha y hora
                    sensor2Collection.insertOne(doc2);
                    System.out.println("✅ Datos insertados en Sensor2: " + doc2.toJson());
                }

                System.out.println("--------------------");
                Thread.sleep(2000);

            } catch (Exception e) {
                System.out.println("❌ Error procesando JSON: " + e.getMessage());
            }
        }

        scanner.close();
        puertoSerie.closePort();
        mongoClient.close();
    }
}
