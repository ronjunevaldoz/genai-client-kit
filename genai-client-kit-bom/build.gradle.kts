import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `java-platform`
    alias(libs.plugins.vanniktech.publish)
}

javaPlatform { allowDependencies() }

dependencies {
    constraints {
        api(projects.genaiClientKitCore)
        api(projects.genaiClientKitNetwork)
        api(projects.genaiClientKitElevenlabs)
        api(projects.genaiClientKitSuno)
        api(projects.genaiClientKitBytedance)
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    pom {
        name = "genai-client-kit-bom"
        description = "Bill of materials aligning genai-client-kit artifact versions."
        url = "https://github.com/ronjunevaldoz/genai-client-kit"
        inceptionYear = "2026"

        licenses {
            license {
                name = "Apache-2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0"
            }
        }

        developers {
            developer {
                id = "ronjunevaldoz"
                name = "Ron June Valdoz"
                url = "https://github.com/ronjunevaldoz"
            }
        }

        scm {
            url = "https://github.com/ronjunevaldoz/genai-client-kit"
            connection = "scm:git:git://github.com/ronjunevaldoz/genai-client-kit.git"
            developerConnection = "scm:git:ssh://git@github.com/ronjunevaldoz/genai-client-kit.git"
        }
    }
}
