package com.example.fundfind.logic

import java.util.Date
import java.util.ArrayList

class Fundacion(
    var idFundacion: Int,
    var nombre: String?,
    var rfc: String?,
    var direccion: String?,
    var categoria: String?,
    var correoContacto: String?,
    var telefonoContacto: String?,
    var documentacionLegal: String?,
    var isVerificado: Boolean,
    var estadoCuenta: String?,
    var fechaRegistro: Date?
) : java.io.Serializable {
    internal var sistema: FundFindSistema? = null
    var latitud: Double = 4.579
    var longitud: Double = -74.216
    var fechaVerificacion: Date? = null
    var responsableVerificacion: String? = null
    // Métodos
    fun registrar() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = Date()
        }

        this.estadoCuenta = "ACTIVA"
        this.isVerificado = false
        println("Fundacion " + nombre + " registrada")
    }

    // Se cambió 'Fundacion?' por 'Fundacion?' para quitar el acento
    fun editarPerfil(datosActualizados: Fundacion?) {
        if (datosActualizados == null) return


        if (datosActualizados.nombre != null) {
            this.nombre = datosActualizados.nombre
        }
        if (datosActualizados.rfc != null) {
            this.rfc = datosActualizados.rfc
        }
        if (datosActualizados.direccion != null) {
            this.direccion = datosActualizados.direccion
        }
        if (datosActualizados.categoria != null) {
            this.categoria = datosActualizados.categoria
        }
        if (datosActualizados.correoContacto != null) {
            this.correoContacto = datosActualizados.correoContacto
        }
        if (datosActualizados.telefonoContacto != null) {
            this.telefonoContacto = datosActualizados.telefonoContacto
        }
        if (datosActualizados.documentacionLegal != null) {
            this.documentacionLegal = datosActualizados.documentacionLegal
        }

        println("Fundacion " + nombre + " actualizada")
    }

    fun gestionarSolicitudes(): MutableList<Solicitud?> {
        println("Gestion de Solicitudes de la fundacion " + nombre)
        return sistema?.getSolicitudesDeFundacion(this) ?: mutableListOf()
    }

    fun editarSolicitud(solicitud: Solicitud?) {
        if (solicitud == null) return
        require(solicitud.fundacion === this) { "La solicitud pertenece a otra fundación." }
        solicitud.actualizarDatos()
        println("Editando solicitud " + solicitud.titulo + " de la fundacion " + nombre)
    }

    fun eliminarSolicitud(solicitud: Solicitud?) {
        if (solicitud == null) return
        require(solicitud.fundacion === this) { "La solicitud pertenece a otra fundación." }
        solicitud.eliminar()
        println("Eliminando solicitud " + solicitud.titulo + " de la fundacion " + nombre)
    }

    fun publicarSolicitud(nueva: Solicitud?) {
        if (nueva == null) return
        nueva.fundacion = this
        nueva.fechaPublicacion = Date()
        nueva.estado = "PUBLICADA"
        println("Solicitud '" + nueva.titulo + "' publicada por la fundacion " + nombre)
    }

    fun verNotificaciones(): MutableList<Notificacion?> {
        println("Notificaciones de fundacion " + nombre)
        return sistema?.notificaciones?.filter { it.receptorFundacion === this }?.toMutableList() ?: mutableListOf()
    }

    fun responderMensaje(mensaje: Mensaje?, respuesta: String?): Mensaje? {
        if (mensaje == null) return null

        // Nota: Si 'Mensaje()' te marca error, asegúrate de pasarle los parámetros
        // requeridos por su constructor entre los paréntesis o inicializarlo vacío.
        val respuestaMensaje = Mensaje()
        respuestaMensaje.contenido = respuesta
        respuestaMensaje.fechaEnvio = Date()
        respuestaMensaje.remitenteFundacion = this
        respuestaMensaje.destinatarioDonador = mensaje.remitenteDonador
        respuestaMensaje.isLeido = false

        println("Fundacion " + nombre + " respondio mensaje.")
        return respuestaMensaje
    }
}
