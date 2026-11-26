package com.example.fundfind.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fundfind.logic.*
import java.time.format.DateTimeFormatter
import java.util.Locale

fun fecha(s: Solicitud): String =
    s.fechaLimite?.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("es"))) ?: "Sin fecha límite"

@Composable fun Estadistica(numero: String, label: String, modifier: Modifier = Modifier, fondo: Color = Color.White, texto: Color = Bosque) {
    Column(modifier.background(fondo, RoundedCornerShape(16.dp)).padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(numero, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = texto)
        Text(label, fontSize = 11.sp, color = if (fondo == Bosque) Color.White else Gris)
    }
}

@Composable fun FichaSolicitud(e: EstadoApp, s: Solicitud, version: Int = e.version) {
    e.revision
    Tarjeta {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Avatar(s.fundacion?.nombre.orEmpty(), oscuro = version == 1)
            Column(Modifier.weight(1f)) {
                Text(s.fundacion?.nombre.orEmpty(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(if (s.fundacion?.isVerificado == true) "Organización verificada" else "Pendiente de verificación", fontSize = 10.sp, color = Gris)
            }
            if (s.urgente && version == 2) Etiqueta("Urgente", Melocoton)
        }
        Text(s.titulo.orEmpty(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Bosque)
        Etiqueta(s.tipoDonacion.orEmpty(), if (s.tipoDonacion == "Dinero") Color(0xFFEDF5C8) else Melocoton)
        Nota(s.ubicacion.ifBlank { s.fundacion?.direccion.orEmpty() })
        Nota("Hasta ${fecha(s)}")
        Accion("Ver solicitud", principal = version == 1) { e.ver(s) }
    }
}

@Composable fun Buscador(e: EstadoApp, cambiarPantalla: Boolean = false) {
    OutlinedTextField(
        e.busqueda, { e.busqueda = it }, modifier = Modifier.fillMaxWidth(), singleLine = true,
        placeholder = { Text("Buscar fundaciones o solicitudes", fontSize = 12.sp) },
        leadingIcon = { Icono("buscar", Modifier.size(18.dp), VerdeSuave) },
        trailingIcon = if (cambiarPantalla) {{ BotonIcono("explorar", "Buscar solicitudes") { e.ir(e.destino("EXPLORAR")) } }} else null,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
            unfocusedBorderColor = Borde, focusedBorderColor = Bosque
        ),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSearch = { e.ir(e.destino("EXPLORAR")) })
    )
}

@Composable fun Inicio(e: EstadoApp, version: Int) {
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().background(if (version == 2) Bosque else Fondo).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f)) { Logo(version == 2) }
                BotonIcono("avisos", "Notificaciones", if (version == 2) Menta else VerdeAccion) { e.ir(Rutas.AVISOS) }
                Box(Modifier.clickable { e.ir(e.destino("PERFIL")) }) { Avatar(e.usuario?.nombre.orEmpty(), oscuro = version == 1) }
            }
            if (version == 2) { Spacer(Modifier.height(10.dp)); Buscador(e, true) }
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Titulo("Encuentra una forma de ayudar", true)
            if (version == 1) Buscador(e, true)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Estadistica(
                    e.sistema.solicitudesActivas.size.toString(), "solicitudes activas", Modifier.weight(1f),
                    if (version == 1) Bosque else Color(0xFFEDF5C8), if (version == 1) Verde else Bosque
                )
                Estadistica(
                    if (version == 1) e.sistema.solicitudesActivas.filterNotNull().count { it.fechaLimite?.isBefore(java.time.LocalDate.now().plusDays(7)) == true }.toString()
                    else e.sistema.fundaciones.count { it.isVerificado }.toString(),
                    if (version == 1) "Cierran esta semana" else "fundaciones verificadas", Modifier.weight(1f)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (version == 1) "Solicitudes de donación" else "Solicitudes recientes", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Enlace("Ver todas") { e.ir(e.destino("EXPLORAR")) }
            }
            val recientes = e.resultados().take(if (version == 1) 4 else 2)
            if (recientes.isEmpty()) Aviso("Todavía no hay solicitudes publicadas. Explora las fundaciones o prueba el recorrido como invitado.")
            recientes.forEach { FichaSolicitud(e, it, version) }
            if (version == 2) {
                Text("Fundaciones destacadas", fontWeight = FontWeight.Bold)
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    e.sistema.fundaciones.filter { it.isVerificado }.take(6).forEach { f ->
                        Column(
                            Modifier.width(165.dp).background(Color.White, RoundedCornerShape(16.dp))
                                .clickable { e.fundacionId = f.idFundacion; e.ir(Rutas.FUNDACIONES) }.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Avatar(f.nombre.orEmpty())
                            Text(f.nombre.orEmpty(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Nota(f.categoria.orEmpty())
                        }
                    }
                }
            }
            Enlace("Ver todas las fundaciones") { e.ir(Rutas.FUNDACIONES) }
        }
        Navegacion(e)
    }
}

