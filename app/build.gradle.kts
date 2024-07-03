import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.yifeplayte.steamguarddump"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yifeplayte.steamguarddump"
        minSdk = 24
        targetSdk = 34
        versionCode = 7
        versionName = "1.3.2"

        applicationVariants.configureEach {
            outputs.configureEach {
                if (this is BaseVariantOutputImpl) {
                    outputFileName = outputFileName.replace("app", rootProject.name).replace(Regex("debug|release"), versionName)
                }
            }
        }
    }

    buildTypes {
        named("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
        }
        named("debug") {
            versionNameSuffix = "-debug-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
        }
    }

    androidResources {
        additionalParameters += "--allow-reserved-package-id"
        additionalParameters += "--package-id"
        additionalParameters += "0x45"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    implementation("com.alibaba.fastjson2:fastjson2:2.0.51.android8")
    implementation("com.github.kyuubiran:EzXHelper:2.1.2")
    implementation("io.matthewnelson.encoding:base32:2.2.1")
    implementation("io.matthewnelson.encoding:base64:2.2.1")
    compileOnly("de.robv.android.xposed:api:82")
}
