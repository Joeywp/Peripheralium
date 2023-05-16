import site.siredvin.peripheralium.gradle.mavenDependencies
import org.jetbrains.changelog.date
import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseUploadTask
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.loom)
    alias(libs.plugins.kotlin)
    idea
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("org.jetbrains.changelog") version "1.3.1"
    id("com.modrinth.minotaur") version "2.+"
}

val modVersion: String by extra
val minecraftVersion: String by extra
val modBaseName: String by extra

base {
    archivesName.set("${modBaseName}-fabric-${minecraftVersion}")
    version = modVersion
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
    // location of the maven that hosts JEI files since January 2023
    maven {
        name = "Jared's maven"
        url = uri("https://maven.blamejared.com/")
        content {
            includeGroup("mezz.jei")
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

sourceSets {
    test {
        compileClasspath += project(":core").sourceSets["testFixtures"].output
        runtimeClasspath += project(":core").sourceSets["testFixtures"].output
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings(loom.officialMojangMappings())

    implementation(libs.bundles.kotlin)

    implementation(project(":core")) {
        exclude("cc.tweaked")
    }

    // TODO: dark mark here, if I will try to move this dependency
    // to libs it will change down toi 0.14.17
    // Like, what???
    modImplementation("net.fabricmc:fabric-loader:0.14.19")

    modImplementation(libs.bundles.fabric.core)
    modImplementation(libs.bundles.fabric)
    modImplementation(libs.bundles.ccfabric) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }

    modRuntimeOnly(libs.bundles.externalMods.fabric.runtime) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }

    testImplementation(kotlin("test"))
    testCompileOnly(libs.autoService)
    testAnnotationProcessor(libs.autoService)
    testImplementation(libs.byteBuddy)
    testImplementation(libs.byteBuddyAgent)
    testImplementation(libs.bundles.test)
}

loom {
    runs {
        named("client") {
            configName = "Fabric Client"
        }
        named("server") {
            configName = "Fabric Server"
        }
        create("datagen") {
            client()
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.modid=peripheralium")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/generated/resources")}")
            vmArg("-Dfabric-api.datagen.strict-validation")
        }
    }
}

sourceSets.main.configure {
    resources.srcDir("src/generated/resources")
}

tasks.test {
    dependsOn(tasks.generateDLIConfig)
    useJUnitPlatform()
    systemProperty("junit.jupiter.extensions.autodetection.enabled", true)
}

tasks {
    processResources {
        from(project(":core").sourceSets.main.get().resources)
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.version)) }
        exclude(".cache")
    }
    withType<JavaCompile> {
        if (this.name != "compileTestJava") {
            source(project(":core").sourceSets.main.get().allSource)
        }
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        if (this.name != "compileTestKotlin") {
            source(project(":core").sourceSets.main.get().allSource)
        }
    }
}

val rootProjectDir: File by extra

changelog {
    version.set(modVersion)
    path.set("${rootProjectDir}/CHANGELOG.md")
    header.set(provider { "[${version.get()}] - ${date()}" })
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf())
}


val CURSEFORGE_RELEASE_TYPE: String by extra
val CURSEFORGE_ID: String by extra
val curseforgeKey: String by extra

curseforge {
    options(closureOf<Options> {
        forgeGradleIntegration = false
    })
    apiKey = curseforgeKey
    project(closureOf<CurseProject> {
        id = CURSEFORGE_ID
        releaseType = CURSEFORGE_RELEASE_TYPE
        addGameVersion("Fabric")
        addGameVersion(minecraftVersion)
        try {
            changelog = "${project.changelog.get(project.version as String).withHeader(false).toText()}"
            changelogType = "markdown"
        } catch (ignored: Exception) {
            changelog = "Seems not real release"
            changelogType = "markdown"
        }
        mainArtifact(tasks.remapJar.get().archivePath, closureOf<CurseArtifact> {
            displayName = "Peripheralium $version for $minecraftVersion"
            relations(closureOf<CurseRelation> {
                requiredDependency("cc-tweaked")
                requiredDependency("forge-config-api-port-fabric")
                requiredDependency("fabric-language-kotlin")
            })
        })
    })
}
project.afterEvaluate {
    tasks.getByName<CurseUploadTask>("curseforge${CURSEFORGE_ID}") {
        dependsOn(tasks.remapJar)
    }
}


val MODRINTH_ID: String by extra
val MODRINTH_RELEASE_TYPE: String by extra
val modrinthKey: String by extra

modrinth {
    token.set(modrinthKey)
    projectId.set(MODRINTH_ID)
    versionNumber.set("${minecraftVersion}-${project.version}")
    versionName.set("Peripheralium ${version} for ${minecraftVersion}")
    versionType.set(MODRINTH_RELEASE_TYPE)
    uploadFile.set(tasks.remapJar.get())
    gameVersions.set(listOf(minecraftVersion))
    loaders.set(listOf("fabric")) // Must also be an array - no need to specify this if you're using Loom or ForgeGradl
    try {
        changelog.set("${project.changelog.get(project.version as String).withHeader(false).toText()}")
    } catch (ignored: Exception) {
        changelog.set("")
    }
    dependencies {
        required.project("fabric-language-kotlin")
        required.project("cc-tweaked")
        required.project("forge-config-api-port")
    }
}

tasks.create("uploadMod") {
    dependsOn(tasks.modrinth, tasks.curseforge)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = base.archivesName.get()
            from(components["java"])

            mavenDependencies {
                exclude(dependencies.create("site.siredvin:"))
                exclude(libs.jei.fabric.get())

            }
        }
    }
    repositories {
        mavenLocal()
    }
}