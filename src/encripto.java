import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;

public class encripto {

    private static final String CLAVE_SECRETA = "MiClaveSecreta12";
    private static final String ALGORITMO = "AES";

    private static final String CARPETA_OCULTO = "Oculto";
    private static final String CARPETA_RESUELTO = "Resuelto";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        crearCarpetas();

        while (!salir) {
            System.out.println("\n--- MENÚ DE ENCRIPTACIÓN AVANZADO ---");
            System.out.println("1. Escribir/Pegar texto, encriptar y guardar");
            System.out.println("2. Ver mensajes disponibles y desencriptar para leer");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    opcionEncriptar(scanner);
                    break;
                case "2":
                    opcionDesencriptar(scanner);
                    break;
                case "3":
                    salir = true;
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida. Intenta de nuevo.");
            }
        }
        scanner.close();
    }

    private static void opcionEncriptar(Scanner scanner) {
        System.out.println("\nEscribe o pega el texto que deseas encriptar.");
        System.out.println("(Cuando termines, escribe la palabra 'fin' en una línea nueva y presiona Enter):");

        StringBuilder textoOriginal = new StringBuilder();

        while (true) {
            String linea = scanner.nextLine();
            if (linea.trim().equalsIgnoreCase("fin")) {
                break;
            }
            textoOriginal.append(linea).append(System.lineSeparator());
        }

        if (textoOriginal.toString().trim().isEmpty()) {
            System.out.println("No escribiste nada. Volviendo al menú...");
            return;
        }

        try {
            String fechaHora = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String nombreArchivo = "mensaje_" + fechaHora + ".txt";
            Path rutaArchivo = Paths.get(CARPETA_OCULTO, nombreArchivo);
            String textoEncriptado = encriptar(textoOriginal.toString().trim());
            Files.writeString(rutaArchivo, textoEncriptado);

            System.out.println("\n-> ¡Éxito! Texto encriptado y guardado como: " + nombreArchivo);
            System.out.println("-> Contenido encriptado que se guardó:");
            System.out.println(textoEncriptado);

        } catch (Exception e) {
            System.out.println("Error al encriptar o guardar el archivo: " + e.getMessage());
        }
    }

    private static void opcionDesencriptar(Scanner scanner) {
        File carpeta = new File(CARPETA_OCULTO);
        File[] archivos = carpeta.listFiles((dir, name) -> name.endsWith(".txt"));

        if (archivos == null || archivos.length == 0) {
            System.out.println("\nNo hay mensajes encriptados en la carpeta 'Oculto'.");
            return;
        }

        System.out.println("\n--- MENSAJES DISPONIBLES ---");
        for (int i = 0; i < archivos.length; i++) {
            System.out.println((i + 1) + ". " + archivos[i].getName());
        }
        System.out.println("0. Cancelar y volver al menú");
        System.out.print("Elige el número del mensaje que quieres leer: ");

        try {
            int seleccion = Integer.parseInt(scanner.nextLine());

            if (seleccion == 0) return;

            if (seleccion < 1 || seleccion > archivos.length) {
                System.out.println("Selección no válida.");
                return;
            }
            File archivoSeleccionado = archivos[seleccion - 1];
            Path rutaArchivoEncriptado = archivoSeleccionado.toPath();
            String textoEncriptado = Files.readString(rutaArchivoEncriptado);
            String textoDesencriptado = desencriptar(textoEncriptado);
            System.out.println("\n--- LEYENDO MENSAJE DESENCRIPTADO ---");
            System.out.println(textoDesencriptado);
            System.out.println("-------------------------------------");
            String nombreResuelto = "resuelto_" + archivoSeleccionado.getName();
            Path rutaArchivoDesencriptado = Paths.get(CARPETA_RESUELTO, nombreResuelto);
            Files.writeString(rutaArchivoDesencriptado, textoDesencriptado);
            System.out.println("-> (Se ha guardado una copia legible en la carpeta 'Resuelto' como '" + nombreResuelto + "')");

        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingresa un número válido de la lista.");
        } catch (Exception e) {
            System.out.println("Error al leer o desencriptar el archivo: " + e.getMessage());
        }
    }


    private static void crearCarpetas() {
        try {
            Files.createDirectories(Paths.get(CARPETA_OCULTO));
            Files.createDirectories(Paths.get(CARPETA_RESUELTO));
        } catch (Exception e) {
            System.out.println("Error al crear las carpetas: " + e.getMessage());
        }
    }

    private static String encriptar(String datos) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(CLAVE_SECRETA.getBytes(), ALGORITMO);
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] datosEncriptados = cipher.doFinal(datos.getBytes());
        return Base64.getEncoder().encodeToString(datosEncriptados);
    }

    private static String desencriptar(String datosEncriptadosBase64) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(CLAVE_SECRETA.getBytes(), ALGORITMO);
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] bytesEncriptados = Base64.getDecoder().decode(datosEncriptadosBase64);
        byte[] datosDesencriptados = cipher.doFinal(bytesEncriptados);
        return new String(datosDesencriptados);
    }
}