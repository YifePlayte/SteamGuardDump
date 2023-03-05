package com.yifeplayte.steamguarddump.hook

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.init.InitFields.appContext
import com.github.kyuubiran.ezxhelper.init.InitFields.isAppContextInited
import com.github.kyuubiran.ezxhelper.utils.*
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Method

object GetDecryptedData : BaseHook() {
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
                    if (!isAppContextInited)
                        EzXHelperInit.initAppContext()
                    val clipboardManager =
                        appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData =
                        ClipData.newPlainText(Log.currentLogger.toastTag, param.result as String)
                    clipboardManager.setPrimaryClip(clipData)
                    Log.toast("SteamGuard data copied!")
                    break
                }
            }
        }
    }
}