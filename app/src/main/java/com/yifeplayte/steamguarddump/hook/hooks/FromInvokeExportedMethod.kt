package com.yifeplayte.steamguarddump.hook.hooks

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yifeplayte.steamguarddump.hook.BaseHook
import com.yifeplayte.steamguarddump.hook.utils.ClipboardUtils.copy
import com.yifeplayte.steamguarddump.hook.utils.SteamGuardUtils.enhanceJson

object FromInvokeExportedMethod : BaseHook() {
    private var targetPromise: Any? = null
    override fun init() {
        val clazzExportedModule = loadClassOrNull("expo.modules.core.ExportedModule") ?: return
        clazzExportedModule.methodFinder()
            .filterByName("invokeExportedMethod")
            .filterNonAbstract()
            .singleOrNull()
            ?.createHook {
                before { param ->
                    if (param.args[0] == "getValueWithKeyAsync") {
                        val typedArray = (param.args[1] as Collection<Any?>).toTypedArray()
                        if (!(typedArray[0] as String).startsWith("SteamGuard")) return@before
                        targetPromise = typedArray[2]
                        targetPromise!!.javaClass.methodFinder()
                            .filterByName("resolve")
                            .filterNonAbstract()
                            .singleOrNull()?.createHook {
                                before {
                                    if (it.thisObject == targetPromise)
                                        copy(enhanceJson(it.args[0] as String))
                                }
                            }
                    }
                }
            }
    }
}