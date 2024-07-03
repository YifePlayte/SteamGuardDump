package com.yifeplayte.steamguarddump.hook.hooks

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yifeplayte.steamguarddump.hook.BaseHook
import com.yifeplayte.steamguarddump.hook.utils.ClipboardUtils.copy
import com.yifeplayte.steamguarddump.hook.utils.SteamGuardUtils.enhanceJson
import java.lang.reflect.Method

object FromDecryptItem : BaseHook() {
    override fun init() {
        val clazzNameList = listOf(
            "expo.modules.securestore.SecureStoreModule\$HybridAESEncrypter",
            "expo.modules.securestore.SecureStoreModule\$AESEncrypter",
            "expo.modules.securestore.SecureStoreModule\$LegacySDK20Encrypter",
            "expo.modules.securestore.encryptors.AESEncryptor",
            "expo.modules.securestore.encryptors.HybridAESEncryptor",
        )
        val clazzList = mutableListOf<Class<*>>()
        val methodList = mutableListOf<Method>()
        for (clazzName in clazzNameList) {
            loadClassOrNull(clazzName)?.let { clazzList.add(it) }
        }
        for (clazz in clazzList) {
            methodList.addAll(
                clazz.methodFinder().filterByName("decryptItem").filterNonAbstract().toList()
            )
        }
        methodList.createHooks {
            after { param ->
                val throwable = Throwable()
                for (i in throwable.stackTrace) {
                    if (i.methodName == "readJSONEncodedItem" || i.methodName == "readLegacySDK20Item") {
                        param.result?.let { copy(enhanceJson(it as String)) }
                        return@after
                    }
                }
            }
        }
    }
}