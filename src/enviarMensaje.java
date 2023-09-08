import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class enviarMensaje {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int puerto = 12345;
        ServerSocket serverSocket = new ServerSocket(puerto);
        Socket socket = serverSocket.accept();

        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());

        Mensaje objetoRecibido = (Mensaje) entrada.readObject();
        System.out.println("Mensaje recibido: " + objetoRecibido.getClavePublica());

        Mensaje objetoParaEnviar = new Mensaje("¡Hola, cliente!");
        salida.writeObject(objetoParaEnviar);

        entrada.close();
        salida.close();
        socket.close();
    }
}
