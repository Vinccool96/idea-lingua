import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.*

plugins {
    id("java")
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.intellij.platform") version "2.3.0"
}

group = "io.github.vinccool96.lingua"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        create("WS", "2025.1")

        pluginVerifier()
        zipSigner()

        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        bundledPlugins("com.intellij.modules.json", "JavaScript", "AngularJS")
    }
}

val properties = Properties().apply {
    load(rootProject.file("local.properties").reader())
}

intellijPlatform {
    pluginConfiguration {
        name = "Lingua"

        ideaVersion {
            sinceBuild = "251"
        }

        changeNotes = """
      Initial version
    """.trimIndent()
    }

    signing {
        certificateChainFile = rootProject.file(properties.getProperty("intellijPlatformCertificateChainPath"))
        privateKeyFile = rootProject.file(properties.getProperty("intellijPlatformPrivateKeyPath"))
        password = properties.getProperty("intellijPlatformPassword")
    }

    publishing {
        token = properties.getProperty("intellijPlatformPublishingToken")
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

kotlin {
    // Set the JVM compatibility versions
    java {
        compilerOptions {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }
    }

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}
