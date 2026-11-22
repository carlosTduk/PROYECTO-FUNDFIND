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

@Composable fun Bienvenida(e: EstadoApp, version: Int) {
    BoxWithConstraints(Modifier.fillMaxSize().background(Bosque)) {
        val alto=maxHeight
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).heightIn(min=alto)) {
            Column(Modifier.padding(24.dp),verticalArrangement=Arrangement.spacedBy(24.dp)) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)){Logo(true)}
                    TextButton(onClick={e.version=if(version==1)2 else 1;e.ir(e.destino("BIENVENIDA"),true)}){
                        Text("V$version ⇄",color=Verde,fontSize=12.sp)
                    }
                }
                Ilustracion(version)
            }
            Column(Modifier.fillMaxWidth().weight(1f).background(if(version==2) Fondo else Bosque,
                RoundedCornerShape(topStart=28.dp,topEnd=28.dp)).padding(24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                Text("Donde la intención de ayudar encuentra una necesidad real",fontSize=26.sp,lineHeight=33.sp,
                    fontWeight=FontWeight.Bold,color=if(version==1)Color.White else Bosque)
                Nota(if(version==1)"Conecta con fundaciones verificadas y conoce lo que realmente necesitan." else
                    "Conoce lo que necesitan las fundaciones verificadas y decide cómo ayudar.",if(version==1)Menta else Gris)
                Spacer(Modifier.height(32.dp))
                Spacer(Modifier.weight(1f))
                Accion("Crear cuenta"){e.ir(e.destino("REGISTRO"))}
                Accion("Iniciar sesión",false){e.ir(e.destino("LOGIN"))}
                Accion("Ingresar como invitado",false){e.entrarInvitado()}
            }
        }
    }
}

@Composable fun Login(e: EstadoApp, version: Int, mostrarError: Boolean = false) {
    var correo by rememberSaveable{mutableStateOf("")}
    var clave by remember{mutableStateOf("")}
    var recordar by rememberSaveable{mutableStateOf(false)}
    var error by rememberSaveable{mutableStateOf(mostrarError)}
    var social by remember{mutableStateOf(false)}
    val prefs=androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("acceso",0)
    LaunchedEffect(Unit) { correo=prefs.getString("correo","").orEmpty();recordar=correo.isNotEmpty() }
    Pagina(e) {
        Enlace("← Volver al inicio"){e.ir(e.destino("BIENVENIDA"))}
        if(version==1)Logo() else Box(Modifier.fillMaxWidth(),contentAlignment=Alignment.Center){
            Box(Modifier.background(Menta,RoundedCornerShape(12.dp)).padding(16.dp)){Icono("corazon",Modifier.size(26.dp))}
        }
        Titulo("Qué gusto verte de nuevo",true,version==2)
        Nota("Ingresa tus datos para continuar ayudando.",centro=version==2)
        if(error && version==1)Aviso("Correo o contraseña incorrectos. Revisa tus datos e inténtalo de nuevo.",true)
        Campo("Correo electrónico",correo,"nombre@correo.com",{correo=it},keyboard=KeyboardType.Email)
        Campo("Contraseña",clave,"Mínimo 8 caracteres",{clave=it},password=true)
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            Checkbox(recordar,{recordar=it});Text("Recordarme",fontSize=11.sp,color=Gris,modifier=Modifier.weight(1f))
            Enlace("¿Olvidaste tu contraseña?"){e.ir(Rutas.RECUPERAR)}
        }
        if(error && version==2)Aviso("Correo o contraseña incorrectos. Revisa tus datos e inténtalo de nuevo.",true)
        Accion("Iniciar sesión"){
            error=!e.login(correo,clave)
            if(!error)prefs.edit().putString("correo",if(recordar)correo else "").apply()
        }
        Accion("Ingresar como invitado",false){e.entrarInvitado()}
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            HorizontalDivider(Modifier.weight(1f));Text("O CONTINÚA CON",fontSize=9.sp,color=Gris);HorizontalDivider(Modifier.weight(1f))
        }
        Accion("Continuar con Google",false){social=true}
        Enlace("¿Aún no tienes cuenta? Regístrate gratis"){e.ir(e.destino("REGISTRO"))}
        Nota("Las fundaciones ingresan con su correo y RFC, como en el proyecto original.")
    }
    if(social)AccesoSocial(e){social=false}
}

@Composable fun Registro(e: EstadoApp, version: Int) {
    var nombre by rememberSaveable{mutableStateOf("")}
    var correo by rememberSaveable{mutableStateOf("")}
    var clave by remember{mutableStateOf("")}
    var lugar by rememberSaveable{mutableStateOf("")}
    var aceptar by rememberSaveable{mutableStateOf(false)}
    var social by remember{mutableStateOf(false)}
    var error by rememberSaveable{mutableStateOf<String?>(null)}
    Pagina(e) {
        Enlace("← Volver al inicio"){e.ir(e.destino("BIENVENIDA"))}
        Titulo("Crea tu cuenta de donador",true)
        if(version==2)Nota("Encuentra fundaciones verificadas y apoya una causa real.")
        Campo("Nombre completo",nombre,if(version==1)"Ej. Laura Gómez" else "Ej. María Fernanda Gómez",{nombre=it})
        Campo("Correo electrónico",correo,"nombre@correo.com",{correo=it},keyboard=KeyboardType.Email)
        Campo("Contraseña",clave,"Mínimo 8 caracteres",{clave=it},password=true)
        Campo("Ubicación",lugar,"Ciudad o municipio",{lugar=it})
        Row(verticalAlignment=Alignment.CenterVertically) {
            Checkbox(aceptar,{aceptar=it})
            Column { Nota("Acepto los términos y condiciones y el aviso de privacidad de FundFind.")
                Enlace("Leer condiciones de uso local") {e.aviso="Esta aplicación guarda los registros en este dispositivo. No envía pagos ni mensajes a un servidor. El modo invitado utiliza datos de demostración separados. Puedes eliminar tu cuenta desde tu perfil."}
            }
        }
        error?.let{Aviso(it,true)}
        Accion("Crear cuenta") {
            if(!aceptar)error="Acepta las condiciones para crear la cuenta."
            else {error=null;e.registrar(nombre,correo,clave,lugar)}
        }
        Accion("Ingresar como invitado",false){e.entrarInvitado()}
        if(version==1)Nota("O regístrate con",centro=true)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            Accion("Google",false,Modifier.weight(1f)){social=true}
            Accion("Facebook",false,Modifier.weight(1f)){social=true}
        }
        Enlace("¿Ya tienes cuenta? Inicia sesión"){e.ir(e.destino("LOGIN"))}
    }
    if(social)AccesoSocial(e){social=false}
}

@Composable private fun AccesoSocial(e: EstadoApp, dismiss: () -> Unit) {
    AlertDialog(onDismissRequest=dismiss,title={Text("Acceso local")},
        text={Text("Google y Facebook requieren un proveedor de autenticación que este proyecto local no tiene configurado. Puedes crear una cuenta local o recorrer toda la aplicación como invitado.")},
        confirmButton={TextButton(onClick={dismiss();e.entrarInvitado()}){Text("Ingresar como invitado")}},
        dismissButton={TextButton(onClick={dismiss();e.ir(e.destino("REGISTRO"))}){Text("Crear cuenta local")}})
}
