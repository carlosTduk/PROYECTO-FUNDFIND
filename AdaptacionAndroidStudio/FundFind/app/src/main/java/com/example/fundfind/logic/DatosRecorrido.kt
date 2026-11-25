package com.example.fundfind.logic

import java.time.LocalDate
import java.util.Date

object DatosRecorrido {
    fun crear(): FundFindSistema = FundFindSistema().apply {
        val invitado = UsuarioDonador(100, "Invitado", "invitado@fundfind.local", "", "Soacha, Cundinamarca",
            4.579, -74.216, arrayListOf("Comida"), "ACTIVA", Date())
        usuarios.add(invitado)
        val nombres = listOf("Comunidad que Alimenta", "Hogar Esperanza", "Fundación Abrigo Vivo", "Banco de Alimentos Sur",
            "Vida Sana", "Manos Unidas por la Niñez", "Fundación Abrigo", "Fundación Semillas")
        nombres.forEachIndexed { i, nombre ->
            fundaciones.add(Fundacion(100 + i, nombre, "DEMO${100 + i}", if (i == 1) "Bogotá, Colombia" else "Soacha, Cundinamarca",
                listOf("Seguridad alimentaria", "Infancia", "Adultos mayores", "Nutrición infantil", "Salud", "Infancia", "Ropa", "Educación")[i],
                "contacto${i + 1}@fundfind.local", "3001234567", "Documentación de demostración local", i !in 5..6, "ACTIVA", Date()).also {
                it.latitud = 4.579 + i * .015; it.longitud = -74.216 + i * .004
            })
        }
        vincularModelos()
        val titulos = listOf("Despensas para 120 familias", "Útiles escolares para 60 niños", "Ropa de abrigo para adultos mayores",
            "Alimentos no perecederos para comedores", "Kits de higiene", "Donación de medicamentos", "Rifa para recaudar fondos", "Útiles escolares para 80 niños")
        titulos.forEachIndexed { i, titulo ->
            val s = requireNotNull(publicarSolicitud(fundaciones.first { it.idFundacion == 100 + i }, titulo,
                if (i == 0) "Reunimos alimentos no perecederos para entregar despensas mensuales a familias en situación vulnerable."
                else "Tu ayuda permite acompañar a las personas que más lo necesitan. Contacta con la fundación para coordinar la entrega.",
                listOf("Comida", "Útiles escolares", "Ropa", "Comida", "Higiene", "Medicamentos", "Voluntariado", "Útiles escolares")[i],
                fundaciones.first { it.idFundacion == 100 + i }.categoria))
            s.cantidad = listOf("120 despensas", "60 kits", "40 prendas", "100 kg", "30 kits", "20 unidades", "10 voluntarios", "80 kits")[i]
            s.fechaLimite = LocalDate.now().plusDays((9 + i * 4).toLong())
            s.ubicacion = s.fundacion?.direccion.orEmpty(); s.urgente = i < 2
            if (i in 5..6) { s.reportes = if (i == 5) 2 else 1; s.motivoReporte = if (i == 5) "Sospechosa" else "Reportada" }
        }
        invitado.favoritos.add(solicitudes[2].idSolicitud)
        val d = registrarIntencion(invitado, solicitudes[0], "Comida", "10 despensas", "Invitado", "invitado@fundfind.local", true)
        d.confirmar()
        registrarIntencion(invitado, solicitudes[1], "Útiles escolares", "5 cuadernos", "Invitado", "invitado@fundfind.local", true)
        enviarMensaje(invitado, fundaciones.first { it.idFundacion == 100 }, "Hola, ¿en qué horario puedo entregar mi ayuda?")
    }
}
