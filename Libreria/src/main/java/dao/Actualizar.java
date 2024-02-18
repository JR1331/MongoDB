package dao;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

import ConexionDB.MongoDB;

public class Actualizar {
    public static void actualizarLibro(Document libroActualizado) {
        try {
            MongoClient mongoClient = MongoDB.getClient();
            MongoDatabase database = mongoClient.getDatabase("Librerias");
            MongoCollection<Document> collection = database.getCollection("libros");

            
            String nombreLibro = libroActualizado.getString("nombre");

            
            Document filtro = new Document("nombre", nombreLibro);

            
            Document update = new Document("$set", libroActualizado);

            
            UpdateOptions options = new UpdateOptions().upsert(true);

           
            collection.updateOne(filtro, update, options);

            System.out.println("Libro actualizado correctamente en MongoDB.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void actualizarPrestamo(Document prestamo) {
        try {
            MongoClient mongoClient = MongoDB.getClient();
            MongoDatabase database = mongoClient.getDatabase("Librerias");
            MongoCollection<Document> collection = database.getCollection("prestamos");

            
            Document filtro = new Document("_id", prestamo.getObjectId("_id"));

            
            collection.replaceOne(filtro, prestamo);

            System.out.println("Préstamo actualizado correctamente en MongoDB.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
