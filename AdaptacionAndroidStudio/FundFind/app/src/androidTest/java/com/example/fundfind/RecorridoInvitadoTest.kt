package com.example.fundfind

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fundfind.layout.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecorridoInvitadoTest {
    @get:Rule val compose=createAndroidComposeRule<MainActivity>()
    private val estado get()=(compose.activity.application as FundFindApplication).estado

    private fun captura(nombre: String) {
        val archivo=java.io.File(compose.activity.getExternalFilesDir(null), "verificacion/$nombre.png")
        archivo.parentFile?.mkdirs()
        archivo.outputStream().use { compose.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG,100,it) }
    }

    @Test fun confirmacionesConservanFormularioYNoDuplicanAcciones() {
        listOf(Rutas.V1_DONAR,Rutas.V2_DONAR).forEach { ruta ->
            compose.runOnIdle { estado.entrarInvitado(); estado.visitar(ruta) }
            val anteriores=compose.runOnIdle { estado.sistema.donaciones.size }
            if(ruta==Rutas.V2_DONAR) {
                compose.onNodeWithText("Seleccionar").performScrollTo().performClick()
                compose.onNodeWithText("Comida").performClick()
            }
            compose.onAllNodes(hasSetTextAction())[0].performScrollTo().performTextInput("10 kg de arroz")
            compose.onNodeWithText("Confirmar intención").let { if(ruta==Rutas.V2_DONAR)it.performScrollTo();it.performClick() }
            compose.onNodeWithText("Cancelar").assertIsDisplayed()
            compose.onNode(isDialog()).assertExists()
            compose.onNodeWithText("Cancelar").performClick()
            compose.runOnIdle { check(estado.sistema.donaciones.size==anteriores) }
            compose.onNodeWithText("10 kg de arroz").assertExists()
            compose.onNodeWithText("Confirmar intención").performClick()
            compose.onNodeWithText("Confirmar",substring=false).performClick()
            compose.runOnIdle { check(estado.sistema.donaciones.size==anteriores+1) }
        }
        compose.runOnIdle { estado.ir(Rutas.RESENAS) }
        val anteriores=compose.runOnIdle { estado.sistema.retroalimentaciones.size }
        compose.onNode(hasSetTextAction()).performScrollTo().performTextInput("Excelente atención local")
        compose.onNodeWithText("Publicar retroalimentación").performScrollTo().performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle { check(estado.sistema.retroalimentaciones.size==anteriores) }
        compose.onNodeWithText("Excelente atención local").assertExists()
        compose.onNodeWithText("Publicar retroalimentación").performClick()
        compose.onNodeWithText("Confirmar",substring=false).performClick()
        compose.runOnIdle { check(estado.sistema.retroalimentaciones.size==anteriores+1) }
    }

    @Test fun invitadoDisponibleEnAccesosDeAmbasVersiones() {
        listOf(Rutas.V1_LOGIN,Rutas.V1_REGISTRO,Rutas.V2_LOGIN,Rutas.V2_REGISTRO).forEach { ruta ->
            compose.runOnIdle { estado.salir(); estado.ir(ruta,true) }
            compose.onNodeWithText("Ingresar como invitado").performScrollTo().performClick()
            compose.onNodeWithText("Recorrido completo").assertIsDisplayed()
            compose.runOnIdle { check(estado.invitado); check(estado.usuario!=null) }
        }
    }

    @Test fun todasLasPantallasDelDisenoRenderizanEnModoInvitado() {
        compose.runOnIdle { estado.entrarInvitado() }
        Rutas.entries.filter { it.version!=0 }.forEach { ruta ->
            compose.runOnIdle { estado.visitar(ruta) }
            compose.waitForIdle()
            compose.onRoot().assertExists()
            captura(ruta.name)
            compose.onNodeWithText("Recorrido completo").assertIsDisplayed()
        }
    }

    @Test fun navegaALosRolesFundacionYAdministracion() {
        compose.runOnIdle { estado.entrarInvitado() }
        compose.onNodeWithText("Recorrido completo").performClick()
        compose.onNodeWithText("Fundación",useUnmergedTree=true).performClick()
        compose.onNodeWithText("Resumen de tu fundación").assertIsDisplayed()
        compose.onNodeWithText("Publicar nueva solicitud").performClick()
        compose.onNodeWithText("Nueva solicitud").assertIsDisplayed()
        compose.onNodeWithText("Recorrido completo").performClick()
        compose.onNodeWithText("Admin",useUnmergedTree=true).performClick()
        compose.onNodeWithText("Panel administrativo").assertIsDisplayed()
    }
}
