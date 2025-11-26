package proyectopoo;
import java.util.Date;

public class Donacion {
    private int idDonacion;
    private Date fecha;
    private double monto;
    private String tipoDonativo;
    private String estado;
    private String comprobante;
    private UsuarioDonador usuario;
    private Solicitud solicitud;

    public Donacion(int idDonacion, Date fecha, double monto, String tipoDonativo, String estado, String comprobante, UsuarioDonador usuario, Solicitud solicitud) {
        this.idDonacion = idDonacion;
        this.fecha = fecha;
        this.monto = monto;
        this.tipoDonativo = tipoDonativo;
        this.estado = estado;
        this.comprobante = comprobante;
        this.usuario = usuario;
        this.solicitud = solicitud;
    }

    // Getters y setters
    public int getIdDonacion() {
        return idDonacion;
    }

    public void setIdDonacion(int idDonacion) {
        this.idDonacion = idDonacion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getTipoDonativo() {
        return tipoDonativo;
    }

    public void setTipoDonativo(String tipoDonativo) {
        this.tipoDonativo = tipoDonativo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public UsuarioDonador getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDonador usuario) {
        this.usuario = usuario;
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    // Métodos
    public void registrarIntencion() {
        this.estado = "INTENCIONREGISTRADA";//Se cambia el estado a esto
        this.fecha = new Date();
        System.out.println("Intencion de donacion registrada por " + usuario.getNombre());//usa el getter de UsuarioDonador
    }

    public void confirmar() {
        this.estado = "CONFIRMADA";//El estado se cambia a esto
        if (this.fecha == null) {//si no es null la fecha se cambia a la fecha actual
            this.fecha = new Date();
        }
        System.out.println("Donacion confirmada.");
    }

    public void cancelar() {
        this.estado = "CANCELADA";//Se cambia a este estado
        System.out.println("Donacion cancelada.");
    }

    public String generarComprobante() {
        this.comprobante = "COMP-" + idDonacion;
        System.out.println("Comprobante generado: " + comprobante);
        return comprobante;
    }
}

