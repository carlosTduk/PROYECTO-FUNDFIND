package proyectopoo;
import java.util.Date;


public class Mensaje {
    private int idMensaje;
    private String contenido;
    private Date fechaEnvio;
    private UsuarioDonador remitenteDonador;
    private Fundación remitenteFundacion;
    private Fundación destinatarioFundacion;
    private UsuarioDonador destinatarioDonador;
    private boolean leido;
    
    public Mensaje() {//Constructor vacio solo pone que no se ha leido
        this.leido = false;
    }

    public Mensaje(int idMensaje, String contenido, Date fechaEnvio, UsuarioDonador remitenteDonador, Fundación remitenteFundacion, Fundación destinatarioFundacion, UsuarioDonador destinatarioDonador, boolean leido) {
        this.idMensaje = idMensaje;
        this.contenido = contenido;
        this.fechaEnvio = fechaEnvio;
        this.remitenteDonador = remitenteDonador;
        this.remitenteFundacion = remitenteFundacion;
        this.destinatarioFundacion = destinatarioFundacion;
        this.destinatarioDonador = destinatarioDonador;
        this.leido = leido;
    }

    // Getters y setters
    public int getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(int idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public UsuarioDonador getRemitenteDonador() {
        return remitenteDonador;
    }

    public void setRemitenteDonador(UsuarioDonador remitenteDonador) {
        this.remitenteDonador = remitenteDonador;
    }

    public Fundación getRemitenteFundacion() {
        return remitenteFundacion;
    }

    public void setRemitenteFundacion(Fundación remitenteFundacion) {
        this.remitenteFundacion = remitenteFundacion;
    }

    public Fundación getDestinatarioFundacion() {
        return destinatarioFundacion;
    }

    public void setDestinatarioFundacion(Fundación destinatarioFundacion) {
        this.destinatarioFundacion = destinatarioFundacion;
    }

    public UsuarioDonador getDestinatarioDonador() {
        return destinatarioDonador;
    }

    public void setDestinatarioDonador(UsuarioDonador destinatarioDonador) {
        this.destinatarioDonador = destinatarioDonador;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }

    // Métodos 
    public void enviar() {
        this.fechaEnvio = new Date();
        this.leido = false;
        System.out.println("Mensaje enviado: " + contenido);
    }

    public void marcarComoLeido() {
        this.leido = true;
        System.out.println("Mensaje " + idMensaje + " marcado como leido.");
    }
}

