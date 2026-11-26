package com.example.fundfind.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.fundfind.logic.FundFindSistema

@Composable fun Bienvenida(e: EstadoApp, version: Int) {
    BoxWithConstraints(Modifier.fillMaxSize().background(Bosque)) {
        val alto = maxHeight
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).heightIn(min = alto)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) { Logo(true) }
                    TextButton(onClick = { e.version = if (version == 1) 2 else 1; e.ir(e.destino("BIENVENIDA"), true) }) {
                        Text("V$version ⇄", color = Verde, fontSize = 12.sp)
                    }
                }
                Ilustracion(version)
            }
            Column(
                Modifier.fillMaxWidth().weight(1f)
                    .background(if (version == 2) Fondo else Bosque, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Donde la intención de ayudar encuentra una necesidad real",
                    fontSize = 26.sp, lineHeight = 33.sp, fontWeight = FontWeight.Bold,
                    color = if (version == 1) Color.White else Bosque
                )
                Nota(
                    if (version == 1) "Conecta con fundaciones verificadas y conoce lo que realmente necesitan."
                    else "Conoce lo que necesitan las fundaciones verificadas y decide cómo ayudar.",
                    if (version == 1) Menta else Gris
                )
                Spacer(Modifier.height(32.dp))
                Spacer(Modifier.weight(1f))
                Accion("Crear cuenta") { e.ir(e.destino("REGISTRO")) }
                Accion("Iniciar sesión", false) { e.ir(e.destino("LOGIN")) }
                Accion("Ingresar como invitado", false) { e.entrarInvitado() }
            }
        }
    }
}

@Composable fun Login(e: EstadoApp, version: Int, mostrarError: Boolean = false) {
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var recordar by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf(mostrarError) }
    val prefs = androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("acceso", 0)
    LaunchedEffect(Unit) { correo = prefs.getString("correo", "").orEmpty(); recordar = correo.isNotEmpty() }

    Pagina(e) {
        Enlace("← Volver al inicio") { e.ir(e.destino("BIENVENIDA")) }
        if (version == 1) Logo() else Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(Modifier.background(Menta, RoundedCornerShape(12.dp)).padding(16.dp)) { Icono("corazon", Modifier.size(26.dp)) }
        }
        Titulo("Qué gusto verte de nuevo", true, version == 2)
        Nota("Ingresa como donador o fundación.", centro = version == 2)
        if (error && version == 1) Aviso("Correo o contraseña/RFC incorrectos. Revisa tus datos.", true)
        Campo("Correo electrónico", correo, "nombre@correo.com", { correo = it }, keyboard = KeyboardType.Email)
        Campo("Contraseña (o RFC si eres fundación)", clave, "Mínimo 8 caracteres", { clave = it }, password = true)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(recordar, { recordar = it })
            Text("Recordarme", fontSize = 11.sp, color = Gris, modifier = Modifier.weight(1f))
            Enlace("¿Olvidaste tu contraseña?") { e.ir(Rutas.RECUPERAR) }
        }
        if (error && version == 2) Aviso("Correo o contraseña/RFC incorrectos. Revisa tus datos.", true)
        Accion("Iniciar sesión") {
            error = !e.login(correo, clave)
            if (!error) prefs.edit().putString("correo", if (recordar) correo else "").apply()
        }
        Accion("Ingresar como invitado", false) { e.entrarInvitado() }
        Enlace("¿Aún no tienes cuenta? Regístrate como donador o fundación") { e.ir(e.destino("REGISTRO")) }
    }
}

@Composable fun Registro(e: EstadoApp, version: Int) {
    var tipoCuenta by rememberSaveable { mutableStateOf("Donador") }
    var nombre by rememberSaveable { mutableStateOf("") }
    var rfc by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var lugar by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("Alimentos y despensas") }
    var mision by rememberSaveable { mutableStateOf("") }
    var aceptar by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    Pagina(e) {
        Enlace("← Volver al inicio") { e.ir(e.destino("BIENVENIDA")) }
        Titulo(if (tipoCuenta == "Donador") "Crea tu cuenta de donador" else "Registra tu fundación", true)

        // Selector de tipo de usuario
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Accion("Soy Donador", principal = tipoCuenta == "Donador", modifier = Modifier.weight(1f)) {
                tipoCuenta = "Donador"; error = null
            }
            Accion("Soy Fundación", principal = tipoCuenta == "Fundación", modifier = Modifier.weight(1f)) {
                tipoCuenta = "Fundación"; error = null
            }
        }

        if (tipoCuenta == "Donador") {
            Campo("Nombre completo", nombre, "Ej. Camila González", { nombre = it })
            Campo("Correo electrónico", correo, "nombre@correo.com", { correo = it }, keyboard = KeyboardType.Email)
            Campo("Contraseña", clave, "Mínimo 8 caracteres", { clave = it }, password = true)
            Campo("Ubicación", lugar, "Ciudad o municipio", { lugar = it })
        } else {
            Campo("Nombre de la fundación", nombre, "Ej. Fundación Manos que Ayudan", { nombre = it })
            Campo("RFC", rfc, "Ej. FMA010203ABC", { rfc = it })
            Campo("Correo institucional", correo, "contacto@fundacion.org", { correo = it }, keyboard = KeyboardType.Email)
            Campo("Contraseña", clave, "Mínimo 8 caracteres", { clave = it }, password = true)
            Campo("Teléfono de contacto", telefono, "Ej. 3331234567", { telefono = it }, keyboard = KeyboardType.Phone)
            Campo("Dirección exacta de la sede", lugar, "Calle, número, colonia y ciudad", { lugar = it })
            Selector("Categoría principal de apoyo", categoria, FundFindSistema.CATEGORIAS_GENERALES) { categoria = it }
            Campo("Descripción o misión de la fundación", mision, "¿A quién ayudan y cómo trabajan?", { mision = it }, multiline = true)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(aceptar, { aceptar = it })
            Column {
                Nota("Acepto los términos y condiciones y el aviso de privacidad de FundFind.")
                Enlace("Leer condiciones de uso local") {
                    e.aviso = "Esta aplicación guarda los registros en este dispositivo. Puedes administrar tus datos desde tu perfil."
                }
            }
        }
        error?.let { Aviso(it, true) }
        Accion(if (tipoCuenta == "Donador") "Crear cuenta de donador" else "Registrar fundación y publicar") {
            if (!aceptar) {
                error = "Acepta las condiciones para crear la cuenta."
            } else {
                error = null
                if (tipoCuenta == "Donador") {
                    e.registrar(nombre, correo, clave, lugar)
                } else {
                    e.registrarFundacion(nombre, rfc, correo, clave, telefono, lugar, categoria, mision)
                }
            }
        }
        Enlace("¿Ya tienes cuenta? Inicia sesión") { e.ir(e.destino("LOGIN")) }
    }
}