@Composable fun Explorar(e: EstadoApp, version: Int) {
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().background(if (version == 2) Bosque else Fondo).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Explorar", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (version == 2) Menta else VerdeAccion)
            Buscador(e)
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.width(155.dp)) {
                    Selector("", e.filtroUbicacion.ifBlank { "Ubicación" }, listOf("Todas") + e.sistema.solicitudes.map { it.ubicacion }.filter { it.isNotBlank() }.distinct()) {
                        e.filtroUbicacion = if (it == "Todas") "" else it
                    }
                }
                Box(Modifier.width(185.dp)) {
                    Selector("", e.filtroCategoria.ifBlank { "Categoría" }, listOf("Todas") + FundFindSistema.CATEGORIAS_GENERALES) {
                        e.filtroCategoria = if (it == "Todas") "" else it
                    }
                }
            }
            val filtros = listOf(e.filtroUbicacion, e.filtroCategoria).filter { it.isNotEmpty() }
            if (filtros.isNotEmpty()) Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                filtros.forEach { Etiqueta(it) }; Enlace("Limpiar") { e.limpiarFiltros() }
            }
            val lista = e.resultados()
            if (lista.isEmpty()) {
                Spacer(Modifier.height(40.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box(Modifier.size(88.dp).background(Menta, CircleShape), contentAlignment = Alignment.Center) { Icono("buscar", Modifier.size(36.dp)) }
                }
                Titulo("No encontramos solicitudes con esos filtros", centro = true)
                Nota("Prueba con otra palabra o quita algunos filtros para ver más fundaciones que necesitan ayuda.", centro = true)
                Accion("Limpiar filtros") { e.limpiarFiltros(); e.ir(e.destino("EXPLORAR"), true) }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${lista.size} solicitudes", fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Box(Modifier.width(165.dp)) { Selector("", e.orden, listOf("Recientes", "Cercanía", "Fecha límite")) { e.orden = it } }
                }
                lista.forEach { FichaSolicitud(e, it, version) }
            }
        }
        Navegacion(e)
    }
}

