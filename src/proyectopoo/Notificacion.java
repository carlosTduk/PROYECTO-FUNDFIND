package proyectopoo;
import java.util.Date;

public class Notificacion {
    private int idNotificacion;
    private String mensaje;
    private String tipo;
    private Date fecha;
    private boolean leida;
    private UsuarioDonador receptorUsuario;
    private Fundación receptorFundacion;
    private String enlaceRecurso;

    public Notificacion() {//Constructor vacio solo pone que no se ha leido
        this.leida = false;
    }

    public Notificacion(int idNotificacion, String mensaje, String tipo, Date fecha, boolean leida, UsuarioDonador receptorUsuario, Fundación receptorFundacion, String enlaceRecurso) {
        this.idNotificacion = idNotificacion;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.fecha = fecha;
        this.leida = leida;
        this.receptorUsuario = receptorUsuario;
        this.receptorFundacion = receptorFundacion;
        this.enlaceRecurso = enlaceRecurso;
    }

    // Getters y setters
    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public UsuarioDonador getReceptorUsuario() {
        return receptorUsuario;
    }

    public void setReceptorUsuario(UsuarioDonador receptorUsuario) {
        this.receptorUsuario = receptorUsuario;
    }

    public Fundación getReceptorFundacion() {
        return receptorFundacion;
    }

    public void setReceptorFundacion(Fundación receptorFundacion) {
        this.receptorFundacion = receptorFundacion;
    }

    public String getEnlaceRecurso() {
        return enlaceRecurso;
    }

    public void setEnlaceRecurso(String enlaceRecurso) {
        this.enlaceRecurso = enlaceRecurso;
    }

    // Métodos 
    public void enviar() {
        if (this.fecha == null) {//si no es null la fecha se cambia a la fecha actual
            this.fecha = new Date();
        }
        this.leida = false;//Al enviarlo se marca que no se ha leido
        System.out.println("Notificacion enviada: " + mensaje);
    }

    public void marcarComoLeida() {
        this.leida = true;//Ahora si se marca que ya se leyó
        System.out.println("Notificacion " + idNotificacion + " leida");
    }
}
