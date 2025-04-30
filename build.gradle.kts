plugins {
    id("java")
    id("io.freefair.lombok") version "8.4" // Optional, for Lombok
    id("com.github.johnrengelman.shadow") version "8.1.1" // Maven Shade equivalent
    `maven-publish`
}

group = "moe.ku6"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
}

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenCentral()
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.30")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.named("build") {
    finalizedBy("publishToMavenLocal")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "moe.ku6"
            artifactId = "LibChestGUI"
            version = "1.0.0"
        }
    }
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier.set("") // Replace default 'all' suffix
    }

    build {
        dependsOn(shadowJar)
    }
}
