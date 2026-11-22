package com.example.fundfind.layout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fundfind.logic.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

@Composable fun ResumenFundacion(e: EstadoApp) {
    val f=e.fundacion
    val propias=e.sistema.getSolicitudesDeFundacion(f).filterNotNull()
    val intenciones=e.sistema.donaciones.filter{it.solicitud?.fundacion===f}
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().background(Bosque).padding(20.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            Avatar(f?.nombre.orEmpty())
            Column(Modifier.weight(1f)){Text(f?.nombre.orEmpty(),color=Color.White,fontWeight=FontWeight.Bold,fontSize=15.sp);Nota(if(f?.isVerificado==true)"Organización verificada"else"Pendiente de verificación",Verde)}
            BotonIcono("avisos","Notificaciones de la fundación",Menta){e.ir(Rutas.AVISOS)}
        }
        Box(Modifier.weight(1f)){Pagina(e,nav=true) {
            Titulo("Resumen de tu fundación")
            Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                Estadistica(propias.count{it.activa()}.toString(),"solicitudes activas",Modifier.weight(1f),Color(0xFFEDF5C8))
                Estadistica(propias.count{it.estado=="BORRADOR"||it.reportes>0}.toString(),"pendientes de revisión",Modifier.weight(1f))
            }
            Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                Estadistica(intenciones.filter{it.estado!="CANCELADA"}.map{it.usuario.idUsuario}.distinct().size.toString(),"personas interesadas",Modifier.weight(1f))
                Estadistica(propias.size.toString(),"publicaciones en historial",Modifier.weight(1f))
            }
            Accion("Publicar nueva solicitud"){e.editarSolicitud=false;e.ir(Rutas.V2_CREAR)}
            Row(verticalAlignment=Alignment.CenterVertically){Text("Historial reciente",fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f));Enlace("Ver todo"){e.ir(Rutas.MIS_SOLICITUDES)}}
            if(propias.isEmpty())Aviso("Publica tu primera solicitud para recibir intenciones de ayuda.")
            propias.takeLast(4).reversed().forEach{s->Tarjeta(Modifier.clickable{e.solicitudId=s.idSolicitud;e.ir(Rutas.MIS_SOLICITUDES)}) {
                Text(s.titulo.orEmpty(),fontSize=14.sp,fontWeight=FontWeight.SemiBold);Nota("Vence ${fecha(s)}")
                Etiqueta(if(s.activa())"Activa"else if(s.estado=="PUBLICADA")"Vencida"else s.estado.orEmpty())
            }}
        }}
    }
}

