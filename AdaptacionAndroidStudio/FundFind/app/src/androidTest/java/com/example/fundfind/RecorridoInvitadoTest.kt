package com.example.fundfind

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fundfind.layout.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecorridoInvitadoTest {
    @get:Rule val compose=createAndroidComposeRule<MainActivity>()
    private val estado get()=(compose.activity.application as FundFindApplication).estado

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
