package com.example.fundfind.layout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable fun FundFindApp(e: EstadoApp) {
    val revision = e.revision
    BackHandler(enabled = !e.ruta.name.endsWith("BIENVENIDA")) { e.atras() }
    TemaFundFind {
        Surface(Modifier.fillMaxSize(), color = Fondo) {
            Column(Modifier.fillMaxSize().safeDrawingPadding().imePadding()) {
                if (e.invitado) Row(
                    Modifier.fillMaxWidth().background(Menta).padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Invitado · ${e.rol}", fontSize = 11.sp, modifier = Modifier.weight(1f))
                    TextButton(onClick = { e.ir(Rutas.RECORRIDO) }, contentPadding = PaddingValues(4.dp)) {
                        Text("Recorrido completo", fontSize = 11.sp)
                    }
                }
                key(e.ruta) {
                    Box(Modifier.weight(1f)) {
                        when (e.ruta) {
                            Rutas.V1_BIENVENIDA -> Bienvenida(e, 1)
                            Rutas.V2_BIENVENIDA -> Bienvenida(e, 2)
                            Rutas.V1_REGISTRO -> Registro(e, 1)
                            Rutas.V2_REGISTRO -> Registro(e, 2)
                            Rutas.V1_LOGIN -> Login(e, 1)
                            Rutas.V2_LOGIN -> Login(e, 2)
                            Rutas.V2_ERROR -> Login(e, 2, true)
                            Rutas.V1_INICIO -> Inicio(e, 1)
                            Rutas.V2_INICIO -> Inicio(e, 2)
                            Rutas.V1_EXPLORAR -> Explorar(e, 1)
                            Rutas.V2_EXPLORAR -> Explorar(e, 2)
                            Rutas.V1_VACIO -> Explorar(e, 1)
                            Rutas.V2_VACIO -> Explorar(e, 2)
                            Rutas.V1_DETALLE -> Detalle(e, 1)
                            Rutas.V2_DETALLE -> Detalle(e, 2)
                            Rutas.V1_DONAR -> Donar(e, 1)
                            Rutas.V2_DONAR -> Donar(e, 2)
                            Rutas.V1_CONFIRMACION -> Confirmacion(e, 1)
                            Rutas.V2_CONFIRMACION -> Confirmacion(e, 2)
                            Rutas.V1_PERFIL -> Perfil(e, 1)
                            Rutas.V2_PERFIL -> Perfil(e, 2)
                            Rutas.V2_FUNDACION -> ResumenFundacion(e)
                            Rutas.V2_CREAR -> CrearSolicitud(e)
                            Rutas.V2_PUBLICADA -> Publicada(e)
                            Rutas.V2_ADMIN -> PanelAdmin(e)
                            Rutas.V2_VERIFICAR -> Verificar(e)
                            Rutas.V2_MODERAR -> Moderar(e)
                            Rutas.V2_REPORTES -> Reportes(e)
                            Rutas.RECORRIDO -> Recorrido(e)
                            else -> Complementaria(e, revision)
                        }
                    }
                }
            }
        }
        e.aviso?.let { texto ->
            AlertDialog(
                onDismissRequest = { e.aviso = null },
                title = { Text("FundFind") },
                text = { Text(texto) },
                confirmButton = { TextButton(onClick = { e.aviso = null }) { Text("Entendido") } }
            )
        }
    }
}

@Composable fun Cabecera(e: EstadoApp, titulo: String, dark: Boolean = e.version == 2, volver: Boolean = true) {
    Row(
        Modifier.fillMaxWidth().background(if (dark) Bosque else Fondo).padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (volver) BotonIcono("atras", "Volver", if (dark) Menta else VerdeAccion) { e.atras() }
        Text(titulo, color = if (dark) Color.White else Bosque, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
    }
}

@Composable fun Pagina(
    e: EstadoApp,
    title: String? = null,
    nav: Boolean = false,
    footer: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        if (title != null) Cabecera(e, title)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content
        )
        if (footer != null) Column(
            Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = footer
        )
        if (nav) Navegacion(e)
    }
}

