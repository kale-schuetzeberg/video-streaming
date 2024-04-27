plugins {
    java
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.diffplug.spotless") version "6.25.0"
}

group = "com.baddog"
version = "0.0.1-SNAPSHOT"
description = "Video Streaming Service"

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
    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // MongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    // RabbitMQ
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("com.rabbitmq:amqp-client:5.20.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

    // Dev tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
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

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    java {
        indentWithSpaces()
        importOrder()
        googleJavaFormat()
        removeUnusedImports()
    }
    kotlin {
        ktlint()
    }
    kotlinGradle {
        ktlint()
    }
}
