package com.example.fundfind.logic

import java.util.Date
import java.util.ArrayList

class FundFindSistema(cargarIniciales: Boolean = true) : java.io.Serializable {
    val usuarios: MutableList<UsuarioDonador> = ArrayList()
    val fundaciones: MutableList<Fundacion> = ArrayList()
    val solicitudes: MutableList<Solicitud> = ArrayList()
    val mensajes: MutableList<Mensaje> = ArrayList()
    val retroalimentaciones: MutableList<Retroalimentacion> = ArrayList()
    val donaciones: MutableList<Donacion> = arrayListOf()
    val notificaciones: MutableList<Notificacion> = arrayListOf()

    companion object {
        val CATEGORIAS_GENERALES = listOf(
            "Dinero",
            "Alimentos y despensas",
            "Ropa y abrigo",
            "Salud y medicamentos",
            "Educación y útiles",
            "Higiene personal",
            "Voluntariado",
            "Otro"
        )
    }

    init {
        if (cargarIniciales) cargarDatosIniciales()
        vincularModelos()
    }

    val solicitudesActivas: MutableList<Solicitud?>
        get() = solicitudes.filter { it.activa() }.toMutableList()

    fun getSolicitudesDeFundacion(f: Fundacion?): MutableList<Solicitud?> =
        solicitudes.filter { it.fundacion === f }.toMutableList()

    fun getMensajesParaFundacion(f: Fundacion?): MutableList<Mensaje?> =
        mensajes.filter { it.destinatarioFundacion === f }.toMutableList()

    fun getRetroalimentacionesParaFundacion(f: Fundacion?): MutableList<Retroalimentacion?> =
        retroalimentaciones.filter { it.fundacion === f }.toMutableList()

    fun loginDonador(correo: String?, contrasena: String?): UsuarioDonador? =
        usuarios.firstOrNull { it.login(correo, contrasena) }

    fun loginFundacion(correoContacto: String?, claveORfc: String?): Fundacion? {
        if (correoContacto.isNullOrBlank() || claveORfc.isNullOrBlank()) return null
        return fundaciones.firstOrNull { f ->
            val correoOk = f.correoContacto.equals(correoContacto.trim(), ignoreCase = true)
            val rfcOk = !f.rfc.isNullOrBlank() && f.rfc.equals(claveORfc.trim(), ignoreCase = true)
            val claveOk = f.claveAcceso.isNotBlank() && Credenciales.coincide(claveORfc, f.claveAcceso)
            correoOk && (rfcOk || claveOk) && f.estadoCuenta == "ACTIVA"
        }
    }

