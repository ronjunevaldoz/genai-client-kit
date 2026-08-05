import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("genaiclientkit.library")
}

kotlin {
    explicitApi()
    jvm()
    iosArm64()
    iosSimulatorArm64()
    js {
        browser()
        nodejs()
    }
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    androidLibrary {
        namespace = "io.github.ronjunevaldoz.genaiclient.bytedance"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.genaiClientKitCore)
            implementation(projects.genaiClientKitNetwork)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

mavenPublishing {
    pom {
        name = "genai-client-kit-bytedance"
        description = "ByteDance (Seedream/Seedance/Doubao via Volcano Engine Ark) client for genai-client-kit. Contract only; implementation pending."
    }
}
