package proyectopoo;

public class Ubicacion {
    private double latitud;
    private double longitud;
    private String direccionTexto;

    public Ubicacion() {//Constructor vacio
    }

    public Ubicacion(double latitud, double longitud, String direccionTexto) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccionTexto = direccionTexto;
    }

    // Getters y setters
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

    public String getDireccionTexto() {
        return direccionTexto;
    }

    public void setDireccionTexto(String direccionTexto) {
        this.direccionTexto = direccionTexto;
    }

    /**
     * Distancia aproximada en km entre esta ubicacion y otra (Haversine).
     */
    public double calcularDistancia(Ubicacion otra) {
        if (otra == null) return 0.0;//Si la ubicacion mandada es null regresa 0.0

        double R = 6371.0; // radio Tierra km
        //convierte todo lo siguiente en radianes ya que seno y coseno trabajan con radianes
        double latRad1 = Math.toRadians(this.latitud);//this es la ubicacion actual del usuario donador
        double latRad2 = Math.toRadians(otra.latitud);//otra es la ubicacion de la fundacion
        double deltaLat = Math.toRadians(otra.latitud - this.latitud);//es la diferiencia de latitudes entre los dos puntos
        double deltaLon = Math.toRadians(otra.longitud - this.longitud);//es la diferiencia de longitudes entre los dos puntos

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(latRad1) * Math.cos(latRad2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);//Es la formula de Haversine para calcular distancias en la Tierra

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));//Aqui se esta sacando el angulo en radianes entre los 2 puntos.

        return R * c;//Regresa la distancia multiplicando el radio de la tierra por el angulo. Lo regresa en km ya que el radio esta en km
    }
}
