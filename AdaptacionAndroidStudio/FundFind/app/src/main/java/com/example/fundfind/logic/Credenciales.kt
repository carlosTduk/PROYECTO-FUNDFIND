package com.example.fundfind.logic

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Credenciales {
    fun crear(clave: String): String {
        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        return "pbkdf2:" + Base64.getEncoder().encodeToString(sal) + ":" +
            Base64.getEncoder().encodeToString(derivar(clave, sal))
    }

    fun coincide(clave: String, guardada: String): Boolean {
        // Preserve the original project's sample accounts while hashing new accounts.
        if (!guardada.startsWith("pbkdf2:")) return MessageDigest.isEqual(clave.toByteArray(), guardada.toByteArray())
        return runCatching {
            val partes = guardada.split(':')
            MessageDigest.isEqual(derivar(clave, Base64.getDecoder().decode(partes[1])), Base64.getDecoder().decode(partes[2]))
        }.getOrDefault(false)
    }

    private fun derivar(clave: String, sal: ByteArray): ByteArray {
        val spec = PBEKeySpec(clave.toCharArray(), sal, 120_000, 256)
        return try { SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded }
        finally { spec.clearPassword() }
    }
}
