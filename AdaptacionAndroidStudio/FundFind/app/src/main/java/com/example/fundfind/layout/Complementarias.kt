package com.example.fundfind.layout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fundfind.logic.*

@Composable fun Complementaria(e: EstadoApp, revision: Int) {
    when(e.ruta) {
        Rutas.EDITAR_PERFIL->EditarPerfil(e)
        Rutas.PERFIL_FUNDACION->EditarFundacion(e)
        Rutas.MENSAJES->Mensajes(e)
        Rutas.MIS_SOLICITUDES->MisSolicitudes(e)
        Rutas.RESENAS->Resenas(e)
        Rutas.HISTORIAL,Rutas.INTERESADOS->Historial(e)
        Rutas.FAVORITOS->Pagina(e,"Solicitudes guardadas",nav=true) {
            val lista=e.sistema.solicitudes.filter{e.usuario?.favoritos?.contains(it.idSolicitud)==true&&it.estado!="ELIMINADA"}
            if(lista.isEmpty())Aviso("No tienes solicitudes guardadas. Usa el corazón en el detalle de una solicitud.")
            lista.forEach{FichaSolicitud(e,it);Enlace("Quitar de favoritos"){e.favorito(it)}}
        }
        Rutas.AVISOS->Pagina(e,"Notificaciones",nav=true) {
            val lista=if(e.rol=="fundacion")e.fundacion?.verNotificaciones().orEmpty()else e.usuario?.verNotificaciones().orEmpty()
            if(lista.isEmpty())Aviso("No tienes notificaciones pendientes.")
            lista.filterNotNull().reversed().forEach{n->Tarjeta {
                Text(n.mensaje.orEmpty(),fontWeight=FontWeight.SemiBold);Etiqueta(if(n.isLeida)"Leída"else"Nueva")
                Enlace("Abrir") {
                    e.cambiar{n.marcarComoLeida()}
                    when(n.tipo) {
                        "MENSAJE"->{e.mensajeId=n.enlaceRecurso?.toIntOrNull()?:0;e.ir(Rutas.MENSAJES)}
                        "DONACION"->{e.donacionId=n.enlaceRecurso?.toIntOrNull()?:0;e.ir(if(e.rol=="fundacion")Rutas.INTERESADOS else Rutas.HISTORIAL)}
                        "SOLICITUD"->{e.solicitudId=n.enlaceRecurso?.toIntOrNull()?:0;e.ir(e.destino("DETALLE"))}
                        else->e.ir(if(e.rol=="fundacion")Rutas.PERFIL_FUNDACION else e.destino("PERFIL"))
                    }
                }
            }}
            Enlace("Ver mensajes"){e.ir(Rutas.MENSAJES)}
        }
        Rutas.FUNDACIONES->Pagina(e,"Fundaciones",nav=true) {
            var filtro by rememberSaveable{mutableStateOf("")}
            Campo("Buscar",filtro,"Nombre, categoría o ubicación",{filtro=it})
            val lista=e.usuario?.verListadoFundaciones(filtro)?.filterNotNull()?:e.sistema.fundaciones.filter{"${it.nombre} ${it.categoria}".contains(filtro,true)}
            lista.forEach{f->Tarjeta {
                Avatar(f.nombre.orEmpty());Titulo(f.nombre.orEmpty());Nota("${f.categoria} · ${f.direccion}")
                Etiqueta(if(f.isVerificado)"Verificada"else"Pendiente de verificación")
                Accion("Ver solicitudes",false){e.limpiarFiltros();e.busqueda=f.nombre.orEmpty();e.ir(e.destino("EXPLORAR"))}
                Enlace("Contactar fundación"){e.fundacionId=f.idFundacion;e.ir(Rutas.MENSAJES)}
                Enlace("Reseñas"){e.fundacionId=f.idFundacion;e.ir(Rutas.RESENAS)}
            }}
        }
        Rutas.DOCUMENTO->Pagina(e,e.documento) {
            val f=e.organizacion
            Titulo(f?.nombre.orEmpty())
            Aviso("Documento de demostración del recorrido local. No es un documento legal auténtico.")
            Tarjeta {Dato("Documento",e.documento);Dato("Organización",f?.nombre.orEmpty());Dato("RFC",f?.rfc.orEmpty());Dato("Registro",f?.documentacionLegal.orEmpty())}
            Accion("Volver a la verificación",false){e.atras()}
        }
        Rutas.RECUPERAR->Pagina(e,"Acceso local") {
            Titulo("Recuperar el acceso")
            Nota("La versión local no tiene servicio de correo para restablecer contraseñas. Puedes continuar como invitado o crear otra cuenta con un correo diferente.")
            Accion("Ingresar como invitado"){e.entrarInvitado()}
            Accion("Crear cuenta local",false){e.ir(e.destino("REGISTRO"))}
        }
        else->Pagina(e,e.ruta.titulo){Accion("Volver al inicio"){e.ir(e.inicio())}}
    }
}

