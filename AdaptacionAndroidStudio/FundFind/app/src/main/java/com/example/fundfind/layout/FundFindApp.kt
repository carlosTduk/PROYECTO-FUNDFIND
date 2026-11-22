package com.example.fundfind.layout

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
                if (e.invitado) Row(Modifier.fillMaxWidth().background(Menta).padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("Invitado · ${e.rol}", fontSize = 11.sp, modifier = Modifier.weight(1f))
                    TextButton(onClick = { e.ir(Rutas.RECORRIDO) }, contentPadding = PaddingValues(4.dp)) { Text("Recorrido completo", fontSize = 11.sp) }
                }
                key(e.ruta) {
                    Box(Modifier.weight(1f)) {
                        when (e.ruta) {
                            Rutas.V1_BIENVENIDA -> Bienvenida(e,1)
                            Rutas.V2_BIENVENIDA -> Bienvenida(e,2)
                            Rutas.V1_REGISTRO -> Registro(e,1)
                            Rutas.V2_REGISTRO -> Registro(e,2)
                            Rutas.V1_LOGIN -> Login(e,1)
                            Rutas.V2_LOGIN -> Login(e,2)
                            Rutas.V2_ERROR -> Login(e,2,true)
                            Rutas.V1_INICIO -> Inicio(e,1)
                            Rutas.V2_INICIO -> Inicio(e,2)
                            Rutas.V1_EXPLORAR -> Explorar(e,1)
                            Rutas.V2_EXPLORAR -> Explorar(e,2)
                            Rutas.V1_VACIO -> Explorar(e,1)
                            Rutas.V2_VACIO -> Explorar(e,2)
                            Rutas.V1_DETALLE -> Detalle(e,1)
                            Rutas.V2_DETALLE -> Detalle(e,2)
                            Rutas.V1_DONAR -> Donar(e,1)
                            Rutas.V2_DONAR -> Donar(e,2)
                            Rutas.V1_CONFIRMACION -> Confirmacion(e,1)
                            Rutas.V2_CONFIRMACION -> Confirmacion(e,2)
                            Rutas.V1_PERFIL -> Perfil(e,1)
                            Rutas.V2_PERFIL -> Perfil(e,2)
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
        e.aviso?.let { texto -> AlertDialog(onDismissRequest = {e.aviso=null}, title={Text("FundFind")}, text={Text(texto)},
            confirmButton={TextButton(onClick={e.aviso=null}){Text("Entendido")}}) }
    }
}

@Composable fun Cabecera(e: EstadoApp, titulo: String, dark: Boolean = e.version == 2, volver: Boolean = true) {
    Row(Modifier.fillMaxWidth().background(if(dark) Bosque else Fondo).padding(horizontal=8.dp,vertical=4.dp),verticalAlignment=Alignment.CenterVertically) {
        if(volver) BotonIcono("atras","Volver",if(dark) Menta else VerdeAccion) {e.atras()}
        Text(titulo,color=if(dark) Color.White else Bosque,fontSize=16.sp,fontWeight=FontWeight.SemiBold,modifier=Modifier.weight(1f))
    }
}

@Composable fun Pagina(e: EstadoApp, title: String? = null, nav: Boolean = false,
                      footer: (@Composable ColumnScope.() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize()) {
        if(title!=null) Cabecera(e,title)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp),content=content)
        if(footer!=null) Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp),content=footer)
        if(nav) Navegacion(e)
    }
}