@Composable fun CrearSolicitud(e: EstadoApp) {
    val original=if(e.editarSolicitud)e.solicitud else null
    var titulo by rememberSaveable{mutableStateOf(original?.titulo.orEmpty())}
    var tipo by rememberSaveable{mutableStateOf(original?.tipoDonacion.orEmpty())}
    var cantidad by rememberSaveable{mutableStateOf(original?.cantidad.orEmpty())}
    var limite by rememberSaveable{mutableStateOf(original?.fechaLimite?.format(DateTimeFormatter.ofPattern("dd/MM/uuuu")).orEmpty())}
    var lugar by rememberSaveable{mutableStateOf(original?.ubicacion?:e.fundacion?.direccion.orEmpty())}
    var descripcion by rememberSaveable{mutableStateOf(original?.descripcion.orEmpty())}
    var causa by rememberSaveable{mutableStateOf(original?.causaSocial?:e.fundacion?.categoria.orEmpty())}
    var error by rememberSaveable{mutableStateOf<String?>(null)}
    fun guardar(publicar: Boolean) {
        val f=e.fundacion?:return
        val fecha=if(limite.isBlank())null else runCatching{LocalDate.parse(limite,DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT))}.getOrNull()
        error=when {
            titulo.isBlank()->"Escribe el título de la solicitud."
            limite.isNotBlank()&&fecha==null->"Introduce una fecha válida en formato dd/mm/aaaa."
            publicar&&(tipo.isBlank()||cantidad.isBlank()||lugar.isBlank()||descripcion.isBlank()||causa.isBlank())->"Completa todos los campos antes de publicar."
            publicar&&(fecha==null||!fecha.isAfter(LocalDate.now()))->"La fecha debe ser posterior a hoy."
            else->null
        }
        if(error!=null)return
        if(e.cambiar {
            val s=original ?: if(publicar)requireNotNull(e.sistema.publicarSolicitud(f,titulo,descripcion,tipo,causa))
                else Solicitud((e.sistema.solicitudes.maxOfOrNull{it.idSolicitud}?:0)+1,titulo,descripcion,tipo,causa,"BORRADOR",null,f).also{e.sistema.solicitudes.add(it)}
            require(s.fundacion===f){"Solo puedes editar solicitudes de tu fundación."}
            s.titulo=titulo.trim();s.tipoDonacion=tipo;s.cantidad=cantidad.trim();s.fechaLimite=fecha;s.ubicacion=lugar.trim();s.descripcion=descripcion.trim();s.causaSocial=causa.trim()
            if(original!=null){f.editarSolicitud(s);if(publicar)s.publicar()}
            e.solicitudId=s.idSolicitud
        }){e.editarSolicitud=false;e.ir(if(publicar)Rutas.V2_PUBLICADA else Rutas.MIS_SOLICITUDES)}
    }
    Pagina(e,if(original==null)"Nueva solicitud"else"Editar solicitud",footer={
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            Accion(if(original?.activa()==true)"Guardar cambios"else"Guardar borrador",false,Modifier.weight(1f)){guardar(false)}
            Accion("Publicar solicitud",true,Modifier.weight(1f), confirmacion="¿Deseas publicar esta solicitud?"){guardar(true)}
        }
    }) {
        Campo("Título",titulo,"Ej. Despensas para 120 familias",{titulo=it})
        Selector("Tipo de donativo",tipo,listOf("Comida","Ropa","Útiles escolares","Higiene","Voluntariado","Otro")){tipo=it}
        Campo("Cantidad requerida",cantidad,"Ej. 120 despensas",{cantidad=it})
        Campo("Fecha límite",limite,"dd/mm/aaaa",{limite=it})
        error?.let{Aviso(it,true)}
        Campo("Ubicación",lugar,"Dirección o punto de referencia",{lugar=it})
        Campo("Descripción",descripcion,"Describe cómo ayudará esta donación",{descripcion=it},multiline=true)
        Campo("Causa social",causa,"Ej. Seguridad alimentaria",{causa=it})
    }
}

@Composable fun Publicada(e: EstadoApp) {
    Pagina(e) {
        Spacer(Modifier.height(84.dp))
        Box(Modifier.fillMaxWidth(),contentAlignment=Alignment.Center){Box(Modifier.size(80.dp).background(Verde,CircleShape),contentAlignment=Alignment.Center){Icono("check",Modifier.size(36.dp))}}
        Spacer(Modifier.height(12.dp));Titulo("¡Solicitud publicada!",true,true)
        Nota("Ya es visible para los donadores. Te avisaremos cuando alguien quiera ayudar.",centro=true)
        Spacer(Modifier.height(32.dp));Accion("Ver mis solicitudes"){e.ir(Rutas.MIS_SOLICITUDES)}
        Accion("Publicar otra solicitud",false){e.editarSolicitud=false;e.ir(Rutas.V2_CREAR)}
    }
}

