import java.io.*;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Pedir al usuario que ingrese el número de marcos de página
        System.out.print("Ingrese el número de marcos de página: ");
        int numMarcos = scanner.nextInt();

        PageTable pageTable = new PageTable(numMarcos);  // Tabla de páginas inicializada con el número de marcos
        FaultsCounter faultsCounter = new FaultsCounter();
        Imagen imagenModificada = null;

        while (true) {
            System.out.println("Menú:");
            System.out.println("1. Generar referencias");
            System.out.println("2. Calcular hits y fallas de página");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el tamaño de página: ");
                    int tamanoPagina = scanner.nextInt();
                    System.out.print("Ingrese el nombre del archivo BMP con el mensaje escondido: ");
                    String archivoImagen = scanner.next();
                    imagenModificada = new Imagen(archivoImagen);

                    generarReferencias(imagenModificada, tamanoPagina);
                    break;

                case 2:
                    System.out.print("Ingrese el nombre del archivo de referencias: ");
                    String archivoReferencias = scanner.next();
                    simularPaginacion(archivoReferencias, numMarcos);
                    break;

                case 3:
                    System.out.println("Saliendo del programa.");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    // Método para generar referencias desde la imagen BMP
    public static void generarReferencias(Imagen imagen, int tamanoPagina) {
        try {
            FileWriter writer = new FileWriter("referencias.txt");
            int filas = imagen.getAlto();
            int columnas = imagen.getAncho();
            int tamanoMensaje = imagen.leerLongitud(); // Se obtiene la longitud del mensaje desde la imagen
    
            // Calcular número de páginas necesarias
            int totalBytes = filas * columnas * 3 + tamanoMensaje; // Total de bytes de la imagen y el mensaje
            int paginasVirtuales = (int) Math.ceil((double) totalBytes / tamanoPagina);
    
            // Escribir cabecera del archivo de referencias
            writer.write("TP=" + tamanoPagina + "\n");
            writer.write("NF=" + filas + "\n");
            writer.write("NC=" + columnas + "\n");
            writer.write("NR=" + totalBytes + "\n");
            writer.write("NP=" + paginasVirtuales + "\n");
    
            // Escribir referencias generadas
            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    // Referencias para los colores R, G y B
                    writer.write("Imagen[" + i + "][" + j + "].R," + (i * columnas + j) / tamanoPagina + "," + (i * columnas + j) % tamanoPagina + ",R\n");
                    writer.write("Imagen[" + i + "][" + j + "].G," + (i * columnas + j) / tamanoPagina + "," + (i * columnas + j) % tamanoPagina + ",R\n");
                    writer.write("Imagen[" + i + "][" + j + "].B," + (i * columnas + j) / tamanoPagina + "," + (i * columnas + j) % tamanoPagina + ",R\n");
                }
            }
    
            // Referencias para el mensaje
            for (int k = 0; k < tamanoMensaje; k++) {
                writer.write("Mensaje[" + k + "]," + ((filas * columnas * 3) + k) / tamanoPagina + "," + ((filas * columnas * 3) + k) % tamanoPagina + ",W\n");
            }
    
            writer.close();
            System.out.println("Referencias generadas y guardadas en 'referencias.txt'.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Simular hits y fallos de página leyendo el archivo de referencias
    public static void simularPaginacion(String archivoReferencias, int numMarcos) {
        PageTable pageTable = new PageTable(numMarcos);  // Inicializar la tabla de páginas con el número de marcos
        FaultsCounter faultsCounter = new FaultsCounter();
    
        try {
            BufferedReader reader = new BufferedReader(new FileReader(archivoReferencias));
            String linea;
            while ((linea = reader.readLine()) != null) {
                // Procesar cada línea del archivo de referencias
                if (linea.startsWith("Imagen") || linea.startsWith("Mensaje")) {
                    String[] partes = linea.split(",");
                    int paginaVirtual = Integer.parseInt(partes[1]);
                    
                    // Intentamos cargar la página y verificamos si es un hit o un fallo de página
                    boolean hit = pageTable.loadPage(paginaVirtual);
                    if (hit) {
                        faultsCounter.countHit();
                    } else {
                        faultsCounter.countFault();
                    }
                }
            }
    
            reader.close();
            System.out.println("Simulación completada.");
            System.out.println("Total de fallas de página: " + faultsCounter.getFaults());
            System.out.println("Total de hits: " + faultsCounter.getHits());
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
