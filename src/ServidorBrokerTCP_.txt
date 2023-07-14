/*
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ServidorBrokerTCP {// TOPICO:DATOS
    private static final int PUERTO = 9999;
    private HashSet<Cliente> clientes;

    public ServidorBrokerTCP() {
        clientes = new HashSet<>();
    }

    public void iniciar() {
        try {
            ServerSocket serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado. Esperando conexiones...");

            while (true) {
                Socket socket = serverSocket.accept();
                Cliente cliente = new Cliente(socket);
                clientes.add(cliente);
                cliente.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void eliminarCliente() {
        clientes.remove();
        System.out.println("Cliente desconectado");
    }
}*/
