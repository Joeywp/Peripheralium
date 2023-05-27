import org.gradle.kotlin.dsl.repositories

plugins {
    java
}

repositories {
    mavenCentral()
    // For CC:T common code
    maven {
        url = uri("https://squiddev.cc/maven/")
        content {
            includeGroup("cc.tweaked")
            includeModule("org.squiddev", "Cobalt")
        }
    }
    // For Forge configuration common code
    maven {
        name = "Fuzs Mod Resources"
        url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
        content {
            includeGroup("fuzs.forgeconfigapiport")
        }
    }
}

fun connectIntegrationRepositories(targetProject: Project) {
    targetProject.repositories {
        maven {
            url = uri("https://www.cursemaven.com")
            name = "Curse Maven"
            content {
                includeGroup("curse.maven")
            }
        }
        maven {
            url = uri("https://api.modrinth.com/maven")
            name = "Modrinth"
            content {
                includeGroup("maven.modrinth")
            }
        }
        maven {
            url = uri("https://maven.architectury.dev/")
            content {
                includeGroup("dev.architectury")
            }
        }
    }
}

class BaseShakingExtension(private val targetProject: Project) {
    val projectPart: Property<String> = targetProject.objects.property(String::class.java)
    val integrationRepositories: Property<Boolean> = targetProject.objects.property(Boolean::class.java)

    fun shake() {
        val minecraftVersion: String by targetProject.extra
        val modBaseName: String by targetProject.extra
        val modVersion: String by targetProject.extra

        integrationRepositories.convention(false)

        if (integrationRepositories.get())
            connectIntegrationRepositories(targetProject)
        targetProject.base {
            archivesName.set("$modBaseName-$projectPart-$minecraftVersion")
            version = modVersion
        }

        targetProject.sourceSets.main.configure {
            resources.srcDir("src/generated/resources")
        }
    }
}

val baseShaking = BaseShakingExtension(project)
project.extensions.add("baseShaking", baseShaking)
