import java.io.Serializable;

public class Mensaje implements Serializable {
    private String mensajeHasheadoEncriptado;
    private String mensajeEncriptado;

    public Mensaje(String mensajeHasheadoEncriptado, String mensajeEncriptado) {
        this.mensajeHasheadoEncriptado = mensajeHasheadoEncriptado;
        this.mensajeEncriptado = mensajeEncriptado;
    }
    public Mensaje(String clavePublica) {
        this.mensajeHasheadoEncriptado = null;
        this.mensajeEncriptado = clavePublica;
    }

    public String getMensajeHasheadoEncriptado() {
        return mensajeHasheadoEncriptado;
    }
    public String getClavePublica() {
        return mensajeHasheadoEncriptado;
    }

    public void setMensajeHasheadoEncriptado(String mensajeHasheadoEncriptado) {
        this.mensajeHasheadoEncriptado = mensajeHasheadoEncriptado;
    }

    public String getMensajeEncriptado() {
        return mensajeEncriptado;
    }

    public void setMensajeEncriptado(String mensajeEncriptado) {
        this.mensajeEncriptado = mensajeEncriptado;
    }
}
