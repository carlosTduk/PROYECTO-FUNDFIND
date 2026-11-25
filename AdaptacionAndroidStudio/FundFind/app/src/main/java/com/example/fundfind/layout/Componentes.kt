package com.example.fundfind.layout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Bosque = Color(0xFF143E36)
val VerdeAccion = Color(0xFF237A50)
val VerdeSuave = Color(0xFF577568)
val Verde = Color(0xFFB9DF42)
val Fondo = Color(0xFFFAF9F3)
val Menta = Color(0xFFE3F1E8)
val Gris = Color(0xFF717A75)
val Borde = Color(0xFFDDDFD8)
val Melocoton = Color(0xFFFBECE3)
val Terracota = Color(0xFFAB593B)

@Composable
fun TemaFundFind(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(primary = VerdeAccion, onPrimary = Color.White,
        secondary = Verde, background = Fondo, surface = Fondo, onSurface = Bosque,
        primaryContainer = Menta, onPrimaryContainer = Bosque,
        secondaryContainer = Menta, onSecondaryContainer = Bosque,
        surfaceVariant = Menta, outline = Borde, error = Terracota),
        typography = Typography(bodyLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
            bodyMedium = TextStyle(fontSize = 12.sp, lineHeight = 17.sp),
            titleLarge = TextStyle(fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold)), content = content)
}

