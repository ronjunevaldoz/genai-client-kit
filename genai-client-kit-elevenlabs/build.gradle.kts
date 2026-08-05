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
        namespace = "io.github.ronjunevaldoz.genaiclient.elevenlabs"
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
            implementation(libs.ktor.client.content.negotiation)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.serialization.json)
        }
    }
}

mavenPublishing {
    pom {
        name = "genai-client-kit-elevenlabs"
        description = "ElevenLabs speech-to-text (Scribe), text-to-speech, and realtime streaming client for genai-client-kit."
    }
}
