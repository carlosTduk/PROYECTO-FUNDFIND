package proyectopoo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class FundFindSistema {//Esta clase guarda todos los usuarios, fundaaciones y las solicitudes
    private List<UsuarioDonador> usuarios = new ArrayList<UsuarioDonador>();//Aqui se guardan todos los usuarios que existan en el sistema
    private List<Fundación> fundaciones = new ArrayList<Fundación>();//aqui se guardan todas las fundaciones
    private List<Solicitud> solicitudes = new ArrayList<Solicitud>();//Aqui se guardan las solicitudes 
    private List<Mensaje> mensajes = new ArrayList<Mensaje>();//Aqui se guardan lss mensajes 
    private List<Retroalimentacion> retroalimentaciones = new ArrayList<Retroalimentacion>();//Aqui se guardan las retroalimentaciones

    public FundFindSistema() {//Constructor
        cargarDatosIniciales();//metodo para tener usuarios y fundaciones precargados para que no marque error de que todo está null
    }

    //getters
    public List<UsuarioDonador> getUsuarios() {
        return usuarios;
    }

    public List<Fundación> getFundaciones() {//se muestran las fundaciones cuando utilizamos el combobox
        return fundaciones;
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public List<Solicitud> getSolicitudesActivas() {//Es una lista  que muestra solo las Soictudes activas, osea si estan en esta PUBLICADA
        List<Solicitud> activas = new ArrayList<Solicitud>();
        for (int i = 0; i < solicitudes.size(); i++) {
            Solicitud s = solicitudes.get(i);
            if (s.activa()) {
                activas.add(s);
            }
        }
        return activas;
    }

    public List<Solicitud> getSolicitudesDeFundacion(Fundación f) {//Muestra las solicitudes de una fundacion especifica. Recorre todas y muestra las que pertenecen a esa fundacion y las agrega a la lista res
        List<Solicitud> res = new ArrayList<Solicitud>();
        for (int i = 0; i < solicitudes.size(); i++) {
            Solicitud s = solicitudes.get(i);
            if (s.getFundacion() == f) {
                res.add(s);
            }
        }
        return res;
    }
    
   public List<Mensaje> getMensajesParaFundacion(Fundación f) {
        List<Mensaje> res = new ArrayList<Mensaje>();
        for (int i = 0; i < mensajes.size(); i++) {
            Mensaje m = mensajes.get(i);
            if (m.getDestinatarioFundacion() == f) {// mensajes donde la fundación es destinataria
                res.add(m);
            }
        }
        return res;
    } 
   
   public List<Retroalimentacion> getRetroalimentacionesParaFundacion(Fundación f) {
        List<Retroalimentacion> res = new ArrayList<Retroalimentacion>();
        for (int i = 0; i < retroalimentaciones.size(); i++) {
            Retroalimentacion r = retroalimentaciones.get(i);
            if (r.getFundacion() == f) {
             res.add(r);
            }
        }
        return res;
    }


    // los logins
    public UsuarioDonador loginDonador(String correo, String contrasena) {
        for (int i = 0; i < usuarios.size(); i++) {//reccorre toda la lista usuarios
            UsuarioDonador u = usuarios.get(i);
            if (u.login(correo, contrasena)) {//llama al metodo de la clase Usuario Donador
                return u;//Si se valida el login lo regresa porque ya entró
            }
        }
        return null;//Si no coincide regresa null
    }

    public Fundación loginFundacion(String correoContacto, String rfc) {
        for (int i = 0; i < fundaciones.size(); i++) { //recorre toda la lista fundaciones
            Fundación f = fundaciones.get(i);
            boolean correoOk = f.getCorreoContacto().equals(correoContacto);//Revisa que si sean equals y lo marca como true o false
            boolean rfcOk = f.getRfc().equals(rfc);
            boolean activa = "ACTIVA".equals(f.getEstadoCuenta());
            if (correoOk && rfcOk && activa) {//revisa que todos estén en true
                return f;//Si si están en true se valida el login
            }
        }
        return null;//Si hay algun false regresa null
    }

    //acciones del donador
    public Mensaje enviarMensaje(UsuarioDonador usuario, Fundación fundacion, String texto) {//recibe al donador, a la fundacion y el mensaje
        if (usuario == null || fundacion == null) {
            return null;//si alguno es null regresa null y no hace nada
        }
        Mensaje m = usuario.enviarMensaje(fundacion, texto);
        mensajes.add(m);
        return m;
    }

    public Retroalimentacion dejarRetroalimentacion(UsuarioDonador usuario, Fundación fundacion, int calificacion, String comentario) {//recibe al donador, a la fundacion, la calificacion y el comentario
        if (usuario == null || fundacion == null) {
            return null;//si alguno es null regresa null y no hace nada
        }
        Retroalimentacion r = usuario.dejarRetroalimentacion(fundacion, null, calificacion, comentario);
        if (r != null) {
        retroalimentaciones.add(r);
        }
        return r;
    }

    //acciones de la fundacion
    public Solicitud publicarSolicitud(Fundación fundacion, String titulo, String descripcion, String tipoDonacion, String causaSocial) {//recibe esta informacion
        if (fundacion == null) {//si alguno es null regresa null y no hace nada
            return null;
        }
        
        int nuevoId = solicitudes.size() + 1;//calcula un nuevo id sumandole 1 a las que ya hay 

        Solicitud s = new Solicitud(nuevoId, titulo, descripcion, tipoDonacion, causaSocial, "BORRADOR", null, fundacion); //La info que lleva, por default el estado se pone como BORRAADOR y aun no hay fecha

        fundacion.publicarSolicitud(s);//metodo de la clase Fundacion, donde ahora el estado se cambia a PUBLICADA y le one la fecha. Y tambien imprime el mensaje
        solicitudes.add(s);//agrega la solicitud a s que es Solicitud por si la inferfaz la quiere mostrar

        return s;
    }

    private void cargarDatosIniciales() {
        List<String> intereses1 = new ArrayList<String>();
        intereses1.add("Viveres");
     
        UsuarioDonador u1 = new UsuarioDonador(1, "Camila", "camijal06@gmail.com","12345678", "Guadalajara, Jalisco", 20.67, -103.35,intereses1, "ACTIVA", new Date() );
        usuarios.add(u1);//significa que se añade u1 a la ista usuarios
        
        List<String> intereses2 = new ArrayList<String>();
        intereses2.add("Ropa");// le interesa donar ropa

        UsuarioDonador u2 = new UsuarioDonador(2, "Carlos", "carlosserna@gmail.com", "abcdef", "Zapopan, JAL", 20.70, -103.40, intereses2,"ACTIVA",new Date());
        usuarios.add(u2);

        Fundación f1 = new Fundación(1,"Fundacion Sonrisas", "FSR010101ABC", "Guadalajara, JAL", "Niños", "contacto@sonrisas.org", "3330000000", "Acta Constitutiva", true, "ACTIVA", new Date());

        Fundación f2 = new Fundación(2, "Fundacion Patitas", "PAT020202DEF", "Zapopan, JAL", "Animales", "info@patitas.org", "3331111111", "Documento Legal", true, "ACTIVA", new Date() );

        fundaciones.add(f1);
        fundaciones.add(f2);
    }
}
