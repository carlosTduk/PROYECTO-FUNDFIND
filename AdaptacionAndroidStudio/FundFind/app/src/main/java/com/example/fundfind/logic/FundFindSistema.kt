package com.example.fundfind.logic

import java.util.Date
import java.util.ArrayList

class FundFindSistema(cargarIniciales: Boolean = true) : java.io.Serializable {
    // getters
    // Esta clase guarda todos los usuarios, fundaciones y las solicitudes
    val usuarios: MutableList<UsuarioDonador> = ArrayList<UsuarioDonador>()

    // se muestran las fundaciones cuando utilizamos el combobox
    val fundaciones: MutableList<Fundacion> = ArrayList<Fundacion>()
    val solicitudes: MutableList<Solicitud> = ArrayList<Solicitud>()
    val mensajes: MutableList<Mensaje> = ArrayList<Mensaje>()
    val retroalimentaciones: MutableList<Retroalimentacion> = ArrayList<Retroalimentacion>()

    init { // Constructor
        if (cargarIniciales) cargarDatosIniciales()
        vincularModelos()
    }

    val solicitudesActivas: MutableList<Solicitud?>
        get() {
            val activas: MutableList<Solicitud?> = ArrayList<Solicitud?>()
            for (i in solicitudes.indices) {
                val s = solicitudes.get(i)
                if (s.activa()) {
                    activas.add(s)
                }
            }
            return activas
        }

    fun getSolicitudesDeFundacion(f: Fundacion?): MutableList<Solicitud?> {
        val res: MutableList<Solicitud?> = ArrayList<Solicitud?>()
        for (i in solicitudes.indices) {
            val s = solicitudes.get(i)
            if (s.fundacion === f) {
                res.add(s)
            }
        }
        return res
    }

    fun getMensajesParaFundacion(f: Fundacion?): MutableList<Mensaje?> {
        val res: MutableList<Mensaje?> = ArrayList<Mensaje?>()
        for (i in mensajes.indices) {
            val m = mensajes.get(i)
            if (m.destinatarioFundacion === f) {
                res.add(m)
            }
        }
        return res
    }

    fun getRetroalimentacionesParaFundacion(f: Fundacion?): MutableList<Retroalimentacion?> {
        val res: MutableList<Retroalimentacion?> = ArrayList<Retroalimentacion?>()
        for (i in retroalimentaciones.indices) {
            val r: Retroalimentacion = retroalimentaciones.get(i)
            // SE CORRIGIÓ: getFundacion() -> fundacion
            if (r.fundacion === f) {
                res.add(r)
            }
        }
        return res
    }

    // los logins
    fun loginDonador(correo: String?, contrasena: String?): UsuarioDonador? {
        for (i in usuarios.indices) {
            val u = usuarios.get(i)
            if (u.login(correo, contrasena)) {
                return u
            }
        }
        return null
    }

    fun loginFundacion(correoContacto: String?, rfc: String?): Fundacion? {
        for (i in fundaciones.indices) {
            val f = fundaciones.get(i)
            val correoOk = f.correoContacto == correoContacto
            val rfcOk = f.rfc == rfc
            val activa = "ACTIVA" == f.estadoCuenta
            if (correoOk && rfcOk && activa) {
                return f
            }
        }
        return null
    }

    // acciones del donador
    fun enviarMensaje(
        usuario: UsuarioDonador?,
        fundacion: Fundacion?,
        texto: String?
    ): Mensaje? { // cambiado tipo de retorno si fuera necesario, se mantiene Mensaje
        if (usuario == null || fundacion == null) {
            return null
        }
        require(!texto.isNullOrBlank()) { "Escribe un mensaje." }
        val m = usuario.enviarMensaje(fundacion, texto)
        requireNotNull(m)
        m.idMensaje = (mensajes.maxOfOrNull { it.idMensaje } ?: 0) + 1
        m.enviar()
        mensajes.add(m)
        notificar("Nuevo mensaje de ${usuario.nombre}", null, fundacion, "MENSAJE", m.idMensaje.toString())
        return m
    }

