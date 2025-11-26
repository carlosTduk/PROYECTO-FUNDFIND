
package proyectopoo;
import java.util.Date;
import java.util.ArrayList;

public class Fundacion {
    private int idFundacion;
    private String nombre;
    private String rfc;
    private String direccion; 
    private String categoria;
    private String correoContacto;
    private String telefonoContacto;
    private String documentacionLegal;
    private boolean verificado;
    private String estadoCuenta;
    private Date fechaRegistro;

    public Fundacion(int idFundacion, String nombre, String rfc, String direccion, String categoria, String correoContacto, String telefonoContacto, String documentacionLegal, boolean verificado, String estadoCuenta, Date fechaRegistro) {
        this.idFundacion = idFundacion;
        this.nombre = nombre;
        this.rfc = rfc;
        this.direccion = direccion;
        this.categoria = categoria;
        this.correoContacto = correoContacto;
        this.telefonoContacto = telefonoContacto;
        this.documentacionLegal = documentacionLegal;
        this.verificado = verificado;
        this.estadoCuenta = estadoCuenta;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdFundacion() {
        return idFundacion;
    }

    public void setIdFundacion(int idFundacion) {
        this.idFundacion = idFundacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCorreoContacto() {
        return correoContacto;
    }

    public void setCorreoContacto(String correoContacto) {
        this.correoContacto = correoContacto;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public String getDocumentacionLegal() {
        return documentacionLegal;
    }

    public void setDocumentacionLegal(String documentacionLegal) {
        this.documentacionLegal = documentacionLegal;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
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
    
    //Metodos
    
    public void registrar() {
        if (this.fechaRegistro == null) {//Si no es null se guarda con la fecha
            this.fechaRegistro = new Date();
        }
        
        this.estadoCuenta = "ACTIVA";
        this.verificado = false;
        System.out.println("Fundacion " + nombre + " registrada");
    }

    public void editarPerfil(Fundacion datosActualizados) {
        if (datosActualizados == null) return;//Si no mandan nada se sale del metodo

        if (datosActualizados.getNombre() != null) {//Si si hay informacion se actualiza
            this.nombre = datosActualizados.getNombre();
        }
        if (datosActualizados.getRfc() != null) {
            this.rfc = datosActualizados.getRfc();
        }
        if (datosActualizados.getDireccion() != null) {
            this.direccion = datosActualizados.getDireccion();
        }
        if (datosActualizados.getCategoria() != null) {
            this.categoria = datosActualizados.getCategoria();
        }
        if (datosActualizados.getCorreoContacto() != null) {
            this.correoContacto = datosActualizados.getCorreoContacto();
        }
        if (datosActualizados.getTelefonoContacto() != null) {
            this.telefonoContacto = datosActualizados.getTelefonoContacto();
        }
        if (datosActualizados.getDocumentacionLegal() != null) {
            this.documentacionLegal = datosActualizados.getDocumentacionLegal();
        }

        System.out.println("Fundacion " + nombre + " actualizada");
    }

    public List<Solicitud> gestionarSolicitudes() {
        System.out.println("Gestion de Solicitudes de la fundacion " + nombre);
        return new ArrayList<>();//Regresa una nueva lista vacia en lo que implementamos las fundaciones o la Base de datos
    }

    public void editarSolicitud(Solicitud solicitud) {
        if (solicitud == null) return;
        System.out.println("Editando solicitud " + solicitud.getTitulo() +" de la fundacion " + nombre);
    }

    public void eliminarSolicitud(Solicitud solicitud) {
        if (solicitud == null) return;
        System.out.println("Eliminando solicitud " + solicitud.getTitulo() +" de la fundacion " + nombre);
    }

    public void publicarSolicitud(Solicitud nueva) {
        if (nueva == null) return;
        nueva.setFundacion(this);//this es de la fundacion actual que esta haciendo esta accion
        nueva.setFechaPublicacion(new Date());
        nueva.setEstado("PUBLICADA");
        System.out.println("Solicitud '" + nueva.getTitulo() +"' publicada por la fundacion " + nombre);
    }

    public List<Notificacion> verNotificaciones() {
        System.out.println("Notificaciones de fundacion " + nombre);
        return new ArrayList<>();
    }

    public Mensaje responderMensaje(Mensaje mensaje, String respuesta) {
        if (mensaje == null) return null;

        Mensaje respuestaMensaje = new Mensaje();
        respuestaMensaje.setContenido(respuesta);//Se llama al setter de la clase Mensaje
        respuestaMensaje.setFechaEnvio(new Date());//Crea la fecha con la fecha actual
        respuestaMensaje.setRemitenteFundacion(this);//this es el Fundacion que esta realizando este metodo
        respuestaMensaje.setDestinatarioDonador(mensaje.getRemitenteDonador());//Dice de que donador viene y va el mensaje

        System.out.println("Fundacion " + nombre + " respondio mensaje.");
        return respuestaMensaje;//
    }
    
}
