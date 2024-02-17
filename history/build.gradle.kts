plugins {
    java
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.diffplug.spotless") version "6.25.0"
}

springBoot {
    mainClass.set("com.baddog.history.HistoryApplication")
}

group = "com.baddog"
version = "0.0.1-SNAPSHOT"
description = "Streaming History Service"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testCompileOnly("org.projectlombok:lombok:1.18.30")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Copy>("getDependencies") {
    from(sourceSets.main.get().runtimeClasspath)
    into("runtime/")

    doFirst {
        val runtimeDir = File("runtime")
        runtimeDir.deleteRecursively()
        runtimeDir.mkdir()
    }

    doLast {
        File("runtime").deleteRecursively()
    }
}

//configure<com.diffplug.gradle.spotless.SpotlessExtension> {
//    java {
//        indentWithSpaces()
//        importOrder()
//        googleJavaFormat()
//        removeUnusedImports()
//    }
//    kotlin {
//        ktlint()
//    }
//    kotlinGradle {
//        ktlint()
//    }
//}
