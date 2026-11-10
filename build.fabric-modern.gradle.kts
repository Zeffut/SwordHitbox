import net.fabricmc.loom.api.LoomGradleExtensionAPI

// Unobfuscated Minecraft must use non-remapping Loom. The legacy buildscript remains
// unchanged for already supported releases; using identity mappings corrupts dev class tweakers.
plugins {
    id("net.fabricmc.fabric-loom")
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

val loom = extensions.getByType(LoomGradleExtensionAPI::class.java)
val mcVersion = property("deps.minecraft") as String
base.archivesName = "${property("mod.id")}-fabric-$mcVersion"
version = "${property("mod.version")}+$mcVersion"
group = property("mod.group") as String

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.shedaniel.me/")
}

dependencies {
    add("minecraft", "com.mojang:minecraft:$mcVersion")
    add("implementation", "net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    add("implementation", "net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    add("implementation", "me.shedaniel.cloth:cloth-config-fabric:${property("deps.cloth_config")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
}

loom.runs.named("client") {
    client()
    vmArgs("-Dswordhitbox.dev=true")
}

val expandProps = mapOf("version" to version.toString(), "minecraft" to mcVersion)
tasks.named<ProcessResources>("processResources") {
    exclude("META-INF/neoforge.mods.toml")
    inputs.properties(expandProps)
    filesMatching("fabric.mod.json") { expand(expandProps) }
}
sourceSets.named("main") {
    val generated = layout.buildDirectory.dir("generated/stonecutter/main")
    java.setSrcDirs(listOf(generated.get().dir("java")))
    resources.setSrcDirs(listOf(generated.get().dir("resources")))
}
listOf("processResources", "compileJava", "sourcesJar").forEach { name ->
    tasks.matching { it.name == name }.configureEach { dependsOn("stonecutterGenerate") }
}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
}
tasks.withType<JavaCompile>().configureEach { options.release.set(25) }

val modrinthToken = providers.environmentVariable("MODRINTH_TOKEN")
if (modrinthToken.isPresent) {
    publishMods {
        file.set(tasks.named<Jar>("jar").flatMap { it.archiveFile })
        displayName.set("${property("mod.name")} ${property("mod.version")} (Fabric $mcVersion)")
        version.set("${property("mod.version")}+fabric-$mcVersion")
        type = me.modmuss50.mpp.ReleaseType.STABLE
        changelog.set(providers.gradleProperty("publish.changelog")
            .orElse("Portage vers Minecraft $mcVersion."))
        modLoaders.add("fabric")
        modrinth {
            projectId.set("xCnjRarB")
            accessToken.set(modrinthToken)
            minecraftVersions.add(mcVersion)
        }
    }
}
