package com.example.fundfind.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fundfind.logic.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

@Composable fun ResumenFundacion(e: EstadoApp) {
    val f = e.fundacion
    val propias = f?.gestionarSolicitudes().orEmpty().filterNotNull()
    val intenciones = e.sistema.donaciones.filter { it.solicitud?.fundacion === f }
    Pagina(e, nav = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Titulo(f?.nombre.orEmpty(), true)
                Nota(if (f?.isVerificado == true) "Organización verificada" else "Pendiente de verificación")
            }
            BotonIcono("avisos", "Notificaciones") { e.ir(Rutas.AVISOS) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Estadistica(propias.count { it.activa() }.toString(), "solicitudes activas", Modifier.weight(1f), Bosque, Verde)
            Estadistica(intenciones.size.toString(), "intenciones recibidas", Modifier.weight(1f))
        }
        Accion("Publicar nueva solicitud") { e.editarSolicitud = false; e.ir(Rutas.V2_CREAR) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Solicitudes publicadas", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Enlace("Ver todas") { e.ir(Rutas.MIS_SOLICITUDES) }
        }
        if (propias.isEmpty()) Aviso("Aún no has publicado solicitudes.")
        propias.takeLast(2).reversed().forEach { s ->
            Tarjeta(Modifier.clickable { e.ver(s) }) {
                Row {
                    Text(s.titulo.orEmpty(), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Etiqueta(if (s.activa()) "Activa" else s.estado.orEmpty())
                }
                Nota("${s.tipoDonacion} · ${s.cantidad} · ${fecha(s)}")
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Donadores interesados", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Enlace("Gestionar") { e.ir(Rutas.INTERESADOS) }
        }
        if (intenciones.isEmpty()) Nota("Todavía no recibes intenciones de apoyo.")
        intenciones.takeLast(2).reversed().forEach { FilaDonacion(e, it) }
    }
}

@Composable fun CrearSolicitud(e: EstadoApp) {
    val original = if (e.editarSolicitud) e.solicitud else null
    var titulo by rememberSaveable { mutableStateOf(original?.titulo.orEmpty()) }
    var categoria by rememberSaveable { mutableStateOf(original?.tipoDonacion.orEmpty().ifBlank { "Alimentos y despensas" }) }
    var cantidad by rememberSaveable { mutableStateOf(original?.cantidad.orEmpty()) }
    var limite by rememberSaveable { mutableStateOf(original?.fechaLimite?.format(DateTimeFormatter.ofPattern("dd/MM/uuuu")).orEmpty()) }
    var lugar by rememberSaveable { mutableStateOf(original?.ubicacion ?: e.fundacion?.direccion.orEmpty()) }
    var descripcion by rememberSaveable { mutableStateOf(original?.descripcion.orEmpty()) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    fun guardar(publicar: Boolean) {
        val f = e.fundacion ?: return
        val fecha = if (limite.isBlank()) null else runCatching {
            LocalDate.parse(limite, DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT))
        }.getOrNull()
        error = when {
            titulo.isBlank() -> "Escribe el título de la solicitud."
            limite.isNotBlank() && fecha == null -> "Introduce una fecha válida en formato dd/mm/aaaa."
            publicar && (categoria.isBlank() || cantidad.isBlank() || lugar.isBlank() || descripcion.isBlank()) -> "Completa todos los campos antes de publicar."
            publicar && (fecha == null || !fecha.isAfter(LocalDate.now())) -> "La fecha debe ser posterior a hoy."
            else -> null
        }
        if (error != null) return
        if (e.cambiar {
                val s = original ?: if (publicar) requireNotNull(e.sistema.publicarSolicitud(f, titulo, descripcion, categoria, categoria))
                else Solicitud((e.sistema.solicitudes.maxOfOrNull { it.idSolicitud } ?: 0) + 1, titulo, descripcion, categoria, categoria, "BORRADOR", null, f).also {
                    e.sistema.solicitudes.add(it)
                }
                require(s.fundacion === f) { "Solo puedes editar solicitudes de tu fundación." }
                s.titulo = titulo.trim()
                s.tipoDonacion = categoria
                s.causaSocial = categoria
                s.cantidad = cantidad.trim()
                s.fechaLimite = fecha
                s.ubicacion = lugar.trim()
                s.descripcion = descripcion.trim()
                if (original != null) {
                    f.editarSolicitud(s)
                    if (publicar) s.publicar()
                }
                e.solicitudId = s.idSolicitud
            }) {
            e.editarSolicitud = false
            e.ir(if (publicar) Rutas.V2_PUBLICADA else Rutas.MIS_SOLICITUDES)
        }
    }

    Pagina(e, if (original == null) "Nueva solicitud" else "Editar solicitud", footer = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Accion(if (original?.activa() == true) "Guardar cambios" else "Guardar borrador", false, Modifier.weight(1f)) { guardar(false) }
            Accion("Publicar solicitud", true, Modifier.weight(1f), confirmacion = "¿Deseas publicar esta solicitud?") { guardar(true) }
        }
    }) {
        Campo("Título", titulo, "Ej. Recaudación para cirugía o despensas", { titulo = it })
        Selector("Categoría", categoria, FundFindSistema.CATEGORIAS_GENERALES) { categoria = it }
        Campo(
            if (categoria == "Dinero") "Meta económica requerida (MXN)" else "Cantidad requerida",
            cantidad,
            if (categoria == "Dinero") "Ej. $5,000 MXN" else "Ej. 120 despensas",
            { cantidad = it }
        )
        Campo("Fecha límite", limite, "dd/mm/aaaa", { limite = it })
        error?.let { Aviso(it, true) }
        Campo("Ubicación de entrega o sede", lugar, "Dirección exacta o punto de referencia", { lugar = it })
        Campo("Descripción", descripcion, "Describe cómo ayudará esta donación", { descripcion = it }, multiline = true)
    }
}

