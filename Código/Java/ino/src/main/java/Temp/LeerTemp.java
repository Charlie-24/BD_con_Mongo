package Temp;

import com.mongodb.client.*;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class LeerTemp {
    public static void main(String[] args) {
        // Crear la ventana
        JFrame frame = new JFrame("Datos de MongoDB");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear un área de texto con desplazamiento
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Conectar con MongoDB y obtener datos
        String uri = "mongodb://localhost:27017";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("PruebaTemp");
            MongoCollection<Document> collection = database.getCollection("temp1");

            StringBuilder jsonOutput = new StringBuilder();
            for (Document doc : collection.find()) {
                // Obtener valores y redondear a 2 decimales
                double temperatura = doc.getDouble("temperatura");
                double humedad = doc.getDouble("humedad");

                String tempFormateada = String.format(Locale.US, "%.2f", temperatura);
                String humedadFormateada = String.format(Locale.US, "%.2f", humedad);

                jsonOutput.append("🌡️ Temperatura: ").append(tempFormateada).append("°C\n")
                          .append("💧 Humedad: ").append(humedadFormateada).append("%\n\n");
            }
            textArea.setText(jsonOutput.toString());
        } catch (Exception e) {
            textArea.setText("Error al conectar con MongoDB: " + e.getMessage());
        }

        // Hacer visible la ventana
        frame.setVisible(true);
    }
}