    fun dejarRetroalimentacion(
        usuario: UsuarioDonador?,
        fundacion: Fundacion?,
        calificacion: Int,
        comentario: String?
    ): Retroalimentacion? {
        if (usuario == null || fundacion == null) {
            return null
        }
        // SE CORRIGIÓ: Manejo correcto de asignación no nula (!!) tras validar que no sea null
        val r: Retroalimentacion? = usuario.dejarRetroalimentacion(fundacion, null, calificacion, comentario)
        if (r != null) {
            r.idRetroalimentacion = (retroalimentaciones.maxOfOrNull { it.idRetroalimentacion } ?: 0) + 1
            r.registrar()
            retroalimentaciones.add(r)
        }
        return r
    }

    // acciones de la fundacion
    fun publicarSolicitud(
        fundacion: Fundacion?,
        titulo: String?,
        descripcion: String?,
        tipoDonacion: String?,
        causaSocial: String?
    ): Solicitud? {
        if (fundacion == null) {
            return null
        }

        require(!titulo.isNullOrBlank() && !descripcion.isNullOrBlank() && !tipoDonacion.isNullOrBlank() && !causaSocial.isNullOrBlank()) { "Completa los datos de la solicitud." }
        val nuevoId = (solicitudes.maxOfOrNull { it.idSolicitud } ?: 0) + 1

        val s = Solicitud(
            nuevoId,
            titulo,
            descripcion,
            tipoDonacion,
            causaSocial,
            "BORRADOR",
            null,
            fundacion
        )

        fundacion.publicarSolicitud(s)
        solicitudes.add(s)

        return s
    }

    val donaciones: MutableList<Donacion> = arrayListOf()
    val notificaciones: MutableList<Notificacion> = arrayListOf()

    fun vincularModelos() {
        usuarios.forEach { it.sistema = this }
        fundaciones.forEach { it.sistema = this }
    }

