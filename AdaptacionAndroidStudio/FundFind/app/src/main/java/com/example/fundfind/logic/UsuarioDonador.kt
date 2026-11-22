package com.example.fundfind.logic

import java.util.Date
import java.util.ArrayList

class UsuarioDonador(
    var idUsuario: Int,
    @JvmField var nombre: String?,
    var correo: String?,
    var contrasena: String?,
    var ubicacionTexto: String?,
    var latitud: Double,
    var longitud: Double,
    var intereses: MutableList<String?>?,
    var estadoCuenta: String?,
    var fechaRegistro: Date?
) : java.io.Serializable {
    internal var sistema: FundFindSistema? = null
    val favoritos: MutableSet<Int> = linkedSetOf()
    // Métodos
    fun registrar() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = Date()
        }
        this.estadoCuenta = "ACTIVA"
        println("Usuario " + nombre + " registrado")
    }

    fun login(
        correo: String?,
        contrasena: String?
    ): Boolean {
        val datosCorrectos =
            this.correo != null && this.correo == correo && this.contrasena != null && Credenciales.coincide(contrasena.orEmpty(), this.contrasena.orEmpty())

        val cuentaActiva = "ACTIVA" == this.estadoCuenta

        return datosCorrectos && cuentaActiva
    }

    fun editarPerfil(datosActualizados: UsuarioDonador?) {
        if (datosActualizados == null) return


        if (datosActualizados.nombre != null) {
            this.nombre = datosActualizados.nombre
        }
        if (datosActualizados.correo != null) {
            this.correo = datosActualizados.correo
        }
        if (datosActualizados.contrasena != null) {
            this.contrasena = datosActualizados.contrasena
        }
        if (datosActualizados.ubicacionTexto != null) {
            this.ubicacionTexto = datosActualizados.ubicacionTexto
        }
        if (datosActualizados.intereses != null) {
            this.intereses = datosActualizados.intereses
        }

        this.latitud = datosActualizados.latitud
        this.longitud = datosActualizados.longitud

        println("Perfil de " + nombre + " actualizado.")
    }

    fun eliminarCuenta() {
        this.estadoCuenta = "ELIMINADA"
        println("Usuario " + nombre + " marcado como eliminado.")
    }

    fun verListadoFundaciones(filtros: String?): MutableList<Fundacion?> {
        println("Fundaciones con los filtros: " + filtros)
        return sistema?.fundaciones?.filter { it.estadoCuenta == "ACTIVA" &&
            (filtros.isNullOrBlank() || "${it.nombre} ${it.categoria} ${it.direccion}".contains(filtros, true))
        }?.toMutableList() ?: mutableListOf()
    }

    fun verListadoSolicitudes(): MutableList<Solicitud?> {
        println("Solicitudes para el usuario " + nombre)
        return sistema?.solicitudesActivas ?: mutableListOf()
    }

    fun verNotificaciones(): MutableList<Notificacion?> {
        println("Notificaciones del usuario " + nombre)
        return sistema?.notificaciones?.filter { it.receptorUsuario === this }?.toMutableList() ?: mutableListOf()
    }

    fun enviarMensaje(fundacion: Fundacion?, texto: String?): Mensaje? {
        if (fundacion == null) return null

        val mensaje = Mensaje()
        mensaje.contenido = texto
        mensaje.fechaEnvio = Date()
        mensaje.remitenteDonador = this
        mensaje.destinatarioFundacion = fundacion
        mensaje.isLeido = false // Se corrigió a asignación directa

        println("Mensaje enviado a fundacion " + fundacion.nombre)
        return mensaje
    }

    fun dejarRetroalimentacion(
        fundacion: Fundacion?,
        donacion: Donacion?,
        calificacion: Int,
        comentario: String?
    ): Retroalimentacion? {
        if (fundacion == null) return null

        require(calificacion in 1..5) { "La calificación debe estar entre 1 y 5." }
        val r = Retroalimentacion()
        r.fundacion = fundacion // Se corrigió el set viejo de Java por asignación nativa de Kotlin
        r.usuario = this
        r.calificacion = calificacion
        r.comentario = comentario
        r.fecha = Date()

        println("Retroalimentacion registrada para fundacion " + fundacion.nombre)
        return r
    }
}