@Composable fun PanelAdmin(e: EstadoApp) {
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().background(Bosque).padding(20.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.weight(1f)){Logo(true)};Text("Admin",color=Verde,fontSize=12.sp)}
        Box(Modifier.weight(1f)){Pagina(e,nav=true) {
            Titulo("Panel administrativo")
            val pendientes=e.sistema.fundaciones.filter{!it.isVerificado&&it.estadoCuenta=="ACTIVA"}
            Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                Estadistica(pendientes.size.toString(),"organizaciones por verificar",Modifier.weight(1f),Color(0xFFEDF5C8))
                Estadistica(e.sistema.solicitudesActivas.size.toString(),"publicaciones activas",Modifier.weight(1f))
            }
            Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                Estadistica(e.sistema.solicitudes.count{it.reportes>0&&it.estado!="ELIMINADA"}.toString(),"alertas por revisar",Modifier.weight(1f),Melocoton,Terracota)
                Estadistica(e.sistema.fundaciones.count{it.isVerificado}.toString(),"fundaciones verificadas",Modifier.weight(1f))
            }
            Text("Pendientes de verificación",fontWeight=FontWeight.Bold)
            if(pendientes.isEmpty())Aviso("No hay fundaciones pendientes de verificación.")
            pendientes.forEach{f->Tarjeta {
                Text(f.nombre.orEmpty(),fontSize=14.sp,fontWeight=FontWeight.SemiBold)
                val registro = f.fechaRegistro?.let { java.text.SimpleDateFormat("dd/MM/yyyy",java.util.Locale.getDefault()).format(it) }.orEmpty()
                Nota("Registrada $registro")
                Accion("Revisar",false){e.fundacionId=f.idFundacion;e.ir(Rutas.V2_VERIFICAR)}
            }}
        }}
    }
}

@Composable fun Verificar(e: EstadoApp) {
    val f=e.sistema.fundaciones.firstOrNull{it.idFundacion==e.fundacionId}
    var rechazar by remember{mutableStateOf(false)}
    Pagina(e,"Verificar fundación",footer=if(f!=null){{
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            Accion("Rechazar",false,Modifier.weight(1f)){rechazar=true}
            Accion("Aprobar",true,Modifier.weight(1f), confirmacion="¿Deseas aprobar la verificación de ${f.nombre}?"){if(e.cambiar{check(e.invitado&&e.rol=="admin");e.sistema.verificar(f,true,"Administrador invitado")})e.ir(Rutas.V2_ADMIN)}
        }
    }}else null) {
        if(f==null)Aviso("No hay fundaciones pendientes de revisión.")else {
            Titulo(f.nombre.orEmpty());Nota(f.categoria.orEmpty());Etiqueta(if(f.isVerificado)"Verificada"else"Pendiente",if(f.isVerificado)Menta else Melocoton)
            Tarjeta {Dato("RFC",f.rfc.orEmpty());Dato("Correo",f.correoContacto.orEmpty());Dato("Dirección",f.direccion.orEmpty());Dato("Ayuda que ofrece",f.categoria.orEmpty())}
            Text("Documentos enviados",fontWeight=FontWeight.Bold)
            listOf("RFC.pdf","Acta constitutiva.pdf").forEach{doc->Accion("$doc     Ver",false){e.documento=doc;e.ir(Rutas.DOCUMENTO)}}
            Aviso("Al aprobar o rechazar se registrará la fecha y la persona responsable.")
            f.fechaVerificacion?.let{Nota("Última revisión: $it · ${f.responsableVerificacion}")}
        }
    }
    if(rechazar&&f!=null)AlertDialog(onDismissRequest={rechazar=false},title={Text("Rechazar verificación")},text={Text("La fundación ${f.nombre} quedará marcada como rechazada en este recorrido local.")},
        confirmButton={TextButton(onClick={rechazar=false;if(e.cambiar{check(e.invitado&&e.rol=="admin");e.sistema.verificar(f,false,"Administrador invitado")})e.ir(Rutas.V2_ADMIN)}){Text("Rechazar")}},
        dismissButton={TextButton(onClick={rechazar=false}){Text("Cancelar")}})
}

