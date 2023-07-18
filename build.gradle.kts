plugins {
    java
    id("site.siredvin.root") version "0.4.1"
    id("site.siredvin.github") version "0.4.1"
}

subprojectShaking {
    withKotlin.set(true)
}

val setupSubproject = subprojectShaking::setupSubproject

subprojects {
    setupSubproject(this)
}

githubShaking {
    modBranch.set("1.20")
    dryRun.set(true)
    shake()
}

repositories {
    mavenCentral()
}