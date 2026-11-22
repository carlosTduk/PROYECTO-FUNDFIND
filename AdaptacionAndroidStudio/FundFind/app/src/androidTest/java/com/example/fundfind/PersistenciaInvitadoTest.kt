package com.example.fundfind

import android.content.Context
import android.content.ContextWrapper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.fundfind.layout.EstadoApp
import com.example.fundfind.logic.*
import org.junit.Assert.*
import org.junit.Assume.assumeNotNull
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class PersistenciaInvitadoTest {
    private val contexto get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun contextoAislado(nombre: String): Context = object : ContextWrapper(contexto) {
        override fun getFilesDir() = File(baseContext.cacheDir, nombre).apply { mkdirs() }
    }

    @Test fun conservaCambiosAlReabrirSinMezclarCuentas() {
        val nombre = "prueba-invitado-${UUID.randomUUID()}.db"
        val ctx = contextoAislado(nombre)
        try {
            BaseDatosInvitado(ctx, nombre).use { base ->
                val almacen = AlmacenLocal(ctx, base)
                val cuentas = almacen.cargar()
                cuentas.usuarios.first().nombre = "Cuenta independiente"
                almacen.guardar(cuentas)
                val estado = EstadoApp(almacen)
                estado.entrarInvitado()
                assertTrue(estado.invitado)
                assertTrue(estado.cambiar {
                    val u = requireNotNull(estado.usuario)
                    val f = requireNotNull(estado.fundacion)
                    u.nombre = "Invitado persistente"
                    u.favoritos.add(estado.sistema.solicitudes.first().idSolicitud)
                    val d = estado.sistema.registrarIntencion(u, estado.sistema.solicitudes.first(), "Comida", "7 cajas", u.nombre.orEmpty(), "prueba@fundfind.local", true)
                    estado.sistema.cambiarDonacion(d, "CANCELADA")
                    estado.sistema.enviarMensaje(u, f, "Mensaje persistente")
                    estado.sistema.dejarRetroalimentacion(u, f, 4, "Reseña persistente")
                    estado.sistema.notificaciones.first().marcarComoLeida()
                    estado.sistema.solicitudes[1].marcarCompletada()
                    estado.sistema.verificar(estado.sistema.fundaciones.first { it.idFundacion == 105 }, true, "Prueba")
                })
                estado.salir()
                estado.entrarInvitado()
                assertEquals("Invitado persistente", estado.usuario?.nombre)
            }
            BaseDatosInvitado(ctx, nombre).use { base ->
                val almacen = AlmacenLocal(ctx, base)
                val estado = EstadoApp(almacen)
                estado.entrarInvitado()
                val u = requireNotNull(estado.usuario)
                val f = requireNotNull(estado.fundacion)
                assertEquals("Invitado persistente", u.nombre)
                assertTrue(u.favoritos.contains(estado.sistema.solicitudes.first().idSolicitud))
                val d = estado.sistema.donaciones.single { it.cantidadDescripcion == "7 cajas" }
                assertEquals("CANCELADA", d.estado)
                assertSame(u, d.usuario)
                assertSame(estado.sistema.solicitudes.first(), d.solicitud)
                assertTrue(estado.sistema.getMensajesParaFundacion(f).any { it?.contenido == "Mensaje persistente" })
                assertTrue(estado.sistema.getRetroalimentacionesParaFundacion(f).any { it?.comentario == "Reseña persistente" })
                assertTrue(estado.sistema.notificaciones.first().isLeida)
                assertEquals("COMPLETADA", estado.sistema.solicitudes[1].estado)
                assertTrue(estado.sistema.fundaciones.first { it.idFundacion == 105 }.isVerificado)
                assertEquals("Cuenta independiente", almacen.cargar().usuarios.first().nombre)
                assertFalse(almacen.cargar().usuarios.any { it.idUsuario == 100 })
            }
        } finally {
            contexto.deleteDatabase(nombre)
            ctx.filesDir.deleteRecursively()
        }
    }

    @Test fun lecturaFallidaNoReemplazaLosDatos() {
        val nombre = "prueba-invitado-${UUID.randomUUID()}.db"
        val ctx = contextoAislado(nombre)
        try {
            BaseDatosInvitado(ctx, nombre).use { base ->
                base.writableDatabase.execSQL("INSERT INTO estado_invitado VALUES(1, X'010203')")
                val estado = EstadoApp(AlmacenLocal(ctx, base))
                estado.entrarInvitado()
                assertFalse(estado.invitado)
                assertNotNull(estado.aviso)
                assertTrue(runCatching { base.guardar(DatosRecorrido.crear()) }.isFailure)
                base.readableDatabase.rawQuery("SELECT datos FROM estado_invitado", null).use {
                    assertTrue(it.moveToFirst())
                    assertArrayEquals(byteArrayOf(1, 2, 3), it.getBlob(0))
                }
            }
        } finally {
            contexto.deleteDatabase(nombre)
            ctx.filesDir.deleteRecursively()
        }
    }

    @Test fun escrituraFallidaConservaLaUltimaVersion() {
        val nombre = "prueba-invitado-${UUID.randomUUID()}.db"
        val ctx = contextoAislado(nombre)
        try {
            BaseDatosInvitado(ctx, nombre).use { base ->
                val estado = EstadoApp(AlmacenLocal(ctx, base))
                estado.entrarInvitado()
                base.writableDatabase.execSQL("CREATE TRIGGER fallo_guardado BEFORE INSERT ON estado_invitado BEGIN SELECT RAISE(ABORT, 'Fallo de prueba'); END")
                assertFalse(estado.cambiar { estado.usuario?.nombre = "Cambio no guardado" })
                assertNotNull(estado.aviso)
                assertEquals("Invitado", base.cargar()?.usuarios?.first { it.idUsuario == 100 }?.nombre)
            }
        } finally {
            contexto.deleteDatabase(nombre)
            ctx.filesDir.deleteRecursively()
        }
    }

    @Test fun persisteEntreProcesos() {
        val fase = InstrumentationRegistry.getArguments().getString("fase")
        assumeNotNull(fase)
        val nombre = "prueba-invitado-reinicio.db"
        val ctx = contextoAislado(nombre)
        BaseDatosInvitado(ctx, nombre).use { base ->
            val estado = EstadoApp(AlmacenLocal(ctx, base))
            estado.entrarInvitado()
            assertTrue(estado.invitado)
            when (fase) {
                "guardar" -> assertTrue(estado.cambiar { estado.usuario?.nombre = "Persistencia entre procesos" })
                "recuperar" -> assertEquals("Persistencia entre procesos", estado.usuario?.nombre)
                else -> error("Fase de prueba desconocida")
            }
        }
        if (fase == "recuperar") {
            contexto.deleteDatabase(nombre)
            ctx.filesDir.deleteRecursively()
        }
    }
}
