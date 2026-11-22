package com.example.fundfind.logic

import java.util.Date

class Mensaje : java.io.Serializable {
    // Getters y setters
    var idMensaje: Int = 0
    @JvmField
    var contenido: String? = null
    @JvmField
    var fechaEnvio: Date? = null
    @JvmField
    var remitenteDonador: UsuarioDonador? = null
    @JvmField
    var remitenteFundacion: Fundacion? = null
    @JvmField
    var destinatarioFundacion: Fundacion? = null
    @JvmField
    var destinatarioDonador: UsuarioDonador? = null
    var isLeido: Boolean

    constructor() { //Constructor vacio solo pone que no se ha leido
        this.isLeido = false
    }

    constructor(
        idMensaje: Int,
        contenido: String?,
        fechaEnvio: Date?,
        remitenteDonador: UsuarioDonador?,
        remitenteFundacion: Fundacion?,
        destinatarioFundacion: Fundacion?,
        destinatarioDonador: UsuarioDonador?,
        leido: Boolean
    ) {
        this.idMensaje = idMensaje
        this.contenido = contenido
        this.fechaEnvio = fechaEnvio
        this.remitenteDonador = remitenteDonador
        this.remitenteFundacion = remitenteFundacion
        this.destinatarioFundacion = destinatarioFundacion
        this.destinatarioDonador = destinatarioDonador
        this.isLeido = leido
    }

    // Métodos 
    fun enviar() {
        this.fechaEnvio = Date()
        this.isLeido = false
        println("Mensaje enviado: " + contenido)
    }

    fun marcarComoLeido() {
        this.isLeido = true
        println("Mensaje " + idMensaje + " marcado como leido.")
    }
}

