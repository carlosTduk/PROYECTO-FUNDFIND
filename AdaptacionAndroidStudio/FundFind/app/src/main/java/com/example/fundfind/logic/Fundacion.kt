package com.example.fundfind.logic

import java.util.Date

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
    var latitud: Double = 20.6736
    var longitud: Double = -103.344
    var fechaVerificacion: Date? = null
    var responsableVerificacion: String? = null

    // Nuevos campos para mayor legitimidad y perfil detallado
    var descripcionMision: String = ""
    var horarioAtencion: String = "Lunes a Viernes · 9:00 AM - 5:00 PM"
    var sitioWeb: String = ""
    var referenciasUbicacion: String = ""
    var claveAcceso: String = "" // Permite iniciar sesión con RFC o contraseña creada al registrarse
    var estiloBanner: Int = 1 // 1 = Sede comunitaria, 2 = Centro de acopio, 3 = Refugio/Salud

    fun registrar() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = Date()
        }
        this.estadoCuenta = "ACTIVA"
        this.isVerificado = false
        println("Fundacion $nombre registrada")
    }

    fun editarPerfil(datosActualizados: Fundacion?) {
        if (datosActualizados == null) return
        if (datosActualizados.nombre != null) this.nombre = datosActualizados.nombre
        if (datosActualizados.rfc != null) this.rfc = datosActualizados.rfc
        if (datosActualizados.direccion != null) this.direccion = datosActualizados.direccion
        if (datosActualizados.categoria != null) this.categoria = datosActualizados.categoria
        if (datosActualizados.correoContacto != null) this.correoContacto = datosActualizados.correoContacto
        if (datosActualizados.telefonoContacto != null) this.telefonoContacto = datosActualizados.telefonoContacto
        if (datosActualizados.documentacionLegal != null) this.documentacionLegal = datosActualizados.documentacionLegal
        if (datosActualizados.descripcionMision.isNotBlank()) this.descripcionMision = datosActualizados.descripcionMision
        if (datosActualizados.horarioAtencion.isNotBlank()) this.horarioAtencion = datosActualizados.horarioAtencion
        if (datosActualizados.sitioWeb.isNotBlank()) this.sitioWeb = datosActualizados.sitioWeb
        if (datosActualizados.referenciasUbicacion.isNotBlank()) this.referenciasUbicacion = datosActualizados.referenciasUbicacion
        this.latitud = datosActualizados.latitud
        this.longitud = datosActualizados.longitud
        println("Fundacion $nombre actualizada")
    }

    fun gestionarSolicitudes(): MutableList<Solicitud?> {
        return sistema?.getSolicitudesDeFundacion(this) ?: mutableListOf()
    }

    fun editarSolicitud(solicitud: Solicitud?) {
        if (solicitud == null) return
        require(solicitud.fundacion === this) { "La solicitud pertenece a otra fundación." }
        solicitud.actualizarDatos()
    }

    fun eliminarSolicitud(solicitud: Solicitud?) {
        if (solicitud == null) return
        require(solicitud.fundacion === this) { "La solicitud pertenece a otra fundación." }
        solicitud.eliminar()
    }

    fun publicarSolicitud(nueva: Solicitud?) {
        if (nueva == null) return
        nueva.fundacion = this
        nueva.fechaPublicacion = Date()
        nueva.estado = "PUBLICADA"
    }

    fun verNotificaciones(): MutableList<Notificacion?> {
        return sistema?.notificaciones?.filter { it.receptorFundacion === this }?.toMutableList() ?: mutableListOf()
    }

    fun responderMensaje(mensaje: Mensaje?, respuesta: String?): Mensaje? {
        if (mensaje == null) return null
        val respuestaMensaje = Mensaje()
        respuestaMensaje.contenido = respuesta
        respuestaMensaje.fechaEnvio = Date()
        respuestaMensaje.remitenteFundacion = this
        respuestaMensaje.destinatarioDonador = mensaje.remitenteDonador
        respuestaMensaje.isLeido = false
        return respuestaMensaje
    }
}