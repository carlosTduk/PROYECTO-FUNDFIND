package com.example.fundfind.logic

import java.util.Date

class Donacion(// Getters y setters
    var idDonacion: Int,
    var fecha: Date?,
    var monto: Double,
    var tipoDonativo: String?,
    var estado: String?,
    var comprobante: String?,
    var usuario: UsuarioDonador,
    var solicitud: Solicitud?
) {
    // Métodos
    fun registrarIntencion() {
        this.estado = "INTENCIONREGISTRADA" //Se cambia el estado a esto
        this.fecha = Date()
        println("Intencion de donacion registrada por " + usuario.nombre) //usa el getter de UsuarioDonador
    }

    fun confirmar() {
        this.estado = "CONFIRMADA" //El estado se cambia a esto
        if (this.fecha == null) { //si no es null la fecha se cambia a la fecha actual
            this.fecha = Date()
        }
        println("Donacion confirmada.")
    }

    fun cancelar() {
        this.estado = "CANCELADA" //Se cambia a este estado
        println("Donacion cancelada.")
    }

    fun generarComprobante(): String? {
        this.comprobante = "COMP-" + idDonacion
        println("Comprobante generado: " + comprobante)
        return comprobante
    }
}