@Composable fun Moderar(e: EstadoApp) {
    var retirar by remember{mutableStateOf<Solicitud?>(null)}
    Pagina(e,"Moderación",nav=true) {
        Titulo("Solicitudes reportadas")
        val lista=e.sistema.solicitudes.filter{it.reportes>0&&it.estado!="ELIMINADA"}
        if(lista.isEmpty())Aviso("No hay publicaciones pendientes de moderación.")
        lista.forEach{s->Tarjeta {
            Text(s.titulo.orEmpty(),fontSize=14.sp,fontWeight=FontWeight.SemiBold);Nota("${s.reportes} reportes · ${s.fundacion?.nombre}");Etiqueta(s.motivoReporte,Melocoton)
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                Accion("Revisar",false,Modifier.weight(1f)){e.ver(s)}
                Accion("Aprobar",true,Modifier.weight(1f), confirmacion="¿Deseas aprobar esta publicación?"){e.cambiar{check(e.invitado&&e.rol=="admin");e.sistema.moderar(s,true)}}
                Accion("Eliminar",false,Modifier.weight(1f)){retirar=s}
            }
        }}
    }
    retirar?.let{s->AlertDialog(onDismissRequest={retirar=null},title={Text("Retirar publicación")},text={Text("Se retirará «${s.titulo}» del listado público. Se conservará su historial.")},
        confirmButton={TextButton(onClick={e.cambiar{check(e.invitado&&e.rol=="admin");e.sistema.moderar(s,false)};retirar=null}){Text("Retirar")}},dismissButton={TextButton(onClick={retirar=null}){Text("Cancelar")}})}
}

@Composable fun Reportes(e: EstadoApp) {
    val solicitudes=e.sistema.solicitudes.filter{e.rol=="admin"||it.fundacion===e.fundacion}
    val donaciones=e.sistema.donaciones.filter{e.rol=="admin"||it.solicitud?.fundacion===e.fundacion}
    Pagina(e,if(e.rol=="admin")"Reportes básicos"else"Impacto de tu fundación",nav=true) {
        if(e.rol=="admin")Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            Estadistica(e.sistema.fundaciones.size.toString(),"fundaciones registradas",Modifier.weight(1f))
            Estadistica(e.sistema.fundaciones.count{it.isVerificado}.toString(),"organizaciones verificadas",Modifier.weight(1f),Color(0xFFEDF5C8))
        }
        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            Estadistica(solicitudes.count{it.activa()}.toString(),"solicitudes activas",Modifier.weight(1f))
            Estadistica(donaciones.count{it.estado!="CANCELADA"}.toString(),"intenciones de donación",Modifier.weight(1f))
        }
        Tarjeta {
            Text("Intenciones de donación por mes",fontSize=13.sp,fontWeight=FontWeight.SemiBold)
            val meses=(4 downTo 0).map{java.time.YearMonth.now().minusMonths(it.toLong())}
            val valores=meses.map{m->donaciones.count{d->d.estado!="CANCELADA"&&d.fecha?.toInstant()?.atZone(ZoneId.systemDefault())?.let{java.time.YearMonth.from(it)}==m}}
            val max=(valores.maxOrNull()?:0).coerceAtLeast(1)
            Row(Modifier.fillMaxWidth().height(160.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalAlignment=Alignment.Bottom) {
                meses.forEachIndexed{i,m->Column(Modifier.weight(1f),horizontalAlignment=Alignment.CenterHorizontally) {
                    Text(valores[i].toString(),fontSize=11.sp)
                    Box(Modifier.fillMaxWidth().height((valores[i]*115f/max).coerceAtLeast(2f).dp).background(if(i==4)Verde else Bosque,androidx.compose.foundation.shape.RoundedCornerShape(7.dp)))
                    Text(m.format(DateTimeFormatter.ofPattern("MMM",java.util.Locale.forLanguageTag("es"))),fontSize=10.sp)
                }}
            }
        }
        Nota("El reporte se calcula con los registros de esta sesión local.")
    }
}
