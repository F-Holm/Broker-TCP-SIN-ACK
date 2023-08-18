import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente2 {
    public static boolean servidorActivo = true;
    public static void main(String[] args) {
        final String SERVIDOR = "127.0.0.1";
        final int PUERTO = 12345;


        try {
            Socket socket = new Socket(SERVIDOR, PUERTO);
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);

            System.out.println("Cliente conectado al servidor en " + SERVIDOR + ":" + PUERTO);

            Thread receptor = new Thread(() -> {
                try {
                    String mensaje;
                    while (true) {
                        mensaje = entrada.readLine();
                        if (mensaje == null) {
                            System.out.println("Servidor ha cerrado la conexión.");
                            servidorActivo = false;
                            break;
                        }
                        if (mensaje.equals("CERRAR_SERVIDOR")) {
                            System.out.println("El servidor se ha cerrado.");
                            servidorActivo = false;
                            break;
                        }
                        System.out.println(mensaje);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        entrada.close();
                        salida.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

                if (!servidorActivo){
                    receptor.join();
                    return;
                }

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
                        receptor.join();
                        return;
                    default:
                        System.out.println("Opción inválida.");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
