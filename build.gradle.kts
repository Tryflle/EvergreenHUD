import dev.kikugie.loomx.LoomCompatDependencyExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.4.0"
    id("dev.kikugie.loom-back-compat")
    id("dev.deftu.gradle.bloom") version "0.2.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.0"
    id("org.jetbrains.compose") version "1.11.0"
}

val modid = property("mod.id") as String
val modname = property("mod.name") as String
val modversion = property("mod.version") as String
val mcversion = stonecutter.current.version
val oneconfigmc = stonecutter.current.project
val oneconfigversion = property("oneconfig_version") as String

base {
    archivesName.set(modname)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()

    maven("https://maven.parchmentmc.org")
    maven("https://repo.polyfrost.org/releases")
    maven("https://repo.polyfrost.org/snapshots")
    maven("https://api.modrinth.com/maven") {
        content { includeGroup("maven.modrinth") }
    }
    maven("https://maven.bawnorton.com/releases") {
        content { includeGroup("com.github.bawnorton.mixinsquared") }
    }

    maven("https://maven.logix.dev/snapshots") {
        content {
            excludeGroup("net.kyori")
            excludeGroup("com.terraformersmc")
        }
    }

    maven("https://maven.terraformersmc.com/releases") {
        content {
            includeGroup("com.terraformersmc")
        }
    }

    maven("https://nexus.prsm.wtf/repository/maven-public/maven-repo/releases/")
    maven("https://repo.hypixel.net/repository/Hypixel/")
    maven("https://maven.deftu.dev/releases")

    maven("https://central.sonatype.com/repository/maven-snapshots") {
        content { includeGroup("net.kyori") }
    }

    maven("https://maven.fabricmc.net/releases")
    maven("https://jitpack.io") {
        content { includeGroupAndSubgroups("com.github") }
    }
    maven("https://maven.azureaaron.net/releases") {
        content { includeGroup("net.azureaaron") }
    }
    maven("https://redirector.kotlinlang.org/maven/compose-dev")
}

loom {
    runConfigs.all {
        ideConfigGenerated(stonecutter.current.isActive)
        runDir = "../../run"
    }

    runConfigs.remove(runConfigs["server"])
}

dependencies {
    minecraft("com.mojang:minecraft:$mcversion")
    compileOnly("com.mojang:datafixerupper:4.0.26")
    the<LoomCompatDependencyExtension>().applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-common:0.3.3")!!)

    modImplementation("org.polyfrost.oneconfig:$oneconfigmc-fabric:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:commands:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:config:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:config-impl:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:events:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:internal:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:ui:$oneconfigversion")
    modImplementation("org.polyfrost.oneconfig:utils:$oneconfigversion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}+$mcversion")
    compileOnly(compose.desktop.currentOs)
}

bloom {
    replacement("@MOD_ID@", modid)
    replacement("@MOD_NAME@", modname)
    replacement("@MOD_VERSION@", modversion)
}

tasks.processResources {
    val props = mapOf(
        "mod_id" to modid,
        "mod_name" to modname,
        "mod_version" to modversion,
        "mod_description" to "",
        "mc_version" to mcversion,
        "loader_version" to providers.gradleProperty("loader_version").get()
    )

    inputs.properties(props)

    filesMatching("fabric.mod.json") {
        expand(props)
    }
}

tasks.build {
    doLast {
        val sourceFile = rootProject.projectDir.resolve("versions/${project.name}/build/libs/$modname.jar")
        val targetFile = rootProject.projectDir.resolve("build/libs/$modname-$modversion-${stonecutter.current.version}.jar")
        targetFile.parentFile.mkdirs()
        targetFile.writeBytes(sourceFile.readBytes())
    }
}

val javaVersion = if (stonecutter.eval(mcversion, ">=26")) 25 else 21

tasks.withType<JavaCompile>().configureEach {
    options.release.set(javaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(javaVersion.toString()))
    compilerOptions.freeCompilerArgs.add("-Xnullability-annotations=@org.jspecify.annotations:warn")
}

java {
    withSourcesJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

tasks.jar {
    inputs.property("archivesName", base.archivesName)

    from("LICENSE") {
        rename { "${it}_${inputs.properties["archivesName"]}" }
    }
}

fun <T> optionalProp(property: String, block: (String) -> T?): T? =
    findProperty(property)?.toString()?.takeUnless { it.isBlank() }?.let(block)