@Composable fun Navegacion(e: EstadoApp) {
    val items = when(e.rol) {
        "admin" -> listOf(Triple("Panel","corazon",Rutas.V2_ADMIN),Triple("Verificar","check",Rutas.V2_VERIFICAR),Triple("Moderar","bandera",Rutas.V2_MODERAR),Triple("Reportes","reportes",Rutas.V2_REPORTES))
        "fundacion" -> listOf(Triple("Resumen","inicio",Rutas.V2_FUNDACION),Triple("Solicitudes","lista",Rutas.MIS_SOLICITUDES),Triple("Interesados","personas",Rutas.INTERESADOS),Triple("Impacto","reportes",Rutas.V2_REPORTES),Triple("Perfil","perfil",Rutas.PERFIL_FUNDACION))
        else -> listOf(Triple("Inicio","inicio",e.destino("INICIO")),Triple("Explorar","explorar",e.destino("EXPLORAR")),Triple("Favoritos","corazon",Rutas.FAVORITOS),Triple(if(e.version==1)"Notificaciones" else "Avisos","avisos",Rutas.AVISOS),Triple("Perfil","perfil",e.destino("PERFIL")))
    }
    Row(Modifier.fillMaxWidth().background(Color.White).padding(vertical=6.dp)) {
        items.forEach { (name,icon,route) ->
            val selected = e.ruta==route
            Column(Modifier.weight(1f).heightIn(min=48.dp).clickable {
                if(route==Rutas.V2_VERIFICAR) e.fundacionId=e.sistema.fundaciones.firstOrNull{!it.isVerificado && it.estadoCuenta=="ACTIVA"}?.idFundacion ?: -1
                e.ir(route)
            },horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
                Box(Modifier.background(if(selected&&e.version==1) Verde else Color.Transparent,androidx.compose.foundation.shape.CircleShape).padding(horizontal=10.dp,vertical=2.dp)) {
                    Icono(icon,Modifier.size(21.dp),if(selected) VerdeAccion else VerdeSuave)
                }
                Text(name,fontSize=9.sp,color=if(selected)Bosque else Gris,maxLines=1)
            }
        }
    }
}

@Composable fun Recorrido(e: EstadoApp) {
    Pagina(e,"Recorrido completo") {
        Titulo("Explora FundFind",true)
        Aviso("Sesión de demostración local. Puedes probar todos los roles y las dos versiones sin iniciar sesión.")
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            listOf("Donador" to Rutas.V2_INICIO,"Fundación" to Rutas.V2_FUNDACION,"Admin" to Rutas.V2_ADMIN).forEach { (name,r) ->
                Button(onClick={e.visitar(r)},modifier=Modifier.weight(1f)){Text(name,fontSize=11.sp)}
            }
        }
        listOf(1,2).forEach { version ->
            Titulo("Versión $version · ${if(version==1) "fila superior" else "fila inferior"}")
            Rutas.entries.filter{it.version==version}.forEach {r->
                OutlinedButton(onClick={e.visitar(r)},modifier=Modifier.fillMaxWidth()){Text(r.titulo,modifier=Modifier.weight(1f))}
            }
        }
        Accion("Salir del modo invitado",false){e.salir()}
    }
}

@Composable fun DescargarComprobante(e: EstadoApp, label: String) {
    val context=LocalContext.current
    val d=e.donacion
    val launcher=androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        if(uri!=null && d!=null) e.ejecutar {
            val texto="""FundFind — Comprobante de intención de ayuda
Folio: ${d.comprobante}
Fundación: ${d.solicitud?.fundacion?.nombre}
Solicitud: ${d.solicitud?.titulo}
Apoyo: ${d.tipoDonativo}
Cantidad: ${d.cantidadDescripcion}
Contacto: ${d.nombreContacto} · ${d.contacto}
Estado: ${d.estado}
Fecha: ${d.fecha}
${if(e.invitado) "DEMONSTRACIÓN LOCAL — datos de invitado." else "Registro local de intención. No acredita un pago ni una entrega."}
"""
            requireNotNull(context.contentResolver.openOutputStream(uri)).bufferedWriter().use{it.write(texto)}
            e.aviso="Comprobante guardado."
        }
    }
    Accion(label){if(d!=null) launcher.launch("FundFind-${d.comprobante}.txt") else e.aviso="Selecciona una intención en tu historial."}
}