@Composable fun Publicada(e: EstadoApp) {
    Pagina(e) {
        Spacer(Modifier.height(48.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(Modifier.size(84.dp).background(Verde, CircleShape), contentAlignment = Alignment.Center) { Icono("check", Modifier.size(36.dp)) }
        }
        Titulo("Solicitud publicada", true, true)
        Nota("Tu solicitud ya está visible para los donadores. Cuando alguien registre su intención recibirás una notificación.", centro = true)
        e.solicitud?.let {
            Tarjeta {
                Dato("Título", it.titulo.orEmpty())
                Dato("Categoría", it.tipoDonacion.orEmpty())
                Dato("Cantidad", it.cantidad)
                Dato("Fecha límite", fecha(it))
            }
        }
        Spacer(Modifier.height(24.dp))
        Accion("Ver solicitud publicada") { e.solicitud?.let { e.ver(it) } }
        Accion("Volver al panel", false) { e.ir(Rutas.V2_FUNDACION) }
    }
}

@Composable fun PanelAdmin(e: EstadoApp) {
    val pendientes = e.sistema.fundaciones.filter { !it.isVerificado && it.estadoCuenta == "ACTIVA" }
    val reportadas = e.sistema.solicitudes.filter { it.reportes > 0 && it.estado != "ELIMINADA" }
    Pagina(e, nav = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Titulo("Panel de control", true)
            Spacer(Modifier.weight(1f))
            Enlace("Salir") { e.salir() }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Estadistica(pendientes.size.toString(), "pendientes de verificación", Modifier.weight(1f), Bosque, Verde)
            Estadistica(reportadas.size.toString(), "publicaciones reportadas", Modifier.weight(1f), Melocoton, Terracota)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Fundaciones pendientes", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Enlace("Ver cola") { e.fundacionId = pendientes.firstOrNull()?.idFundacion ?: -1; e.ir(Rutas.V2_VERIFICAR) }
        }
        if (pendientes.isEmpty()) Nota("No hay fundaciones pendientes de revisión.")
        pendientes.forEach { f ->
            Tarjeta(Modifier.clickable { e.fundacionId = f.idFundacion; e.ir(Rutas.V2_VERIFICAR) }) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Avatar(f.nombre.orEmpty())
                    Column(Modifier.weight(1f)) {
                        Text(f.nombre.orEmpty(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Nota(f.rfc.orEmpty())
                    }
                    Etiqueta("En revisión", Melocoton)
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Moderación reciente", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Enlace("Revisar") { e.ir(Rutas.V2_MODERAR) }
        }
        if (reportadas.isEmpty()) Nota("No hay reportes abiertos.")
        reportadas.forEach { s ->
            Tarjeta(Modifier.clickable { e.solicitudId = s.idSolicitud; e.ir(Rutas.V2_MODERAR) }) {
                Row {
                    Text(s.titulo.orEmpty(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Etiqueta(s.motivoReporte.ifBlank { "Reportada" }, Melocoton)
                }
                Nota(s.fundacion?.nombre.orEmpty())
            }
        }
    }
}

@Composable fun Verificar(e: EstadoApp) {
    val pendientes = e.sistema.fundaciones.filter { !it.isVerificado && it.estadoCuenta == "ACTIVA" }
    val f = pendientes.firstOrNull { it.idFundacion == e.fundacionId } ?: pendientes.firstOrNull()
    Pagina(e, "Verificación de fundación", nav = true) {
        if (f == null) { Aviso("No hay fundaciones pendientes de verificación."); return@Pagina }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Avatar(f.nombre.orEmpty(), true)
            Column { Titulo(f.nombre.orEmpty()); Nota("En revisión documental") }
        }
        Tarjeta {
            Dato("RFC", f.rfc.orEmpty())
            Dato("Categoría", f.categoria.orEmpty())
            Dato("Ubicación", f.direccion.orEmpty())
            Dato("Correo", f.correoContacto.orEmpty())
            Dato("Teléfono", f.telefonoContacto.orEmpty())
            Dato("Registro", f.documentacionLegal.orEmpty())
        }
        Text("Documentos adjuntos", fontWeight = FontWeight.Bold)
        listOf("Acta constitutiva", "Comprobante de domicilio", "Constancia RFC").forEach { doc ->
            Tarjeta(Modifier.clickable { e.fundacionId = f.idFundacion; e.documento = doc; e.ir(Rutas.DOCUMENTO) }) {
                Row {
                    Text(doc, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Text("Abrir", fontSize = 12.sp, color = VerdeAccion)
                }
            }
        }
        Accion("Aprobar verificación", confirmacion = "¿Deseas aprobar la verificación de ${f.nombre}?") {
            e.cambiar { e.sistema.verificar(f, true, "Administrador local") }
        }
        Accion("Rechazar solicitud", false, confirmacion = "¿Deseas rechazar la solicitud de verificación de ${f.nombre}?") {
            e.cambiar { e.sistema.verificar(f, false, "Administrador local") }
        }
    }
}

@Composable fun Moderar(e: EstadoApp) {
    val reportadas = e.sistema.solicitudes.filter { it.reportes > 0 && it.estado != "ELIMINADA" }
    Pagina(e, "Moderación de contenido", nav = true) {
        if (reportadas.isEmpty()) { Aviso("No hay publicaciones pendientes de moderación."); return@Pagina }
        reportadas.forEach { s ->
            Tarjeta {
                Row {
                    Text(s.titulo.orEmpty(), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Etiqueta("${s.reportes} reporte(s)", Melocoton)
                }
                Nota("${s.fundacion?.nombre} · ${s.motivoReporte}")
                Text(s.descripcion.orEmpty(), fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Accion("Aprobar", true, Modifier.weight(1f), confirmacion = "¿Deseas aprobar esta publicación y retirar los reportes?") {
                        e.cambiar { e.sistema.moderar(s, true) }
                    }
                    Accion("Retirar", false, Modifier.weight(1f), confirmacion = "¿Deseas retirar esta publicación por incumplir las normas?") {
                        e.cambiar { e.sistema.moderar(s, false) }
                    }
                }
            }
        }
    }
}

@Composable fun Reportes(e: EstadoApp) {
    Pagina(e, if (e.rol == "fundacion") "Impacto" else "Reportes e impacto", nav = true) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Estadistica(e.sistema.solicitudesActivas.size.toString(), "solicitudes activas", Modifier.weight(1f), Bosque, Verde)
            Estadistica(e.sistema.donaciones.size.toString(), "intenciones registradas", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Estadistica(e.sistema.fundaciones.count { it.isVerificado }.toString(), "fundaciones verificadas", Modifier.weight(1f))
            Estadistica(e.sistema.donaciones.count { it.estado == "CONFIRMADA" }.toString(), "entregas confirmadas", Modifier.weight(1f))
        }
        Text("Solicitudes por categoría", fontWeight = FontWeight.Bold)
        val grupos = e.sistema.solicitudes.filter { it.estado != "ELIMINADA" }.groupBy { it.tipoDonacion.orEmpty() }
        val max = (grupos.maxOfOrNull { it.value.size } ?: 1).toFloat()
        Tarjeta {
            if (grupos.isEmpty()) Nota("Aún no hay solicitudes registradas.")
            grupos.forEach { (cat, lista) ->
                Text("$cat (${lista.size})", fontSize = 12.sp)
                LinearProgressIndicator(progress = { lista.size / max }, modifier = Modifier.fillMaxWidth().height(8.dp))
            }
        }
    }
}