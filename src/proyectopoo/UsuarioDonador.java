package proyectopoo;

import java.util.Date;
import java.util.List;

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
    
    //constructor

    public UsuarioDonador(int idUsuario, String nombre, String correo, String contraseña, String ubicacion, double latitud, double longitud, List<String> intereses, String estadoCuenta, Date fechaRegistro) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contraseña = contraseña;
        this.ubicacion = ubicacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.intereses = intereses;
        this.estadoCuenta = estadoCuenta;
        this.fechaRegistro = fechaRegistro;
    }
    

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

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public List<String> getIntereses() {
        return intereses;
    }

    public void setIntereses(List<String> intereses) {
        this.intereses = intereses;
    }

    public String getEstadoCuenta() {
        return estadoCuenta;
    }

    public void setEstadoCuenta(String estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    
}

