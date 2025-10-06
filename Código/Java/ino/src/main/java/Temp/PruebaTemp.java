package Temp;

import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.util.Locale;

public class PruebaTemp {

    public static void main(String[] args) throws IOException {
        // Configurar el puerto serie
        String puertoSerial = "COM5";  // Asegúrate de que este puerto es el correcto
        SerialPort puerto = SerialPort.getCommPort(puertoSerial);
        puerto.setBaudRate(9600);
        puerto.openPort();

        // Conectar a MongoDB
        MongoClient cliente = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase baseDatos = cliente.getDatabase("PruebaTemp");
        MongoCollection<Document> coleccion = baseDatos.getCollection("temp1");

        Gson gson = new Gson();
        StringBuilder buffer = new StringBuilder();

        System.out.println("Esperando datos del sensor...");

        while (true) {
            // Leer datos disponibles en el puerto
            while (puerto.bytesAvailable() > 0) {
                char c = (char) puerto.getInputStream().read();  // Leer carácter por carácter
                buffer.append(c);

                // Si encontramos una llave de cierre "}", intentamos procesar el JSON
                if (c == '}') {
                    String jsonData = buffer.toString().trim();
                    buffer.setLength(0);  // Limpiar el buffer

                    System.out.println("Datos recibidos: " + jsonData);

                    // Validar JSON correctamente
                    if (!jsonData.startsWith("{") || !jsonData.endsWith("}")) {
                        System.out.println("❌ Dato ignorado (No es JSON válido): " + jsonData);
                        continue;
                    }

                    try {
                        JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);

                        // Verificar si el JSON contiene las claves correctas
                        if (jsonObject.has("temperatura") && jsonObject.has("humedad")) {
                            // Redondear a dos decimales con separador de punto
                            float temperatura = Float.parseFloat(String.format(Locale.US, "%.2f", jsonObject.get("temperatura").getAsFloat()));
                            float humedad = Float.parseFloat(String.format(Locale.US, "%.2f", jsonObject.get("humedad").getAsFloat()));

                            System.out.println("🌡️ Temperatura: " + temperatura + "°C | 💧 Humedad: " + humedad + "%");

                            // Guardar en MongoDB
                            Document documento = new Document("temperatura", temperatura)
                                .append("humedad", humedad)
                                .append("timestamp", System.currentTimeMillis());

                            coleccion.insertOne(documento);
                            System.out.println("✅ Datos insertados en MongoDB.\n");  // 🔹 Salto de línea agregado
                        } else {
                            System.out.println("❌ No se encontraron los valores esperados en el JSON.");
                        }
                    } catch (Exception e) {
                        System.out.println("⚠️ Error al procesar el JSON: " + e.getMessage());
                    }
                }
            }

            // Esperar 2 segundos antes de la siguiente lectura
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