    fun registrarFundacion(
        nombre: String,
        rfc: String,
        correo: String,
        clave: String,
        telefono: String,
        direccion: String,
        categoria: String,
        mision: String
    ): Fundacion {
        require(nombre.isNotBlank() && direccion.isNotBlank() && categoria.isNotBlank()) {
            "Completa el nombre, dirección y categoría de la fundación."
        }
        require(rfc.trim().length in 12..13) { "Introduce un RFC válido (12 o 13 caracteres)." }
        require(correo.matches(Regex("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))) { "Introduce un correo válido." }
        require(clave.length >= 8) { "La contraseña debe tener al menos 8 caracteres." }
        require(telefono.count { it.isDigit() } >= 7) { "Introduce un teléfono válido." }
        require(fundaciones.none { it.correoContacto.equals(correo.trim(), true) }) { "Este correo ya está registrado." }
        require(usuarios.none { it.correo.equals(correo.trim(), true) }) { "Este correo ya está en uso por un donador." }

        val nueva = Fundacion(
            idFundacion = (fundaciones.maxOfOrNull { it.idFundacion } ?: 0) + 1,
            nombre = nombre.trim(),
            rfc = rfc.trim().uppercase(),
            direccion = direccion.trim(),
            categoria = categoria.trim(),
            correoContacto = correo.trim(),
            telefonoContacto = telefono.trim(),
            documentacionLegal = "Acta constitutiva y constancia RFC en revisión",
            isVerificado = true,
            estadoCuenta = "ACTIVA",
            fechaRegistro = Date()
        ).apply {
            this.claveAcceso = Credenciales.crear(clave)
            this.descripcionMision = mision.ifBlank { "Organización comprometida con el bienestar comunitario en ${direccion.trim()}." }
            this.latitud = 20.6736 + (fundaciones.size * 0.012)
            this.longitud = -103.344 - (fundaciones.size * 0.010)
            this.estiloBanner = (fundaciones.size % 3) + 1
            this.sistema = this@FundFindSistema
        }
        fundaciones.add(nueva)
        return nueva
    }

    fun enviarMensaje(usuario: UsuarioDonador?, fundacion: Fundacion?, texto: String?): Mensaje? {
        if (usuario == null || fundacion == null) return null
        require(!texto.isNullOrBlank()) { "Escribe un mensaje." }
        val m = requireNotNull(usuario.enviarMensaje(fundacion, texto))
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
        if (usuario == null || fundacion == null) return null
        val r = usuario.dejarRetroalimentacion(fundacion, null, calificacion, comentario)
        if (r != null) {
            r.idRetroalimentacion = (retroalimentaciones.maxOfOrNull { it.idRetroalimentacion } ?: 0) + 1
            r.registrar()
            retroalimentaciones.add(r)
        }
        return r
    }

    fun publicarSolicitud(
        fundacion: Fundacion?,
        titulo: String?,
        descripcion: String?,
        categoriaGeneral: String?,
        causaSocial: String? = categoriaGeneral
    ): Solicitud? {
        if (fundacion == null) return null
        require(!titulo.isNullOrBlank() && !descripcion.isNullOrBlank() && !categoriaGeneral.isNullOrBlank()) {
            "Completa los datos de la solicitud."
        }
        val nuevoId = (solicitudes.maxOfOrNull { it.idSolicitud } ?: 0) + 1
        val s = Solicitud(
            nuevoId,
            titulo,
            descripcion,
            categoriaGeneral,
            causaSocial ?: categoriaGeneral,
            "BORRADOR",
            null,
            fundacion
        )
        fundacion.publicarSolicitud(s)
        solicitudes.add(s)
        return s
    }

    fun vincularModelos() {
        usuarios.forEach { it.sistema = this }
        fundaciones.forEach { it.sistema = this }
    }

    fun registrarUsuario(nombre: String, correo: String, clave: String, ubicacion: String): UsuarioDonador {
        require(nombre.isNotBlank() && ubicacion.isNotBlank()) { "Completa tu nombre y ubicación." }
        require(correo.matches(Regex("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))) { "Introduce un correo válido." }
        require(clave.length >= 8) { "La contraseña debe tener al menos 8 caracteres." }
        require(usuarios.none { it.correo.equals(correo.trim(), true) }) { "Este correo ya está registrado." }
        require(fundaciones.none { it.correoContacto.equals(correo.trim(), true) }) { "Este correo pertenece a una fundación." }
        return UsuarioDonador(
            (usuarios.maxOfOrNull { it.idUsuario } ?: 0) + 1,
            nombre.trim(),
            correo.trim(),
            Credenciales.crear(clave),
            ubicacion.trim(),
            20.6736,
            -103.344,
            arrayListOf(),
            "ACTIVA",
            Date()
        ).also {
            it.registrar(); it.sistema = this; usuarios.add(it)
        }
    }

    fun registrarIntencion(
        usuario: UsuarioDonador,
        solicitud: Solicitud,
        tipo: String,
        cantidad: String,
        nombre: String,
        contacto: String,
        comprobante: Boolean,
        montoNumerico: Double = 0.0
    ): Donacion {
        require(usuarios.contains(usuario) && solicitudes.contains(solicitud)) { "La cuenta o solicitud no pertenece a esta sesión." }
        require(usuario.estadoCuenta == "ACTIVA" && solicitud.activa()) { "Esta solicitud ya no está disponible." }
        require(tipo.isNotBlank() && cantidad.isNotBlank() && nombre.isNotBlank()) { "Completa todos los campos." }
        if (tipo.equals("Dinero", ignoreCase = true)) {
            require(montoNumerico > 0.0) { "Ingresa un monto económico mayor a $0." }
        }
        require(contacto.contains('@') || contacto.count { it.isDigit() } >= 7) { "Introduce un teléfono o correo válido." }

        return Donacion(
            (donaciones.maxOfOrNull { it.idDonacion } ?: 0) + 1,
            Date(),
            montoNumerico,
            tipo,
            null,
            null,
            usuario,
            solicitud
        ).also {
            it.cantidadDescripcion = if (tipo.equals("Dinero", true)) "$${"%.2f".format(montoNumerico)} MXN" else cantidad.trim()
            it.nombreContacto = nombre.trim()
            it.contacto = contacto.trim()
            it.deseaComprobante = comprobante
            it.registrarIntencion()
            it.generarComprobante()
            donaciones.add(it)
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
        notificaciones.add(
            Notificacion(
                (notificaciones.maxOfOrNull { it.idNotificacion } ?: 0) + 1,
                texto, tipo, Date(), false, usuario, fundacion, recurso
            ).also { it.enviar() }
        )
    }

    fun cambiarDonacion(donacion: Donacion, estado: String) {
        require(donaciones.contains(donacion))
        require(donacion.estado != "CANCELADA") { "La intención ya fue cancelada." }
        when (estado) {
            "CONFIRMADA" -> donacion.confirmar()
            "CANCELADA" -> donacion.cancelar()
            else -> error("Estado no válido")
        }
        notificar("Intención ${estado.lowercase()}: ${donacion.solicitud?.titulo}", donacion.usuario, null, "DONACION", donacion.idDonacion.toString())
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
        if (aprobar) {
            solicitud.reportes = 0; solicitud.motivoReporte = ""; solicitud.publicar()
        } else solicitud.eliminar()
        notificar(if (aprobar) "Publicación aprobada" else "Publicación retirada", null, solicitud.fundacion, "SOLICITUD", solicitud.idSolicitud.toString())
    }

    private fun cargarDatosIniciales() {
        usuarios.add(
            UsuarioDonador(1, "Camila", "camijal06@gmail.com", "12345678", "Guadalajara, Jalisco",
                20.6736, -103.344, arrayListOf("Alimentos y despensas"), "ACTIVA", Date())
        )
        usuarios.add(
            UsuarioDonador(2, "Carlos", "carlosserna@gmail.com", "abcdef12", "Zapopan, Jalisco",
                20.7214, -103.3918, arrayListOf("Ropa y abrigo"), "ACTIVA", Date())
        )

        val f1 = Fundacion(
            1, "Fundación Sonrisas Jalisco", "FSR010101ABC",
            "Av. Vallarta 2450, Col. Arcos Vallarta, Guadalajara, JAL",
            "Alimentos y despensas", "contacto@sonrisas.org", "3330001122",
            "Acta Constitutiva #4821 · Donataria Autorizada SAT", true, "ACTIVA", Date()
        ).apply {
            latitud = 20.6744; longitud = -103.3872
            descripcionMision = "Brindamos comedores comunitarios, despensas semanales y acompañamiento educativo a más de 300 niñas y niños en Guadalajara."
            horarioAtencion = "Lunes a Sábado · 8:30 AM - 6:00 PM"
            sitioWeb = "www.fundacionsonrisas.org.mx"
            referenciasUbicacion = "A 2 cuadras de la Glorieta Minerva, portón verde."
            estiloBanner = 1
        }

        val f2 = Fundacion(
            2, "Fundación Patitas Seguras", "PAT020202DEF",
            "Av. Patria 1820, Col. Jacarandas, Zapopan, JAL",
            "Dinero", "info@patitas.org", "3331112233",
            "Registro Estatal OSC #9102 · Acta Notarial Vigente", true, "ACTIVA", Date()
        ).apply {
            latitud = 20.7089; longitud = -103.4125
            descripcionMision = "Rescate, rehabilitación veterinaria y adopción responsable de animales en situación de calle en la Zona Metropolitana."
            horarioAtencion = "Martes a Domingo · 10:00 AM - 5:00 PM"
            sitioWeb = "www.patitasseguras.org"
            referenciasUbicacion = "Frente al Parque Metropolitano, acceso sur."
            estiloBanner = 3
        }

        fundaciones.add(f1)
        fundaciones.add(f2)
    }
}