package com.example.fundfind.logic

import java.util.Date

class Retroalimentacion : java.io.Serializable {
    var idRetroalimentacion: Int = 0
    var calificacion: Int = 0
    var comentario: String? = null
    var fecha: Date? = null
    var fundacion: Fundacion? = null
    var usuario: UsuarioDonador? = null

    constructor()

    constructor(
        idRetroalimentacion: Int,
        calificacion: Int,
        comentario: String?,
        fecha: Date?,
        fundacion: Fundacion?,
        usuario: UsuarioDonador?
    ) {
        this.idRetroalimentacion = idRetroalimentacion
        this.calificacion = calificacion
        this.comentario = comentario
        this.fecha = fecha
        this.fundacion = fundacion
        this.usuario = usuario
    }

    // Métodos
    fun registrar() {
        if (this.fecha == null) {
            this.fecha = Date()
        }
        println("Retroalimentacion registrada para " + fundacion?.nombre + " con calificacion " + calificacion + ". Comentario: " + comentario)
    }

    fun eliminar() {
        println("Retroalimentacion " + idRetroalimentacion + " eliminada")
    }
}

