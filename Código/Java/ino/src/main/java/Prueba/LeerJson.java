package Prueba;
import com.mongodb.client.*;
import org.bson.Document;
import javax.swing.*;
import java.awt.*;

public class LeerJson {
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
            MongoDatabase database = mongoClient.getDatabase("PruebaUltrasonido");
            MongoCollection<Document> collection = database.getCollection("Ultrasonido");

            StringBuilder jsonOutput = new StringBuilder();
            for (Document doc : collection.find()) {
                jsonOutput.append(doc.toJson()).append("\n\n");
            }
            textArea.setText(jsonOutput.toString());
        } catch (Exception e) {
            textArea.setText("Error al conectar con MongoDB: " + e.getMessage());
        }

        // Hacer visible la ventana
        frame.setVisible(true);
    }
}
