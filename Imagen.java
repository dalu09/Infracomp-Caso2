import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Imagen {
    private byte[] header = new byte[54]; // Cabecera de la imagen BMP
    private byte[][][] imagen; // Matriz de la imagen en formato RGB
    private int alto, ancho; // Dimensiones de la imagen

    // Constructor que lee la imagen BMP
    public Imagen(String input) {
        try {
            FileInputStream fis = new FileInputStream(input);
            // Leer la cabecera de la imagen
            fis.read(header);

            // Extraer el ancho y alto de la imagen desde la cabecera (formato little endian)
            ancho = ((header[21] & 0xFF) << 24) | ((header[20] & 0xFF) << 16) | 
                     ((header[19] & 0xFF) << 8) | (header[18] & 0xFF);
            alto = ((header[25] & 0xFF) << 24) | ((header[24] & 0xFF) << 16) | 
                    ((header[23] & 0xFF) << 8) | (header[22] & 0xFF);

            // Inicializar la matriz imagen
            imagen = new byte[alto][ancho][3]; // R, G, B

            // Leer los píxeles de la imagen (formato RGB almacenado como BGR)
            byte[] pixel = new byte[3]; // Un píxel tiene 3 bytes: B, G, R
            for (int i = 0; i < alto; i++) {
                for (int j = 0; j < ancho; j++) {
                    fis.read(pixel);
                    imagen[i][j][0] = pixel[2]; // R
                    imagen[i][j][1] = pixel[1]; // G
                    imagen[i][j][2] = pixel[0]; // B
                }
            }

            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para esconder un mensaje en la imagen
    public void esconderMensaje(char[] mensaje, int longitud) {
        int contador = 0;
        byte elByte;
        escribirBits(contador, longitud, 16); // Esconder la longitud del mensaje

        contador = 2; // Empezar a esconder el mensaje después de la longitud
        for (int i = 0; i < longitud; i++) {
            elByte = (byte) mensaje[i];
            escribirBits(contador, elByte, 8); // Esconder cada carácter del mensaje
            contador++;
        }
    }

    // Método para recuperar el mensaje escondido en la imagen
    public char[] recuperarMensaje() {
        int longitud = leerLongitud(); // Leer la longitud del mensaje
        char[] mensaje = new char[longitud];

        int bytesFila = ancho * 3; // Cada fila de la matriz tiene "ancho * 3" bytes
        for (int posCaracter = 0; posCaracter < longitud; posCaracter++) {
            mensaje[posCaracter] = 0;
            for (int i = 0; i < 8; i++) {
                int numBytes = 16 + (posCaracter * 8) + i;
                int fila = numBytes / bytesFila;
                int col = (numBytes % bytesFila) / 3;
                int color = (numBytes % bytesFila) % 3;
                mensaje[posCaracter] |= (char) ((imagen[fila][col][color] & 1) << i);
            }
        }
        return mensaje;
    }

    // Método auxiliar para escribir bits en los píxeles
    private void escribirBits(int contador, int valor, int numbits) {
        int bytesPorFila = ancho * 3;
        for (int i = 0; i < numbits; i++) {
            int fila = (8 * contador + i) / bytesPorFila;
            int col = ((8 * contador + i) % bytesPorFila) / 3;
            int color = ((8 * contador + i) % bytesPorFila) % 3;
            int mascara = (valor >> i) & 1;
            imagen[fila][col][color] = (byte) ((imagen[fila][col][color] & 0xFE) | mascara);
        }
    }

    // Método para leer la longitud del mensaje escondido
    public int leerLongitud() {
        int longitud = 0;
        for (int i = 0; i < 16; i++) {
            int col = (i % (ancho * 3)) / 3;
            longitud |= (imagen[0][col][(i % 3)] & 1) << i;
        }
        return longitud;
    }

    // Método para obtener el ancho de la imagen
    public int getAncho() {
        return this.ancho;
    }

    // Método para obtener el alto de la imagen
    public int getAlto() {
        return this.alto;
    }

    // Método para escribir la imagen modificada en un archivo BMP
    public void escribirImagen(String output) {
        try {
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(header); // Escribir la cabecera

            byte[] pixel = new byte[3];
            for (int i = 0; i < alto; i++) {
                for (int j = 0; j < ancho; j++) {
                    pixel[0] = imagen[i][j][2]; // R
                    pixel[1] = imagen[i][j][1]; // G
                    pixel[2] = imagen[i][j][0]; // B
                    fos.write(pixel);
                }
            }

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
