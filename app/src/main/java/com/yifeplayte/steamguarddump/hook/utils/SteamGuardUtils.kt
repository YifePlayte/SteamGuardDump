package com.yifeplayte.steamguarddump.hook.utils

import com.alibaba.fastjson2.JSON
import io.matthewnelson.encoding.base32.Base32
import io.matthewnelson.encoding.base64.Base64
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArray
import io.matthewnelson.encoding.core.Encoder.Companion.encodeToString

object SteamGuardUtils {
    @JvmStatic
    fun addUriToJson(string: String): String {
        val steamGuard = JSON.parseObject(string)
        val accounts = steamGuard.getJSONObject("accounts")
        for (steamId in accounts.keys) {
            val account = accounts.getJSONObject(steamId)
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