@Composable private fun Historial(e: EstadoApp) {
    val fundacion=e.ruta==Rutas.INTERESADOS
    val lista=e.sistema.donaciones.filter{if(fundacion)it.solicitud?.fundacion===e.fundacion else it.usuario===e.usuario}
    Pagina(e,if(fundacion)"Personas interesadas"else"Historial de intenciones",nav=true) {
        if(lista.isEmpty())Aviso("No hay intenciones registradas.")
        lista.reversed().forEach{d->
            FilaDonacion(e,d)
            if(fundacion) {Nota("${d.nombreContacto} · ${d.contacto}")
                if(d.estado=="INTENCIONREGISTRADA")Accion("Confirmar recepción de ${d.cantidadDescripcion}", confirmacion="¿Confirmas que recibiste ${d.cantidadDescripcion}?"){e.cambiar{require(d.solicitud?.fundacion===e.fundacion);e.sistema.cambiarDonacion(d,"CONFIRMADA")}}
            }
            if(d.idDonacion==e.donacionId||lista.size==1) {
                Enlace("Ver comprobante"){e.donacionId=d.idDonacion;e.ir(e.destino("CONFIRMACION"))}
                if(d.estado!="CANCELADA")Enlace("Cancelar intención", confirmacion="¿Deseas cancelar esta intención de ayuda?") {e.cambiar{require(if(fundacion)d.solicitud?.fundacion===e.fundacion else d.usuario===e.usuario);e.sistema.cambiarDonacion(d,"CANCELADA")}}
                if(!fundacion)Enlace("Dejar retroalimentación"){e.fundacionId=requireNotNull(d.solicitud?.fundacion).idFundacion;e.ir(Rutas.RESENAS)}
            }
        }
        if(fundacion)Accion("Mensajes",false){e.ir(Rutas.MENSAJES)}
    }
}

@Composable private fun MisSolicitudes(e: EstadoApp) {
    var eliminar by remember{mutableStateOf<Solicitud?>(null)}
    Pagina(e,"Mis solicitudes",nav=true) {
        Accion("Publicar nueva solicitud"){e.editarSolicitud=false;e.ir(Rutas.V2_CREAR)}
        val lista=e.fundacion?.gestionarSolicitudes().orEmpty().filterNotNull()
        if(lista.isEmpty())Aviso("Aún no has publicado solicitudes.")
        lista.reversed().forEach{s->Tarjeta {
            Titulo(s.titulo.orEmpty());Etiqueta(if(s.activa())"PUBLICADA"else if(s.estado=="PUBLICADA")"VENCIDA"else s.estado.orEmpty());Nota("${s.cantidad} · ${fecha(s)}")
            Accion("Ver detalle",false){e.ver(s)}
            if(s.estado!="ELIMINADA") {
                Enlace("Editar"){e.solicitudId=s.idSolicitud;e.editarSolicitud=true;e.ir(Rutas.V2_CREAR)}
                if(s.activa())Enlace("Marcar completada", confirmacion="¿Deseas marcar esta solicitud como completada?"){e.cambiar{require(s.fundacion===e.fundacion);s.marcarCompletada()}}
                Enlace("Eliminar solicitud"){eliminar=s}
            }
        }}
    }
    eliminar?.let{s->AlertDialog(onDismissRequest={eliminar=null},title={Text("Eliminar solicitud")},text={Text("Se retirará «${s.titulo}». Las intenciones y su historial se conservarán.")},
        confirmButton={TextButton(onClick={e.cambiar{requireNotNull(e.fundacion).eliminarSolicitud(s)};eliminar=null}){Text("Eliminar")}},dismissButton={TextButton(onClick={eliminar=null}){Text("Cancelar")}})}
}

