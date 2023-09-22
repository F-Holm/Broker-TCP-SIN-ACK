package Tests;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
public class recibirMensaje {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int puerto = 12345;
        final String SERVIDOR = "127.0.0.1";
        Socket socket = new Socket(SERVIDOR, puerto);

        ObjectInputStream objetoEntrada = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream objetoSalida = new ObjectOutputStream(socket.getOutputStream());

        Mensaje objetoParaEnviar = new Mensaje("¡Hola, servidor!");
        objetoSalida.writeObject(objetoParaEnviar);

        Mensaje objetoRecibido = (Mensaje) objetoEntrada.readObject();
        System.out.println("Tests.Mensaje recibido: " + objetoRecibido.getClavePublica());

        objetoEntrada.close();
        objetoSalida.close();
        socket.close();
    }
}
