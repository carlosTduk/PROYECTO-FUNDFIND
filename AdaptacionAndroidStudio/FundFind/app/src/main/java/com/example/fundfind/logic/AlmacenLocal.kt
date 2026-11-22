package com.example.fundfind.logic

import android.content.Context
import android.util.AtomicFile
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.File

/** Conserva las cuentas existentes y guarda el recorrido invitado en su propia base SQLite. */
class AlmacenLocal(context: Context, private val baseInvitado: BaseDatosInvitado = BaseDatosInvitado(context)) {
    private val archivo = AtomicFile(File(context.filesDir, "fundfind-v1.bin"))
    var errorCarga: String? = null
        private set

    fun cargarInvitado(): FundFindSistema = baseInvitado.cargar() ?: DatosRecorrido.crear()

    fun guardarInvitado(sistema: FundFindSistema) = baseInvitado.guardar(sistema)

    fun cargar(): FundFindSistema {
        if (!archivo.baseFile.exists()) return FundFindSistema()
        return try {
            ObjectInputStream(archivo.openRead()).use { it.readObject() as FundFindSistema }.also { it.vincularModelos() }
        } catch (e: Exception) {
            errorCarga = "No se pudieron leer los datos guardados. Se conservará el archivo original: ${e.javaClass.simpleName}."
            FundFindSistema()
        }
    }

    fun guardar(sistema: FundFindSistema) {
        check(errorCarga == null) { requireNotNull(errorCarga) }
        val salida = archivo.startWrite()
        try {
            val objetos = ObjectOutputStream(salida)
            objetos.writeObject(sistema); objetos.flush()
            archivo.finishWrite(salida)
        } catch (e: Exception) {
            archivo.failWrite(salida)
            throw e
        }
    }
}
