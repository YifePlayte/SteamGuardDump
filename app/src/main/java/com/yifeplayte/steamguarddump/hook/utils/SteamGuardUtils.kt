package com.yifeplayte.steamguarddump.hook.utils

import android.content.Context.MODE_PRIVATE
import com.alibaba.fastjson2.JSON
import com.github.kyuubiran.ezxhelper.EzXHelper.appContext
import io.matthewnelson.encoding.base32.Base32
import io.matthewnelson.encoding.base64.Base64
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArray
import io.matthewnelson.encoding.core.Encoder.Companion.encodeToString

object SteamGuardUtils {
    @JvmStatic
    fun enhanceJson(jsonString: String): String {
        return jsonString.addUriToJson().addUuidKeyToJson()
    }

    @JvmStatic
    private fun String.addUuidKeyToJson(): String {
        val steamGuard = JSON.parseObject(this)
        val sharedPreferences = appContext.getSharedPreferences("steam.uuid", MODE_PRIVATE)
        val uuid = sharedPreferences.getString("uuidKey", "")
        if (!uuid.isNullOrEmpty()) {
            steamGuard["uuid_key"] = uuid
        }
        return steamGuard.toJSONString()
    }

    @JvmStatic
    private fun String.addUriToJson(): String {
        val steamGuard = JSON.parseObject(this)
        val accounts = steamGuard.getJSONObject("accounts")
        accounts.keys.forEach { steamId ->
            val account = accounts.getJSONObject(steamId)
            if (!account.getString("uri").isNullOrEmpty()) return@forEach
            val accountName = account.getString("account_name")
            val sharedSecretBase64 = account.getString("shared_secret")
            val sharedSecretDecoded = sharedSecretBase64.decodeToByteArray(Base64.Default)
            val sharedSecretBase32 = sharedSecretDecoded.encodeToString(Base32.Default)
            val uri = buildString {
                append("otpauth://totp/Steam:")
                append(accountName)
                append("?secret=")
                append(sharedSecretBase32)
                append("&issuer=Steam")
            }
            account["uri"] = uri
        }
        return steamGuard.toJSONString()
    }
}