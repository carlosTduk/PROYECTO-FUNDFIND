package com.example.fundfind.logic

import java.util.Date
import java.util.ArrayList

class FundFindSistema {
    // getters
    // Esta clase guarda todos los usuarios, fundaciones y las solicitudes
    val usuarios: MutableList<UsuarioDonador> = ArrayList<UsuarioDonador>()

    // se muestran las fundaciones cuando utilizamos el combobox
    val fundaciones: MutableList<Fundacion> = ArrayList<Fundacion>()
    val solicitudes: MutableList<Solicitud> = ArrayList<Solicitud>()
    private val mensajes: MutableList<Mensaje> = ArrayList<Mensaje>()
    private val retroalimentaciones: MutableList<Retroalimentacion> = ArrayList<Retroalimentacion>()

    init { // Constructor
        cargarDatosIniciales()
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
        val m = usuario.enviarMensaje(fundacion, texto)
        mensajes.add(m!!)
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

        val nuevoId = solicitudes.size + 1

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
