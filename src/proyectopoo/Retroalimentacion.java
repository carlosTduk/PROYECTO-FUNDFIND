package proyectopoo;
import java.util.Date;

public class Retroalimentacion {
    private int idRetroalimentacion;
    private int calificacion;
    private String comentario;
    private Date fecha;
    private Fundación fundacion;
    private UsuarioDonador usuario;

    public Retroalimentacion() {
    }

    public Retroalimentacion(int idRetroalimentacion, int calificacion, String comentario, Date fecha, Fundación fundacion, UsuarioDonador usuario) {
        this.idRetroalimentacion = idRetroalimentacion;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fecha = fecha;
        this.fundacion = fundacion;
        this.usuario = usuario;
    }

    // Getters y setters
    public int getIdRetroalimentacion() {
        return idRetroalimentacion;
    }

    public void setIdRetroalimentacion(int idRetroalimentacion) {
        this.idRetroalimentacion = idRetroalimentacion;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Fundación getFundacion() {
        return fundacion;
    }

    public void setFundacion(Fundación fundacion) {
        this.fundacion = fundacion;
    }

    public UsuarioDonador getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDonador usuario) {
        this.usuario = usuario;
    }

    // Métodos
    public void registrar() {
        if (this.fecha == null) {//si no es null la fecha se cambia a la fecha actual
            this.fecha = new Date();
        }
        System.out.println("Retroalimentacion registrada para " + fundacion.getNombre()+ " con calificacion " + calificacion+". Comentario: "+ comentario);
    }

    public void eliminar() {
        System.out.println("Retroalimentacion " + idRetroalimentacion + " eliminada");
    }
}

