package com.example.fundfind.layout

import android.app.Application
import androidx.compose.runtime.*
import com.example.fundfind.logic.*

class FundFindApplication : Application() {
    val estado by lazy { EstadoApp(AlmacenLocal(this)) }
}

class EstadoApp(private val almacen: AlmacenLocal) {
    private val local = almacen.cargar()
    private var demo: FundFindSistema? = null
    private var datos by mutableStateOf(local)
    val sistema: FundFindSistema
        get() {
            revision
            return datos
        }
    var invitado by mutableStateOf(false)
        private set
    var usuario by mutableStateOf<UsuarioDonador?>(null)
    var fundacion by mutableStateOf<Fundacion?>(null)
    var rol by mutableStateOf("donador")
    var version by mutableIntStateOf(2)
    var ruta by mutableStateOf(Rutas.V2_BIENVENIDA)
    private data class Paso(val ruta: Rutas, val version: Int, val rol: String)
    private val anteriores = mutableStateListOf<Paso>()
    var revision by mutableIntStateOf(0)
    var aviso by mutableStateOf(almacen.errorCarga)
    var solicitudId by mutableIntStateOf(1)
    var fundacionId by mutableIntStateOf(100)
    var donacionId by mutableIntStateOf(1)
    var mensajeId by mutableIntStateOf(0)
    var editarSolicitud by mutableStateOf(false)
    var documento by mutableStateOf("Documentación legal")
    var busqueda by mutableStateOf("")
    var filtroUbicacion by mutableStateOf("")
    var filtroCategoria by mutableStateOf("")
    var orden by mutableStateOf("Recientes")

    val solicitud get() = sistema.solicitudes.firstOrNull { it.idSolicitud == solicitudId }
    val organizacion get() = sistema.fundaciones.firstOrNull { it.idFundacion == fundacionId } ?: fundacion
    val donacion get() = sistema.donaciones.firstOrNull { it.idDonacion == donacionId }

    fun destino(nombre: String): Rutas = Rutas.valueOf("V${version}_$nombre")

    fun ir(nueva: Rutas, reemplazar: Boolean = false) {
        if (!reemplazar && nueva != ruta) anteriores.add(Paso(ruta, version, rol))
        if (nueva.version != 0) version = nueva.version
        ruta = nueva
    }

    fun atras() {
        if (anteriores.isNotEmpty()) {
            val paso = anteriores.removeAt(anteriores.lastIndex)
            ruta = paso.ruta; version = paso.version; rol = paso.rol
        } else {
            ir(if (usuario != null || fundacion != null) inicio() else destino("BIENVENIDA"), true)
        }
        if (ruta.version != 0) version = ruta.version
    }

    fun inicio(): Rutas = when (rol) {
        "fundacion" -> Rutas.V2_FUNDACION
        "admin" -> Rutas.V2_ADMIN
        else -> destino("INICIO")
    }

    fun entrarInvitado() {
        ejecutar {
            val recorrido = (demo ?: almacen.cargarInvitado())
                .takeIf { it.usuarios.firstOrNull { u -> u.idUsuario == 100 }?.estadoCuenta == "ACTIVA" }
                ?: DatosRecorrido.crear()
            almacen.guardarInvitado(recorrido)
            demo = recorrido; datos = recorrido; invitado = true
            usuario = recorrido.usuarios.first { it.idUsuario == 100 }
            fundacion = recorrido.fundaciones.first { it.idFundacion == 100 }; rol = "donador"
            anteriores.clear(); ir(destino("INICIO"), true)
        }
    }

    fun visitar(pantalla: Rutas) {
        check(invitado)
        rol = pantalla.rol
        if (pantalla == Rutas.V2_VERIFICAR) fundacionId = 105
        if (pantalla.name.endsWith("DETALLE") || pantalla.name.endsWith("DONAR")) solicitudId = sistema.solicitudes.first().idSolicitud
        if (pantalla.name.endsWith("CONFIRMACION")) donacionId = sistema.donaciones.first().idDonacion
        if (pantalla.name.endsWith("VACIO")) { limpiarFiltros(); busqueda = "reciclaje navideño" }
        if (pantalla.name.endsWith("EXPLORAR")) limpiarFiltros()
        ir(pantalla)
    }

    fun login(correo: String, clave: String): Boolean {
        val u = local.loginDonador(correo.trim(), clave)
        val f = if (u == null) local.loginFundacion(correo.trim(), clave) else null
        if (u == null && f == null) return false
        datos = local; usuario = u; fundacion = f; invitado = false
        rol = if (f == null) "donador" else "fundacion"
        anteriores.clear(); ir(inicio(), true)
        return true
    }

    fun registrar(nombre: String, correo: String, clave: String, lugar: String): Boolean = ejecutar {
        val u = local.registrarUsuario(nombre, correo, clave, lugar)
        almacen.guardar(local); datos = local; usuario = u; fundacion = null; invitado = false; rol = "donador"
        anteriores.clear(); ir(destino("INICIO"), true)
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
    ): Boolean = ejecutar {
        val f = local.registrarFundacion(nombre, rfc, correo, clave, telefono, direccion, categoria, mision)
        almacen.guardar(local); datos = local; usuario = null; fundacion = f; invitado = false; rol = "fundacion"
        anteriores.clear(); ir(Rutas.V2_FUNDACION, true)
    }

    fun salir() {
        usuario = null; fundacion = null; invitado = false; datos = local; rol = "donador"
        anteriores.clear(); limpiarFiltros(); ir(destino("BIENVENIDA"), true)
    }

    fun ejecutar(accion: () -> Unit): Boolean {
        return try { accion(); revision++; true }
        catch (e: Exception) { aviso = e.message ?: "No se pudo completar la operación."; revision++; false }
    }

    fun cambiar(accion: () -> Unit): Boolean = ejecutar {
        accion()
        if (invitado) almacen.guardarInvitado(sistema) else almacen.guardar(sistema)
    }

    fun limpiarFiltros() {
        busqueda = ""; filtroUbicacion = ""; filtroCategoria = ""
    }

    fun resultados(): List<Solicitud> {
        val lista = sistema.solicitudesActivas.filterNotNull().filter { s ->
            val texto = "${s.titulo} ${s.descripcion}${s.tipoDonacion} ${s.ubicacion}${s.fundacion?.nombre}"
            busqueda.split(' ').filter { it.isNotBlank() }.all { texto.contains(it, true) } &&
                    (filtroUbicacion.isBlank() || s.ubicacion.contains(filtroUbicacion, true)) &&
                    (filtroCategoria.isBlank() || s.tipoDonacion.equals(filtroCategoria, true))
        }
        return when (orden) {
            "Cercanía" -> lista.sortedBy { s ->
                usuario?.let { u ->
                    s.fundacion?.let { f ->
                        Ubicacion(u.latitud, u.longitud, u.ubicacionTexto)
                            .calcularDistancia(Ubicacion(f.latitud, f.longitud, f.direccion))
                    }
                } ?: Double.MAX_VALUE
            }
            "Fecha límite" -> lista.sortedBy { it.fechaLimite }
            else -> lista.sortedByDescending { it.fechaPublicacion }
        }
    }

    fun ver(s: Solicitud) { solicitudId = s.idSolicitud; ir(destino("DETALLE")) }

    fun favorito(s: Solicitud) {
        cambiar {
            val u = requireNotNull(usuario)
            if (!u.favoritos.add(s.idSolicitud)) u.favoritos.remove(s.idSolicitud)
        }
    }
}