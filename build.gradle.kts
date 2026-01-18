import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.0"
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
    id("dev.deftu.gradle.bloom") version "0.2.0"
}

val modid = property("mod.id") as String
val modname = property("mod.name") as String
val modversion = property("mod.version") as String
val mcversion = stonecutter.current.version

base {
    archivesName.set(modname)
}

repositories {
    maven("https://maven.parchmentmc.org")
    maven("https://repo.polyfrost.org/releases")
    maven("https://repo.polyfrost.org/snapshots")
    maven("https://api.modrinth.com/maven") {
        content { includeGroup("maven.modrinth") }
    }
    maven("https://maven.bawnorton.com/releases") {
        content { includeGroup("com.github.bawnorton.mixinsquared") }
    }
}

loom {
    runConfigs.all {
        ideConfigGenerated(stonecutter.current.isActive)
        runDir = "../../run"
    }

    runConfigs.remove(runConfigs["server"])
}

stonecutter {
    val ignored = listOf(
        "org.polyfrost.evergreenhud.client.hud.InventoryHud.kt",
        "org.polyfrost.evergreenhud.client.hud.ItemHud.kt",
        "org.polyfrost.evergreenhud.client.hud.PlayerPreviewHud.kt",
    )
    filters.exclude(*ignored.toTypedArray())
}

dependencies {
    minecraft("com.mojang:minecraft:$mcversion")
    compileOnly("com.mojang:datafixerupper:4.0.26")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-common:0.3.3")!!)

    modImplementation("org.polyfrost.oneconfig:$mcversion-fabric:1.0.0-alpha.181")
    modImplementation("org.polyfrost.oneconfig:commands:1.0.0-alpha.181")
    modImplementation("org.polyfrost.oneconfig:config:1.0.0-alpha.181")
    modImplementation("org.polyfrost.oneconfig:config-impl:1.0.0-alpha.181")
    modImplementation("org.polyfrost.oneconfig:events:1.0.0-alpha.181")
    modImplementation("org.polyfrost.oneconfig:internal:1.0.0-alpha.181")
    modImplementation("org.polyfrost.oneconfig:ui:1.0.0-alpha.181")
    modImplementation("org.polyfrost.oneconfig:utils:1.0.0-alpha.181")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}+$mcversion")
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

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
    inputs.property("archivesName", base.archivesName)

    from("LICENSE") {
        rename { "${it}_${inputs.properties["archivesName"]}" }
    }
}

fun <T> optionalProp(property: String, block: (String) -> T?): T? =
    findProperty(property)?.toString()?.takeUnless { it.isBlank() }?.let(block)