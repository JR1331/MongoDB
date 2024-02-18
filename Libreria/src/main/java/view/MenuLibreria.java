package view;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.bson.Document;

import IO.IO;
import dao.Actualizar;
import dao.Buscar;
import dao.Insertar;

public class MenuLibreria {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("¡Bienvenido a la Biblioteca!");

        while (true) {
            mostrarMenuBienvenida();

            int opcion = scanner.nextInt();
            scanner.nextLine();  

            switch (opcion) {
                case 1:
                    alquilarLibro();
                    break;
                case 2:
                    devolverLibro();
                    break;
                case 3:
                	addLibro();
                    break;
                case 0:
                    System.out.println("Gracias por visitar la Biblioteca. ¡Hasta luego!");
                    System.exit(0);
                default:
                    System.out.println("Opción no válida. Inténtalo de nuevo.");
            }
        }
    }

    private static void addLibro() {
        Document document = new Document();

        // Datos generales
        System.out.println("Introduzca el nombre del libro");
        String datoNombre = obtenerInputNoVacio();
        System.out.println("Introduzca su autor");
        String datoAutor = obtenerInputNoVacio();
        System.out.println("Introduzca su idioma");
        String datoIdioma = obtenerInputNoVacio();

        // Lista de categorías
        List<String> categorias = obtenerCategorias();
        
        // Stock por defecto
        String stock = "disponible";

        document.append("nombre", datoNombre)
                .append("autor", datoAutor)
                .append("idioma", datoIdioma)
                .append("categorias", categorias)
                .append("stock", stock);

        // Datos suplementarios
        System.out.println("¿Cuántos datos adicionales quiere añadir?");
        int cant = IO.readInt();
        if (cant > 0) {
            System.out.println("Insértelos en este formato(tipo:dato)");
            for (int i = 0; i < cant; i++) {
                String datos[] = IO.readString().toLowerCase().split(":");
                if (datos.length == 2) {
                    document.append(datos[0], datos[1]);
                } else {
                    System.out.println("Formato incorrecto. Inténtelo de nuevo.");
                    i--; 
                }
            }
        }

        Insertar.addLibro(document);  
    }
    
    private static String obtenerInputNoVacio() {
        String input = "";
        while (input.trim().isEmpty()) {
            input = IO.readString().toLowerCase();
            if (input.trim().isEmpty()) {
                System.out.println("Debe introducir un valor. Inténtelo de nuevo.");
            }
        }
        return input;
    }

    private static List<String> obtenerCategorias() {
        List<String> categorias = new ArrayList<>();
        System.out.println("¿Cuántas categorías quiere añadir?");
        int cantCategorias = IO.readInt();
        for (int i = 0; i < cantCategorias; i++) {
            System.out.println("Introduzca la categoría " + (i + 1) + ":");
            categorias.add(obtenerInputNoVacio());
        }
        return categorias;
    }

	private static void mostrarMenuBienvenida() {
        System.out.println("------ Menú de Bienvenida ------");
        System.out.println("1. Alquilar un libro");
        System.out.println("2. Devolver un libro");
        System.out.println("3. Añadir un libro");
        System.out.println("0. Salir");
        System.out.print("Selecciona una opción: ");
    }

    private static void alquilarLibro() {
        System.out.println("Has seleccionado alquilar un libro.");
        buscarLibro();
    }

    private static void devolverLibro() {
        System.out.println("Has seleccionado devolver un libro.");

        System.out.print("Introduce el nombre del libro que deseas devolver: ");
        String nombreLibro = scanner.nextLine();

        // Buscar el libro en la colección de libros
        List<Document> resultados = Buscar.buscarLibros("nombre", nombreLibro);

        if (resultados != null && !resultados.isEmpty()) {
            Document libroDevuelto = resultados.get(0);

            // Verificar si el libro está marcado como no disponible
            if (libroDevuelto.getString("stock").equals("no disponible")) {
                // Actualizar el stock a disponible
                libroDevuelto.put("stock", "disponible");

                // Actualizar el registro en la colección de libros
                Actualizar.actualizarLibro(libroDevuelto);

                // Buscar el préstamo asociado al libro
                Document prestamos = Buscar.buscarPrestamo(nombreLibro);

                if (prestamos != null ) {

                    // Actualizar la fecha de devolución en el registro de préstamo
                    prestamos.put("fechaDevolucion", new Date());

                    // Actualizar el registro en la colección de préstamos
                    Actualizar.actualizarPrestamo(prestamos);

                    System.out.println("Libro devuelto con éxito. Préstamo registrado.");
                } else {
                    System.out.println("No se encontró un préstamo asociado a este libro.");
                }
            } else {
                System.out.println("El libro seleccionado no está marcado como no disponible.");
            }
        } else {
            System.out.println("No se encontró el libro en la colección.");
        }
    }

    
    private static void buscarLibro() {
        
            System.out.println("=== Buscar Libro ===");
            System.out.println("Seleccione criterio de búsqueda:");
            System.out.println("1. Por título");
            System.out.println("2. Por autor");
            System.out.println("3. Por categoría");
            System.out.print("Selecciona una opción: ");

            int opcion2 = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea después de leer el número

            System.out.print("Introduce el valor de búsqueda: ");
            String valor = scanner.nextLine();

            List<Document> resultados = null;

            switch (opcion2) {
                case 1:
                    resultados = Buscar.buscarLibros("nombre", valor);
                    break;
                case 2:
                    resultados = Buscar.buscarLibros("autor", valor);
                    break;
                case 3:
                    resultados = Buscar.buscarLibros("categoria", valor);
                    break;
                default:
                    System.out.println("Opción no válida.");
                    return;
            }

            if (resultados != null && !resultados.isEmpty()) {
                System.out.println("Resultados de la búsqueda:");
                for (int i = 0; i < resultados.size(); i++) {
                    System.out.println((i + 1) + ". " + resultados.get(i));
                }

                System.out.print("Seleccione el número del libro que desea alquilar: ");
                int seleccion = scanner.nextInt();

                if (seleccion >= 1 && seleccion <= resultados.size()) {
                    Document libroSeleccionado = resultados.get(seleccion - 1);

                    // Verificar si hay stock disponible
                    if (libroSeleccionado.getString("stock").equals("disponible")) {
                        // Actualizar el stock a no disponible
                        libroSeleccionado.put("stock", "no disponible");
                        
                        // Actualizar el registro en la colección de libros
                        Actualizar.actualizarLibro(libroSeleccionado);

                        // Crear el documento de préstamo
                        Document prestamo = new Document();
                        prestamo.append("nombreLibro", libroSeleccionado.getString("nombre"))
                                .append("fecha", new Date());

                        // Insertar en la colección prestamos
                        Insertar.addPrestamo(prestamo);

                        System.out.println("Libro alquilado con éxito. Préstamo registrado.");
                    } else {
                        System.out.println("El libro seleccionado no está disponible en stock.");
                    }
                } else {
                    System.out.println("Selección no válida.");
                }
            } else {
                System.out.println("No se encontraron resultados para la búsqueda.");
            }
        
    }


}
