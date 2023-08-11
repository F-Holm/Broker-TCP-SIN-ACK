import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        final String SERVIDOR = "172.16.255.164";
        final int PUERTO = 12345;

        try {
            Socket socket = new Socket(SERVIDOR, PUERTO);
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);

            System.out.println("Cliente conectado al servidor en " + SERVIDOR + ":" + PUERTO);

            Thread receptor = new Thread(() -> {
                String mensaje;
                try {
                    while ((mensaje = entrada.readLine()) != null) {
                        System.out.println(mensaje);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receptor.start();

            while (true) {
                System.out.println("Elige una acción:");
                System.out.println("1. Suscribirse a un tópico");
                System.out.println("2. Desuscribirse de un tópico");
                System.out.println("3. Enviar mensaje a un tópico");
                System.out.println("4. Desconectarse");
                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        System.out.println("Ingresa el nombre del tópico al que te quieres suscribir:");
                        String topicoSub = scanner.nextLine();
                        salida.println("SUB:" + topicoSub);
                        break;
                    case 2:
                        System.out.println("Ingresa el nombre del tópico del que te quieres desuscribir:");
                        String topicoDesub = scanner.nextLine();
                        salida.println("DESUB:" + topicoDesub);
                        break;
                    case 3:
                        System.out.println("Ingresa el nombre del tópico al que quieres enviar un mensaje:");
                        String topicoMensaje = scanner.nextLine();
                        System.out.println("Escribe el mensaje:");
                        String contenidoMensaje = scanner.nextLine();
                        salida.println(topicoMensaje + ":" + contenidoMensaje);
                        break;
                    case 4:
                        salida.println("DEL:");
                        socket.close();
                        return;
                    default:
                        System.out.println("Opción inválida.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}