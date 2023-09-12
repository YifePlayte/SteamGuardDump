package com.yifeplayte.steamguarddump.hook

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.init.InitFields.appContext
import com.github.kyuubiran.ezxhelper.init.InitFields.isAppContextInited
import com.github.kyuubiran.ezxhelper.utils.Log
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import java.lang.reflect.Method

object GetDecryptedData : BaseHook() {
    var targetPromise: Any? = null
    override fun init() {
        val methodList = mutableListOf<Method>()
        methodList.addAll(findAllMethods("expo.modules.securestore.SecureStoreModule\$HybridAESEncrypter") { name == "decryptItem" })
        methodList.addAll(findAllMethods("expo.modules.securestore.SecureStoreModule\$AESEncrypter") { name == "decryptItem" })
        methodList.addAll(findAllMethods("expo.modules.securestore.SecureStoreModule\$LegacySDK20Encrypter") { name == "decryptItem" })
        methodList.hookAfter { param ->
            Log.i("Hooked after decryptItem")
            val throwable = Throwable()
            for (i in throwable.stackTrace) {
                if (i.methodName == "readJSONEncodedItem" || i.methodName == "readLegacySDK20Item") {
                    param.result?.let { copyResult(it as String) }
                    break
                }
            }
        }
        findAllMethods("expo.modules.core.ExportedModule") { name == "invokeExportedMethod" }.first().hookBefore { param ->
            if (param.args[0] == "getValueWithKeyAsync") {
                val typedArray = (param.args[1] as Collection<Any?>).toTypedArray()
                if (!(typedArray.get(0) as String).startsWith("SteamGuard")) return@hookBefore
                targetPromise = typedArray.get(2)
                targetPromise!!.javaClass.findMethod { name == "resolve" }.hookBefore {
                    if (it.thisObject == targetPromise)
                        copyResult(it.args[0] as String)
                }
            }
        }
    }

    fun copyResult(string: String) {
        if (!isAppContextInited)
            EzXHelperInit.initAppContext()
        val clipboardManager =
            appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData =
            ClipData.newPlainText(Log.currentLogger.toastTag, string)
        clipboardManager.setPrimaryClip(clipData)
        Log.toast("SteamGuard data copied!")
    }
}