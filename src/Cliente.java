/*
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Cliente extends Thread {
    private Socket socket;
    private PrintWriter escritor;
    private BufferedReader lector;
    private HashSet<String> topicos;

    public Cliente(Socket socket) {
        this.socket = socket;
        topicos = new HashSet<>();

        try {
            escritor = new PrintWriter(socket.getOutputStream(), true);
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        try {
            String nombreCliente = lector.readLine();
            System.out.println("Cliente conectado: " + nombreCliente);

            String mensaje;
            while ((mensaje = lector.readLine()) != null) {
                if (mensaje.startsWith("SUB:")) {
                    suscribirse(mensaje.substring(4));
                } else if (mensaje.startsWith("DESUB:")) {
                    deSuscribirse(mensaje.substring(6));
                } else if (mensaje.startsWith("DEL:")) {
                    eliminar();
                } else {
                    enviarMensaje(obtenerTopico(mensaje), obtenerMensaje(mensaje));
                }
            }
            socket.close();
            eliminarCliente();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void enviarMensaje(String topico, String mensaje) {
        System.out.println("Enviando mensaje al tópico " + topico + ": " + mensaje);
        for (Cliente cliente : clientes) if (cliente.estaSuscrito(topico)) cliente.enviar(mensaje);
    }
    public void deSuscribirse(String topico) {
        topicos.remove(topico);
        System.out.println("Cliente no está suscrito al tópico: " + topico);
    }
    public void suscribirse(String topico) {
        topicos.add(topico);
        System.out.println("Cliente suscrito al tópico: " + topico);
    }
    public String obtenerMensaje(String mensaje) {
        for (int i = 0;i < mensaje.length();i++){
            if (mensaje.charAt(i) == ':') return mensaje.substring(i + 1);
        }
        return "";
    }
    public String obtenerTopico(String mensaje) {
        for (int i = 0;i < mensaje.length();i++){
            if (mensaje.charAt(i) == ':') return mensaje.substring(0, i);
        }
        return "";
    }
    public void enviar(String mensaje) {
        escritor.println(mensaje);
    }
    public boolean estaSuscrito(String topico) {
        return topicos.contains(topico);
    }
}*/
