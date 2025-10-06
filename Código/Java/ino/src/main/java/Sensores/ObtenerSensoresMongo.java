package Sensores;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ObtenerSensoresMongo {

    public static List<Document> obtenerDatos() {
        List<Document> listaDatos = new ArrayList<>();

        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Sensores");

        MongoCollection<Document> sensor1Collection = database.getCollection("Sensor1");
        MongoCollection<Document> sensor2Collection = database.getCollection("Sensor2");

        for (Document doc : sensor1Collection.find()) {
            doc.append("sensor", "Sensor 1");
            listaDatos.add(doc);
        }

        for (Document doc : sensor2Collection.find()) {
        	doc.append("sensor", "Sensor 2");
            listaDatos.add(doc);
        }

        mongoClient.close();
        return listaDatos;
    }
}
