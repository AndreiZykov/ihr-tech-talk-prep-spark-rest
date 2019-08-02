package org.iheartradio

import java.security.Key

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * https://stackoverflow.com/questions/1205135/how-to-encrypt-string-in-java
 */
class Encryptor(private val keyStr: String) {

//    private val keyStr: String? = null

    private var aesKey: Key? = null
    private var cipher: Cipher? = null

    @Synchronized
    @Throws(Exception::class)
    private fun init() {
        if (keyStr.length != 16) {
            throw Exception("aes key is not defined!")
        }
        if (aesKey == null) {
            aesKey = SecretKeySpec(keyStr.toByteArray(), "AES")
            cipher = Cipher.getInstance("AES")
        }

        if(aesKey == null) {
            throw Exception("could not create SecretKeySpec")
        }

        if(cipher == null) {
            throw Exception("could not create Cipher instance")
        }
    }

    @Synchronized
    @Throws(Exception::class)
    fun encrypt(text: String): String {
        init()
        cipher!!.init(Cipher.ENCRYPT_MODE, aesKey)
        return toHexString(cipher!!.doFinal(text.toByteArray()))
    }

    @Synchronized
    @Throws(Exception::class)
    fun decrypt(text: String): String {
        init()
        cipher!!.init(Cipher.DECRYPT_MODE, aesKey)
        return String(cipher!!.doFinal(toByteArray(text)))
    }

    companion object {

        fun toHexString(bytes: ByteArray): String {
            val sb = StringBuilder()
            for (b in bytes) {
                sb.append(String.format("%02X", b))
            }
            return sb.toString()
        }

        fun toByteArray(s: String): ByteArray {
            val len = s.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
                i += 2
            }
            return data
        }
    }

}