@Composable fun Titulo(text: String, grande: Boolean = false, centro: Boolean = false) {
    Text(text, color = Bosque, fontWeight = FontWeight.Bold, fontSize = if (grande) 24.sp else 20.sp,
        lineHeight = if (grande) 30.sp else 25.sp, textAlign = if (centro) TextAlign.Center else TextAlign.Start,
        modifier = Modifier.fillMaxWidth())
}
@Composable fun Nota(text: String, color: Color = Gris, centro: Boolean = false) {
    Text(text, fontSize = 12.sp, lineHeight = 18.sp, color = color,
        textAlign = if (centro) TextAlign.Center else TextAlign.Start,
        modifier = if (centro) Modifier.fillMaxWidth() else Modifier)
}
@Composable fun Accion(text: String, principal: Boolean = true, modifier: Modifier = Modifier, confirmacion: String? = null, onClick: () -> Unit) {
    var pendiente by rememberSaveable { mutableStateOf(false) }
    if (pendiente && confirmacion != null) ConfirmarAccion(text, confirmacion, { pendiente = false }) { pendiente = false; onClick() }
    Button(onClick = { if (confirmacion == null) onClick() else pendiente = true }, modifier = modifier.fillMaxWidth().heightIn(min = 48.dp),
        shape = RoundedCornerShape(12.dp), border = if (principal) null else androidx.compose.foundation.BorderStroke(1.dp, VerdeAccion),
        colors = ButtonDefaults.buttonColors(containerColor = if (principal) VerdeAccion else Color.White, contentColor = if (principal) Color.White else VerdeAccion),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}
@Composable fun Enlace(text: String, confirmacion: String? = null, onClick: () -> Unit) {
    var pendiente by rememberSaveable { mutableStateOf(false) }
    if (pendiente && confirmacion != null) ConfirmarAccion(text, confirmacion, { pendiente = false }) { pendiente = false; onClick() }
    TextButton(onClick = { if (confirmacion == null) onClick() else pendiente = true }, contentPadding = PaddingValues(0.dp)) {
        Text(text, fontSize = 12.sp, color = VerdeAccion)
    }
}

@Composable private fun ConfirmarAccion(titulo: String, mensaje: String, cancelar: () -> Unit, confirmar: () -> Unit) {
    AlertDialog(onDismissRequest = cancelar, containerColor = Fondo,
        titleContentColor = Bosque, textContentColor = Bosque,
        shape = RoundedCornerShape(16.dp),
        title = { Text(titulo) },
        text = { Text(mensaje, modifier = Modifier.heightIn(max = 300.dp).verticalScroll(androidx.compose.foundation.rememberScrollState())) },
        dismissButton = { TextButton(onClick = cancelar) { Text("Cancelar") } },
        confirmButton = { Button(onClick = confirmar, shape = RoundedCornerShape(12.dp)) { Text("Confirmar") } })
}
@Composable fun Campo(label: String, value: String, hint: String, onChange: (String) -> Unit,
                      password: Boolean = false, multiline: Boolean = false, keyboard: KeyboardType = KeyboardType.Text) {
    var visible by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 12.sp, color = Bosque)
        OutlinedTextField(value, onChange, modifier = Modifier.fillMaxWidth(), singleLine = !multiline,
            minLines = if (multiline) 3 else 1,
            placeholder = { Text(hint, fontSize = 13.sp, color = Gris) },
            textStyle = TextStyle(fontSize = 14.sp, color = Bosque), shape = RoundedCornerShape(12.dp),
            visualTransformation = if (password && !visible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = if (password) KeyboardType.Password else keyboard),
            trailingIcon = if (password) {{ TextButton(onClick = { visible = !visible }) { Text(if (visible) "Ocultar" else "Ver", fontSize = 10.sp) } }} else null,
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                unfocusedBorderColor = Borde, focusedBorderColor = VerdeAccion))
    }
}
@Composable fun Selector(label: String, value: String, opciones: List<String>, change: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Column {
        if (label.isNotEmpty()) Text(label, fontSize = 12.sp)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Borde),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)) {
                Text(value.ifEmpty { "Seleccionar" }, modifier = Modifier.weight(1f), textAlign = TextAlign.Start, fontSize = 13.sp)
                Icono("abajo", Modifier.size(16.dp))
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                opciones.forEach { op -> DropdownMenuItem(text = { Text(op) }, onClick = { change(op); open = false }) }
            }
        }
    }
}
@Composable fun Tarjeta(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier = modifier.fillMaxWidth(), color = Color.White, shape = RoundedCornerShape(16.dp), shadowElevation = 2.dp) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}
@Composable fun Aviso(text: String, error: Boolean = false) {
    Text(text, fontSize = 12.sp, lineHeight = 17.sp, color = if (error) Terracota else Bosque,
        modifier = Modifier.fillMaxWidth().background(if (error) Melocoton else Menta, RoundedCornerShape(10.dp)).padding(12.dp))
}
@Composable fun Etiqueta(text: String, color: Color = Menta) {
    Text(text, fontSize = 10.sp, color = if (color == Melocoton) Terracota else Bosque,
        modifier = Modifier.background(color, CircleShape).padding(horizontal = 9.dp, vertical = 4.dp))
}
@Composable fun Avatar(nombre: String, grande: Boolean = false, oscuro: Boolean = false) {
    val iniciales = nombre.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
    Box(Modifier.size(if (grande) 72.dp else 36.dp).background(if (oscuro) Bosque else if (grande) Verde else Menta, CircleShape), contentAlignment = Alignment.Center) {
        Text(iniciales, color = if (oscuro) Color.White else Bosque, fontSize = if (grande) 24.sp else 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
@Composable fun Logo(claro: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.size(30.dp).background(Verde, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) { Icono("corazon", Modifier.size(21.dp)) }
        Text("Fund", color = if (claro) Color.White else Bosque, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("Find", color = if (claro) Verde else Color(0xFF7C9D2A), fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.offset(x = (-8).dp))
    }
}
@Composable fun BotonIcono(icono: String, description: String, color: Color = VerdeAccion, action: () -> Unit) {
    IconButton(onClick = action, modifier = Modifier.semantics { contentDescription = description }) { Icono(icono, Modifier.size(22.dp), color) }
}

@Composable fun Icono(nombre: String, modifier: Modifier = Modifier, color: Color = VerdeAccion) {
    Canvas(modifier) {
        val sx = size.width / 24; val sy = size.height / 24
        fun path(vararg pts: Pair<Float, Float>, close: Boolean = false) {
            val p = Path(); pts.forEachIndexed { i, q -> if (i == 0) p.moveTo(q.first * sx, q.second * sy) else p.lineTo(q.first * sx, q.second * sy) }
            if (close) p.close(); drawPath(p, color, style = Stroke(1.6f * sx))
        }
        when (nombre) {
            "corazon", "favoritos" -> {
                val p = Path().apply { moveTo(12*sx,21*sy); cubicTo(8*sx,17*sy,2*sx,12*sy,2*sx,7*sy); cubicTo(2*sx,1*sy,9*sx,0f,12*sx,6*sy); cubicTo(15*sx,0f,22*sx,1*sy,22*sx,7*sy); cubicTo(22*sx,12*sy,16*sx,17*sy,12*sx,21*sy) }
                drawPath(p, color, style = Stroke(1.6f*sx))
            }
            "buscar" -> { drawCircle(color, 7*sx, Offset(10*sx,10*sy), style=Stroke(1.7f*sx)); path(15f to 15f,22f to 22f) }
            "atras" -> { path(14f to 4f,6f to 12f,14f to 20f); path(6f to 12f,22f to 12f) }
            "check" -> path(3f to 12f,9f to 18f,22f to 4f)
            "abajo" -> path(5f to 9f,12f to 16f,19f to 9f)
            "inicio" -> { path(2f to 10f,12f to 2f,22f to 10f); path(4f to 9f,4f to 22f,20f to 22f,20f to 9f); path(9f to 22f,9f to 13f,15f to 13f,15f to 22f) }
            "perfil", "personas" -> { drawCircle(color,4*sx,Offset(12*sx,6*sy),style=Stroke(1.6f*sx)); val p=Path().apply {moveTo(4*sx,23*sy); lineTo(4*sx,18*sy); cubicTo(4*sx,11*sy,20*sx,11*sy,20*sx,18*sy);lineTo(20*sx,23*sy)};drawPath(p,color,style=Stroke(1.6f*sx)) }
            "avisos" -> { path(3f to 18f,6f to 14f,6f to 8f,8f to 4f,16f to 4f,18f to 8f,18f to 14f,21f to 18f,3f to 18f);path(9f to 22f,15f to 22f) }
            "explorar" -> { drawCircle(color,10*sx,center,style=Stroke(1.6f*sx));path(16f to 7f,14f to 14f,7f to 17f,10f to 10f,close=true) }
            "reportes" -> { path(3f to 2f,3f to 22f,23f to 22f);path(8f to 18f,8f to 12f);path(13f to 18f,13f to 6f);path(18f to 18f,18f to 10f) }
            "bandera" -> {path(4f to 23f,4f to 2f,12f to 2f,15f to 5f,22f to 5f,22f to 15f,14f to 15f,11f to 12f,4f to 12f)}
            else -> {path(4f to 5f,21f to 5f);path(4f to 12f,21f to 12f);path(4f to 19f,21f to 19f)}
        }
    }
}

@Composable fun Ilustracion(version: Int) {
    Box(Modifier.fillMaxWidth().height(if (version == 1) 230.dp else 280.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w=size.width;val h=size.height
            if(version==1) drawRoundRect(Color(0xFF205A4D),size=size,cornerRadius=androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()))
            else drawCircle(Color(0xFF205448),w*.45f,Offset(w/2,h/2))
            rotate(-13f, Offset(w*.68f,h*.46f)) {drawRoundRect(Color(0xFF956046),topLeft=Offset(w*.47f,h*.37f),size=Size(w*.48f,h*.17f),cornerRadius=androidx.compose.ui.geometry.CornerRadius(h*.09f))}
            rotate(14f, Offset(w*.30f,h*.68f)) {drawRoundRect(Color(0xFFDD9271),topLeft=Offset(w*.04f,h*.57f),size=Size(w*.46f,h*.17f),cornerRadius=androidx.compose.ui.geometry.CornerRadius(h*.09f))}
            if(version==1) listOf(.14f to .2f,.23f to .43f,.68f to .15f,.85f to .3f).forEach { drawCircle(Verde.copy(alpha=.6f),w*.014f,Offset(w*it.first,h*it.second)) }
        }
        Box(Modifier.size(72.dp).background(Verde,CircleShape), contentAlignment=Alignment.Center) {Icono("corazon",Modifier.size(30.dp))}
    }
}