    fun registrarUsuario(nombre: String, correo: String, clave: String, ubicacion: String): UsuarioDonador {
        require(nombre.isNotBlank() && ubicacion.isNotBlank()) { "Completa tu nombre y ubicación." }
        require(correo.matches(Regex("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))) { "Introduce un correo válido." }
        require(clave.length >= 8) { "La contraseña debe tener al menos 8 caracteres." }
        require(usuarios.none { it.correo.equals(correo.trim(), true) }) { "Este correo ya está registrado." }
        return UsuarioDonador((usuarios.maxOfOrNull { it.idUsuario } ?: 0) + 1, nombre.trim(), correo.trim(),
            Credenciales.crear(clave), ubicacion.trim(), 4.579, -74.216, arrayListOf(), "ACTIVA", Date()).also {
            it.registrar(); it.sistema = this; usuarios.add(it)
        }
    }

    fun registrarIntencion(usuario: UsuarioDonador, solicitud: Solicitud, tipo: String, cantidad: String,
                           nombre: String, contacto: String, comprobante: Boolean): Donacion {
        require(usuarios.contains(usuario) && solicitudes.contains(solicitud)) { "La cuenta o solicitud no pertenece a esta sesión." }
        require(usuario.estadoCuenta == "ACTIVA" && solicitud.activa()) { "Esta solicitud ya no está disponible." }
        require(tipo.isNotBlank() && cantidad.isNotBlank() && nombre.isNotBlank()) { "Completa todos los campos." }
        require(contacto.contains('@') || contacto.count { it.isDigit() } >= 7) { "Introduce un teléfono o correo válido." }
        return Donacion((donaciones.maxOfOrNull { it.idDonacion } ?: 0) + 1, Date(), 0.0, tipo, null, null, usuario, solicitud).also {
            it.cantidadDescripcion = cantidad.trim(); it.nombreContacto = nombre.trim(); it.contacto = contacto.trim()
            it.deseaComprobante = comprobante; it.registrarIntencion(); it.generarComprobante(); donaciones.add(it)
            notificar("Registraste tu intención: ${solicitud.titulo}", usuario, null, "DONACION", it.idDonacion.toString())
            notificar("${usuario.nombre} quiere ayudar: ${solicitud.titulo}", null, solicitud.fundacion, "DONACION", it.idDonacion.toString())
        }
    }

    fun responder(fundacion: Fundacion, mensaje: Mensaje, texto: String): Mensaje {
        require(mensaje.destinatarioFundacion === fundacion && texto.isNotBlank()) { "No se puede responder este mensaje." }
        return requireNotNull(fundacion.responderMensaje(mensaje, texto.trim())).also {
            it.idMensaje = (mensajes.maxOfOrNull { m -> m.idMensaje } ?: 0) + 1
            it.enviar(); mensajes.add(it); mensaje.marcarComoLeido()
            notificar("Respuesta de ${fundacion.nombre}", it.destinatarioDonador, null, "MENSAJE", it.idMensaje.toString())
        }
    }

    fun notificar(texto: String, usuario: UsuarioDonador?, fundacion: Fundacion?, tipo: String, recurso: String) {
        notificaciones.add(Notificacion((notificaciones.maxOfOrNull { it.idNotificacion } ?: 0) + 1,
            texto, tipo, Date(), false, usuario, fundacion, recurso).also { it.enviar() })
    }

    fun cambiarDonacion(donacion: Donacion, estado: String) {
        require(donaciones.contains(donacion))
        require(donacion.estado != "CANCELADA") { "La intención ya fue cancelada." }
        when (estado) {
            "CONFIRMADA" -> donacion.confirmar()
            "CANCELADA" -> donacion.cancelar()
            else -> error("Estado no válido")
        }
        notificar("Intención ${estado.lowercase()}: ${donacion.solicitud?.titulo}", donacion.usuario,
            null, "DONACION", donacion.idDonacion.toString())
    }

    fun verificar(fundacion: Fundacion, aprobada: Boolean, responsable: String) {
        require(fundaciones.contains(fundacion) && responsable.isNotBlank())
        fundacion.isVerificado = aprobada
        fundacion.estadoCuenta = if (aprobada) "ACTIVA" else "RECHAZADA"
        fundacion.fechaVerificacion = Date(); fundacion.responsableVerificacion = responsable
        notificar(if (aprobada) "Fundación verificada" else "Verificación rechazada", null, fundacion, "PERFIL", "")
    }

    fun moderar(solicitud: Solicitud, aprobar: Boolean) {
        require(solicitudes.contains(solicitud))
        if (aprobar) { solicitud.reportes = 0; solicitud.motivoReporte = ""; solicitud.publicar() }
        else solicitud.eliminar()
        notificar(if (aprobar) "Publicación aprobada" else "Publicación retirada", null, solicitud.fundacion, "SOLICITUD", solicitud.idSolicitud.toString())
    }

    private fun cargarDatosIniciales() {
        val intereses1: MutableList<String?> = ArrayList<String?>()
        intereses1.add("Viveres")

        val u1 = UsuarioDonador(
            1,
            "Camila",
            "camijal06@gmail.com",
            "12345678",
            "Guadalajara, Jalisco",
            20.67,
            -103.35,
            intereses1,
            "ACTIVA",
            Date()
        )
        usuarios.add(u1)

        val intereses2: MutableList<String?> = ArrayList<String?>()
        intereses2.add("Ropa")

        val u2 = UsuarioDonador(
            2,
            "Carlos",
            "carlosserna@gmail.com",
            "abcdef",
            "Zapopan, JAL",
            20.70,
            -103.40,
            intereses2,
            "ACTIVA",
            Date()
        )
        usuarios.add(u2)

        val u3 = UsuarioDonador(
            3,
            "utest",
            "utest",
            "1234",
            "ddd, ddd",
            20.70,
            -103.40,
            intereses2,
            "ACTIVA",
            Date()
        )
        usuarios.add(u3)

        val f1 = Fundacion(
            1,
            "Fundacion Sonrisas",
            "FSR010101ABC",
            "Guadalajara, JAL",
            "Niños",
            "contacto@sonrisas.org",
            "3330000000",
            "Acta Constitutiva",
            true,
            "ACTIVA",
            Date()
        )

        val f2 = Fundacion(
            2,
            "Fundacion Patitas",
            "PAT020202DEF",
            "Zapopan, JAL",
            "Animales",
            "info@patitas.org",
            "3331111111",
            "Documento Legal",
            true,
            "ACTIVA",
            Date()
        )

        val f3 = Fundacion(
            3,
            "testf",
            "12345",
            "aaaa",
            "aaa",
            "testf",
            "aaaa",
            "aaa",
            true,
            "ACTIVA",
            Date()
        )

        fundaciones.add(f1)
        fundaciones.add(f2)
        fundaciones.add(f3)
    }
}
