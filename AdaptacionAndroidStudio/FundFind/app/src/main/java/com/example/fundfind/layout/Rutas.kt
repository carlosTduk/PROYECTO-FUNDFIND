package com.example.fundfind.layout

enum class Rutas(val titulo: String, val version: Int, val rol: String = "donador") {
    V1_BIENVENIDA("01 Bienvenida", 1), V1_REGISTRO("02 Registro donador", 1),
    V1_LOGIN("03 Inicio de sesión", 1), V1_INICIO("04 Dashboard donador", 1),
    V1_EXPLORAR("05 Explorar y filtros", 1), V1_VACIO("06 Explorar sin resultados", 1),
    V1_DETALLE("07 Detalle de solicitud", 1), V1_DONAR("08 Intención de donar", 1),
    V1_CONFIRMACION("09 Confirmación de intención", 1), V1_PERFIL("10 Perfil del donador", 1),
    V2_BIENVENIDA("01 Bienvenida", 2), V2_LOGIN("02 Inicio de sesión", 2),
    V2_REGISTRO("03 Registro de donador", 2), V2_INICIO("04 Dashboard del donador", 2),
    V2_DETALLE("05 Detalle de solicitud", 2), V2_DONAR("06 Intención de donar", 2),
    V2_CONFIRMACION("07 Confirmación de intención", 2), V2_ERROR("08 Inicio de sesión — error", 2),
    V2_EXPLORAR("09 Explorar y filtros", 2), V2_VACIO("10 Explorar sin resultados", 2),
    V2_PERFIL("11 Perfil del donador", 2), V2_FUNDACION("12 Dashboard de fundación", 2, "fundacion"),
    V2_CREAR("13 Crear solicitud", 2, "fundacion"), V2_PUBLICADA("14 Solicitud publicada", 2, "fundacion"),
    V2_ADMIN("15 Panel administrativo", 2, "admin"), V2_VERIFICAR("16 Verificación de fundación", 2, "admin"),
    V2_MODERAR("17 Moderación de publicaciones", 2, "admin"), V2_REPORTES("18 Reportes básicos", 2, "admin"),
    FAVORITOS("Favoritos", 0), AVISOS("Notificaciones", 0), MENSAJES("Mensajes", 0),
    EDITAR_PERFIL("Editar datos personales", 0), HISTORIAL("Historial de intenciones", 0),
    MIS_SOLICITUDES("Mis solicitudes", 0, "fundacion"), INTERESADOS("Personas interesadas", 0, "fundacion"),
    PERFIL_FUNDACION("Perfil de fundación", 0, "fundacion"), FUNDACIONES("Fundaciones", 0),
    RESENAS("Retroalimentación", 0), DOCUMENTO("Documentación legal", 0, "admin"),
    RECORRIDO("Recorrido completo", 0), RECUPERAR("Acceso local", 0);
}
