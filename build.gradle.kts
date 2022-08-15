@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.loom)
    alias(libs.plugins.loom.quiltflower)
}

group = "me.djtheredstoner"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://maven.xpple.dev/maven2") {
        mavenContent {
            includeGroup("dev.xpple")
        }
    }

    maven(url = "https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

val transitiveInclude by configurations.creating

dependencies {
    minecraft(libs.minecraft)
    mappings(libs.yarn) {
        artifact {
            classifier = "v2"
        }
    }
    modImplementation(libs.fabric.loader)

    modImplementation(libs.fabric.api)
    modImplementation(libs.clientarguments)

    implementation(libs.ice4j)
    transitiveInclude(libs.ice4j)

    implementation(libs.autoservice)
    annotationProcessor(libs.autoservice)

    modRuntimeOnly(libs.devauth)

    transitiveInclude.incoming.artifacts.forEach {
        (it.id.componentIdentifier as ModuleComponentIdentifier).run {
            // provided by Minecraft
            if (group == "net.java.dev.jna") return@forEach
            // provided by fabric-language-kotlin
            if (group == "org.jetbrains.kotlin") return@forEach
            if (group == "org.jetbrains" && module == "annotations") return@forEach

            include("$group:$module:$version")
        }
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to project.version))
    }
}