
package proyectopoo;
import java.util.Date;

public class Fundación {
    private int idFundacion;
    private String nombre;
    private String rfc;
    private String direccion; 
    private String categoria;
    private double correoContacto;
    private double telefonoContacto;
    private String documentacionLegal;
    private boolean verificado;
    private String estadoCuenta;
    private Date fechaRegistro;

    public Fundación(int idFundacion, String nombre, String rfc, String direccion, String categoria, double correoContacto, double telefonoContacto, String documentacionLegal, boolean verificado, String estadoCuenta, Date fechaRegistro) {
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

    public double getCorreoContacto() {
        return correoContacto;
    }

    public void setCorreoContacto(double correoContacto) {
        this.correoContacto = correoContacto;
    }

    public double getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(double telefonoContacto) {
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
    
    
    
    
}
