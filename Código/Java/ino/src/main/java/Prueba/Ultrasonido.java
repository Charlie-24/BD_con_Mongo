package Prueba;

import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Ultrasonido {

    public static void main(String[] args) {
        // Configurar el puerto serie para recibir datos del Arduino
        String puertoSerial = "COM5";  // Asegúrate de que este puerto es el correcto
        SerialPort puerto = SerialPort.getCommPort(puertoSerial);
        puerto.setBaudRate(9600);
        puerto.openPort();

        // Conectar a MongoDB y usar la base de datos y colección correctas
        MongoClient cliente = MongoClients.create("mongodb://localhost:27017"); // Conexión local
        MongoDatabase baseDatos = cliente.getDatabase("PruebaTemp"); // Usamos la base de datos existente
        MongoCollection<Document> coleccion = baseDatos.getCollection("temp1"); // Usamos la colección existente

        // Instancia de Gson para procesar el JSON
        Gson gson = new Gson();

        System.out.println("Esperando datos...");

        while (true) {
            // Leer datos del puerto
            if (puerto.bytesAvailable() > 0) {
                byte[] buffer = new byte[1024];
                int bytesRead = puerto.readBytes(buffer, buffer.length);

                if (bytesRead > 0) {
                    // Convertir los datos leídos a un String
                    String jsonData = new String(buffer, 0, bytesRead).trim();
                    System.out.println("Datos recibidos: " + jsonData);

                    // Deserializar el JSON y obtener la distancia
                    try {
                        // Convertir la cadena JSON en un objeto JSON
                        JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);

                        if (jsonObject != null && jsonObject.has("distancia")) {
                            int distancia = jsonObject.get("distancia").getAsInt();
                            System.out.println("Distancia medida: " + distancia + " cm");

                            // Crear el documento para insertar en MongoDB
                            Document documento = new Document("distancia", distancia)
                                .append("timestamp", System.currentTimeMillis());

                            // Insertar el documento en MongoDB
                            coleccion.insertOne(documento);
                            System.out.println("Distancia insertada en MongoDB.");
                        } else {
                            System.out.println("No se encontró la propiedad 'distancia' en el JSON.");
                        }
                    } catch (Exception e) {
                        System.out.println("Error al procesar el JSON: " + e.getMessage());
                    }
                }
            }

            // Esperar 2 segundos antes de la siguiente lectura
            try {
                Thread.sleep(2000);  // Espera de 2 segundos (2000 ms)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
