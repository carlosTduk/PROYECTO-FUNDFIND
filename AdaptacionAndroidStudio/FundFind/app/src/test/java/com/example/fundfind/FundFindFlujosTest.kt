package com.example.fundfind
import org.junit.Test
import com.example.fundfind.logic.*
import java.io.*
import java.time.LocalDate

class FundFindFlujosTest { @Test fun recorridoLocalCompleto() {
    val demo=DatosRecorrido.crear()
    check(demo.usuarios.size==4)
    check(demo.solicitudesActivas.size==8)
    val u=demo.usuarios.first{it.idUsuario==100}
    val f=demo.fundaciones.first{it.idFundacion==100}
    check(u.verListadoFundaciones("Comunidad").size==1)
    check(f.gestionarSolicitudes().size==1)
    check(u.verListadoSolicitudes().size==8)
    val registrado=demo.registrarUsuario("Prueba","test@example.org","clave12345","Bogotá")
    check(registrado.contrasena!="clave12345")
    check(demo.loginDonador("test@example.org","clave12345")===registrado)
    check(demo.loginDonador("test@example.org","incorrecta")==null)
    check(runCatching{demo.registrarUsuario("Duplicado","test@example.org","clave12345","Bogotá")}.isFailure)
    val solicitud=demo.publicarSolicitud(f,"Alimentos","Descripción","Comida","Infancia")!!
    solicitud.fechaLimite=LocalDate.now().plusDays(2)
    val d=demo.registrarIntencion(u,solicitud,"Comida","5 paquetes","Invitado","invitado@example.org",true)
    check(d.comprobante!=null&&d.estado=="INTENCIONREGISTRADA")
    check(u.verNotificaciones().any{it?.enlaceRecurso==d.idDonacion.toString()})
    check(f.verNotificaciones().any{it?.enlaceRecurso==d.idDonacion.toString()})
    demo.cambiarDonacion(d,"CONFIRMADA");check(d.estado=="CONFIRMADA")
    demo.cambiarDonacion(d,"CANCELADA");check(d.estado=="CANCELADA")
    check(runCatching{demo.cambiarDonacion(d,"CONFIRMADA")}.isFailure)
    val m=demo.enviarMensaje(u,f,"Mensaje de prueba")!!
    val reply=demo.responder(f,m,"Respuesta")
    check(reply.destinatarioDonador===u&&m.isLeido)
    check(runCatching{demo.enviarMensaje(u,f," ")}.isFailure)
    check(runCatching{demo.dejarRetroalimentacion(u,f,9,"Inválida")}.isFailure)
    demo.dejarRetroalimentacion(u,f,5,"Excelente")
    check(demo.getRetroalimentacionesParaFundacion(f).size==1)
    f.eliminarSolicitud(solicitud);check(!solicitud.activa())
    val otra=demo.solicitudes[1]
    check(runCatching{f.eliminarSolicitud(otra)}.isFailure)
    demo.verificar(f,true,"Prueba");check(f.isVerificado&&f.fechaVerificacion!=null)
    otra.reportes=1;demo.moderar(otra,false);check(otra.estado=="ELIMINADA")
    otra.fechaLimite=LocalDate.now().minusDays(1);otra.estado="PUBLICADA";check(!otra.activa())
    val bytes=ByteArrayOutputStream().also{b->ObjectOutputStream(b).use{it.writeObject(demo)}}.toByteArray()
    val restaurado=ObjectInputStream(ByteArrayInputStream(bytes)).use{it.readObject() as FundFindSistema}
    restaurado.vincularModelos()
    check(restaurado.donaciones.last().solicitud===restaurado.solicitudes.last())
    check(restaurado.usuarios.first{it.idUsuario==100}.verNotificaciones().isNotEmpty())
    registrado.eliminarCuenta();check(demo.loginDonador("test@example.org","clave12345")==null)
    val distancia=Ubicacion(0.0,0.0,"").calcularDistancia(Ubicacion(0.0,1.0,""))
    check(distancia in 111.0..112.0)
    println("PASS: registro, login, duplicados, solicitudes, donaciones, notificaciones, mensajes, reseñas, moderación, serialización y ubicación")
}

}