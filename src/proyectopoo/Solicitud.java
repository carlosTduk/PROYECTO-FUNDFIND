package proyectopoo;
import java.util.Date;

public class Solicitud {
    private int idSolicitud;
    private String titulo;
    private String descripcion;
    private String tipoDonacion;
    private String causaSocial;
    private String estado;
    private Date fechaPublicacion;
    private Fundación fundacion;

    public Solicitud(int idSolicitud, String titulo, String descripcion, String tipoDonacion, String causaSocial, String estado,Date fechaPublicacion , Fundación fundacion) {
        this.idSolicitud = idSolicitud;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipoDonacion = tipoDonacion;
        this.causaSocial = causaSocial;
        this.estado = estado;
        this.fechaPublicacion = fechaPublicacion;
        this.fundacion = fundacion;
    }

    // Getters y setters
    public int getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoDonacion() {
        return tipoDonacion;
    }

    public void setTipoDonacion(String tipoDonacion) {
        this.tipoDonacion = tipoDonacion;
    }

    public String getCausaSocial() {
        return causaSocial;
    }

    public void setCausaSocial(String causaSocial) {
        this.causaSocial = causaSocial;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Date fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public Fundación getFundacion() {
        return fundacion;
    }

    public void setFundacion(Fundación fundacion) {
        this.fundacion = fundacion;
    }

    // Métodos 
    public void publicar() {
        this.estado = "PUBLICADA";
        this.fechaPublicacion = new Date();//Guarda la fecha actual
        System.out.println("Solicitud '" + titulo + "' publicada.");
    }

    public void actualizarDatos() {
        System.out.println("Datos de la solicitud '" + titulo + "' actualizados");
    }

    public void marcarCompletada() {
        this.estado = "COMPLETADA";
        System.out.println("Solicitud '" + titulo + "' completada.");
    }

    public void eliminar() {
        this.estado = "ELIMINADA";
        System.out.println("Solicitud '" + titulo + "' eliminada");
    }

    public boolean activa() {
        return "PUBLICADA".equalsIgnoreCase(estado);//regresa true si el estado si es igual a publicada y si no, si ya fue eliminada o ya se completo regresa false
    }
}
