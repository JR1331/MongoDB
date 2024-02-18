package dao;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ConexionDB.MongoDB;

public class Insertar {
    public static void addLibro(Document document) {
        try {
            MongoClient mongoClient = MongoDB.getClient();
            MongoDatabase database = mongoClient.getDatabase("Librerias");
            
            // Utiliza el nombre de la colección proporcionado
            MongoCollection<Document> collection = database.getCollection("libros");
            
            collection.insertOne(document);

            System.out.println("Documentos insertados correctamente en MongoDB en la colección: ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void addPrestamo(Document document) {
        try {
            MongoClient mongoClient = MongoDB.getClient();
            MongoDatabase database = mongoClient.getDatabase("Librerias");
            MongoCollection<Document> collection = database.getCollection("prestamos");

            
            collection.insertOne(document);

            System.out.println("Préstamo registrado correctamente en MongoDB.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
