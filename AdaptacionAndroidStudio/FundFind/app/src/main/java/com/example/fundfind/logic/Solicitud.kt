package com.example.fundfind.logic

import java.util.Date

class Solicitud(// Getters y setters
    var idSolicitud: Int,
    @JvmField var titulo: String?,
    var descripcion: String?,
    var tipoDonacion: String?,
    var causaSocial: String?,
    @JvmField var estado: String?,
    @JvmField var fechaPublicacion: Date?,
    @JvmField var fundacion: Fundacion?
) {
    // Métodos 
    fun publicar() {
        this.estado = "PUBLICADA"
        this.fechaPublicacion = Date() //Guarda la fecha actual
        println("Solicitud '" + titulo + "' publicada.")
    }

    fun actualizarDatos() {
        println("Datos de la solicitud '" + titulo + "' actualizados")
    }

    fun marcarCompletada() {
        this.estado = "COMPLETADA"
        println("Solicitud '" + titulo + "' completada.")
    }

    fun eliminar() {
        this.estado = "ELIMINADA"
        println("Solicitud '" + titulo + "' eliminada")
    }

    fun activa(): Boolean {
        return "PUBLICADA".equals(
            estado,
            ignoreCase = true
        ) //regresa true si el estado si es igual a publicada y si no, si ya fue eliminada o ya se completo regresa false
    }
}
