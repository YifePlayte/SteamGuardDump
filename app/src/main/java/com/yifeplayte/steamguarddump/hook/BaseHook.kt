package com.yifeplayte.steamguarddump.hook

abstract class BaseHook {
    var isInit: Boolean = false
    abstract fun init()
}