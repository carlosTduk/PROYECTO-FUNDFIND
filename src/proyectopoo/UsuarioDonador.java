package proyectopoo;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;


public class UsuarioDonador {
   
    // Atributos
    private int idUsuario;
    private String nombre;
    private String correo;
    private String contrasena;
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
        this.contrasena = contraseña;
        this.ubicacion = ubicacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.intereses = intereses;
        this.estadoCuenta = estadoCuenta;
        this.fechaRegistro = fechaRegistro;
    }
    
    // Getters y setters
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

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getUbicacionTexto() {
        return ubicacion;
    }

    public void setUbicacionTexto(String ubicacionTexto) {
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
    
    // Métodos
    public void registrar() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = new Date();
        }
        this.estadoCuenta = "ACTIVA";
        System.out.println("Usuario " + nombre + " registrado");
    }

    public boolean login(String correo, String contraseña) {//Valida que si este correcto el login con los datos que tenemos
        boolean datosCorrectos = this.correo != null && this.correo.equals(correo) && this.contrasena != null && this.contrasena.equals(contrasena);

        boolean cuentaActiva = "ACTIVA".equals(this.estadoCuenta);

        return datosCorrectos && cuentaActiva;
    }

    public void editarPerfil(UsuarioDonador datosActualizados) {
        if (datosActualizados == null) return;//Si no mandan nada se sale del metodo

        if (datosActualizados.getNombre() != null) {//Si si hay informacion se actualiza 
            this.nombre = datosActualizados.getNombre();
        }
        if (datosActualizados.getCorreo() != null) {
            this.correo = datosActualizados.getCorreo();
        }
        if (datosActualizados.getContrasena() != null) {
            this.contrasena = datosActualizados.getContrasena();
        }
        if (datosActualizados.getUbicacionTexto() != null) {
            this.ubicacion = datosActualizados.getUbicacionTexto();
        }
        if (datosActualizados.getIntereses() != null) {
            this.intereses = datosActualizados.getIntereses();
        }

        this.latitud = datosActualizados.getLatitud();
        this.longitud = datosActualizados.getLongitud();

        System.out.println("Perfil de " + nombre + " actualizado.");
    }

    public void eliminarCuenta() {
        this.estadoCuenta = "ELIMINADA";
        System.out.println("Usuario " + nombre + " marcado como eliminado.");
    }

    public List<Fundacion> verListadoFundaciones(String filtros) {
        System.out.println("Fundaciones con los filtros: " + filtros);
        return new ArrayList<>();//Regresa una nueva lista vacia en lo que implementamos las fundaciones o la Base de datos
    }

    public List<Fundacion> verListadoSolicitudes() {
        System.out.println("Solicitudes para el usuario " + nombre);
        return new ArrayList<>();
    }

    public List<Notificacion> verNotificaciones() {
        System.out.println("Notificaciones del usuario " + nombre);
        return new ArrayList<>();//Regresa tambien una nueva lista vacia
    }

    public Mensaje enviarMensaje(Fundacion fundacion, String texto) {//Regresa un objeto Mensaje
        if (fundacion == null) return null;//Si es null no se manda nada

        Mensaje mensaje = new Mensaje();//nuevo objeto Mensaje, pero aun no tiene contenido
        mensaje.setContenido(texto);//Se llama al setter de la clase Mensaje
        mensaje.setFechaEnvio(new Date());//Crea la fecha con la fecha actual
        mensaje.setRemitenteDonador(this);//this es el UsuarioDonador que esta realizando este metodo
        mensaje.setDestinatarioFundacion(fundacion);

        System.out.println("Mensaje enviado a fundacion " + fundacion.getNombre());
        return mensaje;
    }

    public Retroalimentacion dejarRetroalimentacion(Fundacion fundacion, Donacion donacion, int calificacion,String comentario) {
        if (fundacion == null) return null;//Si es null o se manda nada

        Retroalimentacion r = new Retroalimentacion();//nuevo objeto Retroalimentacion, pero aun está vacia
        r.setFundacion(fundacion);//Asigna a la fundacion a la cual se le esta haciendo esto
        r.setUsuario(this);//Es el Usuario donador actual
        r.setCalificacion(calificacion);//Guarda el valor que se mando
        r.setComentario(comentario);//Guarda el comentario que se mando
        r.setFecha(new Date());

        System.out.println("Retroalimentacion registrada para fundacion " + fundacion.getNombre());
        return r;//regresa la fundacion, el usuario, la calificacion, el comentario y la fecha
    }
}