@Composable fun Navegacion(e: EstadoApp) {
    val items = when (e.rol) {
        "admin" -> listOf(
            Triple("Panel", "corazon", Rutas.V2_ADMIN),
            Triple("Verificar", "check", Rutas.V2_VERIFICAR),
            Triple("Moderar", "bandera", Rutas.V2_MODERAR),
            Triple("Reportes", "reportes", Rutas.V2_REPORTES)
        )
        "fundacion" -> listOf(
            Triple("Resumen", "inicio", Rutas.V2_FUNDACION),
            Triple("Solicitudes", "lista", Rutas.MIS_SOLICITUDES),
            Triple("Interesados", "personas", Rutas.INTERESADOS),
            Triple("Impacto", "reportes", Rutas.V2_REPORTES),
            Triple("Perfil", "perfil", Rutas.PERFIL_FUNDACION)
        )
        else -> listOf(
            Triple("Inicio", "inicio", e.destino("INICIO")),
            Triple("Explorar", "explorar", e.destino("EXPLORAR")),
            Triple("Favoritos", "corazon", Rutas.FAVORITOS),
            Triple(if (e.version == 1) "Notificaciones" else "Avisos", "avisos", Rutas.AVISOS),
            Triple("Perfil", "perfil", e.destino("PERFIL"))
        )
    }
    Row(Modifier.fillMaxWidth().background(Color.White).padding(vertical = 6.dp)) {
        items.forEach { (name, icon, route) ->
            val selected = e.ruta == route
            Column(
                Modifier.weight(1f).heightIn(min = 48.dp).clickable {
                    if (route == Rutas.V2_VERIFICAR) {
                        e.fundacionId = e.sistema.fundaciones.firstOrNull { !it.isVerificado && it.estadoCuenta == "ACTIVA" }?.idFundacion ?: -1
                    }
                    e.ir(route)
                },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier.background(
                        if (selected && e.version == 1) Verde else Color.Transparent,
                        androidx.compose.foundation.shape.CircleShape
                    ).padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Icono(icon, Modifier.size(21.dp), if (selected) VerdeAccion else VerdeSuave)
                }
                Text(name, fontSize = 9.sp, color = if (selected) Bosque else Gris, maxLines = 1)
            }
        }
    }
}

@Composable fun Recorrido(e: EstadoApp) {
    Pagina(e, "Recorrido completo") {
        Titulo("Explora FundFind", true)
        Aviso("Sesión de demostración local. Puedes probar todos los roles y las dos versiones sin iniciar sesión.")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Donador" to Rutas.V2_INICIO, "Fundación" to Rutas.V2_FUNDACION, "Admin" to Rutas.V2_ADMIN).forEach { (name, r) ->
                Button(onClick = { e.visitar(r) }, modifier = Modifier.weight(1f)) { Text(name, fontSize = 11.sp) }
            }
        }
        listOf(1, 2).forEach { version ->
            Titulo("Versión $version · ${if (version == 1) "fila superior" else "fila inferior"}")
            Rutas.entries.filter { it.version == version }.forEach { r ->
                OutlinedButton(onClick = { e.visitar(r) }, modifier = Modifier.fillMaxWidth()) {
                    Text(r.titulo, modifier = Modifier.weight(1f))
                }
            }
        }
        Accion("Salir del modo invitado", false) { e.salir() }
    }
}

