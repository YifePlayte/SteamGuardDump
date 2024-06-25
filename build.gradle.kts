plugins {
    id("com.android.application") version "8.5.0" apply false
    id("com.android.library") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}
