rootProject.name = "genai-client-kit"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(":genai-client-kit-core")
include(":genai-client-kit-network")
include(":genai-client-kit-elevenlabs")
include(":genai-client-kit-suno")
include(":genai-client-kit-bytedance")
include(":genai-client-kit-bom")
