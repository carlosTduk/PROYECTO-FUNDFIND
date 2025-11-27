package proyectopoo;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class PROYECTOPOO {
    private static Scanner leer = new Scanner(System.in);
    private static FundFindSistema sistema = new FundFindSistema();
    
    public static void main(String[] args) {
        menuPrincipal();
    }
    
    private static void menuPrincipal() {
        int opcion;

        do {
            System.out.println("====== FUND FIND ======");
            System.out.println("1  Iniciar sesion como Usuario Donador");
            System.out.println("2  Iniciar sesion como Fundacion");
            System.out.println("3  Salir");
            System.out.print("Elige una opcion: ");
            
            opcion = leer.nextInt();
            leer.nextLine(); // limpia el salto de línea


            switch (opcion) {
                case 1:
                    loginDonador(sistema);
                    break;
                case 2:
                    loginFundacion(sistema);
                    break;
                case 3:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opcion no valida");
                    break;
            }

        } while (opcion != 3);
    }
    private static void loginDonador(FundFindSistema sistema) {
        System.out.println("\nLOGIN USUARIO DONADOR");
        System.out.print("Correo: ");
        String correo = leer.nextLine();
        System.out.print("Contraseña: ");
        String contrasena = leer.nextLine();

        UsuarioDonador u = sistema.loginDonador(correo, contrasena);//usa el metodo de FundFindSistema

        if (u == null) {
            System.out.println("Correo o contraseña incorrectos\n");
        } else {
            System.out.println("Bienvenido, " + u.getNombre() + "!\n");
            menuDonador(sistema, u);//Se abre su menu
        }
    }
    
    private static void loginFundacion(FundFindSistema sistema) {
        System.out.println("\nLOGIN FUNDACION");
        System.out.print("Correo de contacto: ");
        String correo = leer.nextLine();
        System.out.print("RFC: ");
        String rfc = leer.nextLine();

        Fundación f = sistema.loginFundacion(correo, rfc);//usa el metodo de FundFindSistema

        if (f == null) {
            System.out.println("Datos incorrectos\n");
        } else {
            System.out.println("Bienvenida, fundacion " + f.getNombre() + "\n");
            menuFundacion(sistema, f);//Se abre su menu
        }
    }
    
    private static void menuDonador(FundFindSistema sistema, UsuarioDonador usuario) {
        int opcion;

        do {
            System.out.println("MENU USUARIO DONADOR");
            System.out.println("1 Ver listado de Fundaciones");
            System.out.println("2 Enviar mensaje a una Fundación");
            System.out.println("3 Dejar retroalimentacion a una Fundación");
            System.out.println("4 Ver solicitudes publicadas");
            System.out.println("5 Cerrar sesion");
            System.out.print("Elige una opcion: ");

            opcion = leer.nextInt();
            leer.nextLine(); // limpia el salto de línea

            switch (opcion) {
                case 1:
                    mostrarFundaciones(sistema);
                    break;
                case 2:
                    enviarMensajeDesde(sistema, usuario);
                    break;
                case 3:
                    dejarRetroalimentacion(sistema, usuario);
                    break;
                case 4:
                    verSolicitudesPublicadas(sistema);
                    break;
                case 5:
                    System.out.println("Cerrando sesion ...\n");
                    break;
                default:
                    System.out.println("Opcion no valida");
                    break;
            }

        } while (opcion != 5);
    }
    
    private static void menuFundacion(FundFindSistema sistema, Fundación fundacion) {
        int opcion;

        do {
            System.out.println("MENU FUNDACION");
            System.out.println("1 Ver mis solicitudes");
            System.out.println("2 Publicar nueva solicitud");
            System.out.println("3 Cerrar sesion");
            System.out.print("Elige una opcion: ");

            opcion = leer.nextInt();
            leer.nextLine(); // limpia el salto de línea

            switch (opcion) {
                case 1:
                    verSolicitudesDeFundacion(sistema, fundacion);
                    break;
                case 2:
                    publicarSolicitudNueva(sistema, fundacion);
                    break;
                case 3:
                    verMensajesFundacion(fundacion); 
                break;
                case 4:
                    verRetroalimentacionesFundacion(fundacion);  // 👈 NUEVO
                break;
                case 5:
                    System.out.println("Cerrando sesion...\n");
                    break;
                default:
                    System.out.println("Opcion no valida");
                    break;
            }

        } while (opcion != 3);
    }

    //opciones donador
    private static void mostrarFundaciones(FundFindSistema sistema) {
        System.out.println("\nLISTADO DE FUNDACIONES");
        List<Fundación> fundaciones = sistema.getFundaciones();
        for (int i = 0; i < fundaciones.size(); i++) {
            Fundación f = fundaciones.get(i);
            System.out.println((i + 1) + "- " + f.getNombre()+ " - Categoria: " + f.getCategoria());
        }
        System.out.println();
    }

    private static void enviarMensajeDesde(FundFindSistema sistema, UsuarioDonador usuario) {
        List<Fundación> fundaciones = sistema.getFundaciones();
        int opcion;

        mostrarFundaciones(sistema);
        System.out.print("Elige el numero de la fundacion: ");
        
        opcion = leer.nextInt();
        leer.nextLine(); // limpia el salto de línea

        if (opcion < 1 || opcion > fundaciones.size()) {
            System.out.println("Fundacion no valida\n");
            return;
        }

        Fundación destino = fundaciones.get(opcion - 1);//-1 porque como las listas empiezan en 0, el usuario eligio el numero que en la lista en realidad esta como un numero menos
        System.out.print("Escribe tu mensaje: ");
        String texto = leer.nextLine();

        Mensaje m = sistema.enviarMensaje(usuario, destino, texto);
        System.out.println("Mensaje creado\n");
    }

    private static void dejarRetroalimentacion(FundFindSistema sistema, UsuarioDonador usuario) {
        List<Fundación> fundaciones = sistema.getFundaciones();
        int calificacion;
        int opcion;
        
        mostrarFundaciones(sistema);
        System.out.print("Elige el numero de la fundacion: ");
        opcion = leer.nextInt();
        leer.nextLine(); // limpia el salto de línea

        if (opcion < 1 || opcion > fundaciones.size()) {
            System.out.println("Fundacion no valida\n");
            return;
        }

        Fundación destino = fundaciones.get(opcion - 1);

        System.out.print("Calificacion (1 a 5): ");
        calificacion = leer.nextInt();
        leer.nextLine(); // limpia el salto de línea
        
        System.out.print("Comentario: ");
        String comentario = leer.nextLine();

        Retroalimentacion r = sistema.dejarRetroalimentacion(usuario, destino, calificacion, comentario);
        System.out.println("Retroalimentacion creada: " + r.getComentario() + "\n");
    }

    private static void verSolicitudesPublicadas(FundFindSistema sistema) {
        System.out.println("\nSOLICITUDES PUBLICADAS");
        List<Solicitud> activas = sistema.getSolicitudesActivas();//metodo de FundFindSistema
        
        for (int i = 0; i < activas.size(); i++) {
            Solicitud s = activas.get(i);
            System.out.println(s.getTitulo()+ " | Fundacion: " + s.getFundacion().getNombre()+ " | Estado: " + s.getEstado());
        }
        System.out.println();
    }

    //opciones de fundacion
    private static void verSolicitudesDeFundacion(FundFindSistema sistema, Fundación fundacion) {
        System.out.println("\nMIS SOLICITUDES");
        List<Solicitud> mias = sistema.getSolicitudesDeFundacion(fundacion);
        for (int i = 0; i < mias.size(); i++) {
            Solicitud s = mias.get(i);
            System.out.println(s.getTitulo()+ " | Estado: " + s.getEstado());
        }
        System.out.println();
    }

    private static void publicarSolicitudNueva(FundFindSistema sistema, Fundación fundacion) {
        System.out.println("\nPUBLICAR NUEVA SOLICITUD");
        System.out.print("Titulo: ");
        String titulo = leer.nextLine();
        System.out.print("Descripcion: ");
        String descripcion = leer.nextLine();
        System.out.print("Tipo de donacion: ");
        String tipo = leer.nextLine();
        System.out.print("Causa social: ");
        String causa = leer.nextLine();

        Solicitud s = sistema.publicarSolicitud(fundacion, titulo, descripcion, tipo, causa);//metodo de FundFindSistema
        System.out.println("Solicitud publicada: " + s.getTitulo() + "\n");
    }
    
    private static void verMensajesFundacion(Fundación fundacion) {
    List<Mensaje> lista = sistema.getMensajesParaFundacion(fundacion);

    System.out.println("\n--- Mensajes recibidos por " + fundacion.getNombre() + " ---");

    if (lista.isEmpty()) {
        System.out.println("No tienes mensajes.");
    } else {
        for (int i = 0; i < lista.size(); i++) {
            Mensaje m = lista.get(i);

            String nombreRemitente = "Desconocido";
            if (m.getRemitenteDonador() != null) {
                nombreRemitente = m.getRemitenteDonador().getNombre();
            }
            System.out.println((i + 1) + ") De: " + nombreRemitente+ " | Contenido: " + m.getContenido());
        }
    }
    System.out.println();
}

    private static void verRetroalimentacionesFundacion(Fundación fundacion) {
    List<Retroalimentacion> lista = sistema.getRetroalimentacionesParaFundacion(fundacion);

    System.out.println("\nRETROALIMENTACIONES");

    if (lista.isEmpty()) {
        System.out.println("No tienes retroalimentaciones.");
    } else {
        for (int i = 0; i < lista.size(); i++) {
            Retroalimentacion r = lista.get(i);

            String nombreUsuario = "Desconocido";
            if (r.getUsuario() != null) {
                nombreUsuario = r.getUsuario().getNombre();
            }

            System.out.println((i + 1) + ") De: " + nombreUsuario + " | Calificacion: " + r.getCalificacion() +" | Comentario: " + r.getComentario() +" | Fecha: " + r.getFecha());
        }
    }
    System.out.println();
}


    
}