@Composable fun Dato(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, fontSize = 11.sp, color = Gris, modifier = Modifier.weight(1f))
        Text(value, fontSize = 12.sp, color = Bosque, modifier = Modifier.weight(1.3f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}

@Composable fun Detalle(e: EstadoApp, version: Int) {
    val s = e.solicitud
    if (s == null) { Pagina(e, "Detalle de solicitud") { Aviso("Esta solicitud ya no está disponible.") }; return }
    Pagina(e, "Detalle de solicitud", footer = {
        if (s.activa()) Accion("Quiero ayudar") { if (e.usuario != null) e.ir(e.destino("DONAR")) else e.ir(e.destino("LOGIN")) }
        Accion("Ver perfil de fundación", false) { e.fundacionId = requireNotNull(s.fundacion).idFundacion; e.ir(Rutas.FUNDACIONES) }
    }) {
        BannerFundacion(s.fundacion)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Avatar(s.fundacion?.nombre.orEmpty(), oscuro = version == 1)
            Column(Modifier.weight(1f)) {
                Text(s.fundacion?.nombre.orEmpty(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Nota(if (s.fundacion?.isVerificado == true) "Organización verificada · ${s.fundacion?.rfc}" else "Pendiente de verificación")
            }
            BotonIcono(
                "corazon",
                if (e.usuario?.favoritos?.contains(s.idSolicitud) == true) "Quitar de favoritos" else "Guardar solicitud",
                if (e.usuario?.favoritos?.contains(s.idSolicitud) == true) VerdeAccion else VerdeSuave
            ) { e.favorito(s) }
        }
        Titulo(s.titulo.orEmpty())
        Nota(s.descripcion.orEmpty())
        Tarjeta {
            Dato("Categoría", s.tipoDonacion.orEmpty())
            Dato("Cantidad solicitada", s.cantidad)
            Dato("Fecha límite", fecha(s))
            Dato("Ubicación exacta", s.ubicacion.ifBlank { s.fundacion?.direccion.orEmpty() })
        }
        Aviso("En esta fase se registra tu intención de apoyo y se genera tu comprobante oficial en PDF.", true)
        Row {
            Enlace("Ver reseñas") { e.fundacionId = requireNotNull(s.fundacion).idFundacion; e.ir(Rutas.RESENAS) }
            Spacer(Modifier.weight(1f))
            Enlace("Reportar publicación", confirmacion = "¿Deseas reportar esta publicación para su revisión?") {
                e.cambiar { s.reportes++; s.motivoReporte = "Reportada" }
                e.aviso = "Reporte registrado para revisión."
            }
        }
        if (e.rol == "fundacion" && s.fundacion === e.fundacion) {
            Accion("Editar solicitud", false) { e.editarSolicitud = true; e.ir(Rutas.V2_CREAR) }
            Accion("Marcar completada", false, confirmacion = "¿Deseas marcar esta solicitud como completada?") { e.cambiar { s.marcarCompletada() } }
        }
    }
}

@Composable fun Donar(e: EstadoApp, version: Int) {
    val s = e.solicitud
    if (s == null) { Pagina(e, "Quiero ayudar") { Aviso("Selecciona una solicitud disponible.") }; return }
    var categoria by rememberSaveable { mutableStateOf(s.tipoDonacion.orEmpty().ifBlank { "Dinero" }) }
    var montoDinero by rememberSaveable { mutableStateOf("") }
    var cantidad by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf(e.usuario?.nombre.orEmpty()) }
    var contacto by rememberSaveable { mutableStateOf(e.usuario?.correo.orEmpty()) }
    var comprobante by rememberSaveable { mutableStateOf(true) }

    val esDinero = categoria.equals("Dinero", ignoreCase = true)
    val resumenConfirmacion = if (esDinero) "Donación económica · $$montoDinero MXN" else "$categoria · $cantidad"

    val confirmar: () -> Unit = {
        val monto = montoDinero.toDoubleOrNull() ?: 0.0
        val desc = if (esDinero) "$$montoDinero MXN" else cantidad
        if (e.cambiar {
                val d = e.sistema.registrarIntencion(requireNotNull(e.usuario), s, categoria, desc, nombre, contacto, comprobante, monto)
                e.donacionId = d.idDonacion
            }) e.ir(e.destino("CONFIRMACION"))
    }

    Pagina(e, "Quiero ayudar", footer = if (version == 1) {{
        Accion("Confirmar intención", confirmacion = "¿Deseas confirmar tu ayuda a ${s.fundacion?.nombre}?\n\n$resumenConfirmacion", onClick = confirmar)
    }} else null) {
        if (version == 1) Aviso("${s.titulo}\n${s.fundacion?.nombre}") else Titulo("Registra tu intención de ayuda")
        Nota("Para: ${s.titulo}")
        Selector("Categoría de apoyo", categoria, FundFindSistema.CATEGORIAS_GENERALES) { categoria = it }
        if (esDinero) {
            Campo("Monto a donar (MXN)", montoDinero, "Ej. 250.00", { montoDinero = it }, keyboard = KeyboardType.Decimal)
        } else {
            Campo("Cantidad o descripción", cantidad, "Ej. 10 kg de arroz y lentejas", { cantidad = it })
        }
        Campo("Nombre de contacto", nombre, "Tu nombre completo", { nombre = it })
        Campo("Correo o teléfono", contacto, "nombre@correo.com", { contacto = it })
        if (version == 1) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(comprobante, { comprobante = it })
                Nota("Quiero recibir un comprobante digital en PDF")
            }
        } else {
            Aviso("Al confirmar podrás descargar tu comprobante oficial en PDF.", true)
            Accion("Confirmar intención", confirmacion = "¿Deseas confirmar tu ayuda a ${s.fundacion?.nombre}?\n\n$resumenConfirmacion", onClick = confirmar)
        }
    }
}

