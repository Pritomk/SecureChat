package com.example.securechat.utils.crypto

import android.security.keystore.KeyProperties
import com.example.securechat.utils.AppConstants
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher

object RSACryptoManager {

    private val keyStore by lazy {
        KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
    }

    fun fetchKey(): KeyStore.PrivateKeyEntry? {
        return keyStore.getEntry(AppConstants.RSA_ALIAS, null) as? KeyStore.PrivateKeyEntry
    }

    fun fetchPrivateKey(): PrivateKey? {
        return keyStore.getKey(AppConstants.RSA_ALIAS, null) as? PrivateKey
    }

    fun generateRsaKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM)
        keyPairGenerator.initialize(512)

        return keyPairGenerator.generateKeyPair()
    }


    fun encryptRSA(aesKey: ByteArray, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance(RSA_TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, publicKey)
        }
        return cipher.doFinal(aesKey)
    }

    fun decryptRSA(encryptedKey: ByteArray): ByteArray {
        val privateKeyEntry =
            keyStore.getEntry(AppConstants.RSA_ALIAS, null) as KeyStore.PrivateKeyEntry
        val cipher = Cipher.getInstance(RSA_TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)
        }
        return cipher.doFinal(encryptedKey)
    }

    fun decryptUsingPrivateKeyRsa(encryptedKey: ByteArray, privateKeyStr: String): ByteArray {
        val privateKey = stringToPrivateKey(privateKeyStr)
        val cipher = Cipher.getInstance(RSA_TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, privateKey)
        }
        return cipher.doFinal(encryptedKey)

    }

    private fun stringToPrivateKey(privateKeyString: String): PrivateKey {
        val privateKeyBytes = Base64.getDecoder().decode(privateKeyString)
        val keySpec = PKCS8EncodedKeySpec(privateKeyBytes)
        val keyFactory = KeyFactory.getInstance(RSA_ALGORITHM) // Change "RSA" to the appropriate algorithm if needed
        return keyFactory.generatePrivate(keySpec)
    }


    const val RSA_ALGORITHM = KeyProperties.KEY_ALGORITHM_RSA
    private const val RSA_BLOCK = KeyProperties.BLOCK_MODE_ECB
    private const val RSA_PADDING = KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1
    private const val RSA_TRANSFORMATION = "$RSA_ALGORITHM/$RSA_BLOCK/$RSA_PADDING"

}