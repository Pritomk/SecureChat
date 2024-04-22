package com.example.securechat.utils.crypto

import android.security.keystore.KeyProperties
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object AESCryptoManager {


    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(AES_ALGORITHM).generateKey()
    }

    fun encrypt(rawData: String, encryptionCallback: (ByteArray, ByteArray) -> Unit) {
        val createdKey = createKey()
        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, createdKey)
        encryptionCallback(cipher.doFinal(rawData.toByteArray()), createdKey.encoded)
    }

    fun decrypt(encryptedData: ByteArray, encryptionKeys: ByteArray): String {
        val secretKeySpec = SecretKeySpec(encryptionKeys, AES_ALGORITHM)
        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        return String(cipher.doFinal(encryptedData))
    }


    private const val AES_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val AES_BLOCK_MODE = KeyProperties.BLOCK_MODE_ECB
    private const val AES_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private const val AES_TRANSFORMATION = "$AES_ALGORITHM/$AES_BLOCK_MODE/$AES_PADDING"

}