@Composable fun Confirmacion(e: EstadoApp, version: Int) {
    Pagina(e) {
        Spacer(Modifier.height(36.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(Modifier.size(84.dp).background(Verde, CircleShape), contentAlignment = Alignment.Center) { Icono("check", Modifier.size(36.dp)) }
        }
        Spacer(Modifier.height(8.dp))
        Titulo("¡Gracias por ayudar!", true, true)
        Nota("Registramos tu intención de apoyo. La fundación se pondrá en contacto contigo para coordinar la entrega.", centro = true)
        val d = e.donacion
        if (d != null) Tarjeta {
            Dato("Folio", d.comprobante.orEmpty())
            Dato("Fundación", d.solicitud?.fundacion?.nombre.orEmpty())
            Dato("Solicitud", d.solicitud?.titulo.orEmpty())
            Dato("Categoría", d.tipoDonativo.orEmpty())
            Dato("Aportación", d.cantidadDescripcion)
            Dato("Donador", d.nombreContacto)
        }
        Spacer(Modifier.height(16.dp))
        DescargarComprobante(e, "Descargar comprobante en PDF")
        Accion("Volver al inicio", false) { e.ir(e.inicio()) }
    }
}

@Composable fun Perfil(e: EstadoApp, version: Int) {
    val u = e.usuario
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().background(if (version == 2) Bosque else Fondo).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Avatar(u?.nombre.orEmpty(), true, version == 1)
            Text(u?.nombre.orEmpty(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (version == 2) Menta else VerdeAccion)
            Nota(u?.correo.orEmpty(), if (version == 2) Menta else Gris)
            Nota(u?.ubicacionTexto.orEmpty(), if (version == 2) Menta else Gris)
            if (version == 1) Accion("Editar datos personales", false) { e.ir(Rutas.EDITAR_PERFIL) }
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Text("Historial de intenciones", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Enlace("Ver todo") { e.ir(Rutas.HISTORIAL) } }
            val donaciones = e.sistema.donaciones.filter { it.usuario === u }
            if (donaciones.isEmpty()) Nota("Aún no has registrado intenciones de ayuda.")
            donaciones.takeLast(2).reversed().forEach { d -> FilaDonacion(e, d) }
            Text("Solicitudes guardadas", fontWeight = FontWeight.Bold)
            val favoritas = e.sistema.solicitudes.filter { u?.favoritos?.contains(it.idSolicitud) == true }
            if (favoritas.isEmpty()) Nota("Guarda una solicitud desde su detalle para encontrarla aquí.")
            favoritas.take(2).forEach { s -> Tarjeta(Modifier.clickable { e.ver(s) }) { Text(s.titulo.orEmpty(), fontSize = 13.sp); Nota(s.fundacion?.nombre.orEmpty()); Etiqueta("Guardada") } }
            if (version == 2) Accion("Editar datos personales", false) { e.ir(Rutas.EDITAR_PERFIL) }
            Enlace("Mensajes") { e.ir(Rutas.MENSAJES) }
            Enlace("Cerrar sesión") { e.salir() }
        }
        Navegacion(e)
    }
}

@Composable fun FilaDonacion(e: EstadoApp, d: Donacion) {
    e.revision
    Tarjeta(Modifier.clickable { e.donacionId = d.idDonacion; e.ir(if (e.rol == "fundacion") Rutas.INTERESADOS else Rutas.HISTORIAL) }) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Column(Modifier.weight(1f)) {
                Text(d.solicitud?.titulo.orEmpty(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Nota("${d.tipoDonativo} · ${d.cantidadDescripcion}")
            }
            Etiqueta(
                when (d.estado) { "CONFIRMADA" -> "Confirmada"; "CANCELADA" -> "Cancelada"; else -> "En revisión" },
                if (d.estado == "INTENCIONREGISTRADA") Melocoton else Menta
            )
        }
    }
}