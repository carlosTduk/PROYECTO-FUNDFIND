package com.example.fundfind.logic

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/** Guarda el recorrido completo sin cambiar los modelos ni sus relaciones existentes. */
class BaseDatosInvitado(context: Context, nombre: String = "fundfind-invitado.db") :
    SQLiteOpenHelper(context.applicationContext, nombre, null, 1, {
        // El manejador predeterminado elimina una base corrupta; conservamos el original.
        throw SQLiteException("No se pudo leer la base del invitado. Sus datos se conservarán.")
    }) {
    private var lecturaFallida = false

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE estado_invitado (id INTEGER PRIMARY KEY CHECK(id = 1), datos BLOB NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        throw SQLiteException("Se requiere una migración de los datos del invitado ($oldVersion a $newVersion).")
    }

    /** Devuelve null únicamente cuando todavía no se ha guardado ningún recorrido. */
    fun cargar(): FundFindSistema? {
        try {
            return readableDatabase.rawQuery("SELECT datos FROM estado_invitado WHERE id = 1", null).use { cursor ->
                if (!cursor.moveToFirst()) null
                else ObjectInputStream(ByteArrayInputStream(cursor.getBlob(0))).use {
                    (it.readObject() as FundFindSistema).also { sistema -> sistema.vincularModelos() }
                }
            }
        } catch (e: Exception) {
            lecturaFallida = true
            throw IllegalStateException("No se pudieron recuperar los datos del invitado. Se conservarán sin sobrescribirlos.", e)
        }
    }

    /** Una única escritura SQLite reemplaza el estado de forma atómica. */
    fun guardar(sistema: FundFindSistema) {
        check(!lecturaFallida) { "No se pueden sobrescribir los datos del invitado mientras exista un error de lectura." }
        val bytes = ByteArrayOutputStream().use { buffer ->
            ObjectOutputStream(buffer).use { it.writeObject(sistema) }
            buffer.toByteArray()
        }
        val valores = ContentValues().apply { put("id", 1); put("datos", bytes) }
        check(writableDatabase.insertWithOnConflict("estado_invitado", null, valores, SQLiteDatabase.CONFLICT_REPLACE) != -1L) {
            "No se guardaron los cambios del invitado. Revisa el espacio disponible y vuelve a intentarlo."
        }
    }
}
