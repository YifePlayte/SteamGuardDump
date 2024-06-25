package com.yifeplayte.steamguarddump.hook.hooks

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.MemberExtensions.isNotAbstract
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yifeplayte.steamguarddump.hook.BaseHook
import com.yifeplayte.steamguarddump.hook.utils.ClipboardUtils.copy
import com.yifeplayte.steamguarddump.hook.utils.SteamGuardUtils.addUriToJson
import java.lang.reflect.Method

object FromDecryptItem : BaseHook() {
    override fun init() {
        val methodList = mutableListOf<Method>()
        val clazzHybridAESEncrypter = loadClass("expo.modules.securestore.SecureStoreModule\$HybridAESEncrypter")
        val clazzAESEncrypter = loadClass("expo.modules.securestore.SecureStoreModule\$AESEncrypter")
        val clazzLegacySDK20Encrypter = loadClass("expo.modules.securestore.SecureStoreModule\$LegacySDK20Encrypter")
        methodList.addAll(clazzHybridAESEncrypter.methodFinder().filterByName("decryptItem").toList())
        methodList.addAll(clazzAESEncrypter.methodFinder().filterByName("decryptItem").toList())
        methodList.addAll(clazzLegacySDK20Encrypter.methodFinder().filterByName("decryptItem").toList())
        methodList.filter { it.isNotAbstract }.createHooks {
            after { param ->
                val throwable = Throwable()
                for (i in throwable.stackTrace) {
                    if (i.methodName == "readJSONEncodedItem" || i.methodName == "readLegacySDK20Item") {
                        param.result?.let { copy(addUriToJson(it as String)) }
                        return@after
                    }
                }
            }
        }
    }
}