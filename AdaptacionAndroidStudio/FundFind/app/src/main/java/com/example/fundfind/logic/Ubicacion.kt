package com.example.fundfind.logic

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Ubicacion : java.io.Serializable {
    // Getters y setters
    var latitud: Double = 0.0
    var longitud: Double = 0.0
    var direccionTexto: String? = null

    constructor()

    constructor(latitud: Double, longitud: Double, direccionTexto: String?) {
        this.latitud = latitud
        this.longitud = longitud
        this.direccionTexto = direccionTexto
    }

    /**
     * Distancia aproximada en km entre esta ubicacion y otra (Haversine).
     */
    fun calcularDistancia(otra: Ubicacion?): Double {
        if (otra == null) return 0.0 //Si la ubicacion mandada es null regresa 0.0


        val R = 6371.0 // radio Tierra km
        //convierte todo lo siguiente en radianes ya que seno y coseno trabajan con radianes
        val latRad1 = Math.toRadians(this.latitud) //this es la ubicacion actual del usuario donador
        val latRad2 = Math.toRadians(otra.latitud) //otra es la ubicacion de la fundacion
        val deltaLat =
            Math.toRadians(otra.latitud - this.latitud) //es la diferiencia de latitudes entre los dos puntos
        val deltaLon =
            Math.toRadians(otra.longitud - this.longitud) //es la diferiencia de longitudes entre los dos puntos

        val a =
            sin(deltaLat / 2) * sin(deltaLat / 2) + cos(latRad1) * cos(latRad2) * sin(deltaLon / 2) * sin(
                deltaLon / 2
            ) //Es la formula de Haversine para calcular distancias en la Tierra

        val c = 2 * atan2(
            sqrt(a),
            sqrt(1 - a)
        ) //Aqui se esta sacando el angulo en radianes entre los 2 puntos.

        return R * c //Regresa la distancia multiplicando el radio de la tierra por el angulo. Lo regresa en km ya que el radio esta en km
    }
}
