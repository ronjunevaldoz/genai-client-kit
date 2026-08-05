import com.vanniktech.maven.publish.SonatypeHost
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jlleitschuh.gradle.ktlint.KtlintExtension

/**
 * Shared setup for every genai-client-kit published KMP library module: plugin application,
 * ktlint/detekt config, and the common Maven Central publishing metadata. Each module still
 * declares its own Kotlin targets, Android namespace, source-set dependencies, and POM
 * name/description (KMP target DSL isn't safely configurable here due to Kotlin Gradle plugin
 * classloader isolation between the included build and the consuming project).
 */
class GenAiClientKitLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")
            pluginManager.apply("com.android.kotlin.multiplatform.library")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
            pluginManager.apply("com.vanniktech.maven.publish")
            pluginManager.apply("org.jetbrains.dokka")
            pluginManager.apply("org.jlleitschuh.gradle.ktlint")
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            extensions.configure(KtlintExtension::class.java) {
                version.set("1.3.1")
                android.set(false)
                outputToConsole.set(true)
                filter { exclude("**/build/**") }
            }

            extensions.configure(DetektExtension::class.java) {
                config.setFrom(rootProject.file("detekt.yml"))
                buildUponDefaultConfig = true
                allRules = false
            }

            extensions.configure(com.vanniktech.maven.publish.MavenPublishBaseExtension::class.java) {
                publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
                signAllPublications()
                pom {
                    url.set("https://github.com/ronjunevaldoz/genai-client-kit")
                    inceptionYear.set("2026")
                    licenses {
                        license {
                            name.set("Apache-2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }
                    developers {
                        developer {
                            id.set("ronjunevaldoz")
                            name.set("Ron June Valdoz")
                            url.set("https://github.com/ronjunevaldoz")
                        }
                    }
                    scm {
                        url.set("https://github.com/ronjunevaldoz/genai-client-kit")
                        connection.set("scm:git:git://github.com/ronjunevaldoz/genai-client-kit.git")
                        developerConnection.set("scm:git:ssh://git@github.com/ronjunevaldoz/genai-client-kit.git")
                    }
                }
            }
        }
}