@Composable fun DescargarComprobante(e: EstadoApp, label: String) {
    val context = LocalContext.current
    val d = e.donacion
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null && d != null) e.ejecutar {
            val pdf = android.graphics.pdf.PdfDocument()
            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdf.startPage(pageInfo)
            val canvas = page.canvas

            val paintFondo = android.graphics.Paint().apply { color = android.graphics.Color.parseColor("#FAF9F3") }
            val paintBosque = android.graphics.Paint().apply { color = android.graphics.Color.parseColor("#143E36"); isAntiAlias = true }
            val paintVerde = android.graphics.Paint().apply { color = android.graphics.Color.parseColor("#B9DF42"); isAntiAlias = true }
            val paintBlanco = android.graphics.Paint().apply { color = android.graphics.Color.WHITE; isAntiAlias = true }
            val paintBorde = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#DDDFD8")
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = 2f
                isAntiAlias = true
            }

            canvas.drawRect(0f, 0f, 595f, 842f, paintFondo)
            canvas.drawRect(0f, 0f, 595f, 120f, paintBosque)
            val txtLogo = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE; textSize = 28f; isFakeBoldText = true; isAntiAlias = true
            }
            val txtLogoVerde = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#B9DF42"); textSize = 28f; isFakeBoldText = true; isAntiAlias = true
            }
            val txtSubHeader = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#E3F1E8"); textSize = 13f; isAntiAlias = true
            }
            canvas.drawText("Fund", 48f, 62f, txtLogo)
            canvas.drawText("Find", 116f, 62f, txtLogoVerde)
            canvas.drawText("Comprobante Oficial de Intención de Ayuda", 48f, 90f, txtSubHeader)

            canvas.drawCircle(297.5f, 190f, 36f, paintVerde)
            val paintCheck = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#143E36")
                strokeWidth = 5f
                style = android.graphics.Paint.Style.STROKE
                strokeCap = android.graphics.Paint.Cap.ROUND
                isAntiAlias = true
            }
            canvas.drawLine(282f, 190f, 293f, 202f, paintCheck)
            canvas.drawLine(293f, 202f, 314f, 178f, paintCheck)

            val txtTitulo = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#143E36"); textSize = 24f
                isFakeBoldText = true; textAlign = android.graphics.Paint.Align.CENTER; isAntiAlias = true
            }
            val txtNota = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#717A75"); textSize = 13f
                textAlign = android.graphics.Paint.Align.CENTER; isAntiAlias = true
            }
            canvas.drawText("¡Gracias por ayudar!", 297.5f, 260f, txtTitulo)
            canvas.drawText("Registramos tu intención de apoyo. La fundación se pondrá en contacto contigo.", 297.5f, 285f, txtNota)

            val cardRect = android.graphics.RectF(48f, 320f, 547f, 620f)
            canvas.drawRoundRect(cardRect, 20f, 20f, paintBlanco)
            canvas.drawRoundRect(cardRect, 20f, 20f, paintBorde)

            val txtLabel = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#717A75"); textSize = 13f; isAntiAlias = true
            }
            val txtValor = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#143E36"); textSize = 14f
                isFakeBoldText = true; textAlign = android.graphics.Paint.Align.RIGHT; isAntiAlias = true
            }

            val fechaFmt = d.fecha?.let {
                java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(it)
            }.orEmpty()

            val filas = listOf(
                "Folio de comprobante" to d.comprobante.orEmpty(),
                "Fundación" to d.solicitud?.fundacion?.nombre.orEmpty(),
                "Solicitud" to d.solicitud?.titulo.orEmpty(),
                "Categoría" to d.tipoDonativo.orEmpty(),
                "Aportación" to d.cantidadDescripcion,
                "Donador" to d.nombreContacto,
                "Contacto" to d.contacto,
                "Fecha de registro" to fechaFmt
            )

            var y = 365f
            filas.forEach { (k, v) ->
                canvas.drawText(k, 76f, y, txtLabel)
                canvas.drawText(v.take(38), 519f, y, txtValor)
                y += 32f
            }

            val pie = if (e.invitado) "DEMONSTRACIÓN LOCAL — Datos de recorrido invitado."
            else "Documento generado por FundFind · Registro local de intención de apoyo."
            canvas.drawText(pie, 297.5f, 760f, txtNota)

            pdf.finishPage(page)
            requireNotNull(context.contentResolver.openOutputStream(uri)).use { out ->
                pdf.writeTo(out)
            }
            pdf.close()
            e.aviso = "Comprobante PDF guardado correctamente."
        }
    }
    Accion(label) {
        if (d != null) launcher.launch("FundFind-${d.comprobante}.pdf")
        else e.aviso = "Selecciona una intención en tu historial."
    }
}