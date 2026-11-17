package com.example.fundfind.logic

import java.util.Date

class Notificacion {
    // Getters y setters
    var idNotificacion: Int = 0
    var mensaje: String? = null
    var tipo: String? = null
    var fecha: Date? = null
    var isLeida: Boolean
    var receptorUsuario: UsuarioDonador? = null
    var receptorFundacion: Fundacion? = null
    var enlaceRecurso: String? = null

    constructor() { //Constructor vacio solo pone que no se ha leido
        this.isLeida = false
    }

    constructor(
        idNotificacion: Int,
        mensaje: String?,
        tipo: String?,
        fecha: Date?,
        leida: Boolean,
        receptorUsuario: UsuarioDonador?,
        receptorFundacion: Fundacion?,
        enlaceRecurso: String?
    ) {
        this.idNotificacion = idNotificacion
        this.mensaje = mensaje
        this.tipo = tipo
        this.fecha = fecha
        this.isLeida = leida
        this.receptorUsuario = receptorUsuario
        this.receptorFundacion = receptorFundacion
        this.enlaceRecurso = enlaceRecurso
    }

    // Métodos 
    fun enviar() {
        if (this.fecha == null) { //si no es null la fecha se cambia a la fecha actual
            this.fecha = Date()
        }
        this.isLeida = false //Al enviarlo se marca que no se ha leido
        println("Notificacion enviada: " + mensaje)
    }

    fun marcarComoLeida() {
        this.isLeida = true //Ahora si se marca que ya se leyó
        println("Notificacion " + idNotificacion + " leida")
    }
}