@Composable private fun Mensajes(e: EstadoApp) {
    var texto by rememberSaveable{mutableStateOf("")}
    val esFundacion=e.rol=="fundacion"
    Pagina(e,"Mensajes",nav=true) {
        if(!esFundacion)Selector("Fundación",e.organizacion?.nombre.orEmpty(),e.sistema.fundaciones.filter{it.estadoCuenta=="ACTIVA"}.map{it.nombre.orEmpty()}){name->e.fundacionId=e.sistema.fundaciones.first{it.nombre==name}.idFundacion}
        val lista=if(esFundacion)e.sistema.mensajes.filter{it.destinatarioFundacion===e.fundacion||it.remitenteFundacion===e.fundacion}
            else e.sistema.mensajes.filter{(it.remitenteDonador===e.usuario||it.destinatarioDonador===e.usuario)&&(it.destinatarioFundacion===e.organizacion||it.remitenteFundacion===e.organizacion)}
        if(lista.isEmpty())Aviso("Inicia una conversación con la fundación.")
        lista.forEach{m->Tarjeta(Modifier.clickable{e.mensajeId=m.idMensaje;e.cambiar{m.marcarComoLeido()}}) {
            Text(m.remitenteDonador?.nombre?:m.remitenteFundacion?.nombre.orEmpty(),fontSize=13.sp,fontWeight=FontWeight.Bold)
            Text(m.contenido.orEmpty(),fontSize=14.sp)
            Nota(if(m.isLeido)"Leído"else"Sin leer")
            if(esFundacion&&m.remitenteDonador!=null)Enlace(if(e.mensajeId==m.idMensaje)"Respondiendo a este mensaje"else"Responder"){e.mensajeId=m.idMensaje;e.cambiar{m.marcarComoLeido()}}
        }}
        Campo("Tu mensaje",texto,"Escribe un mensaje",{texto=it},multiline=true)
        Accion(if(esFundacion)"Enviar respuesta"else"Enviar mensaje") {
            if(e.cambiar {
                require(texto.isNotBlank()){"Escribe un mensaje."}
                if(esFundacion) {
                    val m=requireNotNull(e.sistema.mensajes.firstOrNull{it.idMensaje==e.mensajeId&&it.destinatarioFundacion===e.fundacion}){"Selecciona el mensaje que quieres responder."}
                    e.sistema.responder(requireNotNull(e.fundacion),m,texto)
                }else requireNotNull(e.sistema.enviarMensaje(e.usuario,e.organizacion,texto)){"Selecciona una fundación e ingresa como donador o invitado."}
            })texto=""
        }
    }
}

@Composable private fun Resenas(e: EstadoApp) {
    var comentario by rememberSaveable{mutableStateOf("")}
    var nota by rememberSaveable{mutableStateOf("5")}
    val f=e.organizacion
    Pagina(e,"Retroalimentación") {
        Titulo(f?.nombre.orEmpty())
        val lista=e.sistema.getRetroalimentacionesParaFundacion(f).filterNotNull()
        if(lista.isEmpty())Nota("Esta fundación aún no tiene reseñas.")
        lista.forEach{r->Tarjeta{Text("${r.usuario?.nombre} · ${r.calificacion}/5",fontWeight=FontWeight.Bold);Text(r.comentario.orEmpty());
            if(r.usuario===e.usuario)Enlace("Eliminar mi reseña", confirmacion="¿Deseas eliminar tu reseña? Esta acción no se puede deshacer."){e.cambiar{r.eliminar();e.sistema.retroalimentaciones.remove(r)}}
        }}
        Selector("Calificación",nota,listOf("1","2","3","4","5")){nota=it}
        Campo("Comentario",comentario,"Comparte tu experiencia",{comentario=it},multiline=true)
        Accion("Publicar retroalimentación", confirmacion="¿Deseas enviar esta retroalimentación?\n\nCalificación: $nota/5\n$comentario") {
            if(e.cambiar {require(comentario.isNotBlank()){"Escribe un comentario."};requireNotNull(e.sistema.dejarRetroalimentacion(e.usuario,f,nota.toInt(),comentario)){"Ingresa como donador o invitado."}})comentario=""
        }
    }
}

