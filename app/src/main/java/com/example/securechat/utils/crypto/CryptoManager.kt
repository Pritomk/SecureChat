package com.example.securechat.utils.crypto

import com.example.securechat.utils.AppCommonMethods
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64


object CryptoManager {
    fun encrypt(
        rawData: String,
        publicKey: String,
        encryptionCallback: (String, String) -> Unit
    ) {
        AESCryptoManager.encrypt(rawData) { encryptedData, aesSecretKey ->

            val encryptedAesSecretKey =
                byteArrayToPublicKey(publicKey)?.let {
                    RSACryptoManager.encryptRSA(aesSecretKey,
                        it
                    )
                }
            if (encryptedAesSecretKey != null) {
                encryptionCallback(AppCommonMethods.encodeBase64(encryptedData), AppCommonMethods.encodeBase64(encryptedAesSecretKey))
            }
        }
    }

    fun decrypt(encryptedData: ByteArray, encryptedAesSecretKey: ByteArray): String {
        val decryptedAesSecretKey = RSACryptoManager.decryptRSA(encryptedAesSecretKey)
        return AESCryptoManager.decrypt(encryptedData, decryptedAesSecretKey)
    }

    fun decryptWithPrivateKey(
        encryptedData: ByteArray,
        encryptedAesSecretKey: ByteArray,
        privateKeyStr: String
    ): String {
        val decryptedAesSecretKey =
            RSACryptoManager.decryptUsingPrivateKeyRsa(encryptedAesSecretKey, privateKeyStr)
        return AESCryptoManager.decrypt(encryptedData, decryptedAesSecretKey)
    }

    private fun byteArrayToPublicKey(pubKey: String): PublicKey? {
        try {
            val publicKey = Base64.getDecoder().decode(pubKey)
            return KeyFactory.getInstance(RSACryptoManager.RSA_ALGORITHM)
                .generatePublic(X509EncodedKeySpec(publicKey))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}