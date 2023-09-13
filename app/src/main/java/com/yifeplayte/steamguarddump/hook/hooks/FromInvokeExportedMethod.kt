package com.yifeplayte.steamguarddump.hook.hooks

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yifeplayte.steamguarddump.hook.BaseHook
import com.yifeplayte.steamguarddump.hook.utils.ClipboardUtils.copy

object FromInvokeExportedMethod : BaseHook() {
    private var targetPromise: Any? = null
    override fun init() {
        loadClass("expo.modules.core.ExportedModule").methodFinder().filterByName("invokeExportedMethod").firstOrNull()?.createHook {
            before { param ->
                if (param.args[0] == "getValueWithKeyAsync") {
                    val typedArray = (param.args[1] as Collection<Any?>).toTypedArray()
                    if (!(typedArray[0] as String).startsWith("SteamGuard")) return@before
                    targetPromise = typedArray[2]
                    targetPromise!!.javaClass.methodFinder().filterByName("resolve").firstOrNull()?.createHook {
                        before {
                            if (it.thisObject == targetPromise)
                                copy(it.args[0] as String)
                        }
                    }
                }
            }
        }
    }
}