@Composable private fun EditarPerfil(e: EstadoApp) {
    val u=e.usuario?:return
    var nombre by rememberSaveable{mutableStateOf(u.nombre.orEmpty())}
    var correo by rememberSaveable{mutableStateOf(u.correo.orEmpty())}
    var lugar by rememberSaveable{mutableStateOf(u.ubicacionTexto.orEmpty())}
    var intereses by rememberSaveable{mutableStateOf(u.intereses?.joinToString(", ").orEmpty())}
    var latitud by rememberSaveable{mutableStateOf(u.latitud.toString())}
    var longitud by rememberSaveable{mutableStateOf(u.longitud.toString())}
    var eliminar by remember{mutableStateOf(false)}
    Pagina(e,"Editar datos personales") {
        Campo("Nombre completo",nombre,"Nombre",{nombre=it});Campo("Correo electrónico",correo,"nombre@correo.com",{correo=it})
        Campo("Ubicación",lugar,"Ciudad o municipio",{lugar=it});Campo("Intereses",intereses,"Comida, ropa…",{intereses=it})
        Campo("Latitud para ordenar por cercanía",latitud,"4.579",{latitud=it});Campo("Longitud",longitud,"-74.216",{longitud=it})
        Accion("Guardar cambios") {
            if(e.cambiar {
                require(nombre.isNotBlank()&&lugar.isNotBlank()&&android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()){"Revisa nombre, correo y ubicación."}
                require(e.sistema.usuarios.none{it!==u&&it.correo.equals(correo.trim(),true)}){"Ese correo ya está registrado."}
                val lat=requireNotNull(latitud.toDoubleOrNull()){"Latitud no válida."};val lon=requireNotNull(longitud.toDoubleOrNull()){"Longitud no válida."}
                require(lat.isFinite()&&lon.isFinite()&&lat in -90.0..90.0&&lon in -180.0..180.0){"Coordenadas fuera de rango."}
                u.editarPerfil(UsuarioDonador(u.idUsuario,nombre.trim(),correo.trim(),null,lugar.trim(),lat,lon,intereses.split(',').map{it.trim() as String?}.filterNot{it.isNullOrBlank()}.toMutableList(),u.estadoCuenta,u.fechaRegistro))
            })e.ir(e.destino("PERFIL"))
        }
        Enlace("Eliminar cuenta"){eliminar=true}
    }
    if(eliminar)AlertDialog(onDismissRequest={eliminar=false},title={Text("Eliminar cuenta local")},text={Text("La cuenta dejará de permitir el inicio de sesión. Se conservará el historial de sus operaciones.")},
        confirmButton={TextButton(onClick={if(e.cambiar{u.eliminarCuenta()})e.salir();eliminar=false}){Text("Eliminar cuenta")}},dismissButton={TextButton(onClick={eliminar=false}){Text("Cancelar")}})
}

@Composable private fun EditarFundacion(e: EstadoApp) {
    val f=e.fundacion?:return
    var nombre by rememberSaveable{mutableStateOf(f.nombre.orEmpty())}
    var direccion by rememberSaveable{mutableStateOf(f.direccion.orEmpty())}
    var categoria by rememberSaveable{mutableStateOf(f.categoria.orEmpty())}
    var correo by rememberSaveable{mutableStateOf(f.correoContacto.orEmpty())}
    var telefono by rememberSaveable{mutableStateOf(f.telefonoContacto.orEmpty())}
    var documento by rememberSaveable{mutableStateOf(f.documentacionLegal.orEmpty())}
    Pagina(e,"Perfil de fundación",nav=true) {
        Campo("Nombre",nombre,"Nombre de la fundación",{nombre=it});Campo("Dirección",direccion,"Dirección",{direccion=it})
        Campo("Categoría",categoria,"Causa social",{categoria=it});Campo("Correo de contacto",correo,"contacto@fundacion.org",{correo=it})
        Campo("Teléfono",telefono,"Número de contacto",{telefono=it});Campo("Documentación legal",documento,"Referencia de documentación",{documento=it},multiline=true)
        Accion("Guardar perfil") {
            if(e.cambiar {
                require(nombre.isNotBlank()&&direccion.isNotBlank()&&categoria.isNotBlank()&&android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()){"Completa los datos de contacto."}
                require(telefono.count{it.isDigit()}>=7){"Introduce un teléfono válido."}
                f.editarPerfil(Fundacion(f.idFundacion,nombre.trim(),null,direccion.trim(),categoria.trim(),correo.trim(),telefono.trim(),documento,f.isVerificado,f.estadoCuenta,f.fechaRegistro))
            })e.aviso="Perfil actualizado."
        }
        Enlace("Ver mensajes"){e.ir(Rutas.MENSAJES)}
        Enlace("Ver retroalimentación"){e.fundacionId=f.idFundacion;e.ir(Rutas.RESENAS)}
        Enlace("Cerrar sesión"){e.salir()}
    }
}
