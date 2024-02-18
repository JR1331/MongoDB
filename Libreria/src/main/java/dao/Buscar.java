package dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import ConexionDB.MongoDB;

public class Buscar {
	public static List<Document> buscarLibros(String criterio, String valor) {
        List<Document> resultados = new ArrayList<>();

        try {
            MongoClient mongoClient = MongoDB.getClient();
            MongoDatabase database = mongoClient.getDatabase("Librerias");
            MongoCollection<Document> collection = database.getCollection("libros");

            // Crea un documento que representa el criterio de búsqueda
            Document query = new Document();

            switch (criterio.toLowerCase()) {
                case "nombre":
                    query.put("nombre", valor);
                    break;
                case "autor":
                    query.put("autor", valor);
                    break;
                case "categoria":
                    query.put("categorias", valor);
                    break;
                default:
                    System.out.println("Criterio de búsqueda no válido.");
                    return resultados;
            }

            // Realiza la búsqueda en la colección
            FindIterable<Document> result = collection.find(query);

            // Agrega los resultados a la lista
            for (Document document : result) {
                resultados.add(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultados;
    }

	public static Document buscarPrestamo(String nombreLibro) {
	    try {
	        MongoClient mongoClient = MongoDB.getClient();
	        MongoDatabase database = mongoClient.getDatabase("Librerias");
	        MongoCollection<Document> collection = database.getCollection("prestamos");

	        // Crea un filtro para buscar el préstamo por el nombre del libro y que no tenga fecha de devolución
	        Document filtro = new Document("nombreLibro", nombreLibro)
	                .append("fechaDevolucion", new Document("$exists", false));

	        // Realiza la búsqueda en la colección
	        FindIterable<Document> result = collection.find(filtro);

	        // Retorna el primer resultado (debería ser único por nombre de libro y sin fecha de devolución)
	        return result.first();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
}