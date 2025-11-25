package proyectopoo;
import java.util.Date;
import java.util.List;


public class PROYECTOPOO {

    public static void main(String[] args) {
        
    }

public class UsuarioDonador {

    // Atributos
    private int idUsuario;
    private String nombre;
    private String correo;
    private String contraseña;
    private String ubicacion;
    private double latitud;
    private double longitud;
    private List<String> intereses;
    private String estadoCuenta;
    private Date fechaRegistro;

    // Métodos
    public void registrar() {
        // TODO: implementar
    }

    public boolean login(String correo, String contraseña) {
        // TODO: implementar
        return false;
    }

    public void editarPerfil(UsuarioDonador datosActualizados) {
        // TODO: implementar
    }

    public void eliminarCuenta() {
        // TODO: implementar
    }

    public List<Fundacion> verListadoFundaciones(String filtros) {
        // TODO: implementar
        return null;
    }

    public List<Fundacion> verListadoSolicitudes() {
        // TODO: implementar
        return null;
    }

    public List<Notificacion> verNotificaciones() {
        // implementar
        return null;
    }

    public Mensaje enviarMensaje(Fundacion fundacion, String texto) {
        // implementar
        return null;
    }

    public Retroalimentacion dejarRetroalimentacion(Fundacion fundacion, Donacion donacion, int calificacion,String comentario) {
        // implementar
        return null;
    }
}

    
}
